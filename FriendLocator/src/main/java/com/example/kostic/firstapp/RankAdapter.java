package com.example.kostic.firstapp;

import android.content.Context;
import android.graphics.Color;
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

    public void add(RankUser object) {
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
            userHolder.tv_place= (TextView)row.findViewById(R.id.place);
            userHolder.tv_username = (TextView)row.findViewById(R.id.username);
            userHolder.tv_team = (TextView)row.findViewById(R.id.team);
            userHolder.tv_rank = (TextView)row.findViewById(R.id.rank);
            row.setTag(userHolder);

        }
        else
        {
            userHolder =(UserHolder) row.getTag();
        }

        RankUser rankUser = (RankUser)this.getItem(position);
        userHolder.tv_place.setText(String.valueOf(rankUser.getPlace()));

        //color team
        if(rankUser.getTeam().equals("Red Team"))
        {
            userHolder.tv_team.setTextColor(Color.parseColor("#e3e60b16"));
            userHolder.tv_team.setText(rankUser.getTeam());
        }
        else {
            userHolder.tv_team.setTextColor(Color.parseColor("#e310a710"));
            userHolder.tv_team.setText(rankUser.getTeam());
        }

        userHolder.tv_rank.setText(String.valueOf(rankUser.getRank()));

        //bold rankUser
        if (ctx.user.equals(rankUser.getUsername()))
        {
            userHolder.tv_username.setTypeface(null, Typeface.BOLD);
            userHolder.tv_username.setText(rankUser.getUsername());
            return row;
        }

        userHolder.tv_username.setTypeface(null, Typeface.NORMAL);
        userHolder.tv_username.setText(rankUser.getUsername());
        return row;
    }

    static class UserHolder
    {
        TextView tv_place;
        TextView tv_username;
        TextView tv_team;
        TextView tv_rank;
    }

}
