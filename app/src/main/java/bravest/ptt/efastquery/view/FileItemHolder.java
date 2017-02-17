package bravest.ptt.efastquery.view;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;


/**
 * Created by root on 2/17/17.
 */

public class FileItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public View item;
    public ImageView typeImage;
    public TextView fileName;

    private ItemClickListener mItemClickListener;

    public FileItemHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        item = itemView.findViewById(R.id.item_file_manager);
        typeImage = (ImageView) itemView.findViewById(R.id.item_file_manager_folder);
        fileName = (TextView) itemView.findViewById(R.id.item_file_manager_name);

        mItemClickListener = listener;

        item.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.item_file_manager) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClicked(view, getAdapterPosition());
            }
        }
    }
}
