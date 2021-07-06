package com.example.moopnotes.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.moopnotes.NoteEditActivity;
import com.example.moopnotes.R;
import com.example.moopnotes.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>{
    List<Note> mNoteList;

    public NoteAdapter(List <Note> NoteList) {
        mNoteList = NoteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder, @SuppressLint("RecyclerView") final int position){
        holder.mTextViewTitle.setText(mNoteList.get(position).getTitle());
        holder.mTextViewUpdatedAt.setText(mNoteList.get(position).getUpdatedAt());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(view.getContext(), NoteEditActivity.class);
                mIntent.putExtra("id", mNoteList.get(position).getId());
                mIntent.putExtra("title", mNoteList.get(position).getTitle());
                mIntent.putExtra("content", mNoteList.get(position).getContent());
                mIntent.putExtra("createdAt", mNoteList.get(position).getCreatedAt());
                mIntent.putExtra("updatedAt", mNoteList.get(position).getUpdatedAt());
                view.getContext().startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount () {
        return mNoteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewId, mTextViewTitle, mTextViewContent, mTextViewCreatedAt, mTextViewUpdatedAt;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextViewTitle = (TextView) itemView.findViewById(R.id.noteTitle);
            mTextViewUpdatedAt = (TextView) itemView.findViewById(R.id.noteUpdatedAt);
        }
    }
}