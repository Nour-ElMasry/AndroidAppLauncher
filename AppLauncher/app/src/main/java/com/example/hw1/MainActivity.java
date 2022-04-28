package com.example.hw1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ListView lv;
    AppAdapter appAdapter; // Custom ArrayAdapter
    List<App> apps; // List of installed apps obtained from the function called getInstalledApps();
    String[] sortTypes= {"Default", "Name"}; //Array of Strings which represents the menu items which are gonna be in the menu created below for the sorting types

    //Some attributes used for when the searched app is not found 'Google Play Store search'
    String searchApp; //String which will contain the name of the searched app
    @SuppressLint("UseCompatLoadingForDrawables")
    App search = new App("Search App on Google Play", null, "market"); //App which will be added to the list of apps when the searched app is not found

    //Intents used
    Intent gPlay; //Intent for when we want to search for an app on google play
    Intent launchedApps; //Intent for used as a 'Filter' to get the apps with a certain category (see this in the method called getInstalledApps())

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.lv); //get the list view
        apps = getInstalledApps(); //get the visible apps on the device
        appAdapter = new AppAdapter(this, apps);
        lv.setAdapter(appAdapter); //giving the data from the custom ArrayAdapter to the ListView

        lv.setOnItemClickListener((parent, view, position, id) -> {

            //Checking if the clicked app from the ListView is the 'Search App on Google Play' app
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appAdapter.getItem(position).getPackageName());
            if(launchIntent != null){
                startActivity(launchIntent);
            }

            if(appAdapter.getItem(position).getName().equals("Search App on Google Play")){
               if(isGPlayOnDevice())
                {
                    //Intent to start the Google Play Store with the search query being set to the App we didn't find in our list
                   gPlay = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + searchApp));
                }else{
                   gPlay = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=" + searchApp));
               }
               startActivity(gPlay);
            }
        });

        //Spinner for the Sorting Type menu
        Spinner sp = findViewById(R.id.sp);
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortTypes);
        sp.setAdapter(ad); //giving the data from the ArrayAdapter to the spinner

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Checking which sorting type is selected from the spinner
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(sortTypes[position].equals("Name")){
                    //sorting by name
                    appAdapter.sortByName();
                    Toast.makeText(getApplicationContext(),"App List sorted by Name", Toast.LENGTH_LONG).show();

                }else if(sortTypes[position].equals("Default")){
                    //sorting by default which is the original order in which the apps were added to the list
                    appAdapter.sortByDefault();
                    Toast.makeText(getApplicationContext(),"App List sorted by Default", Toast.LENGTH_LONG).show();

                }
                appAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing
            }
        });
    }

    private List<App> getInstalledApps() { //Method to get the list of visible apps on the device

        //intent which will be used as a 'filter' when getting the list of installed apps
        launchedApps = new Intent(Intent.ACTION_MAIN, null);
        launchedApps.addCategory(Intent.CATEGORY_LAUNCHER);

        //we add to this list the applications available on the respective device which have the same intent we initiated previously and also the same category added to the intent
        List<ResolveInfo> ri = getPackageManager().queryIntentActivities(launchedApps, 0);

        List<App> res = new ArrayList<>();

        for(ResolveInfo resInfo : ri) {
            if (!this.getPackageName().equals(resInfo.activityInfo.packageName)) {
                App a = new App(resInfo.loadLabel(getPackageManager()).toString(), resInfo.loadIcon(getPackageManager()), resInfo.activityInfo.packageName);
                res.add(a);
            }
        }
        return res;
    }

    //method to check if google play is available on device
    private boolean isGPlayOnDevice() {
        try {
            getPackageManager().getPackageInfo("com.android.vending", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Adding the search icon to the Menu
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem mi = menu.findItem(R.id.search_button);
        SearchView sv= (SearchView)mi.getActionView();

        sv.setQueryHint("Search Here"); //basically a place holder

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                /* When the user presses on submit and there is no app with the name the user searched for then we add the App called 'search' to
                 the custom ArrayAdapters list and not to the list of apps called 'apps' so when we change the search text or we cancel the search
                 then the search app doesn't remain since the custom ArrayAdapters list is modified according to the original list of apps which
                 don't contain the search app so its automatically 'removed' */

                searchApp = query;
                if (appAdapter.getCount() == 0){
                    appAdapter.addApp(search);
                    Toast.makeText(getApplicationContext(),"App not found!", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter the custom ArrayAdapters List according to the search text
                appAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
}