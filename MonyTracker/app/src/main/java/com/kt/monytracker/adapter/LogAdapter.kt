package com.kt.monytracker.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kt.monytracker.R
import com.kt.monytracker.db.MoneyTrackerRepo
import com.kt.monytracker.model.LogType
import com.kt.monytracker.model.TaskLog
//gán = mutableListOf() để khi không có giá trị truyền vào sẽ là rỗng
class LogAdapter(
    private val dataSet: MutableList<TaskLog> = mutableListOf(),
    private val moneyTrackerRepo: MoneyTrackerRepo
    ) : RecyclerView.Adapter<LogAdapter.LogViewHolder>(){

    fun setData(dataSet: MutableList<TaskLog>){
        //
        this.dataSet.clear()
        this.dataSet.addAll(dataSet)
        notifyDataSetChanged()
    }

    inner class LogViewHolder(view : View) : RecyclerView.ViewHolder(view){


        var tvMoney = itemView.findViewById<TextView>(R.id.tvMoney)
        var tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)

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