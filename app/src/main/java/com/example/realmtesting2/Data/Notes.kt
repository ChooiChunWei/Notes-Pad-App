package com.example.realmtesting2.Data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Notes(

    @PrimaryKey
    var id:Int? = 0,
    var title:String? = null,
    var description:String? = null,
    var date: String? = null,

    var isReminderSet:Boolean = false,
    var year: Int = 0,
    var month:Int = 0,
    var day:Int = 0,
    var hour:Int = 0,
    var minute:Int = 0
): RealmObject()