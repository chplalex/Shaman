package com.chplalex.shaman.DBService

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao interface ShamanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocation(location: Location)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLocation(location: Location)

    @Query("UPDATE locations SET favorite = :favorite WHERE name = :name AND country = :country")
    fun updateLocationFavoriteByNameAndCountry(name: String, country: String, favorite: Boolean)

    @Delete
    fun deleteLocation(location: Location)

    @Query("DELETE FROM locations WHERE id = :id")
    fun deleteLocationById(id: Long)

    @get:Query("SELECT * FROM locations WHERE favorite = 1")
    val favoriteLocations: MutableList<Location>

    @Query("SELECT * FROM locations WHERE name = :name AND country = :country")
    fun getLocationByNameAndCountry(name: String, country: String): List<Location>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertRequest(request: Request)

    @Delete
    fun deleteRequest(request: Request)

    @Query("DELETE FROM requests WHERE time = :time")
    fun deleteRequestByTime(time: Long)

    @get:Query("SELECT l.name, l.country, r.time, r.temperature, l.favorite FROM requests AS r JOIN locations AS l ON l.id = r.location_id ORDER BY r.time DESC")
    val allRequests: MutableList<RequestForAll>

    @Query("SELECT time, temperature FROM requests WHERE location_id = :locationId")
    fun getLocationRequests(locationId: Long): List<RequestForLocation>
}