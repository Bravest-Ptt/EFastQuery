package bravest.ptt.efastquery.view.adapter.recycler;

import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.view.holder.AbsHolder;
import bravest.ptt.efastquery.view.holder.FileItemHolder;

public class FileManagerAdapter extends AbsAdapter {

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileItemHolder holder = new FileItemHolder(getInflater().inflate(R.layout.item_file_manager, parent, false));
        holder.setItemClickListener(getItemClickListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsHolder holder, int position) {
        FileItemHolder itemHolder = (FileItemHolder) holder;
        File file = (File) getData().get(position);

        if (file != null) {
            ImageView fileIcon = itemHolder.getTypeImageView();
            if (file.isFile()) {
                fileIcon.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.oval_file));
                fileIcon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.file));
            } else if (file.isDirectory()){
                fileIcon.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.oval_folder));
                fileIcon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.folder));
            }
            itemHolder.getFileNameTextView().setText(file.getName());
        }
    }
}
