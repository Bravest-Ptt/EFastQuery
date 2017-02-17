package bravest.ptt.efastquery.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;


/**
 * Created by root on 2/17/17.
 */

public class FileManagerAdapter extends RecyclerView.Adapter<FileItemHolder> {

    private Context mContext;
    private ArrayList<File> mData;

    private ItemClickListener mItemClickListener;

    public FileManagerAdapter(Context context, ArrayList<File> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public FileItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) {
            return null;
        }
        return new FileItemHolder(inflater.inflate(R.layout.item_file_manager, null), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(FileItemHolder holder, int position) {
        File file = mData.get(position);
        if (file != null) {
            if (file.isFile()) {
                holder.typeImage.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.oval_file));
                holder.typeImage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.file));
            } else if (file.isDirectory()){
                holder.typeImage.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.oval_folder));
                holder.typeImage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.folder));
            }
            holder.fileName.setText(file.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
