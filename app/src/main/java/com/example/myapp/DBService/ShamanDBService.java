package com.example.myapp.DBService;

import java.util.Date;
import java.util.List;

public class ShamanDBService {

    private final ShamanDao shamanDao;

    public ShamanDBService(ShamanDao shamanDao) {
        this.shamanDao = shamanDao;
    }

    public List<RequestForAll> getAllRequests() {
        return shamanDao.getAllRequests();
    }

}
