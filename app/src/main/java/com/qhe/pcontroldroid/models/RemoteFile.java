package com.qhe.pcontroldroid.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class RemoteFile implements Comparable<RemoteFile> {
    private static final String
            JSON_ABSPATH = "abs_path",
            JSON_NAME = "name",
            JSON_TYPE = "type",
            JSON_SIZE = "size",
            JSON_ITEMS = "item";

    private String mAbsPath;
    private String mFileName;
    private String mFileType;
    private long mFileSize;
    private int mFileContainItems;

    public RemoteFile(String fileName) {
        mFileName = fileName;
    }

    public RemoteFile(String fileName, String fileType, long fileSize, int fileItems) {
        mFileName = fileName;
        mFileType = fileType;
        mFileSize = fileSize;
        mFileContainItems = fileItems;
    }

    public RemoteFile(File file) {
        mAbsPath = file.getAbsolutePath();
        mFileName = file.getName();
        mFileSize = file.length();
        if(file.isDirectory()) {
            mFileType = "folder";
            if(file.list() != null) {
                mFileContainItems = file.list().length;
            } else {
                mFileContainItems = 0;
            }
        } else {
            mFileType = "file";
            mFileContainItems = 0;
        }
    }

    public RemoteFile(JSONObject json) throws  JSONException {
        mAbsPath = json.getString(JSON_ABSPATH);
        mFileName = json.getString(JSON_NAME);
        mFileType = json.getString(JSON_TYPE);
        mFileSize = json.getLong(JSON_SIZE);
        mFileContainItems = json.getInt(JSON_ITEMS);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ABSPATH, mAbsPath);
        json.put(JSON_NAME, mFileName);
        json.put(JSON_TYPE, mFileType);
        json.put(JSON_SIZE, mFileSize);
        json.put(JSON_ITEMS, mFileContainItems);
        return json;
    }



    public String getAbsPath() {
        return mAbsPath;
    }

    public void setAbsPath(String absPath) {
        mAbsPath = absPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        mFileType = fileType;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    public int getFileContainItems() {
        return mFileContainItems;
    }

    public void setFileContainItems(int fileContainItems) {
        mFileContainItems = fileContainItems;
    }

    @Override
    public String toString() {
        return "File Name: " + mFileName + "\n"
                + "File Type: " + mFileType + "\n"
                + "File Size: " + mFileSize + "\n"
                + "File Items: " + mFileContainItems + "\n"
                + "File AbsPath: " + mAbsPath + "\n";
    }

    protected final static int
            FIRST = -1,
            SECOND = 1;

    @Override
    public int compareTo(RemoteFile another) {
        if(mFileType.equals("folder") || another.getFileType().equals("folder")) {
            if(mFileType.equals("folder") == another.getFileType().equals("folder"))
                return mFileName.compareToIgnoreCase(another.getFileName());
            else if(mFileType.equals("folder")) return FIRST;
            else return SECOND;
        }
        return mFileName.compareToIgnoreCase(another.getFileName());
    }


}	
