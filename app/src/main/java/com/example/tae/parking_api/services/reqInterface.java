package com.example.tae.parking_api.services;

import com.example.tae.parking_api.model.Parking_Model;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by TAE on 06/10/2017.
 */

public interface reqInterface {

    @GET(ApiConstants.BASE_URL)
    Observable<List<Parking_Model>> getParkingList();

    @GET("http://ridecellparking.herokuapp.com/api/v1/parkinglocations/search?lat= 37.773&lng=-122.431")
    Observable<List<Parking_Model>> getLocalParking();

}
