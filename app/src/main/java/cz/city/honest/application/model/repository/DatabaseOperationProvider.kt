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
        createSubjectTables(database)
        createSuggestionsTables(database)
        createUserTables(database)
        createSettingsTables(database)
    }

    private fun createSubjectTables(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOt EXISTS watched_subject(id varchar primary key ON CONFLICT REPLACE, honesty_status text,watched_to text)")
        createExchangePointTable(database)
    }

    private fun createExchangePointTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS exchange_point(id varchar primary key on conflict replace,exchange_rates_id varchar not null,latitude float,longitude float, watched_subject_id varchar not null,foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id), foreign key(watched_subject_id) references watched_subject(watched_subject_id) ) ")
    }

    private fun createUserVotesTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS user_vote(user_id varchar not null,suggestion_id varchar not null, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(user_id) references user(user_id))")
    }

    private fun createUserTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS user(id varchar primary key ON CONFLICT REPLACE,score float, username text, logged integer, login_data_class varchar)")
    }

    private fun createExchangeRateTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS exchange_rates(id varchar primary key ON CONFLICT REPLACE)")
        database.execSQL("Create table IF NOT EXISTS exchange_rate(id varchar primary key ON CONFLICT REPLACE,exchange_rates_id varchar not null,buy float,sell float, currency text, foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createAuthorityTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS authority(exchange_rates_id varchar primary key ON CONFLICT REPLACE not null,foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createSuggestionsTables(database: SQLiteDatabase) {
        createSuggestionTable(database)
        createNewExchangePointSuggestionTable(database)
        createCloseExchangePointSuggestionTable(database)
        createExchangeRateChangeSuggestionTable(database)
    }

    private fun createExchangeRateChangeSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS exchange_rate_change_suggestion(id varchar primary key,watched_subject_id varchar not null, exchange_rates_id varchar not null, foreign key(id) references suggestion(id), foreign key(watched_subject_id) references watched_subject(watched_subject_id), foreign key(exchange_rates_id) references exchange_rates(exchange_rates_id))")
    }

    private fun createCloseExchangePointSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS closed_exchange_point_suggestion(id varchar primary key,watched_subject_id varchar not null, foreign key(id) references suggestion(id), foreign key(watched_subject_id) references watched_subject(watched_subject_id))")
    }

    private fun createNewExchangePointSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS new_exchange_point_suggestion(id varchar primary key,latitude float,longitude float, foreign key(id) references suggestion(id))")
    }

    private fun createSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS suggestion(id varchar primary key on conflict replace, status text,votes integer)")
    }

    private fun createUserSuggestionTable(database: SQLiteDatabase){
        database.execSQL("Create table if not exists user_suggestion(user_id varchar not null, suggestion_id varchar not null, processed integer, type varchar, mark_as varchar, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(user_id) references user(user_id))")
    }

    private fun createUserTables(database: SQLiteDatabase){
        createUserTable(database)
        createUserVotesTable(database)
        createUserSuggestionTable(database)
        createLoginDataTables(database)
    }

    private fun createSettingsTables(database: SQLiteDatabase){
        createCurrencySettingsTable(database)
    }

    private fun createLoginDataTables(database: SQLiteDatabase){
        createFacebookLoginDataTable(database)
    }

    private fun createFacebookLoginDataTable(database: SQLiteDatabase){
        database.execSQL("Create table IF NOT EXISTS facebook_login_data(id varchar primary key ON CONFLICT REPLACE, access_token varchar, user_id varchar not null, foreign key(user_id) references user(user_id))")

    }

    private fun createCurrencySettingsTable(database: SQLiteDatabase){
        database.execSQL("Create table if not exists currency_settings(id varchar primary key on conflict replace, currency varchar not null, main_country_currency integer not null)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

