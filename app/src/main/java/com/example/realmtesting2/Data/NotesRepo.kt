package com.example.realmtesting2.Data

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.text.SimpleDateFormat
import java.util.*

class NotesRepo{
    private lateinit var realm:Realm

    fun getNextID():Int{
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        //Auto increment ID
        val currentIdNumber:Number? = realm.where(Notes::class.java).max("id")
        val nextID:Int
        nextID = if(currentIdNumber == null){
            1
        }else{
            currentIdNumber.toInt() + 1
        }
        realm.commitTransaction()
        realm.close()

        return nextID
    }

    private fun getCurrentDateTime(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return formatter.format(date)
    }

    fun addNotes(note: Notes){
        //init realm
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        note.date = getCurrentDateTime()

        //Copy this transaction & commit
        realm.copyToRealmOrUpdate(note)
        realm.commitTransaction()
        realm.close()
    }

    fun editNote(note: Notes){
        realm = Realm.getDefaultInstance()
        note.date = getCurrentDateTime()

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(note)
        realm.commitTransaction()
        realm.close()
    }

    fun delNotes(id:Int){
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.where(Notes:: class.java).equalTo("id", id).findFirst()!!.deleteFromRealm()
        realm.commitTransaction()
        realm.close()
    }

    fun getAllNotes(): RealmResults<Notes>? {
        realm = Realm.getDefaultInstance()
        val result =realm.where<Notes>(Notes::class.java).sort("date", Sort.DESCENDING).findAll()
        realm.close()

        return result
    }

    fun getNote(id:Int): Notes? {
        realm = Realm.getDefaultInstance()
        val result: Notes? = realm.where(Notes::class.java).equalTo("id", id).findFirst()
        realm.close()

        return result
    }
}