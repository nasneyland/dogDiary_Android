package com.najin.dogdiary.setting;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najin.dogdiary.R;
import com.najin.dogdiary.support.Common;
import com.najin.dogdiary.support.ImageLoadTask;

import java.util.ArrayList;

public class DogListAdapter extends RecyclerView.Adapter<DogListAdapter.ViewHolder> {

    Context context;
    OnItemClickListener listener;
    ArrayList<DogListItem> items = new ArrayList<DogListItem>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.cell_dog_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DogListItem item;
        if (position != items.size()) {
            item = items.get(position);
        } else {
            item = null;
        }
        holder.setItem(item);
        holder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (items.size()+1);
    }

    public  static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public DogListAdapter(Context context) {
        this.context = context;
    }

    public void addItem(DogListItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<DogListItem> items) {
        this.items = items;
    }

    public DogListItem getItem(int position) {
        return items.get(position);
    }

    public void setOnClickListener(OnItemClickListener listener) {

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dogImage;
        TextView dogName;
        ImageView selectedDog;

        OnItemClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);

            dogImage = (ImageView) itemView.findViewById(R.id.dogImage);
            dogName = (TextView) itemView.findViewById(R.id.dogName);
            selectedDog = (ImageView) itemView.findViewById(R.id.selectedDog);

            dogImage.setBackground(new ShapeDrawable(new OvalShape()));
            dogImage.setClipToOutline(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(DogListItem item) {
            if (item == null) {
                dogName.setText("");
                selectedDog.setVisibility(View.GONE);
                dogImage.setImageResource(R.drawable.img_plus);
            } else {
                //강아지 이름 셋팅
                dogName.setText(item.getName());
                //강아지 이미지 셋팅
                if (!item.getImage().equals("")) {
                    String url = Common.URL + "/media/" + item.getImage();
                    ImageLoadTask task = new ImageLoadTask(url, dogImage);
                    task.execute();
                } else {
                    dogImage.setImageResource(R.drawable.img_profile);
                }
                //활성 강아지 셋팅
                if (item.getSelected()) {
                    selectedDog.setVisibility(View.VISIBLE);
                } else {
                    selectedDog.setVisibility(View.GONE);
                }
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}

