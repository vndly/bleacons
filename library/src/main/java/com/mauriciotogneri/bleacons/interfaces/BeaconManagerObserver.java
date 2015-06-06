package com.mauriciotogneri.bleacons.interfaces;

public interface BeaconManagerObserver
{
    void onConnected();

    void onDisconnected();
}