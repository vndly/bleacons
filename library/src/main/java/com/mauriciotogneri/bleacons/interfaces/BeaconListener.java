package com.mauriciotogneri.bleacons.interfaces;

import com.mauriciotogneri.bleacons.Reading;

import java.util.List;

public interface BeaconListener
{
    void onReceive(List<Reading> readings);
}