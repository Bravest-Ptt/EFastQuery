package bravest.ptt.efastquery.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bravest.ptt.efastquery.R;

public class FileItemHolder extends AbsHolder {

    public View getItem() {
        return item;
    }

    public ImageView getTypeImageView() {
        return typeImage;
    }

    public TextView getFileNameTextView() {
        return fileName;
    }

    private View item;
    private ImageView typeImage;
    private TextView fileName;

    public FileItemHolder(View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.item_file_manager);
        typeImage = (ImageView) itemView.findViewById(R.id.item_file_manager_folder);
        fileName = (TextView) itemView.findViewById(R.id.item_file_manager_name);

        item.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.item_file_manager) {
            super.onClick(view);
        }
    }
}
