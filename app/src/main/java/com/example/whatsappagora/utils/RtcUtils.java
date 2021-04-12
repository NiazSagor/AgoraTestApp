package com.example.whatsappagora.utils;

public class RtcUtils {
    public static String channelName(String myUid, String peerUid) {
        return myUid + peerUid;
    }
}