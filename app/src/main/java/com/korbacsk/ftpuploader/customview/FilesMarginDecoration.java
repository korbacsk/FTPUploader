package com.korbacsk.ftpuploader.customview;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.korbacsk.ftpuploader.R;

public class FilesMarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public FilesMarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.files_margin);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, margin);
    }
}
