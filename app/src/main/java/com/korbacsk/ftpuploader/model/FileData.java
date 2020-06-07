package com.korbacsk.ftpuploader.model;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.korbacsk.ftpuploader.config.FileState;

public class FileData {
    private String filename;
    private Uri uri;
    private String path;
    private FileState fileState;
    private @Nullable
    String error;

    public FileData(
            String filename,
            Uri uri,
            String path,
            FileState fileState,
            @Nullable String error
    ) {
        this.filename = filename;
        this.uri = uri;
        this.path = path;
        this.fileState = fileState;
        this.error = error;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileState getFileState() {
        return fileState;
    }

    public void setFileState(FileState fileState) {
        this.fileState = fileState;
    }

    @Nullable
    public String getError() {
        return error;
    }

    public void setError(@Nullable String error) {
        this.error = error;
    }


}