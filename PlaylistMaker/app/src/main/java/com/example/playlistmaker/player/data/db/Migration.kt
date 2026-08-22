package com.example.playlistmaker.player.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        
        db.execSQL("ALTER TABLE track_playlist_table ADD COLUMN addedAt INTEGER DEFAULT 0")
    }
}
