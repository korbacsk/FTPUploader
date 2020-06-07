package com.korbacsk.ftpuploader.repository;

import androidx.lifecycle.MutableLiveData;
import com.korbacsk.ftpuploader.config.FileState;
import com.korbacsk.ftpuploader.helper.Debug;
import com.korbacsk.ftpuploader.model.FileData;
import java.util.List;

public class FilesRepository {
    private static volatile FilesRepository instance;

    private MutableLiveData<List<FileData>> files = new MutableLiveData();

    private FilesRepository() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static FilesRepository getInstance() {
        Debug.LogMessage("FilesRepository - getInstance");
        if (instance == null) {

            synchronized (FilesRepository.class) {
                if (instance == null) instance = new FilesRepository();
            }
        }

        return instance;
    }

    public MutableLiveData<List<FileData>> getFiles() {
        Debug.LogMessage("FilesRepository - getFiles");
        if (files == null) {
            this.files = new MutableLiveData<List<FileData>>();
        }

        return files;
    }


    public void setFiles(List<FileData> fileData, boolean isFromMainThread) {
        Debug.LogMessage("FilesRepository - setFiles");
        if (this.files == null) {
            this.files = new MutableLiveData<List<FileData>>();
        }
        this.resetFiles(isFromMainThread);

        if (isFromMainThread) {
            this.files.setValue(fileData);
        } else {
            this.files.postValue(fileData);
        }
    }

    public void resetFiles(boolean isFromMainThread) {
        Debug.LogMessage("FilesRepository - resetFiles");
        if (isFromMainThread) {
            this.files.setValue(null);
        } else {
            this.files.postValue(null);
        }

    }

    public int getNeedUploadCount() {
        Debug.LogMessage("FilesRepository - getNeedUploadCount");
        int needUploadCount = 0;
        MutableLiveData<List<FileData>> fileList = getFiles();
        if (fileList.getValue() != null) {
            for (FileData file : fileList.getValue()) {
                if (file.getFileState() != FileState.UPLOADED) {
                    needUploadCount++;
                }
            }
        }

        return needUploadCount;
    }

    public int getFilesCount() {
        Debug.LogMessage("FilesRepository - getFilesCount");
        MutableLiveData<List<FileData>> fileList = getFiles();


        return fileList.getValue() == null ? 0 : fileList.getValue().size();
    }
}
