package com.example.myapp.RoomService;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocation(String location);

    @Delete
    void deleteLocation(String location);

    @Update
    void setFavorite(String location, boolean favorite);

    @Query("SELECT * FROM locations WHERE id = :id")
    RoomLocation getLocationById(long id);

    @Query("SELECT * FROM locations WHERE favorite = true")
    List<RoomLocation> getFavoriteLocations();

    @Insert
    void insertRequest(String location, Date date);

    @Delete
    void deleteRequest(Date date);

    @Query("SELECT * FROM requests")
    List<RoomRequest> getRequests();
}
