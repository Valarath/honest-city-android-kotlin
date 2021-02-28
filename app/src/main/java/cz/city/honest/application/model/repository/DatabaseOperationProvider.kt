package cz.city.honest.application.model.repository

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class DatabaseOperationProvider constructor(
    databaseConfiguration: DatabaseConfiguration
) : SQLiteOpenHelper(
    databaseConfiguration.context,
    databaseConfiguration.name,
    null,
    databaseConfiguration.version
) {

    init {
        onCreate(database = writableDatabase)
    }

    override fun onCreate(database: SQLiteDatabase) {
        createExchangeRateTable(database)
        createAuthorityTable(database)
        createUserTable(database)
        createSubjectTables(database)
        createSuggestionsTables(database)
        createUserVotesTable(database)
    }

    private fun createSubjectTables(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOt EXISTS watched_subject(id integer primary key, honesty_status text,watched_to text)")
        createExchangePointTable(database)
    }

    private fun createExchangePointTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS exchange_point(id integer primary key,exchange_rates_id not null,latitude float,longitude float,watched_subject_id integer not null,foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id), foreign key(watched_subject_id) references watched_subject(watched_subject_id))")
        //database.execSQL("Create table IF NOT EXISTS exchange_point_has_exchange_rate(exchange_point_id integer not null,exchange_rates_id not null,foreign key(exchange_point_id) references exchange_point(exchange_point_id),foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createUserVotesTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS user_vote(id integer primary key,user_id not null,suggestion_id integer not null, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(user_id) references user(user_id))")
    }

    private fun createUserTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS user(id integer primary key,score float,username text)")
    }

    private fun createExchangeRateTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS exchange_rates(id integer primary key)")
        database.execSQL("Create table IF NOT EXISTS exchange_rate(id integer primary key autoincrement,exchange_rates_id integer not null,buy float,sell float, currency text, foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createAuthorityTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS authority(exchange_rates_id not null,foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
        //database.execSQL("Create table IF NOT EXISTS authority_has_exchange_rate(authority_id integer not null,exchange_rates_id not null,foreign key(authority_id) references authority(authority_id),foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createSuggestionsTables(database: SQLiteDatabase) {
        createSuggestionTable(database)
        createNewExchangePointSuggestionTable(database)
        createCloseExchangePointSuggestionTable(database)
        createExchangeRateChangeSuggestionTable(database)
    }

    private fun createExchangeRateChangeSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS exchange_rate_change_suggestion(id integer primary key,suggestion_id not null,watched_subject__id integer not null, exchange_rates_id integer not null, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(watched_subject_id) references watched_subject(watched_subject_id), foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createCloseExchangePointSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS closed_exchange_point_suggestion(id integer primary key,suggestion_id not null,watched_subject_id integer not null, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(watched_subject_id) references watched_subject(watched_subject_id))")
    }

    private fun createNewExchangePointSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS new_exchange_point_suggestion(id integer primary key,suggestion_id not null,latitude float,longitude float, foreign key(suggestion_id) references suggestion(suggestion_id))")
    }

    private fun createSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS suggestion(id integer primary key, status text,votes integer, voted integer)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

