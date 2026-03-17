package com.harputoglu.orhun

import android.app.Application
import androidx.room.Room
import com.harputoglu.orhun.data.local.AppDatabase

class OrhunApp : Application() {
    
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "orhun_db"
        ).fallbackToDestructiveMigration().build()
    }
}
