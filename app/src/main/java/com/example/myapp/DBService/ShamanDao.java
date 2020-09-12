package com.example.myapp.DBService;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShamanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocation(Location location);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

    @Query("DELETE FROM locations WHERE id = :id")
    void deleteLocationById(long id);

    @Query("SELECT * FROM locations WHERE favorite = 1")
    List<Location> getFavoriteLocations();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertRequest(Request request);

    @Delete
    void deleteRequest(Request request);

    @Query("DELETE FROM requests WHERE time = :time")
    void deleteRequestByTime(long time);

    @Query("SELECT l.name, l.country, r.time, r.temperature, l.favorite FROM requests AS r JOIN locations AS l ON l.id = r.location_id")
    List<RequestForAll> getAllRequests();

    @Query("SELECT time, temperature FROM requests WHERE location_id = :locationId")
    List<RequestForLocation> getLocationRequests(long locationId);
}
