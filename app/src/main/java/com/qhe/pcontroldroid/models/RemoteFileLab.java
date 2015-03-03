package com.qhe.pcontroldroid.models;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by sunshine on 15-3-3.
 */
public class RemoteFileLab {
    private ArrayList<RemoteFile> mFiles = new ArrayList<RemoteFile>();

    public RemoteFileLab() {
        for(int i=0; i<50; i++) {
            RemoteFile file;
            if(i % 2 == 0) {
                 file = new RemoteFile("文件", "file", 339292, 0);
            } else {
                 file = new RemoteFile("文件夹", "folder", 4096, 9);
            }
            mFiles.add(file);
        }
    }

    public RemoteFileLab(JSONArray array) {
        RemoteFile file;
        try {
            for (int i = 0; i < array.length(); i++) {
                file = new RemoteFile(array.getJSONObject(i));
                mFiles.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RemoteFile> getFiles() {
        return  mFiles;
    }
}
