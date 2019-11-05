package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

//5.
//6.
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    //7.
    ArrayList<Comment> comments = null;

    //8. 어뎁터의 생성자
    CommentAdapter(ArrayList<Comment> list){
        comments = list;
    }

    //13. 이미지 버튼 리스너 구현
    public interface onImageButtonClickListener{
        void onImageButtonClick(View v, int position);
    }

    onImageButtonClickListener imageButtonClickListener;

    public void setOnImageButtonClickListener(onImageButtonClickListener listener){
        imageButtonClickListener = listener;
    }

    //1.
    public class ViewHolder extends RecyclerView.ViewHolder{
        //2.
        TextView id_TextView, contents_TextView, date_TextView;
        ImageButton imageButton;

        //3,
        ViewHolder(View itemView){
            super(itemView);

            //4.
            id_TextView = itemView.findViewById(R.id.id_textView);
            contents_TextView = itemView.findViewById(R.id.comment_textView);
            date_TextView = itemView.findViewById(R.id.date_textView);
            imageButton = itemView.findViewById(R.id.menu_imageButton);


        }
    }

    //9. 뷰홀더 생성
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    //10.아이템 뷰와 뷰 바인더를 묶어줌.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) { //15. final
        Comment comment = comments.get(i);

        //댓글을 쓴 아이디
        viewHolder.id_TextView.setText(comment.getName());

        //댓글을 쓴 내용
        viewHolder.contents_TextView.setText(comment.getContents());

        //댓글을 쓴 날짜
        viewHolder.date_TextView.setText(comment.getDate());

        //12.
        //메뉴 이미지 버튼
        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //14, 리스너 구현
                if(imageButtonClickListener !=null){
                    imageButtonClickListener.onImageButtonClick(v, i);
                }
            }
        });

    }

    //11.
    @Override
    public int getItemCount() {
        return comments.size();
    }
}
