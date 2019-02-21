package com.example.mynote;

import android.view.View;

public class CustomOnClickListener implements View.OnClickListener {
    private int position;
    private OnItemClickCallback onItemClickCallback;

    public CustomOnClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View v) {
        onItemClickCallback.OnItemClicked(v, position);
    }

    public interface OnItemClickCallback {
        void OnItemClicked(View view, int position);
    }
}
