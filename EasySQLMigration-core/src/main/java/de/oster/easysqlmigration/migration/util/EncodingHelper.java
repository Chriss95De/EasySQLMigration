package de.oster.easysqlmigration.migration.util;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Created by Christian on 18.12.2017.
 */
public class EncodingHelper {
    public static String guessEncoding(byte[] bytes) {
        String DEFAULT_ENCODING = "UTF-8";
       UniversalDetector detector =
                new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }
}
