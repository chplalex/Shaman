package com.chplalex.shaman.service.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chplalex.shaman.mvp.model.db.Location
import com.chplalex.shaman.mvp.model.db.Request
import com.chplalex.shaman.mvp.model.db.RequestForAll
import com.chplalex.shaman.mvp.model.db.RequestForLocation
import io.reactivex.Completable
import io.reactivex.Single

@Dao interface ShamanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocation(location: Location): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLocation(location: Location): Completable

    @Query("UPDATE locations SET favorite = :favorite WHERE name = :name AND country = :country")
    fun updateLocationFavorite(name: String, country: String, favorite: Boolean): Completable

    @Delete
    fun deleteLocation(location: Location): Completable

    @Query("DELETE FROM locations WHERE id = :id")
    fun deleteLocationById(id: Long): Completable

    @get:Query("SELECT * FROM locations WHERE favorite = 1")
    val favoriteLocations: Single<List<Location>>

    @Query("SELECT * FROM locations WHERE name = :name AND country = :country")
    fun getLocations(name: String, country: String): Single<List<Location>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertRequest(request: Request): Completable

    @Delete
    fun deleteRequest(request: Request): Completable

    @Query("DELETE FROM requests WHERE time = :time")
    fun deleteRequestByTime(time: Long): Completable

    @get:Query("SELECT l.name, l.country, r.time, r.temperature, l.favorite FROM requests AS r JOIN locations AS l ON l.id = r.location_id ORDER BY r.time DESC")
    val getAllRequests: Single<List<RequestForAll>>

    @Query("SELECT time, temperature FROM requests WHERE location_id = :locationId")
    fun getLocationRequests(locationId: Long): Single<List<RequestForLocation>>
}