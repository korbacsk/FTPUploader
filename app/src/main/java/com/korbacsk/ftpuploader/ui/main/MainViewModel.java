package com.korbacsk.ftpuploader.ui.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.korbacsk.ftpuploader.R;
import com.korbacsk.ftpuploader.config.FileState;
import com.korbacsk.ftpuploader.helper.Debug;
import com.korbacsk.ftpuploader.model.FileData;
import com.korbacsk.ftpuploader.repository.FilesRepository;
import com.korbacsk.ftpuploader.service.FTPService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<FileData>> files = new MutableLiveData();
    private boolean isUploadInProgress;

    //Number of files an upload session
    private int needUploadFilesCountInSession = 0;
    private int processedFilesCountInSession = 0;


    public MainViewModel(Application application) {
        super(application);
        isUploadInProgress = false;
    }

    public boolean getIsUploadInProgress() {
        Debug.LogMessage("MainViewModel - getIsUploadInProgress, isUploadInProgress: " + String.valueOf(isUploadInProgress));
        return isUploadInProgress;
    }

    public void setIsUploadInProgress(boolean isUploadInProgress) {
        Debug.LogMessage("MainViewModel - setIsUploadInProgress, isUploadInProgress: " + String.valueOf(isUploadInProgress));
        this.isUploadInProgress = isUploadInProgress;
    }

    public int getNeedUploadFilesCountInSession() {
        return needUploadFilesCountInSession;
    }

    public void setNeedUploadFilesCountInSession(int needUploadFilesCountInSession) {
        this.needUploadFilesCountInSession = needUploadFilesCountInSession;
    }

    public int getProcessedFilesCountInSession() {
        return processedFilesCountInSession;
    }

    public void setProcessedFilesCountInSession(int processedFilesCountInSession) {
        this.processedFilesCountInSession = processedFilesCountInSession;
    }

    public MutableLiveData<List<FileData>> getFiles() {
        return FilesRepository.getInstance().getFiles();
    }

    public void setFiles(List<FileData> fileData, boolean isFromMainThread) {
        FilesRepository.getInstance().setFiles(fileData, isFromMainThread);
    }

    public int getNeedUploadCount() {
        return FilesRepository.getInstance().getNeedUploadCount();
    }

    public int getFilesCount() {
        return FilesRepository.getInstance().getFilesCount();
    }

    public void uploadFiles(
            String host,
            String user,
            String password,
            OnUploadSuccessListener onUploadSuccessListener,
            OnUploadErrorListener onUploadErrorListener) {
        String error = null;
        setIsUploadInProgress(true);
        setNeedUploadFilesCountInSession(getNeedUploadCount());
        setProcessedFilesCountInSession(0);

        if (getFiles().getValue() == null || getFiles().getValue().size() == 0) {
            setIsUploadInProgress(false);
            onUploadErrorListener.onUploadError(
                    getApplication().getApplicationContext().getString(R.string.dialog__error__file_not_selected_title),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__file_not_selected_message),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__file_not_selected_button_ok)
            );

            return;
        }

        if (host.trim().length() == 0) {
            setIsUploadInProgress(false);
            onUploadErrorListener.onUploadError(
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_title),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_message_host),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_button_ok)
            );

            return;
        }

        if (user.trim().length() == 0) {
            setIsUploadInProgress(false);
            onUploadErrorListener.onUploadError(
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_title),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_message_username),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_button_ok)
            );

            return;
        }

        if (password.trim().length() == 0) {
            setIsUploadInProgress(false);
            onUploadErrorListener.onUploadError(
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_title),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_message_password),
                    getApplication().getApplicationContext().getString(R.string.dialog__error__empty_field_button_ok)
            );

            return;
        }


        Thread ftpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FTPService ftpService = new FTPService(host, user, password);
                    String connectionError = ftpService.connect();

                    if (connectionError != null) {
                        Debug.LogMessage("MainViewModel - uploadFiles -> ftpThread, connectionError: " + connectionError);
                        setIsUploadInProgress(false);
                        onUploadErrorListener.onUploadError(
                                getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_title),
                                getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_message_connection_failed, connectionError),
                                getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_button_ok)
                        );

                        return;
                    }

                    ArrayList<FileData> files = (ArrayList<FileData>) getFiles().getValue();
                    FileData fileData;
                    File file;
                    String uploadError = null;

                    for (int i = 0; i < files.size(); i++) {
                        fileData = files.get(i);
                        if (fileData.getFileState() != FileState.UPLOADED) {
                            setProcessedFilesCountInSession((getProcessedFilesCountInSession() + 1));
                            fileData.setFileState(FileState.UPLOADING);
                            setFiles(files, false);
                            //Thread.sleep(3000);

                            file = new File(String.valueOf(fileData.getPath()));

                            uploadError = ftpService.uploadFile(file, "/");
                            if (uploadError != null) {
                                fileData.setError(uploadError);
                                fileData.setFileState(FileState.ERROR);
                                setFiles(files, false);

                                setIsUploadInProgress(false);

                                onUploadErrorListener.onUploadError(
                                        getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_title),
                                        getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_message_upload_failed, uploadError),
                                        getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_button_ok)
                                );

                                return;
                            } else {
                                fileData.setError(null);
                                fileData.setFileState(FileState.UPLOADED);
                                setFiles(files, false);
                            }
                        }


                    }

                    if (getNeedUploadCount() == 0) {
                        setIsUploadInProgress(false);
                        onUploadSuccessListener.onUploadSuccess();
                        return;
                    } else {
                        onUploadErrorListener.onUploadError(
                                getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_title),
                                getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_message_upload_failed, "-"),
                                getApplication().getApplicationContext().getString(R.string.dialog__error__ftp_button_ok)
                        );

                        return;
                    }


                } catch (Exception e) {

                }

            }
        });

        ftpThread.start();


    }
}
