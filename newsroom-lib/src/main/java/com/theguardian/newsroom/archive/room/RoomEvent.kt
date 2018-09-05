package com.theguardian.newsroom.archive.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class RoomEvent(
        @PrimaryKey(autoGenerate = true)
        var id: Long?,
        @ColumnInfo
        val source: String,
        @ColumnInfo
        val title: String
)
