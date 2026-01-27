package com.zhigaras.fireflow

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

interface DatabaseProvider {

    /**
     * Provides a [DatabaseReference] to the root or a specific node of the database.
     *
     * @return A configured [DatabaseReference] instance.
     */
    fun provide(): DatabaseReference

    /**
     * Default implementation of [DatabaseProvider] that points to the
     * root of the default [FirebaseDatabase] instance.
     */
    class Default : DatabaseProvider {
        /**
         * Attention! Make sure your app make a FirebaseApp::initializeApp call before use this method.
         * @return The [DatabaseReference] for the default Firebase app.
         */
        override fun provide(): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
        }
    }
}
