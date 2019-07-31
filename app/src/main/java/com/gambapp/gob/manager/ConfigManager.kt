package com.gambapp.gob.manager

import android.content.Context
import android.util.Log
import com.gambapp.gob.helpers.PreferenceHelper
import io.realm.Realm
import io.realm.RealmConfiguration



class ConfigManager(private var context: Context) {


    private var onboardingKey = "onboardingKey"
    private var realmDatabaseVersionKey = "realmDatabaseVersionKey"
    private var firstConfigurationKey = "firstcfgkey"
    private val testDbFileName = "testdb.realm"


    private var realmDatabaseVersion: Int = 1
    private val sharedPreferences: PreferenceHelper = PreferenceHelper

    fun initConfiguration(context: Context) {


        val alreadyInit = sharedPreferences.getIntegerPreference(context, firstConfigurationKey, 0)

        if (alreadyInit == 0) {
            sharedPreferences.setStringPreference(context, "firstConfigurationKey", firstConfigurationKey)
            sharedPreferences.setIntegerPreference(context, "realmDatabaseVersion", realmDatabaseVersion)
        }


        Realm.init(context)
        val realmConfiguration = RealmConfiguration.Builder()
                .name(testDbFileName)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)



    }

    private fun updateDatabaseVersionKey(version: Int) {
        sharedPreferences.setIntegerPreference(context, "realmDatabaseVersionKey", version)

    }

    fun shouldUpdateRealmDatabaseVersion(version: Int) {
        val stockedVersion = sharedPreferences.getIntegerPreference(context, realmDatabaseVersionKey,0)
        Log.d("ConfigstockedVersion",stockedVersion.toString())
        Log.d("Configversion",version.toString())

        if (version != stockedVersion){
            Log.d("Config","sa reconfig")
            reconfigDataBase(version)
        }

    }

    fun reconfigDataBase(version: Int) {
        RealmManager(context).deleteDatabase()
        RealmManager(context).setupDatabase()
        RealmManager(context).setupFreeGame()
        updateDatabaseVersionKey(version)
    }

    private val PREF_NAME = "SharedPreferences_Name"
    private val DATA_NAME = "BytesData_Name"

    fun getBytes(ctx: Context): ByteArray {
        val prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val str = prefs.getString(DATA_NAME, null).trim()
        Log.d("tag",str)
        return str.toByteArray(Charsets.ISO_8859_1)
    }

    fun setBytes(ctx: Context, bytes: ByteArray) {
        val prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val e = prefs.edit()
        e.putString(DATA_NAME, String(bytes, Charsets.ISO_8859_1).trim())
        e.commit()
    }

}
