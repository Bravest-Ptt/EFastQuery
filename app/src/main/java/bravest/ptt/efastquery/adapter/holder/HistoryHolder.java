package bravest.ptt.efastquery.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bravest.ptt.efastquery.R;

public class HistoryHolder extends AbsHolder {
    public TextView getRequestTextView() {
        return requestTextView;
    }

    public TextView getExplainsTextView() {
        return explainsTextView;
    }

    private TextView requestTextView;
    private TextView explainsTextView;

    public HistoryHolder(View itemView) {
        super(itemView);

        requestTextView = (TextView) itemView.findViewById(R.id.item_content_request);
        explainsTextView = (TextView) itemView.findViewById(R.id.item_content_explains);

        ImageView voiceImageView = (ImageView) itemView.findViewById(R.id.item_voice);
        ImageView deleteImageView = (ImageView) itemView.findViewById(R.id.item_delete);
        View item = itemView.findViewById(R.id.item_content);
        voiceImageView.setOnClickListener(this);
        deleteImageView.setOnClickListener(this);
        item.setOnClickListener(this);
    }
}
