package com.kt.monytracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt.monytracker.adapter.LogAdapter
import com.kt.monytracker.db.MoneyTrackerDB
import com.kt.monytracker.db.MoneyTrackerRepo
import com.kt.monytracker.model.LogType
import com.kt.monytracker.model.TaskLog
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.log

//kết thừa coroutine
class MainActivity : AppCompatActivity(), CoroutineScope {

    //coroutine
    private val job = Job()
    //coroutine
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    //
    private val moneyTrackerRepo: MoneyTrackerRepo by lazy {
        val moneyTrackerDB = MoneyTrackerDB(applicationContext)
        MoneyTrackerRepo.create(moneyTrackerDB)
    }

    //
    private lateinit var logAdapter: LogAdapter

    lateinit var recyclerview:RecyclerView
    lateinit var loading: ProgressBar
    lateinit var container: LinearLayout
    lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview = findViewById(R.id.recyclerview)
        loading = findViewById(R.id.loading)
        container = findViewById(R.id.trackMoneyContainer)
        tvStatus = findViewById(R.id.tvStatus)

        //Tên actionbar
        title = "Money Tracker"

        var linearLayoutManager = LinearLayoutManager(applicationContext)

        //gạch phân cách giữa các row
        val decoration = DividerItemDecoration(this,linearLayoutManager.orientation)
        ContextCompat.getDrawable(this,R.drawable.bg_divider)

        //LogAdapter recyclerview
        var logadapter = LogAdapter(moneyTrackerRepo = moneyTrackerRepo)
        //

        recyclerview.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(decoration)
            adapter = logadapter
        }

        loadTasks(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            loadTasks(false)
        }
    }

    private fun loadTasks(enableDelay: Boolean){
        launch {
            if (enableDelay){
                //delay 2s để nó thực hiện load
                delay(2000L)
            }

            //lấy dữ liệu
            val listTasks = moneyTrackerRepo.selectAll()

            withContext(Dispatchers.Main){
                //
                if (listTasks.isEmpty()){
                    showEmptyTask()
                }else{
                    Log.d("DL",listTasks.toString())
                    displayTasks(listTasks)
                }
            }
        }
    }

    //show nếu nó không có dữ liệu
    private fun showEmptyTask(){
        //load được sẽ ẩn loading đi
        loading.visibility = View.GONE
        container.visibility = View.GONE

        tvStatus.visibility = View.VISIBLE
        tvStatus.text = "Empty !"
    }
    //
    private fun displayTasks(listTasks: MutableList<TaskLog>){
        loading.visibility = View.GONE
        tvStatus.visibility = View.GONE
        container.visibility = View.VISIBLE

        logAdapter.setData(listTasks)
    }

    fun mockTaskLogs(): MutableList<TaskLog>{
        val result = mutableListOf<TaskLog>()
        for (i in 1..10){
            if (i%2 == 0){
                result.add(
                    TaskLog(
                        id = i,
                        name = "Item $i",
                        money = 200 * i,
                        type = LogType.ADD
                    )
                )
            }else{
                result.add(
                    TaskLog(
                        id = i,
                        name = "Item $i",
                        money = 200 * i,
                        type = LogType.SUBTRACT
                    )
                )
            }
        }
        return result
    }
    //Để hiển thị chức năng add trên actionbar thì ta sẽ override lại 2 function
    //vd: chức năng add, update, delete item
    //override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    //ovirride
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.addTaskLog){
            val intent = Intent(this, AddTaskLogActivity::class.java)
            startActivityForResult(intent,1)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //coroutine
    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }
}