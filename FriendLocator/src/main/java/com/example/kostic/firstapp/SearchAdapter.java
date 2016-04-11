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

public class SearchAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public SearchAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(EventMarker object) {
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
        MarkerHolder markerHolder;

        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.search_table, parent, false);
            markerHolder = new MarkerHolder();

            markerHolder.tv_username = (TextView)row.findViewById(R.id.username);
            markerHolder.tv_team = (TextView)row.findViewById(R.id.team);
            markerHolder.tv_distance = (TextView)row.findViewById(R.id.distance);

            row.setTag(markerHolder);

        }
        else
        {
            markerHolder =(MarkerHolder) row.getTag();
        }

        EventMarker marker = (EventMarker)this.getItem(position);

        //color team
        if(marker.getTeam().equals("Red Team"))
        {
            markerHolder.tv_team.setTextColor(Color.parseColor("#e3e60b16"));
            markerHolder.tv_team.setText(marker.getTeam());
        }
        else {
            markerHolder.tv_team.setTextColor(Color.parseColor("#e310a710"));
            markerHolder.tv_team.setText(marker.getTeam());
        }

        markerHolder.tv_username.setText(marker.getUser());
        markerHolder.tv_distance.setText(marker.getInfo());

        return row;
    }

    static class MarkerHolder
    {
        TextView tv_username;
        TextView tv_team;
        TextView tv_distance;
    }

}
