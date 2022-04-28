package com.example.hw1;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppAdapter extends BaseAdapter implements Filterable {

    static class AppTag{ //Tag used in the getView overridden method
        private TextView name;
        private ImageView img;
    }

    private final Activity context;
    private final List<App> originalApps; //originalApps represents the original list of visible installed apps on the device
    private List<App> appsFiltered; //appsFiltered represents a list of the original apps after applying the filter

    public AppAdapter(Activity context, List<App> originalApps) {
        this.context = context;
        this.originalApps = originalApps;
        this.appsFiltered = originalApps;
    }

    public void addApp(App a){ //for adding the search app to the appsFiltered list
        appsFiltered.add(a);
        notifyDataSetChanged();
    }

    public void sortByName(){
        Collections.sort(appsFiltered, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDefault(){
        Collections.sort(appsFiltered, Comparator.comparingInt(App::getId));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return appsFiltered.size();
    }

    @Override
    public App getItem(int position) {
        return appsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) { // View for each app in the list
        View elem;

        LayoutInflater inflater = context.getLayoutInflater();
        elem = inflater.inflate(R.layout.activity_2, null);

        AppTag at = new AppTag();
        at.name = elem.findViewById(R.id.title);
        at.img = elem.findViewById(R.id.icon);

        at.name.setText(appsFiltered.get(position).getName());
        at.img.setImageDrawable(appsFiltered.get(position).getIcon());

        elem.setTag(at);
        return elem;
    }

    @Override
    public Filter getFilter() { //Filter for the appsFiltered when searching for app
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<App> res = new ArrayList<>();
                String searchStr = constraint.toString();
                for(App a : originalApps){
                    if(a.getName().contains(searchStr)){
                        res.add(a);
                    }
                    filterResults.count = res.size();
                    filterResults.values = res;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                appsFiltered = (List<App>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}