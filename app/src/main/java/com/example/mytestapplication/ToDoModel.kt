package com.example.mytestapplication

class ToDoModel {
    companion object Elements {
        fun createList(): ToDoModel = ToDoModel()
    }
    var ID: String? = null
    var title:String? = null
    var status: Boolean? = false
}