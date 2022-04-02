package com.kt.monytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import com.kt.monytracker.db.MoneyTrackerDB
import com.kt.monytracker.db.MoneyTrackerRepo
import com.kt.monytracker.model.LogType
import com.kt.monytracker.model.TaskLog
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.carbonylgroup.textfieldboxes.ExtendedEditText
import kotlin.coroutines.CoroutineContext

//Add Task Log
class AddTaskLogActivity : AppCompatActivity(), CoroutineScope {

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
    //Nếu mà include cái coroutine này thì ae có thể
    //viết 1 cái base activity kế thừa từ CoroutineScope này và override lại 2 cái phương thức trên để có thể sử dụng được chung


    lateinit var stickySwitch: StickySwitch
    lateinit var btnAddTask: Button
    lateinit var extendedEditTaskName: ExtendedEditText
    lateinit var extendedEditMoney: ExtendedEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task_log)

        stickySwitch = findViewById(R.id.sticky_switch)
        btnAddTask = findViewById(R.id.btnAddTask)
        extendedEditTaskName = findViewById(R.id.extendedEditTaskName)
        extendedEditMoney = findViewById(R.id.extendedEditMoney)

        title = "Add Task Log"
        //nút back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var logType = LogType.ADD
        stickySwitch.setA(object :StickySwitch.OnSelectedChangeListener{
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                logType = LogType.valueOf(text)
            }
        })

        btnAddTask.setOnClickListener {
            val taskName = extendedEditTaskName.text.toString()
            val money = extendedEditMoney.text.toString()

            launch {
                moneyTrackerRepo.insert(
                    TaskLog(
                        name = taskName,
                        money = money.toInt(),
                        type = logType
                    )
                )

                finish()
                setResult(1)
            }
        }

    }


    //bắt sự kiện cho nút back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //nếu nó back là home
        if (item.itemId == android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}