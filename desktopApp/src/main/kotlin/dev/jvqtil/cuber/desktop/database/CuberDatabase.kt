package dev.jvqtil.cuber.desktop.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

@Database(
    entities = [SolveEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CuberDatabase : RoomDatabase() {
    abstract fun solveDao(): SolveDao
}

object DesktopDatabase {
    fun create(): CuberDatabase {
        val directory = appDataDirectory()
        Files.createDirectories(directory)
        val databaseFile = directory.resolve("cuber.db")

        return Room.databaseBuilder<CuberDatabase>(
            name = databaseFile.toAbsolutePath().toString(),
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    private fun appDataDirectory(): Path {
        val os = System.getProperty("os.name")
            .lowercase(Locale.US)

        val home = Paths.get(
            System.getProperty("user.home")
        )

        return when {
            os.contains("win") -> Paths.get(
                System.getenv("APPDATA")
                    ?: home.resolve("AppData/Roaming").toString(),
                "Cuber Desktop",
            )

            os.contains("mac") -> home.resolve(
                "Library/Application Support/Cuber Desktop"
            )

            else -> Paths.get(
                System.getenv("XDG_DATA_HOME")
                    ?: home.resolve(".local/share").toString(),
                "Cuber Desktop",
            )
        }
    }
}
