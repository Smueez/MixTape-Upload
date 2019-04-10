package com.example.user.mixtapeupload;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Activity context_cmmnt;
    private List<Comment> cmmnt_list;

    public CommentAdapter(Activity context_cmmnt,List<Comment>cmmnt_list){
        super(context_cmmnt,R.layout.comment_list,cmmnt_list);

        this.context_cmmnt = context_cmmnt;
        this.cmmnt_list = cmmnt_list;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = context_cmmnt.getLayoutInflater();
        View comment_list = inflater.inflate(R.layout.comment_list,null,true);

        TextView name_cmmnt = comment_list.findViewById(R.id.textView10);
        TextView txt_cmmnt = comment_list.findViewById(R.id.textView12);

        Comment comment = cmmnt_list.get(position);
        name_cmmnt.setText(comment.getComment_name());
        txt_cmmnt.setText(comment.getComment_text());

        return comment_list;
    }

}
