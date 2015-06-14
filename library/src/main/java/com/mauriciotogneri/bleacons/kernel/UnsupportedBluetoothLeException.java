package com.mauriciotogneri.bleacons.kernel;

/**
 * Signals that the current device doesn't support Bluetooth Low Energy.
 */
public class UnsupportedBluetoothLeException extends Exception
{
    private static final long serialVersionUID = -2865765651529657978L;

    /**
     * Constructs the exception.
     *
     * @param currentVersion the device's Android version
     */
    public UnsupportedBluetoothLeException(int currentVersion)
    {
        super("Minimum version required: 18. Current version: " + currentVersion);
    }
}