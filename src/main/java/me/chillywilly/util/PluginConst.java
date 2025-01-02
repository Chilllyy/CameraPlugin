package me.chillywilly.util;

import java.io.File;

import me.chillywilly.CameraPlugin;

public class PluginConst {
    public class Network {
        //Outgoing
        public static final String SCREENSHOT_PACKET_ID = "camera:screenshot";
        public static final String CHECK_FOR_COMPANION_ID = "camera:companion_syn";

        //Incoming
        public static final String COMPANION_FOUND_ID = "camera:companion_ack";
        public static final String SCREENSHOT_TAKEN_ID = "camera:uploaded";
    }

    public class Storage {
        public static final File data_folder = new File(CameraPlugin.plugin.getDataFolder(), "data");
        public static final File images_folder = new File(data_folder, "images");
        public static final File shoots_folder = new File(data_folder, "shoots");
        public static final File web_folder = new File(data_folder, "web");

        public static void init() {
            data_folder.mkdirs();
            images_folder.mkdirs();
            shoots_folder.mkdirs();
            web_folder.mkdirs();
        }
    }
}