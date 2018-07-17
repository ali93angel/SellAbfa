package com.app.leon.sellabfa.Models.DbTables;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.leon.sellabfa.Models.InterCommunation.OnLoadParams;
import com.orm.SugarRecord;

public class OnLoad extends SugarRecord implements Parcelable {
    public String idCustom;
    public String zone;
    public int trackNumber;
    public String billId;//
    public Integer radif;
    public String eshterak;//
    public String qeraatCode;//
    public String firstName;//
    public String sureName;//
    public String address;//
    public String pelak;
    public String karbariForoosh;//
    public String karbariMasraf;//
    public int ahadAsli;//
    public int ahadFari;//
    public int ahadMasraf;//
    public int ahadAbBaha;//
    public String qotr;
    public boolean hasFazelab;
    public Integer sifoonQotr;
    public String postalCode;
    public Integer preNumber;
    public String preDate;
    public Float preAverage;
    public String preCounterState;
    public String counterSerial;//
    public String counterInstallDate;
    public String fazelabInstallDate;
    public String specialWarning;//show dialog
    public String zarfiat;
    public String tavizCounterNumber;
    public String tavizDate;
    public int l1, l2, d1, d2;
    public Double latitude, longitude;
    public int offLoadStateId;

    public OnLoad() {
    }

    public OnLoad(OnLoadParams onLoad) {
        setOnLoad(onLoad);
    }
    private OnLoad(Parcel parcel) {
        eshterak = parcel.readString();
    }

    private void setOnLoad(OnLoadParams onLoad) {
        idCustom = onLoad.id;
        zone = onLoad.zone;
        trackNumber = onLoad.trackNumber;
        billId = onLoad.billId;
        radif = onLoad.radif;
        eshterak = onLoad.eshterak;
        qeraatCode = onLoad.qeraatCode;
        firstName = onLoad.firstName;
        sureName = onLoad.sureName;
        address = onLoad.address;
        pelak = onLoad.pelak;
        karbariForoosh = onLoad.karbariForoosh;
        ahadAsli = onLoad.ahadAsli;
        ahadFari = onLoad.ahadFari;
        ahadMasraf = onLoad.ahadMasraf;
        ahadAbBaha = onLoad.ahadAbBaha;
        qotr = onLoad.qotr;
        hasFazelab = onLoad.hasFazelab;
        sifoonQotr = onLoad.sifoonQotr;
        postalCode = onLoad.postalCode;
        preNumber = onLoad.preNumber;
        preDate = onLoad.preDate;
        preAverage = onLoad.preAverage;
        preCounterState = onLoad.preCounterState;
        counterSerial = onLoad.counterSerial;
        counterInstallDate = onLoad.counterInstallDate;
        fazelabInstallDate = onLoad.fazelabInstallDate;
        specialWarning = onLoad.specialWarning;
        zarfiat = onLoad.zarfiat;
        tavizCounterNumber = onLoad.tavizCounterNumber;
        tavizDate = onLoad.tavizDate;
        karbariMasraf = onLoad.karbariMasraf;
    }


    public String getSifoonQotrCustom() {
        if (sifoonQotr == null || sifoonQotr > 3)
            return "";
        String[] qotrList = {"", "100", "125", "200"};
        return qotrList[sifoonQotr];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eshterak);
        parcel.writeString(firstName);
        parcel.writeString(sureName);
        parcel.writeString(address);
    }

    public static final Parcelable.Creator<OnLoad> CREATOR = new Parcelable.Creator<OnLoad>() {
        public OnLoad createFromParcel(Parcel in) {
            return new OnLoad(in);
        }

        public OnLoad[] newArray(int size) {
            return new OnLoad[size];
        }
    };
}
