package com.example.realmtesting2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realmtesting2.Data.Notes
import com.example.realmtesting2.R
import io.realm.RealmResults
import kotlinx.android.synthetic.main.notes_rv_layout.view.*
import java.util.*

class RVAdapter(private val notesList: RealmResults<Notes>, private var clickListener: OnUserClickListener):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.notes_rv_layout,parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note = notesList[position]!!
        holder.itemView.dateTV.text = note.date.toString()

        //Trim the name if too long
        val title = note.title
        if(title!!.length > 9){
            val s = title.toString().substring(0,9)
            holder.itemView.TitleTV.text= "$s..."
        }else{
            holder.itemView.TitleTV.text= title
        }

        //Trim the description if too long
        val desc = note.description
        if(desc!!.length > 85){
            val s = desc.toString().substring(0,85)
            holder.itemView.descTV.text = "$s..."
        }else{
            holder.itemView.descTV.text = desc
        }

        //Alert icon
        if(note.isReminderSet){
            holder.itemView.alertIcon.visibility = View.VISIBLE

            val currentCalendar = Calendar.getInstance()
            val currentMilli = currentCalendar.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.set(note.year,note.month,note.day,note.hour,note.minute,0)

            if(calendar.timeInMillis <= currentMilli){
                holder.itemView.alertIcon.setImageResource(R.drawable.ic_alert_triggered)
            }else{
                holder.itemView.alertIcon.setImageResource(R.drawable.ic_alert)
            }
        }

        //For the user select the item in the RecyclerView
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(notesList[position]!!, position)
        }
    }

    class ViewHolder(v: View?):RecyclerView.ViewHolder(v!!)
}