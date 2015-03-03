package com.qhe.pcontroldroid.utils;

import com.qhe.pcontroldroid.models.RemoteFile;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;


public class FileUtils {
	
	public final static int
		KILOBYTE = 1024,
		MEGABYTE = KILOBYTE * 1024,
		GIGABYTE = MEGABYTE *1024,
		MAX_BYTE_SIZE = KILOBYTE / 2,
		MAX_KILOBYTE_SIZE = MEGABYTE / 2,
		MAX_MEGABYTE_SIZE = GIGABYTE / 2;
	
	public static String formatFileSize(long size) {
		if(size < MAX_BYTE_SIZE)
			return String.format(Locale.ENGLISH, "%d Bytes", size);
		else if(size < MAX_KILOBYTE_SIZE)
			return String.format(Locale.ENGLISH, "%.2f KB", (float)size / KILOBYTE);
		else if(size < MAX_MEGABYTE_SIZE)
			return String.format(Locale.ENGLISH, "%.2f MB", (float)size / MEGABYTE);
		else
			return String.format(Locale.ENGLISH, "%.2f GB", (float)size / GIGABYTE);
	}
	
	public static String formatFileSize(File file) {
		return formatFileSize(file.length());
	}

    /**
     * Compares files by name, where directories come always first
     */

    public static class FileNameComparator implements Comparator<RemoteFile> {
        protected final static int
            FIRST = -1,
            SECOND = 1;

        @Override
        public int compare(RemoteFile lhs, RemoteFile rhs) {
            if(lhs.getFileType().equals("folder") || rhs.getFileType().equals("folder")) {
                if(lhs.getFileType().equals("folder") == rhs.getFileType().equals("folder"))
                    return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
                else if(lhs.getFileType().equals("folder")) return FIRST;
                else return SECOND;
            }

            return 0;
        }
    }
	
}
