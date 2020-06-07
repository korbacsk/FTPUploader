package com.korbacsk.ftpuploader.ui.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.korbacsk.ftpuploader.R;
import com.korbacsk.ftpuploader.adapter.FilesAdapter;
import com.korbacsk.ftpuploader.common.UICommon;
import com.korbacsk.ftpuploader.config.FileState;
import com.korbacsk.ftpuploader.customview.FilesMarginDecoration;
import com.korbacsk.ftpuploader.helper.Debug;
import com.korbacsk.ftpuploader.helper.FileHelper;
import com.korbacsk.ftpuploader.model.FileData;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainFragment extends Fragment implements FilesAdapter.OnItemClickListener {
    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int PICKFILE_RESULT_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;

    private MainViewModel viewModel;

    private FilesAdapter filesAdapter;
    private Observer<List<FileData>> filesObserver;

    private LinearLayoutManager linearLayoutManager;

    @BindView(R.id.editTextHost)
    EditText editTextHost;

    @BindView(R.id.editTextUser)
    EditText editTextUser;

    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

    @BindView(R.id.recyclerViewFiles)
    RecyclerView recyclerViewFiles;

    @BindView(R.id.buttonChooseFiles)
    Button buttonChooseFiles;

    @BindView(R.id.linearLayoutUploadButtonOrStatusInfo)
    LinearLayout linearLayoutUploadButtonOrStatusInfo;

    @BindView(R.id.buttonUpload)
    Button buttonUpload;

    @BindView(R.id.textViewUploadInfo)
    TextView textViewUploadInfo;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        ButterKnife.bind(this, rootView);

        initLayout();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new
                ViewModelProvider(getActivity()).get(MainViewModel.class);

        filesObserver = new Observer<List<FileData>>() {
            @Override
            public void onChanged(@Nullable final List<FileData> fileData) {
                Debug.LogMessage("MainFragment - onActivityCreated, filesObserver -> onChanged");
                filesAdapter.setData(fileData);
                showUploadButtonOrStatusInfo();

            }
        };

        viewModel.getFiles().observe(getViewLifecycleOwner(), filesObserver);
        showUploadButtonOrStatusInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.LogMessage("MainFragment - onActivityResult, requestCode: " + requestCode);
        switch (requestCode) {

            case PICKFILE_RESULT_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    List<FileData> files = new ArrayList<FileData>();
                    FileData fileData;

                    if (data.getClipData() != null) {
                        Debug.LogMessage("MainFragment - onActivityResult, PICKFILE_RESULT_CODE -> getClipData");
                        int count = data.getClipData().getItemCount();
                        int currentItem = 0;


                        while (currentItem < count) {
                            Uri fileUri = data.getClipData().getItemAt(currentItem).getUri();

                            String filePath = FileHelper.getRealPathFromURI_API19(getContext(), fileUri);

                            String[] filePathSegments = filePath.split("/");
                            String filename=filePathSegments[(filePathSegments.length-1)];

                            fileData = new FileData(filename, fileUri, filePath, FileState.NEED_UPLOAD, null);
                            files.add(fileData);

                            currentItem = currentItem + 1;
                        }


                    } else if (data.getData() != null) {

                        Uri fileUri = data.getData();

                        String filePath = FileHelper.getRealPathFromURI_API19(getContext(), fileUri);

                        String[] filePathSegments = filePath.split("/");
                        String filename=filePathSegments[(filePathSegments.length-1)];

                        fileData = new FileData(filename, fileUri, filePath, FileState.NEED_UPLOAD, null);
                        files.add(fileData);

                    }

                    viewModel.setFiles(files, true);
                }


                break;

        }

    }


    private void initLayout() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewFiles.setLayoutManager(linearLayoutManager);
        recyclerViewFiles.addItemDecoration(new FilesMarginDecoration(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewFiles.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getContext().getDrawable(R.drawable.files_list_divider));
        recyclerViewFiles.addItemDecoration(dividerItemDecoration);

        filesAdapter = new FilesAdapter(getActivity(), this);


        recyclerViewFiles.setHasFixedSize(true);
        recyclerViewFiles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFiles.setAdapter(filesAdapter);


        buttonChooseFiles.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (viewModel.getIsUploadInProgress()) {
                    return;
                }
                if (!EasyPermissions.hasPermissions(getActivity(), perms)) {
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.permission__read_external_storage), EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE, perms);
                }

                if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                    Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                    chooseFile.setType("application/x-bittorrent");

                    chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    chooseFile.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
                    chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

                    chooseFile = Intent.createChooser(chooseFile, getString(R.string.fragment_main__file_chooser_title));

                    startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
                }

            }

        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.uploadFiles(
                        editTextHost.getText().toString(),
                        editTextUser.getText().toString(),
                        editTextPassword.getText().toString(),
                        new OnUploadSuccessListener() {
                            @Override
                            public void onUploadSuccess() {
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        showUploadButtonOrStatusInfo();
                                        UICommon.showAlertDialog(
                                                getActivity(),
                                                getString(R.string.dialog__success_upload_title),
                                                getString(R.string.dialog__success_upload_message),
                                                getString(R.string.dialog__success_upload_button_ok));
                                    }
                                });


                            }
                        },
                        new OnUploadErrorListener() {
                            @Override
                            public void onUploadError(String title, String message, String buttonLabel) {

                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        showUploadButtonOrStatusInfo();
                                        UICommon.showAlertDialog(
                                                getActivity(),
                                                title,
                                                message,
                                                buttonLabel);

                                    }
                                });

                            }
                        });


            }
        });


    }

    private void showUploadButtonOrStatusInfo() {
        buttonUpload.setVisibility(View.GONE);
        textViewUploadInfo.setVisibility(View.GONE);

        if (viewModel.getIsUploadInProgress()) {
            int needUploadCount = viewModel.getNeedUploadFilesCountInSession();
            int processed = viewModel.getProcessedFilesCountInSession();

            String text = getString(R.string.fragment_main__upload_in_progress, String.valueOf(processed), String.valueOf(needUploadCount));
            textViewUploadInfo.setText(text);
            textViewUploadInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            textViewUploadInfo.setVisibility(View.VISIBLE);

        } else {
            int filesCount = viewModel.getFilesCount();
            int needUploadCount = viewModel.getNeedUploadCount();
            if (filesCount > 0 && needUploadCount == 0) {
                String text = getString(R.string.fragment_main__upload_status_success, String.valueOf(filesCount), String.valueOf(filesCount));
                textViewUploadInfo.setText(text);
                textViewUploadInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSuccessUpload));
                textViewUploadInfo.setVisibility(View.VISIBLE);
            } else {
                buttonUpload.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onItemClick(FileData fileData) {

    }
}
