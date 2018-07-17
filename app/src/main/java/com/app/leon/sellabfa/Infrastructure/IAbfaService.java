package com.app.leon.sellabfa.Infrastructure;


import com.app.leon.sellabfa.Models.InterCommunation.Location;
import com.app.leon.sellabfa.Models.InterCommunation.LocationUpdateModel;
import com.app.leon.sellabfa.Models.InterCommunation.LoginFeedBack;
import com.app.leon.sellabfa.Models.InterCommunation.LoginInfo;
import com.app.leon.sellabfa.Models.InterCommunation.OnLoadParams;
import com.app.leon.sellabfa.Models.InterCommunation.SimpleMessage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Leon on 12/9/2017.
 */
public interface IAbfaService {

    @POST("/Auth/Account/login")
    Call<LoginFeedBack> login(@Body LoginInfo logininfo);

    @PUT("/Api1/ToziGhabsManager/Add")
    Call<SimpleMessage> toziGhabs(@Body LocationUpdateModel locationUpdateModel);

    @PATCH("/Auth/Account/UpdateDeviceIdAnanymous")
    Call<SimpleMessage> signSerial(
            @Body LoginInfo logininfo);

    //
    @GET("/Api1/TraverseManager/Load")
    Call<ArrayList<OnLoadParams>> download(
            @Query("currentVersion") int currentVersion
    );

    @GET("/Api1/TraverseManager/Load")
    Call<ArrayList<OnLoadParams>> download();


    @PATCH("/Api1/TraverseManager/SetCounterPosition")
    Call<String> counterPosition(
            @Body Location location);

    @PATCH("/Api1/TraverseManager/SetCounterPositions")
    Call<List<String>> counterPositions(
            @Body List<Location> locations);
}

