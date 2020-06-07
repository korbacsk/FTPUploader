package com.korbacsk.ftpuploader.adapter;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.korbacsk.ftpuploader.R;
import com.korbacsk.ftpuploader.config.FileState;
import com.korbacsk.ftpuploader.model.FileData;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading = false;

    public boolean getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public interface OnItemClickListener {
        void onItemClick(FileData fileData);
    }


    private List<FileData> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    public FilesAdapter(Context context, @Nullable OnItemClickListener onItemClickListener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onItemClickListener = onItemClickListener;

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            View itemView = layoutInflater.inflate(R.layout.main_fragment_file_item, parent, false);
            return new FileViewHolder(itemView);
        } else {
            View itemView = layoutInflater.inflate(R.layout.main_fragment_loading_item, parent, false);
            return new FileViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.bind(data.size() > position ? data.get(position) : null, onItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        if (getIsLoading()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_DATA;
        }
    }

    @Override
    public int getItemCount() {
        return getIsLoading() ? 1 : data.size();
    }


    public void setData(List<FileData> newData) {
        data = newData;

        notifyDataSetChanged();
    }

    public void clear() {
        if (data != null) {
            data.clear();
        }

        notifyDataSetChanged();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.textViewFilename)
        @Nullable
        TextView textViewFilename;

        @BindView(R.id.textViewError)
        @Nullable
        TextView textViewError;

        @BindView(R.id.imageViewStatus)
        @Nullable
        ImageView imageViewStatus;

        @BindView(R.id.progressBarUploading)
        @Nullable
        ProgressBar progressBarUploading;

        FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final FileData fileData, final OnItemClickListener onItemClickListener) {
            if (fileData != null && textViewFilename != null) {
                textViewFilename.setText(fileData.getFilename());

                textViewError.setText("");
                if (fileData.getError() != null) {
                    textViewError.setText(fileData.getError());
                }

                imageViewStatus.setVisibility(View.GONE);
                progressBarUploading.setVisibility(View.GONE);
                textViewError.setVisibility(View.GONE);

                if (fileData.getFileState() == FileState.NEED_UPLOAD) {

                } else if (fileData.getFileState() == FileState.UPLOADING) {
                    progressBarUploading.setVisibility(View.VISIBLE);
                } else if (fileData.getFileState() == FileState.UPLOADED) {
                    imageViewStatus.setImageResource(R.drawable.ic_check_black_24dp);
                    imageViewStatus.setColorFilter(ContextCompat.getColor(context,
                            R.color.iconColorSuccessUpload), android.graphics.PorterDuff.Mode.SRC_IN);
                    imageViewStatus.setVisibility(View.VISIBLE);
                } else if (fileData.getFileState() == FileState.ERROR) {
                    if (fileData.getError() != null) {
                        textViewError.setText(fileData.getError());
                        textViewError.setVisibility(View.VISIBLE);
                    }

                    imageViewStatus.setImageResource(R.drawable.ic_error_black_24dp);
                    imageViewStatus.setColorFilter(ContextCompat.getColor(context,
                            R.color.iconColorErrorUpload), android.graphics.PorterDuff.Mode.SRC_IN);
                    imageViewStatus.setVisibility(View.VISIBLE);
                }


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(fileData);
                        }
                    }
                });

            }
        }

    }


}