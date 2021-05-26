package com.wen.android.mtabuscomparison.persistence.migrations

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        BusDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    var sqliteTestOpenHelper: SqliteTestOpenHelper? = null

    @Before
    fun setUp() {
        sqliteTestOpenHelper = SqliteTestOpenHelper(
            ApplicationProvider.getApplicationContext(),
            TEST_DB, null, 8
        )

        SqliteTestHelper.createTable(sqliteTestOpenHelper!!)
    }

    @After
    fun tearDown() {
        sqliteTestOpenHelper!!.writableDatabase.apply {
            println("droping table")
            execSQL("DROP TABLE IF EXISTS stops")
            execSQL("DROP TABLE IF EXISTS favorite_stop")
            close()
        }
    }

    @Test
    fun migrationFrom8To9() {
        helper.runMigrationsAndValidate(TEST_DB, 9, true, BusDatabase.MIGRATION_8_9)

        val roomDb = getMigratedRoomDatabase()
        val favoriteList = FavoriteStop("0001", "0001", null, null, null, null, null)
        roomDb.favoriteStopDao().insertAll(favoriteList)

        // 301043 is from actual migration process, maybe want to use dummy value
        assertEquals("301043", roomDb.busStopDao().getAll()[0].stopId)
        runBlocking {
            assertEquals("0001", roomDb.favoriteStopDao().getAll().first()[0].stopId)
        }
    }

    private fun getMigratedRoomDatabase(): BusDatabase {
        val database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BusDatabase::class.java,
            TEST_DB,
        )
            .addMigrations(BusDatabase.MIGRATION_8_9)
            .build()
        helper.closeWhenFinished(database)
        return database
    }
}

