package com.atom.android.muvik.utils.sqlite

import android.provider.BaseColumns

object SQLiteTableContract {
    object SongEntry : BaseColumns {
        const val TABLE_NAME = "song"
        const val _ID = "id"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_URI = "uri"
        const val COLUMN_NAME_FAVORITE = "favorite"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "$_ID TEXT PRIMARY KEY," +
                    "$COLUMN_NAME_NAME TEXT," +
                    "$COLUMN_NAME_URI TEXT," +
                    "$COLUMN_NAME_FAVORITE INTEGER)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val IS_FAVORITE = "1"

    }

}
