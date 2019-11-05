package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//5.
//6.
public class YoutubeDataAdapter extends RecyclerView.Adapter<YoutubeDataAdapter.ViewHolder>{

    //12.
    public interface OnItemClickListener{
        void OnItemClick(View v, int position);
    }

    //13.
    OnItemClickListener clickListener;

    //14.
    public void setOnItemClickListener(OnItemClickListener listener){
        this.clickListener = listener;
    }


    //7.
    private ArrayList<YoutubeDataModel> dataModels = null;
    private Context mContext;

    //1.
    public class ViewHolder extends RecyclerView.ViewHolder{

        //2.
        ImageView thumbnail_ImageView;
        TextView title_TextView;

        //3.
        ViewHolder(View itemView){
            super(itemView);

            //4.
            thumbnail_ImageView = itemView.findViewById(R.id.thumbnail_imageView); //썸네일
            title_TextView = itemView.findViewById(R.id.title_textView); // 제목

        }
    }

    //8.
    public YoutubeDataAdapter(ArrayList<YoutubeDataModel> dataModels, Context context) {
        this.dataModels = dataModels;
        mContext = context;
    }

    //9.
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.youtube_data_item, viewGroup, false);
        return new ViewHolder(view);
    }

    //10.
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        YoutubeDataModel dataModel = dataModels.get(i);

        //썸네일
        Glide.with(mContext).load(dataModel.getThumbnail()).into(viewHolder.thumbnail_ImageView);

        // 제목
        viewHolder.title_TextView.setText(dataModel.getTitle());

        //15.
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null){
                    clickListener.OnItemClick(viewHolder.itemView, i);
                }
            }
        });

    }

    //11.
    @Override
    public int getItemCount() {
        return dataModels.size();
    }
}
