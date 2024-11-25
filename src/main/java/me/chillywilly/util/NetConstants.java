package me.chillywilly.util;

public class NetConstants {
    //Outgoing
    public static final String SCREENSHOT_PACKET_ID = "camera:screenshot";
    public static final String CHECK_FOR_COMPANION_ID = "camera:companion_syn";

    //Incoming
    public static final String COMPANION_FOUND_ID = "camera:companion_ack";
    public static final String SCREENSHOT_TAKEN_ID = "camera:uploaded";
}
