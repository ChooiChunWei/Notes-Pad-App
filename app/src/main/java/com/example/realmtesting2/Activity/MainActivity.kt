package com.example.realmtesting2.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.realmtesting2.Adapter.OnUserClickListener
import com.example.realmtesting2.Adapter.RVAdapter
import com.example.realmtesting2.Data.Notes
import com.example.realmtesting2.Data.NotesRepo
import com.example.realmtesting2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmResults

class MainActivity : AppCompatActivity(),OnUserClickListener {

    private lateinit var addNotes:FloatingActionButton
    private lateinit var notesRV:RecyclerView
    private lateinit var realm:Realm
    private lateinit var results:RealmResults<Notes>
    private var notesRepo = NotesRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init realm
        realm = Realm.getDefaultInstance()

        //init views
        addNotes = findViewById(R.id.addNotes)
        notesRV = findViewById(R.id.notesRV)

        //onclick add notes btn
        addNotes.setOnClickListener {
            startActivity(Intent(this,
                AddNotesActivity::class.java))
        }

        notesRV.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        getAllNotes()
    }

    private fun getAllNotes() {

        results = notesRepo.getAllNotes()!!

        notesRV.adapter = RVAdapter(results, this)
        notesRV.adapter!!.notifyDataSetChanged()
    }

    override fun onItemClick(note: Notes, position: Int) {
        val intent = Intent(this,EditNotesActivity::class.java)
        val bundle= bundleOf(
            "id" to note.id
        )

        intent.putExtras(bundle)
        //Want to get return from the next activity, then use this way
        startActivityForResult(intent,999)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}