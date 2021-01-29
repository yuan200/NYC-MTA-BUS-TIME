package com.wen.android.mtabuscomparison.stop

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.wen.android.mtabuscomparison.BusApplication
import com.wen.android.mtabuscomparison.utilities.TimeConverts
import com.wen.android.mtabuscomparison.utilities.getLongOrEmpty
import com.wen.android.mtabuscomparison.utilities.getStringOrEmpty
import org.json.JSONArray
import java.io.IOException

@Database(entities = [Stop::class, FavoriteStop::class], version = 9)
@TypeConverters(TimeConverts::class)
abstract class BusDatabase : RoomDatabase() {

    abstract fun allBusDao(): StopDao

    abstract fun busListDao(): FavoriteStopDao

    companion object {
        private var INSTANCE: BusDatabase? = null

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table 'favorite_stop' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'stop_id' TEXT NOT NULL, " +
                        "'stop_id2' TEXT, 'stop_id3' TEXT, 'busLine' Text, 'busName' Text, 'groupName' Text, 'timestamp' INTEGER)")

                val cursor = database.query("select * from Buslist")
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val values = ContentValues();
                        values.put("_id", cursor.getLongOrEmpty("_id"))
                        values.put("stop_id", cursor.getStringOrEmpty("busStopCode"))
                        values.put("stop_id2", cursor.getStringOrEmpty("busStopCode2"))
                        values.put("stop_id3", cursor.getStringOrEmpty("busStopCode3"))
                        values.put("busLine", cursor.getStringOrEmpty("busLine"))
                        values.put("busName", cursor.getStringOrEmpty("busName"))
                        values.put("groupName", cursor.getStringOrEmpty("groupName"))
                        values.put("timestamp", cursor.getStringOrEmpty("timestamp"))
                        database.insert("favorite_stop", SQLiteDatabase.CONFLICT_REPLACE, values)
                    }
                    cursor.close()
                }
                database.execSQL("drop table Buslist")

                database.execSQL("DROP TABLE allbus")
                database.execSQL("""
                    CREATE TABLE stops (
                        stop_id TEXT PRIMARY KEY NOT NULL,
                        stop_name TEXT,
                        stop_lat REAL NOT NULL,
                        stop_lon REAL NOT NULL,
                        route_id TEXT
                    )
                """.trimIndent())

                database.beginTransaction();
                insertList(getListFromFile(BusApplication.instance, "stop/stops.json"), database)
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }

        fun getInstance(context: Context): BusDatabase {
            if (INSTANCE == null) {
                synchronized(BusDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                BusDatabase::class.java,
                                "buslist.db"
                        )
                                .createFromAsset("databases/buslist.db")
                                .addMigrations(MIGRATION_8_9)
                                .allowMainThreadQueries().build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

}
fun loadJSONFromAsset(context: Context, file: String): String? {
    var json: String? = null
    json = try {
        val `is` = context.assets.open(file)
        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        String(buffer, charset("UTF-8"))
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
    return json
}

fun getListFromFile(context: Context, fileName: String): Array<Stop> {
    var jsonStr = loadJSONFromAsset(context, fileName)
    var obj = JSONArray(jsonStr)
    return Gson().fromJson(obj.toString(), Array<Stop>::class.java)
}

fun insertList(stopList: Array<Stop>, database: SupportSQLiteDatabase) {
    val values = ContentValues()
    for (stop in stopList) {
        values.put("stop_id", stop.stopId)
        values.put("stop_name", stop.stopName)
        values.put("stop_lat", stop.stopLat)
        values.put("stop_lon", stop.stopLon)
        values.put("route_id", stop.routeId)

        database.insert("stops",CONFLICT_IGNORE, values)
    }
}