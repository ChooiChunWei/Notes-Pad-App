package com.example.realmtesting2.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.realmtesting2.Notification.AlarmUtils
import com.example.realmtesting2.Data.Notes
import com.example.realmtesting2.R
import com.example.realmtesting2.Data.NotesRepo
import java.time.LocalTime
import java.util.*

class EditNotesActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    private lateinit var titleED: EditText
    private lateinit var descriptionED: EditText
    private lateinit var saveNotesBtn: Button
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var reminderSwitch: Switch
    private lateinit var datetimeTV: TextView
    private lateinit var calendar:Calendar
    private var myDay: Int = 0
    private var myYear: Int = 0
    private var myMonth: Int = 0

    //Realm Database
    private var notesRepo = NotesRepo()
    private var alarmUtils = AlarmUtils(this)
    private var id:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notes)

        //init view
        titleED = findViewById(R.id.title_EditText)
        descriptionED = findViewById(R.id.description_EditText)
        saveNotesBtn = findViewById(R.id.saveNotesBtn)
        toolbar = findViewById(R.id.toolbar)
        reminderSwitch = findViewById(R.id.reminderSwitch)
        datetimeTV = findViewById(R.id.datetimeTV)

        setToolbar()


        //get id from previous activity using bundle
        val bundle = intent.extras
        if(bundle != null){
            id = bundle.getInt("id")

            //get data from database
            val result = notesRepo.getNote(id)

            titleED.setText(result?.title)
            descriptionED.setText(result?.description)

            if(result?.isReminderSet == true){
                reminderSwitch.isChecked = true

                calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, result.year)
                calendar.set(Calendar.MONTH,result.month)
                calendar.set(Calendar.DAY_OF_MONTH,result.day)
                calendar.set(Calendar.HOUR_OF_DAY,result.hour)
                calendar.set(Calendar.MINUTE,result.minute)

                updateDateTimeText(result.year,result.month,result.day,result.hour, result.minute)
            }
        }

        reminderSwitch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                showDateTimeDialog()
            }else{
                datetimeTV.visibility = View.GONE
                alarmUtils.cancelAlarm(id)
            }
        }

        saveNotesBtn.setOnClickListener {
            if(titleED.text.isNotEmpty()){
                if(reminderSwitch.isChecked){
                    dateCheck()
                }else{
                    editNote(id)
                }
            }else{
                Toast.makeText(this,"Title is required to proceed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun dateCheck(){
        val currentCalendar = Calendar.getInstance()
        val currentMilli = currentCalendar.timeInMillis

        if(calendar.timeInMillis <= currentMilli){
            //reminder time is same/earlier than current time, which is not allow
            Toast.makeText(this,"Reminder time cannot be the same/earlier than current time",Toast.LENGTH_SHORT).show()
        }else{
            editNote(id)
            alarmUtils.startAlarm(calendar,titleED.text.toString(),id)
        }
    }

    private fun showDateTimeDialog() {
        calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(this, this, year, month,day)
        datePickerDialog.show()

        datePickerDialog.setOnCancelListener{
            reminderSwitch.isChecked = false
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        myDay = day
        myYear = year
        myMonth = month

        calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,myYear)
        calendar.set(Calendar.MONTH,myMonth)
        calendar.set(Calendar.DAY_OF_MONTH,myDay)

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(this))
        timePickerDialog.show()

        timePickerDialog.setOnCancelListener{
            reminderSwitch.isChecked = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)

        updateDateTimeText(hourOfDay, minute)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateTimeText(myHour: Int, myMinute: Int) {
        datetimeTV.visibility = View.VISIBLE
        val time = LocalTime.of(myHour, myMinute)
        datetimeTV.text = "$myDay/${myMonth + 1}/$myYear   $time"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateTimeText(myYear:Int,myMonth:Int,myDay:Int, myHour: Int, myMinute: Int) {
        datetimeTV.visibility = View.VISIBLE
        val time = LocalTime.of(myHour, myMinute)
        datetimeTV.text = "$myDay/${myMonth + 1}/$myYear   $time"
    }
    

    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        addBackBtn()
    }

    private fun addBackBtn(){
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            if(titleED.text.isNotEmpty() || descriptionED.text.isNotEmpty()){
                popUpExitMsg()
            }else{
                onBackPressed()
            }
        }
    }

    private fun popUpExitMsg(){
        //Pop Up Message
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Exit?")
        builder.setMessage("If you leave before saving, your changes will be lost. \n\nDo you want to exit?")

        builder.setPositiveButton("No"){dialog,which->
            dialog.cancel()
        }

        builder.setNegativeButton("Yes"){dialog,which->
            onBackPressed()
        }

        builder.show()
    }

    //Add Search Icon
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_icon,menu)
        return true
    }
    //When Search Icon is being clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.search){
            popUpDelMsg()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun popUpDelMsg(){
        //Pop Up Message
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Delete?")
        builder.setMessage("Do you want to delete?")

        builder.setPositiveButton("No"){dialog,which->
            dialog.cancel()
        }

        builder.setNegativeButton("Yes"){dialog,which->
            delNotes(id)
            alarmUtils.cancelAlarm(id)
        }

        builder.show()
    }

    private fun delNotes(id:Int){
        try{
            notesRepo.delNotes(id)

            Toast.makeText(this,"Notes Deleted Successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }catch(e:Exception){
            Toast.makeText(this,"Failed $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editNote(id: Int) {
        try{
            //Create a new record
            val notes = Notes()
            notes.title = titleED.text.toString()
            notes.description = descriptionED.text.toString()
            notes.id = id

            if(reminderSwitch.isChecked){
                notes.isReminderSet = true
                notes.year = calendar.get(Calendar.YEAR)
                notes.month = calendar.get(Calendar.MONTH)
                notes.day = calendar.get(Calendar.DAY_OF_MONTH)
                notes.hour = calendar.get(Calendar.HOUR_OF_DAY)
                notes.minute = calendar.get(Calendar.MINUTE)
            }else{
                notes.isReminderSet = false
            }

            notesRepo.editNote(notes)

            //Show Text
            Toast.makeText(this,"Notes Modified Successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }catch (e:Exception){
            Toast.makeText(this,"Failed $e", Toast.LENGTH_SHORT).show()
        }
    }
}