package com.example.realmtesting2.Adapter

import com.example.realmtesting2.Data.Notes

//Create interface for the select item in RecyclerView
interface OnUserClickListener {
    fun onItemClick(note: Notes, position: Int)
}