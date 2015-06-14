package com.mauriciotogneri.bleacons.beacons;

import java.util.Arrays;

/**
 * Represents an iBeacon (https://developer.apple.com/ibeacon). The class includes a filter that can
 * detect this type of beacon.
 */
public class IBeacon extends Beacon
{
    /**
     * The beacon's UUID.
     */
    public final String uuid;

    /**
     * The beacon's major value.
     */
    public final int major;

    /**
     * The beacon's minor value.
     */
    public final int minor;

    /**
     * The beacon's transmission power (in db).
     */
    public final int txPower;

    /**
     * Constructs an IBeacon.
     *
     * @param macAddress the MAC address
     * @param uuid       the UUID
     * @param major      the major value
     * @param minor      the minor value
     * @param txPower    the transmission power (in db)
     */
    public IBeacon(String macAddress, String uuid, int major, int minor, int txPower)
    {
        super(macAddress);

        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.txPower = txPower;
    }

    /**
     * A filter capable to detect AltBeacon packages type.
     */
    public static class Filter extends BeaconFilter<IBeacon>
    {
        private final String filterUUID;
        private final Integer filterMajor;
        private final Integer filterMinor;

        private static final byte HEADER_0 = BeaconFilter.toByte("02");
        private static final byte HEADER_1 = BeaconFilter.toByte("01");
        private static final byte HEADER_2 = BeaconFilter.toByte("06");
        private static final byte HEADER_3 = BeaconFilter.toByte("1A");
        private static final byte HEADER_4 = BeaconFilter.toByte("FF");
        private static final byte HEADER_5 = BeaconFilter.toByte("4C");
        private static final byte HEADER_6 = BeaconFilter.toByte("00");
        private static final byte HEADER_7 = BeaconFilter.toByte("02");
        private static final byte HEADER_8 = BeaconFilter.toByte("15");

        /**
         * Filters the beacons according the the given parameters.
         *
         * @param filterUUID  the UUID to filter (null to accept all)
         * @param filterMajor the major to filter (null to accept all)
         * @param filterMinor the minor to filter (null to accept all)
         */
        public Filter(String filterUUID, Integer filterMajor, Integer filterMinor)
        {
            this.filterUUID = filterUUID;
            this.filterMajor = filterMajor;
            this.filterMinor = filterMinor;
        }

        /**
         * Filters all the iBeacons.
         */
        public Filter()
        {
            this(null, null, null);
        }

        @Override
        public IBeacon getBeacon(String macAddress, byte[] data)
        {
            if (isValidHeader(data))
            {
                String uuid = getUUID(data);
                int major = getMajor(data);
                int minor = getMinor(data);
                int txPower = getTxPower(data);

                if ((filterUUID != null) && (!filterUUID.equals(uuid)))
                {
                    return null;
                }

                if ((filterMajor != null) && (filterMajor != major))
                {
                    return null;
                }

                if ((filterMinor != null) && (filterMinor != minor))
                {
                    return null;
                }

                return new IBeacon(macAddress, uuid, major, minor, txPower);
            }

            return null;
        }

        private boolean isValidHeader(byte[] data)
        {
            return ((data.length >= 30) && //
                    (data[0] == Filter.HEADER_0) && // 0x02
                    (data[1] == Filter.HEADER_1) && // 0x01
                    (data[2] == Filter.HEADER_2) && // 0x06
                    (data[3] == Filter.HEADER_3) && // 0x1A
                    (data[4] == Filter.HEADER_4) && // 0xFF
                    (data[5] == Filter.HEADER_5) && // 0x4C
                    (data[6] == Filter.HEADER_6) && // 0x00
                    (data[7] == Filter.HEADER_7) && // 0x02
                    (data[8] == Filter.HEADER_8)); // 0x15
        }

        private String getUUID(byte[] data)
        {
            String uuid = toHex(Arrays.copyOfRange(data, 9, 25)); // 9-24

            return getFormattedUuid(uuid);
        }

        private int getMajor(byte[] data)
        {
            byte[] array = {data[25], data[26]};

            return toInt(array);
        }

        private int getMinor(byte[] data)
        {
            byte[] array = {data[27], data[28]};

            return toInt(array);
        }

        private int getTxPower(byte[] data)
        {
            return data[29];
        }
    }
}