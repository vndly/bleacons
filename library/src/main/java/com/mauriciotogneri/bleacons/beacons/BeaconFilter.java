package com.mauriciotogneri.bleacons.beacons;

/**
 * Converts the package received from a beacon and returns an object of a specific beacon format.
 *
 * @param <BeaconType> the type of beacon that it returns
 */
public abstract class BeaconFilter<BeaconType extends Beacon>
{
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Helper method to convert from hexadecimal to byte.
     *
     * @param hex the hexadecimal value
     * @return the equivalent byte value
     */
    protected static byte toByte(String hex)
    {
        int result = Integer.parseInt(hex, 16);

        if (result > 127)
        {
            result -= 256;
        }

        return (byte) result;
    }

    /**
     * Helper method to convert from an array of bytes to an integer number.
     *
     * @param bytes the array of bytes
     * @return the equivalent integer number
     */
    protected int toInt(byte[] bytes)
    {
        return Integer.parseInt(toHex(bytes), 16);
    }

    /**
     * Helper method to convert from an array of bytes to a string.
     *
     * @param bytes the array of bytes
     * @return the equivalent String value
     */
    protected String toHex(byte[] bytes)
    {
        char[] chars = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++)
        {
            int value = bytes[i] & 0xFF;
            chars[i * 2] = BeaconFilter.HEX_CHARS[value >>> 4];
            chars[i * 2 + 1] = BeaconFilter.HEX_CHARS[value & 0x0F];
        }

        return new String(chars);
    }

    /**
     * Returns the UUID string formatted (i.e., separating the fields by a dash).
     *
     * @param uuid the input UUID
     * @return the formatted UUID
     */
    protected String getFormattedUuid(String uuid)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(uuid.substring(0, 8)); // 8 bytes
        builder.append("-");
        builder.append(uuid.substring(8, 12)); // 4 bytes
        builder.append("-");
        builder.append(uuid.substring(12, 16)); // 4 bytes
        builder.append("-");
        builder.append(uuid.substring(16, 20)); // 4 bytes
        builder.append("-");
        builder.append(uuid.substring(20, 32)); // 12 bytes

        return builder.toString();
    }

    /**
     * Parses the data array and constructs a specific beacon type if the input is correct. If the
     * data doesn't correspond with the type of beacon that the filter can detect then it should
     * return null.
     *
     * @param macAddress the beacon's mac address
     * @param data       the data contained in the received package
     * @return an object of BeaconType or null
     */
    public abstract BeaconType getBeacon(String macAddress, byte[] data);
}