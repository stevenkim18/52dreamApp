package com.example.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

//5. RecyclerView.Apapter<PostAdapter.ViewHolder>를 상속 받음
//6. 기본이 되는 메소드들을 오버라이드 받는다(빨간 전구 클릭)
//6-1. onCreateViewHolder
//6-2. onBindViewHolder
//6-3. getItemCount
public class PostAdapter2 extends RecyclerView.Adapter<PostAdapter2.ViewHolder>{

    // 7. 데이터를 담을 ArrayList를 변수를 선언한다.
    // 7-1. 여기서는 게시글을 담을 것이라서 <Post>를 해준다.
    private ArrayList<Post> posts = null;

    //A
    public interface onItemClickListener{
        void onItemClick(View v, int pos);
    }

    //B
    private onItemClickListener mListener = null;

    //C
    public void setOnItemClickListener(onItemClickListener listener){
        this.mListener = listener;
    }

    //1. ViewHolder 클래스를 만든다.
    //1-1. RecyclerView.ViewHolder를 상속 받는다.
    public class ViewHolder extends RecyclerView.ViewHolder{

        //2. 아이템에 나타낼 뷰 변수들을 선언한다.
        TextView title_TextView, id_TextView, date_TextView; // 글 제목, 글 작성 아이디, 글 쓴 날짜
        ImageButton menu_ImageButton; // 메뉴 버튼.

        //3. ViewHolder의 생성자를 생성
        ViewHolder(View itemView){
            super(itemView);

            //4. 위에 선언한 뷰 변수에 뷰 선언
            title_TextView = itemView.findViewById(R.id.title_textView); // 글 제목
            id_TextView = itemView.findViewById(R.id.id_textView); // 글 작성 아이디
            date_TextView = itemView.findViewById(R.id.date_textView); // 글 작성 날짜
            menu_ImageButton = itemView.findViewById(R.id.menu_imageButton); // 메뉴 버튼

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    mListener.onItemClick(v, postion);
                }
            });

        }

    }

    //8. Adapter의 생성자를 만들어 준다.
    //8-1. 생성자에서 데티어 리스트 객체를 전달 받는다.
    PostAdapter2(ArrayList<Post> list){
        posts = list;
    }

    //9. onCreateViewHolder 메소드
    //9-1. 항목 구성을 위한 레이아웃 XML 파일 inflate를 위해 호출 되는 메소드

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_board_item, viewGroup, false);
        return new ViewHolder(view);
    }

    //10. onBindViewHolder 메소드
    //10-1. onCreateViewHolder 에서 return 한 ViewHolder를 가지고 옴.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Post post = posts.get(i);
        // 글 제목
        viewHolder.title_TextView.setText(post.getTitle());
        // 글 쓴 아이디
        viewHolder.id_TextView.setText((post.getName()));
        // 글 쓴 날짜
        viewHolder.date_TextView.setText(post.getDate());

        //TODO 메뉴 버튼 설정


        //D.
        //https://do-dam.tistory.com/entry/RecyclerView-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-Click-%EC%9D%B4%EB%B2%A4%ED%8A%B8
//        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//                Toast.makeText(context, i + "",Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this, )
//            }
//        });

    }

    //11. 갯수 리턴
    @Override
    public int getItemCount() {
        return posts.size();
    }
}


