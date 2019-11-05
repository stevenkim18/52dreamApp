package com.example.myapplication;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

//5. RecyclerView.Apapter<PostAdapter.ViewHolder>를 상속 받음
//6. 기본이 되는 메소드들을 오버라이드 받는다(빨간 전구 클릭)
//6-1. onCreateViewHolder
//6-2. onBindViewHolder
//6-3. getItemCount
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable {

    // 7. 데이터를 담을 ArrayList를 변수를 선언한다.
    // 7-1. 여기서는 게시글을 담을 것이라서 <Post>를 해준다.
    private ArrayList<Post> posts = null;
    // 게시물 전체가 담긴 ArrayList 생성 --> SearchView 구현을 위해서
    private ArrayList<Post> fullposts = null;

    //B.
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    //C.
    OnItemClickListener mListener;

    //D.
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public interface OnButtonClickListener{
        void onButtonClick(View v, int position);
    }

    OnButtonClickListener buttonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener listener){
        buttonClickListener = listener;
    }


    //1. ViewHolder 클래스를 만든다.
    //1-1. RecyclerView.ViewHolder를 상속 받는다.
    public class ViewHolder extends RecyclerView.ViewHolder{

        //2. 아이템에 나타낼 뷰 변수들을 선언한다.
        TextView category_TextView, title_TextView, id_TextView, date_TextView; // 카테고리, 글 제목, 글 작성 아이디, 글 쓴 날짜
        ImageButton menu_ImageButton; // 메뉴 버튼.
        ImageView imageView;

        //3. ViewHolder의 생성자를 생성
        ViewHolder(View itemView){
            super(itemView);

            //4. 위에 선언한 뷰 변수에 뷰 선언
            category_TextView = itemView.findViewById(R.id.category_textView); //카테고리
            title_TextView = itemView.findViewById(R.id.title_textView); // 글 제목
            id_TextView = itemView.findViewById(R.id.id_textView); // 글 작성 아이디
            date_TextView = itemView.findViewById(R.id.date_textView); // 글 작성 날짜
            menu_ImageButton = itemView.findViewById(R.id.menu_imageButton); // 메뉴 버튼
            imageView = itemView.findViewById(R.id.post_imageView); //이미지 뷰

        }

//        //이미지 버튼 이벤트 처리를 위해 getter 생성
//        public ImageButton getMenu_ImageButton() {
//            return menu_ImageButton;
//        }
    }

    //8. Adapter의 생성자를 만들어 준다.
    //8-1. 생성자에서 데티어 리스트 객체를 전달 받는다.
    PostAdapter(ArrayList<Post> list){
        posts = list;
        Log.v("리스트", "posts.size() = " + posts.size());
    }

    public void setPostAdapter(ArrayList<Post> list){
        fullposts = new ArrayList<>(list);
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
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Post post = posts.get(i);
        // 글 카테고리
        viewHolder.category_TextView.setText(post.getCategory());
        // 글 제목
        viewHolder.title_TextView.setText(post.getTitle());
        // 글 쓴 아이디
        viewHolder.id_TextView.setText((post.getName()));
        // 글 쓴 날짜
        viewHolder.date_TextView.setText(post.getDate());

        // 글 이미지
        // 사진이 있으면 넣기
        if(post.getPhotoUri() !=null){
            Log.v("사진", "사진넣음");
            viewHolder.imageView.setImageURI(Uri.parse(post.getPhotoUri()));
        }
        // 사진이 없으면 이미지뷰 숨기기
        else {
            viewHolder.imageView.setVisibility(View.GONE);
        }

        //메뉴 버튼 설정
        viewHolder.menu_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.onButtonClick(v, i);
            }
        });

        //A. 아이템 뷰를 클릭 했을 때
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //E.
                if(mListener !=null){
                    mListener.onItemClick(viewHolder.itemView, i);
                }

            }
        });
    }

    //11. 갯수 리턴
    @Override
    public int getItemCount() {
        return posts.size();
    }

    // filter 메서드들
    @Override
    public Filter getFilter() {
        return postFilter;
    }

    private Filter postFilter = new Filter() {
        @Override
        // 검색어를 완료하지 않았을 때 보여주는 메소드
        // 매개변수는 검색중인 단어 --> constraint
        protected FilterResults performFiltering(CharSequence constraint) {
            // 검색어로 필터가 된 게시물들을 담을 ArrayList
            ArrayList<Post> filterPosts = new ArrayList<>();
            Log.v("검색", "검색된 게시물 글자 : " + constraint);
            Log.v("리스트", "posts.size() = " + posts.size());


            // 검색어가 없을 때
            if(constraint == null || constraint.length() == 0) {
                // 필터된 게시물에 전체 게시물을 넣음.
                filterPosts.addAll(fullposts);
                Log.v("검색", "fullPosts 갯수 : " + fullposts.size());
                Log.v("리스트", "fullPosts.size() = " + fullposts.size());

            }
            // 검색어가 하나라도 있을 때
            else{
                //입력된 검색어를 소문자로 바꿔주고 공백을 없앰.
                String filterPattern = constraint.toString().toLowerCase().trim();
                Log.v("검색", "검색된 게시물 글자 필터 : " + filterPattern);

                for(Post post : fullposts){
                    // 게시물의 제목이랑 비교
                    Log.v("검색", "검색된 게시물 글자 필터 : " + filterPattern);
                    Log.v("검색", "비교하는 게시물 제목 : " + post.getTitle().toLowerCase());
                    if(post.getTitle().toLowerCase().contains(filterPattern)){

                        filterPosts.add(post);
                    }
                }
                Log.v("검색", "검색된 게시물 갯수 : " + filterPosts.size());
            }

            FilterResults results = new FilterResults();
            results.values = filterPosts;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            posts.clear();
            posts.addAll((ArrayList<Post>) results.values);
            notifyDataSetChanged();
        }
    };
}


