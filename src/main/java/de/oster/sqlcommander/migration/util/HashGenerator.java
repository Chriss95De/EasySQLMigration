package de.oster.sqlcommander.migration.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Christian on 14.07.2017.
 */
public class HashGenerator
{
    public static String getHash(File file) throws IOException {

        Long hashResult = 0l;

        String str = FileUtils.readFileToString(file, "UTF-8");
        int hash = 7;
        for (int i = 0; i < str.length(); i++) {
            hashResult += (hash * 31 + str.charAt(i));
        }

        return String.valueOf(hashResult);
    }

    private static int convert(int n) {
        return (int)Long.parseLong(String.valueOf(n), 16);
    }
}
