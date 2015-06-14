package com.mauriciotogneri.bleacons.modes;

import android.os.Handler;

import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.beacons.BeaconFilter;
import com.mauriciotogneri.bleacons.kernel.BeaconReading;
import com.mauriciotogneri.bleacons.modes.ReadingModeWindow.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a window based reading mode. It keeps internally the subsequents readings of the same
 * beacon and process them before informing the listeners.
 */
public class ReadingModeWindow extends ReadingMode<Listener>
{
    private final int scanFrequency;
    private final Handler handler = new Handler();
    private final Object mapsLock = new Object();
    private final Map<String, Beacon> currentBeacons = new HashMap<>();
    private final Map<String, List<BeaconReading>> currentReadings = new HashMap<>();
    private final ReadingCalculator readingCalculator;

    public static final ReadingCalculator READING_CALCULATOR_FIRST = new ReadingCalculatorFirst();
    public static final ReadingCalculator READING_CALCULATOR_LAST = new ReadingCalculatorLast();
    public static final ReadingCalculator READING_CALCULATOR_AVERAGE = new ReadingCalculatorAverage();

    private ReadingModeWindow(List<Listener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity, int scanFrequency, ReadingCalculator readingCalculator)
    {
        super(beaconListeners, beaconFilters, maxCapacity);

        this.scanFrequency = scanFrequency;
        this.readingCalculator = readingCalculator;
    }

    protected void onStartScanning()
    {
        openWindow();
    }

    private void openWindow()
    {
        if (isScanning())
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    closeWindow();
                    openWindow();
                }
            }, scanFrequency);
        }
    }

    private void closeWindow()
    {
        if (isScanning())
        {
            List<BeaconReading> beaconReadings = new ArrayList<>();

            synchronized (mapsLock)
            {
                Collection<Beacon> beacons = currentBeacons.values();

                for (Beacon beacon : beacons)
                {
                    List<BeaconReading> readings = currentReadings.get(beacon.macAddress);
                    beaconReadings.add(readingCalculator.calculate(readings));
                }
            }

            List<Listener> beaconListeners = getBeaconListeners();

            for (Listener listener : beaconListeners)
            {
                listener.onReceive(beaconReadings);
            }
        }

        synchronized (mapsLock)
        {
            currentBeacons.clear();
            currentReadings.clear();
        }
    }

    @Override
    public void processReading(Beacon beacon, int rssi, long timestamp)
    {
        synchronized (mapsLock)
        {
            if (!currentBeacons.containsKey(beacon.macAddress))
            {
                currentBeacons.put(beacon.macAddress, beacon);

                List<BeaconReading> readings = new ArrayList<>();
                readings.add(new BeaconReading(beacon, rssi, timestamp));

                currentReadings.put(beacon.macAddress, readings);
            }
            else
            {
                Beacon storedBeacon = currentBeacons.get(beacon.macAddress);

                List<BeaconReading> readings = currentReadings.get(beacon.macAddress);
                readings.add(new BeaconReading(storedBeacon, rssi, timestamp));
            }
        }
    }

    /**
     * Builds a new instance of ReadingModeWindow.
     */
    public static class Builder extends BaseBuilder<Listener>
    {
        private final int scanFrequency;
        private final ReadingCalculator readingCalculator;

        /**
         * Constructs the builder.
         *
         * @param maxCapacity       the maximum capacity of cached beacons (zero for infinite capacity)
         * @param scanFrequency     the scan frequency (in milliseconds)
         * @param readingCalculator the reading calculator used to process the readings
         */
        public Builder(int maxCapacity, int scanFrequency, ReadingCalculator readingCalculator)
        {
            super(maxCapacity);

            this.scanFrequency = scanFrequency;
            this.readingCalculator = readingCalculator;
        }

        /**
         * Returns the reading mode instantiated.
         *
         * @return the reading mode instantiated
         */
        public ReadingModeWindow build()
        {
            return new ReadingModeWindow(beaconListeners, beaconFilters, maxCapacity, scanFrequency, readingCalculator);
        }
    }

    /**
     * Listens for new beacons readings.
     */
    public interface Listener
    {
        /**
         * Called when a window is closed.
         *
         * @param readings the list of beacon readings
         */
        void onReceive(List<BeaconReading> readings);
    }

    /**
     * Interface to implement for a specific reading calculator.
     */
    public interface ReadingCalculator
    {
        /**
         * Returns a BeaconReading from the given list.
         *
         * @param readings the list of beacon readings
         * @return the calculated BeaconReading
         */
        BeaconReading calculate(List<BeaconReading> readings);
    }

    private static class ReadingCalculatorFirst implements ReadingCalculator
    {
        @Override
        public BeaconReading calculate(List<BeaconReading> readings)
        {
            return readings.get(0);
        }
    }

    private static class ReadingCalculatorLast implements ReadingCalculator
    {
        @Override
        public BeaconReading calculate(List<BeaconReading> readings)
        {
            return readings.get(readings.size() - 1);
        }
    }

    private static class ReadingCalculatorAverage implements ReadingCalculator
    {
        @Override
        public BeaconReading calculate(List<BeaconReading> readings)
        {
            int sumRssi = 0;
            long sumTimestamp = 0;

            for (BeaconReading reading : readings)
            {
                sumRssi += reading.rssi;
                sumTimestamp += reading.timestamp;
            }

            int averageRssi = sumRssi / readings.size();
            long averageTimestamp = sumTimestamp / readings.size();

            return new BeaconReading(readings.get(0).beacon, averageRssi, averageTimestamp);
        }
    }
}