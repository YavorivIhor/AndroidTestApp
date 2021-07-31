package com.example.mytestapplication

class TaskModel {
    companion object Elements {
        fun createList(): TaskModel = TaskModel()
    }
    var ID: String? = null
    var title:String? = null
    var status: Boolean? = false
}