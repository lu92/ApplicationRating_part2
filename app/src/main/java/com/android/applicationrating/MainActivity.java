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
    private static ArrayList<Item> collectedItems = new ArrayList<>();
    private static ButtonEnum selectedButton = ButtonEnum.NONE_BUTTON;   //  which button should be selected
    private PackageManager packageManager;
    private ItemAdapter itemAdapter;

    private EditText filterEditText;
    private Button nameButton;
    private Button ratingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("OnCreate", "OnCreate Method");
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);

        filterEditText = (EditText) findViewById(R.id.appFilter);
        filterEditText.addTextChangedListener(getTextWatcher());
        filterEditText.getText().clear();

        nameButton = (Button) findViewById(R.id.nameButton);
        nameButton.setOnClickListener(onClickNameButtonListener());

        ratingButton = (Button) findViewById(R.id.ratingButton);
        ratingButton.setOnClickListener(onClickRatingButtonListener());

        packageManager = getPackageManager();

        new LoadApplications().execute();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //  which button should be selected after totate
        switch (selectedButton) {
            case SORT_BY_NAME_BUTTON:
                nameButton.setEnabled(false);
                ratingButton.setEnabled(true);
                break;

            case SORT_BY_RATING_BUTTON:
                nameButton.setEnabled(true);
                ratingButton.setEnabled(false);
                break;

            case NONE_BUTTON:
                nameButton.setEnabled(true);
                ratingButton.setEnabled(true);
                break;
        }

    }

    public ListView getListView() {
        return listView;
    }

    @Override
    public ButtonEnum getSelectedButton() {
        return selectedButton;
    }

    private View.OnClickListener onClickNameButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NameButton", "clicked");
                selectedButton = ButtonEnum.SORT_BY_NAME_BUTTON;
                nameButton.setEnabled(false);
                ratingButton.setEnabled(true);
                itemAdapter.sortByName();
//                listView.invalidateViews();
            }
        };
    }

    private View.OnClickListener onClickRatingButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RatingButton", "clicked");
                selectedButton = ButtonEnum.SORT_BY_RATING_BUTTON;
                nameButton.setEnabled(true);
                ratingButton.setEnabled(false);
                itemAdapter.sortByRating();
//                listView.invalidateViews();
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
                if (itemAdapter != null) {
                    if (count < before) {
                        // We're deleting char so we need to reset the adapter data
                        Log.i("TextWatcher", "RESET");
                        itemAdapter.resetData();
                    }
                    itemAdapter.getFilter().filter(s);
                itemAdapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    Log.i("ItemAdapterContent", "itemList size = " + itemAdapter.getItemListSize() + "\toriginItemList size = " + itemAdapter.getOriginItemListSize());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }


    @Override
    public void runSelectedApp(int position) {
        ApplicationInfo app = collectedItems.get(position).getApplicationInfo();

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

            if (collectedItems.isEmpty()) {   // run only once
                Log.i("doInBackground", "Getting the apps Info" + collectedItems.size() + "");
                for (ApplicationInfo applicationInfo : checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA))) {
                    String appName = (String) applicationInfo.loadLabel(packageManager);
                    Item item = new Item(applicationInfo, 0, appName);
                    collectedItems.add(item);
                }
            }
            return null;
        }

        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

            ArrayList<ApplicationInfo> appList = new ArrayList<>();

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
            itemAdapter = new ItemAdapter(MainActivity.this, R.layout.item_listview, collectedItems, MainActivity.this);
            listView.setAdapter(itemAdapter);
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