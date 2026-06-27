package it.mycraft.powerlib.common.utils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Small helpers for fetching JSON from a URL.
 */
public class JSONUtils {

    /**
     * Reads and parses the JSON object at the given URL.
     *
     * @param url the URL to read from
     * @return the parsed JSON object, or {@code null} if it could not be read
     */
    public static JsonObject getJSON(String url) {
        try {
            InputStream is = new URL(url).openStream();
            JsonReader jr = null;
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                jr = Json.createReader(rd);
                return jr.readObject();
            } finally {
                if(jr != null)
                    jr.close();
                is.close();
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * @param url the URL to read from
     * @return {@code true} if the URL could be read without error
     */
    public static boolean isValidJSON(String url) {
        try {
            try (InputStream is = new URL(url).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                read(rd);
                return true;
            }
        } catch (IOException ignored) {
            return false;
        }
    }

    private static String read(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}

