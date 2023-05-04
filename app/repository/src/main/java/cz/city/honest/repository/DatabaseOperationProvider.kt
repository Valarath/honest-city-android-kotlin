package cz.city.honest.repository

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
        createAuthorityTable(database)
        createSubjectTables(database)
        createSuggestionTable(database)
        createUserTables(database)
        createSettingsTables(database)
        createPositionTable(database)
    }

    private fun createSubjectTables(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOt EXISTS watched_subject(id varchar primary key ON CONFLICT REPLACE, class text, data text, honesty_status text, watched_to text)")
    }

    private fun createUserVotesTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS user_vote(user_id varchar not null,suggestion_id varchar not null, processed integer not null, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(user_id) references user(user_id))")
    }

    private fun createUserTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS user(id varchar primary key ON CONFLICT REPLACE,score float, username text, logged integer)")
    }

    private fun createAuthorityTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS authority(id varchar primary key ON CONFLICT REPLACE not null, data text)")
    }

    private fun createSuggestionTable(database: SQLiteDatabase) {
        database.execSQL("Create table IF NOT EXISTS suggestion(id varchar primary key on conflict replace, class text, data text, created_at integer, subject_id varchar, foreign key(subject_id) references watched_subject(subject_id))")
    }

    private fun createUserSuggestionTable(database: SQLiteDatabase){
        database.execSQL("Create table if not exists user_suggestion(user_id varchar not null, suggestion_id varchar not null, processed integer, type varchar, mark_as varchar, foreign key(suggestion_id) references suggestion(suggestion_id), foreign key(user_id) references user(user_id), UNIQUE (user_id, suggestion_id) ON CONFLICT REPLACE)")
    }

    private fun createUserTables(database: SQLiteDatabase){
        createUserTable(database)
        createUserVotesTable(database)
        createUserSuggestionTable(database)
        createLoginDataTables(database)
    }

    private fun createSettingsTables(database: SQLiteDatabase){
        createCurrencySettingsTable(database)
        createSubjectSettingsTable(database)
    }

    private fun createLoginDataTables(database: SQLiteDatabase){
        createLoginDataTable(database)
    }

    private fun createLoginDataTable(database: SQLiteDatabase){
        database.execSQL("Create table IF NOT EXISTS login_data(id varchar primary key ON CONFLICT REPLACE, class text, data text, user_id varchar not null, foreign key(user_id) references user(user_id))")
    }

    private fun createCurrencySettingsTable(database: SQLiteDatabase){
        database.execSQL("Create table if not exists currency_settings(id varchar primary key on conflict replace, currency varchar not null, main_country_currency integer not null)")
    }

    private fun createSubjectSettingsTable(database: SQLiteDatabase){
        database.execSQL("Create table if not exists subject_settings(data text)")
    }

    private fun createPositionTable(database: SQLiteDatabase){
        database.execSQL("Create table if not exists position(latitude REAL, longitude REAL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

