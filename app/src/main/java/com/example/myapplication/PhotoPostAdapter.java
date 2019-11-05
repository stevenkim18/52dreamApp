package com.example.myapplication;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

//5.
//6.
public class PhotoPostAdapter extends RecyclerView.Adapter<PhotoPostAdapter.ViewHolder>{

    //이미지뷰 롱클릭 리스터 구현
    //13.
    public interface OnImageViewLongClickListener{
        void onImageViewLongClick(View v, int position);
    }

    //14.
    OnImageViewLongClickListener mListener;

    //15.
    public void setOnImageViewLongClickListener(OnImageViewLongClickListener listener){
        mListener = listener;
    }

    //이미지뷰 일반 클릭 구현
    public interface OnImageViewClickListener{
        void onImageViewClick(View v, int position);
    }

    OnImageViewClickListener imageViewClickListener;

    public void setOnImageViewClick(OnImageViewClickListener listener){
        imageViewClickListener = listener;
    }


    //7.
    ArrayList<PhotoPost> photoPosts = null;



    //1.
    public class ViewHolder extends RecyclerView.ViewHolder{
        //2.
        ImageView imageView; // 게시물 사진

        //3.
        ViewHolder(View itemView){
            super(itemView);

            //4.
            imageView = itemView.findViewById(R.id.photo_imageView);

        }
    }

    //8.
    PhotoPostAdapter(ArrayList<PhotoPost> list){
        photoPosts = list;
    }

    //9.
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
        return new PhotoPostAdapter.ViewHolder(view);
    }

    //10.
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        PhotoPost photoPost =  photoPosts.get(i);

        viewHolder.imageView.setImageURI(Uri.parse(photoPost.getPhotoUri()));

        //12. 이미지 롱클릭 구현
        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //16.
                mListener.onImageViewLongClick(v, i);
                return true;
            }
        });

        // 이미지뷰 일반 클릭 구현
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewClickListener.onImageViewClick(v,i);
            }
        });

    }

    //11.
    @Override
    public int getItemCount() {
        return photoPosts.size();
    }
}
