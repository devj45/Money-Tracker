package com.kt.monytracker.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kt.monytracker.R
import com.kt.monytracker.db.MoneyTrackerRepo
import com.kt.monytracker.model.LogType
import com.kt.monytracker.model.TaskLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//
typealias OnDeleteItem = (task: TaskLog) -> Unit
typealias OnUpdateItem = (task: TaskLog) -> Unit

//gán = mutableListOf() để khi không có giá trị truyền vào sẽ là rỗng
class LogAdapter(
    private val dataSet: MutableList<TaskLog> = mutableListOf(),
    private val moneyTrackerRepo: MoneyTrackerRepo,
    private val onDeleteItem: OnDeleteItem,
    private val onUpdateItem: OnUpdateItem
    )
    : RecyclerView.Adapter<LogAdapter.LogViewHolder>(){

    fun setData(dataSet: MutableList<TaskLog>){
        this.dataSet.clear()
        this.dataSet.addAll(dataSet)
        notifyDataSetChanged()
    }

    inner class LogViewHolder(view : View) : RecyclerView.ViewHolder(view){


        var tvMoney = itemView.findViewById<TextView>(R.id.tvMoney)
        var tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)
        var actionItem = itemView.findViewById<FrameLayout>(R.id.actionItem)

        init {
            actionItem.setOnClickListener {
                showMenuAction(context = it.context, it)
            }
        }

        fun bind(task: TaskLog){

            tvTaskName.text = task.name

            //nếu type của nó là add
            if (task.type == LogType.ADD){
                tvMoney.apply {
                    setTextColor(Color.parseColor("#669900"))
                    text = "+ ${task.money}"
                }
            }
            else{
                tvMoney.apply {
                    setTextColor(Color.RED)
                    text = "- ${task.money}"
                }
            }
        }
        //anchorView : view mà ta click
        private fun showMenuAction(context: Context, anchorView: View){
            //kỹ thuật popupMenu để show
            var popupMenu = PopupMenu(context, anchorView)
            popupMenu.apply {
                inflate(R.menu.menu_task_action)

                //bắt sk
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.updateTaskLog -> {
                            true
                        }
                        R.id.deleteTaskLog -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                //vị trí cần xóa
                                moneyTrackerRepo.delete(dataSet[adapterPosition].id)
                                //chuyển về Main Thread làm việc với UI
                                withContext(Dispatchers.Main){
                                    val currentPos = adapterPosition
                                    val task = dataSet[currentPos]
                                    //remove dataSet
                                    dataSet.removeAt(currentPos)
                                    //remove UI
                                    notifyItemRemoved(currentPos)

                                    //Delete cái task
                                    onDeleteItem(task)
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }
            }.also {
                //show
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_log, parent,false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size


}