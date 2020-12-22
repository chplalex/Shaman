package com.chplalex.shaman.DBService

class ShamanDBService(private val shamanDao: ShamanDao) {

    val allRequests: List<RequestForAll>
        get() = shamanDao.allRequests
}