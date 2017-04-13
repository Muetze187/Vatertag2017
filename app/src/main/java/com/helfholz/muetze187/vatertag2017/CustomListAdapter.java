package com.helfholz.muetze187.vatertag2017;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Muetze187 on 28.06.2016.
 */
public class CustomListAdapter extends BaseAdapter {

    private ArrayList<Teams> listData;
    private LayoutInflater layoutInflater;
    private ViewGroup.LayoutParams params;
    ViewHolder holder;

    public CustomListAdapter(MainActivity aContext, ArrayList<Teams> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);

    }

    public CustomListAdapter(BlauzahnActivity aContext, ArrayList<Teams> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
           convertView = layoutInflater.inflate(R.layout.team_listview, parent, false);
            holder = new ViewHolder();
            holder.teamNameView = (TextView) convertView.findViewById(R.id.name);
            holder.teamDrunkView = (TextView) convertView.findViewById(R.id.drunk);
            holder.alertView = (ImageView) convertView.findViewById(R.id.alert);
            holder.progressDrunk = (ProgressBar) convertView.findViewById(R.id.progressDrunk);
            holder.strackLevel = (TextView) convertView.findViewById(R.id.strackStatus);
            holder.vielfalt = (TextView) convertView.findViewById(R.id.vielfalt);
            holder.vielfaltOne = (TextView) convertView.findViewById(R.id.textViewOne);
            holder.vielfaltTwo = (TextView) convertView.findViewById(R.id.textViewTwo);
            holder.vielfaltThree = (TextView) convertView.findViewById(R.id.textViewThree);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNameView.setText(listData.get(position).getName());
        holder.teamDrunkView.setText(listData.get(position).getDrunk());
        holder.alertView.setImageResource(listData.get(position).getView());
        holder.strackLevel.setText(listData.get(position).getStrackLevelName());
        holder.progressDrunk.setProgress(listData.get(position).getProgressStrack());
        holder.vielfalt.setText(listData.get(position).getVielfalt());
        holder.vielfaltOne.setText(listData.get(position).getCounterOneFormatted());
        holder.vielfaltTwo.setText(listData.get(position).getCounterTwoFormatted());
        holder.vielfaltThree.setText(listData.get(position).getCounterThreeFormatted());
        if(listData.get(position).getAlerted())
            holder.alertView.startAnimation(listData.get(position).animation);
       /* params = holder.alertView.getLayoutParams();
        params.width = 160;
        params.height = 80;
        holder.alertView.setLayoutParams(params);*/
        return convertView;
    }

    static class ViewHolder {
        TextView teamNameView;
        TextView teamDrunkView;
        ImageView alertView;
        ProgressBar progressDrunk;
        TextView strackLevel;
        TextView vielfalt;
        TextView vielfaltOne;
        TextView vielfaltTwo;
        TextView vielfaltThree;


    }


}
