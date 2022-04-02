package com.kt.monytracker.db

import android.content.ContentValues
import com.kt.monytracker.model.LogType
import com.kt.monytracker.model.TaskLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoneyTrackerRepo (private val moneyTrackerDB: MoneyTrackerDB){

    companion object{

        fun create(moneyTrackerDB: MoneyTrackerDB): MoneyTrackerRepo{
            return MoneyTrackerRepo(moneyTrackerDB)
        }
    }

    suspend fun insert(taskLog: TaskLog) = withContext(Dispatchers.IO){
        val database = moneyTrackerDB.writableDatabase

        ContentValues().apply {
            put(DbConfig.TaskLog.COL_TASK_NAME, taskLog.name)
            put(DbConfig.TaskLog.COL_MONEY, taskLog.money.toString())
            put(DbConfig.TaskLog.COL_TYPE, taskLog.type.toString())
        }.also {
            database.insert(DbConfig.TaskLog.TABLE_NAME,null,it)
        }
    }

    suspend fun selectAll() = withContext(Dispatchers.IO) {
        val database = moneyTrackerDB.readableDatabase

        val cursor = database.query(true, DbConfig.TaskLog.TABLE_NAME,
        arrayOf(
            DbConfig.TaskLog.COL_ID,
            DbConfig.TaskLog.COL_TASK_NAME,
            DbConfig.TaskLog.COL_MONEY,
            DbConfig.TaskLog.COL_TYPE
        ),
        null,null,null,null,null,null)

        val result = mutableListOf<TaskLog>()
        cursor.let {
            if (cursor.moveToFirst()){
                do {
                    val idIndext = cursor.getColumnIndex(DbConfig.TaskLog.COL_ID)
                    val idTaskName = cursor.getColumnIndex(DbConfig.TaskLog.COL_TASK_NAME)
                    val idMoney = cursor.getColumnIndex(DbConfig.TaskLog.COL_MONEY)
                    val idType = cursor.getColumnIndex(DbConfig.TaskLog.COL_TYPE)

                    result.add(
                        TaskLog(
                            cursor.getInt(idIndext),
                            cursor.getString(idTaskName),
                            cursor.getString(idMoney).toInt(),
                            LogType.valueOf(cursor.getString(idType))
                        )
                    )
                }while (cursor.moveToNext())


            }
        }
        //return về result
        result
    }

    suspend fun delete(taskId: Int) = withContext(Dispatchers.IO){
        val database = moneyTrackerDB.writableDatabase
        //return về true or false (nếu nó xóa được thì > 1)
        database.delete(DbConfig.TaskLog.TABLE_NAME,"_id = $taskId", null) > 0
    }

    //
    suspend fun update(taskLog: TaskLog){
    }
}