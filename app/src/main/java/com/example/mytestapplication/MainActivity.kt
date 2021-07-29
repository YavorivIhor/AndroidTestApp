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
    var toDoList:MutableList<ToDoModel>? = null
    lateinit var adapter: ToDoAdapter
    private var listViewItem: ListView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab=findViewById<View>(R.id.fab) as FloatingActionButton

        listViewItem = findViewById<ListView>(R.id.item_listView)
        database = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Нове завдання")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Додати"){dialog, i->
                val todoItemData = ToDoModel.createList()
                todoItemData.title = textEditText.text.toString()
                todoItemData.status = false

                val newItemData = database.child("Task").push()
                todoItemData.ID = newItemData.key

                newItemData.setValue(todoItemData)

                dialog.dismiss()
                Toast.makeText(this, "Завдання збережено", Toast.LENGTH_LONG).show()

            }
            alertDialog.show()
        }

        toDoList = mutableListOf<ToDoModel>()
        adapter = ToDoAdapter(this, toDoList!!)
        listViewItem!!.adapter=adapter
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              toDoList!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()
        if (items.hasNext()) {
            val toDoIndexedValue = items.next()
            val itemsIterator = toDoIndexedValue.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val toDoItemData = ToDoModel.createList()
                val map = currentItem.getValue() as HashMap<String, Any>
                toDoItemData.ID = currentItem.key
                toDoItemData.status = map.get("status") as Boolean?
                toDoItemData.title= map.get("title") as String?
                toDoList!!.add(toDoItemData)
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