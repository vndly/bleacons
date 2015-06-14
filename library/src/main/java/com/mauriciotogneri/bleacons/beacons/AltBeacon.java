package com.mauriciotogneri.bleacons.beacons;

import java.util.Arrays;

/**
 * Represents an AltBeacon (http://altbeacon.org). The class includes a filter that can detect this
 * type of beacon.
 */
public class AltBeacon extends Beacon
{
    /**
     * The beacon's UUID.
     */
    public final String uuid;

    /**
     * The first beacon's identifier.
     */
    public final int identifier1;

    /**
     * The seconds beacon's identifier.
     */
    public final int identifier2;

    /**
     * The beacons' reference RSSI (in db).
     */
    public final int referenceRSSI;

    /**
     * Constructs an AltBeacon.
     *
     * @param macAddress    the MAC address
     * @param uuid          the UUID
     * @param identifier1   the first identifier
     * @param identifier2   the second identifier
     * @param referenceRSSI the reference RSSI (in db)
     */
    public AltBeacon(String macAddress, String uuid, int identifier1, int identifier2, int referenceRSSI)
    {
        super(macAddress);

        this.uuid = uuid;
        this.identifier1 = identifier1;
        this.identifier2 = identifier2;
        this.referenceRSSI = referenceRSSI;
    }

    /**
     * A filter capable to detect AltBeacon packages type.
     */
    public static class Filter extends BeaconFilter<AltBeacon>
    {
        private final String filterUUID;
        private final Integer filterIdentifier1;
        private final Integer filterIdentifier2;

        private static final byte HEADER_0 = BeaconFilter.toByte("1B");
        private static final byte HEADER_1 = BeaconFilter.toByte("FF");
        private static final byte HEADER_4 = BeaconFilter.toByte("BE");
        private static final byte HEADER_5 = BeaconFilter.toByte("AC");

        /**
         * Filters the beacons according the the given parameters.
         *
         * @param filterUUID        the UUID to filter (null to accept all)
         * @param filterIdentifier1 the first identifier to filter (null to accept all)
         * @param filterIdentifier2 the second identifier to filter (null to accept all)
         */
        public Filter(String filterUUID, Integer filterIdentifier1, Integer filterIdentifier2)
        {
            this.filterUUID = filterUUID;
            this.filterIdentifier1 = filterIdentifier1;
            this.filterIdentifier2 = filterIdentifier2;
        }

        /**
         * Filters all the AlBeacons.
         */
        public Filter()
        {
            this(null, null, null);
        }

        @Override
        public AltBeacon getBeacon(String macAddress, byte[] data)
        {
            if (isValidHeader(data))
            {
                String uuid = getUUID(data);
                int identifier1 = getIdentifier1(data);
                int identifier2 = getIdentifier2(data);
                int referenceRSSI = getReferenceRSSI(data);

                if ((filterUUID != null) && (!filterUUID.equals(uuid)))
                {
                    return null;
                }

                if ((filterIdentifier1 != null) && (filterIdentifier1 != identifier1))
                {
                    return null;
                }

                if ((filterIdentifier2 != null) && (filterIdentifier2 != referenceRSSI))
                {
                    return null;
                }

                return new AltBeacon(macAddress, uuid, identifier1, identifier2, referenceRSSI);
            }

            return null;
        }

        private boolean isValidHeader(byte[] data)
        {
            return ((data.length >= 28) && //
                    (data[0] == Filter.HEADER_0) && // 0x1B
                    (data[1] == Filter.HEADER_1) && // 0xFF
                    // (data[2] == ?) && // ??
                    // (data[3] == ?) && // ??
                    (data[4] == Filter.HEADER_4) && // 0xBE
                    (data[5] == Filter.HEADER_5)); // 0xAC
        }

        private String getUUID(byte[] data)
        {
            String uuid = toHex(Arrays.copyOfRange(data, 6, 22)); // 6-21

            return getFormattedUuid(uuid);
        }

        private int getIdentifier1(byte[] data)
        {
            byte[] array = {data[22], data[23]};

            return toInt(array);
        }

        private int getIdentifier2(byte[] data)
        {
            byte[] array = {data[24], data[25]};

            return toInt(array);
        }

        private int getReferenceRSSI(byte[] data)
        {
            return data[26] - 256;
        }
    }
}