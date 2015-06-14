# Bleacons

Bleacons is an **Android** library that provides a simple mechanism to listen for Bluetooth Low Energy 4.0 (BLE) devices (often called _beacons_).

## Features

* Listen for beacons in different modes:
   * Continuous
   * Window based
   * Custom defined mode

* Filter beacons by type:
   * iBeacon
   * AltBeacon
   * Custom defined type

## Main components

### Beacons:

A beacon is a transmitter that uses Bluetooth Low Energy 4.0 to broadcast signals that can be heard by compatible devices. In the library, a beacon is represented by the class `Beacon`. Each beacon can be configured to transmit a different package and applications must be able to understand that information. The library provides two built-in beacon types: `IBeacon` and `AltBeacon`. Each class contains the all logic necessary to read the information transmitted by those types of beacon. They achieve that by implementing a specific `BeaconFilter`. A filter is a class that receives a beacon's MAC address and the package data (as a `byte[]`) and returns a specific beacon. You can play with those classes in order to create your own types of beacon.

### Manager:

In order to handle the way that your application will receive the beacons' signals, you must interact with the class `BeaconManager`. Internally, this class will connect to an Android service where the actual reading will happen, although the user has no direct access to this service. Instead, the class `BeaconManager` offers the following API to handle the readings:

* `connect`/`disconnect`: Connects and disconnects from the internal service. These operations don't start/stop the actual reading, they only tell the manager to bind/unbind to the service.

* `start`/`pause`/`stop`: This methods change the state of the reading process. In order for these methods to make an effect, the manager has to be previously bound to the service.

* `setMode`: It tells the manager what reading mode to use to process the received signals from the beacons.

Note: the `BeaconManager` will not turn on/off the Bluetooth on the device. It's responsibility of the application to do that.

### Reading Mode:

Every time the manager receives a package it redirects it to the currently active reading mode (if any). A reading mode is a class that implements `ReadingMode`. As a result, this class will generate a beacon reading (or a list of beacon readings) and sends it to the main application. The library provides two built-in reading modes:

* `ReadingModeContinuous`: This reading mode will inform immediately the application every time that it receives a package.

* `ReadingModeWindow`: This reading mode is window based. It receives as a parameter the scan frequency (in milliseconds) that will be used to calculate the windows. When a window is finished, this class will compute the final reading that will apply for each beacon detected in that window. A class that computes the final reading must implement the interface `ReadingCalculator`. The library provides three built-in calculators:

    * `ReadingCalculatorFirst`: It returns the first reading as a result.

    * `ReadingCalculatorLast`: It returns the last reading as a result.

    * `ReadingCalculatorAverage`: It returns an average of all readings as a result.

### Beacon Reading:

A `BeaconReading` is the result provided by a reading mode instance. It contains the beacon from where the signal has been received, the RSSI value of the reading (in db) and the timestamp when the reading took place.

## Example

The project provides a sample module to help understand how to use the library. Here is a more simplified example that implements an activity that uses a window based reading mode to scan for beacons:

```java
 public class Example extends Activity implements ReadingModeWindow.Listener, BeaconManagerObserver
 {
     // keep the reference to the beacon manager
     private BeaconManager beaconManager;

     @Override
     protected void onCreate(Bundle savedInstanceState)
     {
         // activity initialization...

         try
         {
             // instantiate the beacon manager and connects to the internal service
             beaconManager = new BeaconManager(this, this);
             beaconManager.connect();
         }
         catch (UnsupportedBluetoothLeException e)
         {
             // handle the exception
         }
     }


     @Override
     public void onConnected()
     {
         // parameters of the reading mode
         int maxCachedBeacons = 100;
         int scanFrequency = 1000; // in milliseconds
         ReadingCalculator calculator = ReadingModeWindow.READING_CALCULATOR_AVERAGE;

         // create a windowed reading mode builder
         ReadingModeWindow.Builder builder = new ReadingModeWindow.Builder(maxCachedBeacons, scanFrequency, calculator);

         // let the reading mode inform this class of beacon readings
         builder.addListeners(this);

         // let the reading mode filter only iBeacons signals
         builder.addFilters(new IBeacon.Filter());

         // pass the built reading mode to the manager and start reading
         beaconManager.setMode(builder.build());
         beaconManager.start();
     }

     @Override
     public void onDisconnected()
     {
         // the manager has disconnected from the internal service
     }

     @Override
     public void onReceive(List<BeaconReading> beaconReadings)
     {
         // process the list of readings
     }
 }
```

In order to use the library, your application must include in the manifest:

* The service:

```xml
 <service
     android:name="com.mauriciotogneri.bleacons.kernel.BeaconService"
     android:enabled="true" />
```

* The following permissions:

```xml
 <uses-permission android:name="android.permission.BLUETOOTH" />
 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

## Download

### Latest JAR:

[Download](https://github.com/mauriciotogneri/bleacons/releases/download/v1.0.0/bleacons.jar)

## License

	The MIT License (MIT)

	Copyright (c) 2015 Mauricio Togneri

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.