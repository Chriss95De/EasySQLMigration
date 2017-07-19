package de.oster.easysqlmigration.migration.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * Created by Christian on 14.07.2017.
 */
public class HashGenerator
{
    public static String getHash(InputStream in) throws IOException {

        CRC32 crcMaker = new CRC32();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = in.read(buffer)) != -1) {
            crcMaker.update(buffer, 0, bytesRead);
        }
        long crc = crcMaker.getValue(); // This is your error checking code
        return String.valueOf(crc);
    }

    private static int convert(int n) {
        return (int)Long.parseLong(String.valueOf(n), 16);
    }
}
