package com.example.mytestapplication

interface UpdateAndDelete{
    fun modifyItem(itemID :String ,isDone :Boolean)
    fun onItemDelete(itemID: String)
}