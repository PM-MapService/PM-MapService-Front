package com.campstone.welcome5jo.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ParkingAreaContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<ParkingAreaItem> ITEMS = new ArrayList<ParkingAreaItem>();
//
//    /**
//     * A map of sample (placeholder) items, by ID.
//     */
    public static final Map<Integer, ParkingAreaItem> ITEM_MAP = new HashMap<Integer, ParkingAreaItem>();

    private static void addItem(ParkingAreaItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
//
//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createPlaceholderItem(i));
//        }
//    }
//    private static PlaceholderItem createPlaceholderItem(int position) {
//        return new PlaceholderItem(String.valueOf(position), "Item " + position, makeDetails(position), lat, lon);
//    }
//
//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
//    }




    /**
     * A placeholder item representing a piece of content.
     */
    public static class ParkingAreaItem {
        public final int id;
        public final String name;
        public final double lat;
        public final double lon;
        public double distance=0;

        public ParkingAreaItem(int id, String name, double lat, double lon,double curlat,double curlon) {
            this.id = id;
            this.name = name;
            this.lat = lat;
            this.lon = lon;
            this.distance=calDistance(curlat,curlon);
        }

        public double calDistance(double curlat, double curlon) {
            double theta = curlon - lon;
            double dist = Math.sin(deg2rad(curlat)) * Math.sin(deg2rad(lat)) + Math.cos(deg2rad(curlat)) * Math.cos(deg2rad(lat)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1609.344;
            return dist;
        }
        public double setDistance(double curlat, double curlon) {
            double theta = curlon - lon;
            double dist = Math.sin(deg2rad(curlat)) * Math.sin(deg2rad(lat)) + Math.cos(deg2rad(curlat)) * Math.cos(deg2rad(lat)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1609.344;
            this.distance= dist;
            return dist;
        }
        private static double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }
        private static double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
        }
    }
}