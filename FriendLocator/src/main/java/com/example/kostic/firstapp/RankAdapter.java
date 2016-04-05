package com.example.kostic.firstapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends ArrayAdapter {

    List list = new ArrayList();
    RankActivity ctx;

    public RankAdapter(RankActivity ctx, int resource) {
        super(ctx, resource);
        this.ctx = ctx;
    }

    public void add(User object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        row = convertView;
        UserHolder userHolder;

        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.rank_table, parent, false);
            userHolder = new UserHolder();
            userHolder.tx_rank = (TextView)row.findViewById(R.id.rank);
            userHolder.tx_username = (TextView)row.findViewById(R.id.username);
            row.setTag(userHolder);

        }
        else
        {
            userHolder =(UserHolder) row.getTag();
        }

        User user = (User)this.getItem(position);
        userHolder.tx_rank.setText(String.valueOf(user.getRank()));

        //bold user
        if (ctx.user.equals(user.getUsername()))
        {
            userHolder.tx_username.setTypeface(null, Typeface.BOLD);
            userHolder.tx_username.setText(user.getUsername());
            return row;
        }

        userHolder.tx_username.setTypeface(null, Typeface.NORMAL);
        userHolder.tx_username.setText(user.getUsername());
        return row;
    }

    static class UserHolder
    {
        TextView tx_rank;
        TextView tx_username;
    }

}
