package com.helfholz.muetze187.vatertag2017;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Muetze187 on 28.06.2016.
 */
public class CustomListAdapter extends BaseAdapter {

    private ArrayList<Teams> listData;
    private LayoutInflater layoutInflater;
    ViewHolder holder;

    public CustomListAdapter(MainActivity aContext, ArrayList<Teams> listData) {
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
            convertView = layoutInflater.inflate(R.layout.team_listview, null);
            holder = new ViewHolder();
            holder.headlineView = (TextView) convertView.findViewById(R.id.name);
            holder.reporterNameView = (TextView) convertView.findViewById(R.id.drunk);
            holder.imageView = (ImageView) convertView.findViewById(R.id.alert);
            holder.progressDrunk = (ProgressBar) convertView.findViewById(R.id.progressDrunk);
            holder.strackLevel = (TextView) convertView.findViewById(R.id.strackStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.headlineView.setText(listData.get(position).getName());
        holder.reporterNameView.setText(listData.get(position).getDrunk());
        holder.imageView.setImageResource(listData.get(position).getView());
        holder.strackLevel.setText(listData.get(position).getStrackLevelName());
        holder.progressDrunk.setProgress(listData.get(position).getProgressStrack());
        if(listData.get(position).getAlerted())
            holder.imageView.startAnimation(listData.get(position).animation);

        return convertView;
    }

    static class ViewHolder {
        TextView headlineView;
        TextView reporterNameView;
        ImageView imageView;
        ProgressBar progressDrunk;
        TextView strackLevel;

    }


}
