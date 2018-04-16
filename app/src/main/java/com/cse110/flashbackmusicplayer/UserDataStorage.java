package com.cse110.flashbackmusicplayer;

import java.util.List;

public class UserDataStorage {
    private static List<String> profile_data;
    private static List<List<String>> connection_data;

    public static void setProfile(List<String> profile_from_task) { profile_data = profile_from_task;}
    public static void setContacts(List<List<String>> connections_from_task) { connection_data = connections_from_task;}
    public static List<String> getProfile() {return profile_data;}
    public static List<List<String>> getContacts() {return connection_data;}
    public static String getUID() {return profile_data.get(1);}
}
