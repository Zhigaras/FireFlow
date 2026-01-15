package com.zhigaras.fireflow

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

interface DatabaseProvider {
    
    fun provide(): DatabaseReference
    
    class Base : DatabaseProvider {

        override fun provide(): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
        }
    }
}