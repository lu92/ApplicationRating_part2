package com.android.applicationrating;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListenerActivity {

    private ListView listView;
    private ArrayList<Item> itemList;
    private PackageManager packageManager;
    private ItemAdapter itemAdapter;

    private EditText filterEditText;
    private Button nameButton;
    private Button ratingButton;

    private NameComparator nameComparator = new NameComparator();
    private RatingComparator ratingComparator = new RatingComparator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("OnCreate", "OnCreate Method");
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        itemList = new ArrayList<>();

        itemAdapter = new ItemAdapter(this, R.layout.item_listview, itemList, this);

        filterEditText = (EditText) findViewById(R.id.appFilter);
        filterEditText.addTextChangedListener(getTextWatcher());
        filterEditText.getText().clear();

        nameButton = (Button) findViewById(R.id.nameButton);
        nameButton.setOnClickListener(onClickNameButtonListener());

        ratingButton = (Button) findViewById(R.id.ratingButton);
        ratingButton.setOnClickListener(onClickRatingButtonListener());

        listView.setAdapter(itemAdapter);

        packageManager = getPackageManager();

        new LoadApplications().execute();
    }

    public ListView getListView() {
        return listView;
    }

    private View.OnClickListener onClickNameButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NameButton", "clicked");
                Collections.sort(itemAdapter.getItemList(), nameComparator);
                listView.invalidateViews();
            }
        };
    }

    private View.OnClickListener onClickRatingButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RatingButton", "clicked");
                Collections.sort(itemAdapter.getItemList(), ratingComparator);
                listView.invalidateViews();
            }
        };
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("TextWatcher", s.toString());
                itemAdapter.getFilter().filter(s);
//                itemAdapter.notifyDataSetChanged();
                listView.invalidateViews();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }


    @Override
    public void runSelectedApp(int position) {
        ApplicationInfo app = itemList.get(position).getApplicationInfo();

        try {
            Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);

            if (intent != null) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {

            List<Item> collectedItems = new ArrayList<>();
            for (ApplicationInfo applicationInfo : checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA))) {
                String appName = (String) applicationInfo.loadLabel(packageManager);
                Item item = new Item(applicationInfo, 0, appName);
                collectedItems.add(item);
                itemList.add(item);
            }
            itemAdapter.setOriginItemList(collectedItems);

            return null;
        }

        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

            ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

            for (ApplicationInfo info : list) {
                try {
                    if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                        appList.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return appList;
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            listView.invalidateViews();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, null, "Loading apps info...");
            super.onPreExecute();
        }
    }
}