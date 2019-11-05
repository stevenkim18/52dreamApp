package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

//5.
//6.
public class BibleVerseAdapter extends RecyclerView.Adapter<BibleVerseAdapter.ViewHolder>{

    //7.
    ArrayList<BibleVerse> bibleVerses = null;


    //1.
    public class ViewHolder extends RecyclerView.ViewHolder{
        //2.
        TextView number_TextView, bible_verse_TextView; // 성경 위치, 성경 구절

        //3.
        public ViewHolder(View itemView) {
            super(itemView);

            //4.
            number_TextView = itemView.findViewById(R.id.number_textView);
            bible_verse_TextView = itemView.findViewById(R.id.bibleVerse_textView);

        }
    }

    //8.
    public BibleVerseAdapter(ArrayList<BibleVerse> bibleVerses) {
        this.bibleVerses = bibleVerses;
    }

    //9.
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bible_verse_item, parent,false);
        return new BibleVerseAdapter.ViewHolder(view);
    }

    //10.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BibleVerse bibleVerse = bibleVerses.get(position);

        // 성경 숫자
        holder.number_TextView.setText(bibleVerse.getNumber());

        // 성경 본문
        holder.bible_verse_TextView.setText(bibleVerse.getBible_verse());

    }

    //11.
    @Override
    public int getItemCount() {
        return bibleVerses.size();
    }
}
