package com.example.mytestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() ,UpdateAndDelete {
    lateinit var database:DatabaseReference
    var taskList:MutableList<TaskModel>? = null
    lateinit var adapter: TaskAdapter
    private var listViewItem: ListView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab=findViewById<View>(R.id.fab) as FloatingActionButton

        listViewItem = findViewById<ListView>(R.id.item_listView)
        database = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener { h ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Нове завдання")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Додати"){dialog, j->
                val taskItemData = TaskModel.createList()
                taskItemData.title = textEditText.text.toString()
                taskItemData.status = false

                val newItemData = database.child("Task").push()
                taskItemData.ID = newItemData.key
                newItemData.setValue(taskItemData)
                dialog.dismiss()
                Toast.makeText(this, "Завдання збережено", Toast.LENGTH_LONG).show()
            }
            alertDialog.show()
        }
        taskList = mutableListOf<TaskModel>()
        adapter = TaskAdapter(this, taskList!!)
        listViewItem!!.adapter=adapter
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              taskList!!.clear()
                addItemToList(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()
        if (items.hasNext()) {
            val taskIndexedValue = items.next()
            val itemsIterator = taskIndexedValue.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val taskItemData = TaskModel.createList()
                val map = currentItem.getValue() as HashMap<String, Any>
                taskItemData.ID = currentItem.key
                taskItemData.status = map.get("status") as Boolean?
                taskItemData.title= map.get("title") as String?
                taskList!!.add(taskItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemID: String, isDone: Boolean) {
        val itemReference = database.child("Task").child(itemID)
        itemReference.child("status").setValue(isDone)
    }

    override fun onItemDelete(itemID: String) {
        val itemReference = database.child("Task").child(itemID)
        itemReference.removeValue()
        Toast.makeText(this, "Завдання видалено", Toast.LENGTH_LONG).show()
        adapter.notifyDataSetChanged()
    }
}
