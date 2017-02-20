package com.sampleapp.screens;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sampleapp.R;
import com.sampleapp.com.adapters.ListAdapter;
import com.sampleapp.model.WorldPojo;
import com.sampleapp.sqlite.Database;
import com.sampleapp.utils.CallbackListener;
import com.sampleapp.utils.Utils;
import com.sampleapp.webservices.JsonResponse;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by TYAGI on 2/18/2017.
 */
public class MainScreen extends AppCompatActivity implements CallbackListener, ListAdapter.MyClickListener, View.OnClickListener {

    EditText editText;
    StatefulRecyclerView verticalRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ListAdapter verticalListAdapter;
    Database myDataBase;
    Toolbar mToolbar;
    EditText mEditTextSearch;
    Button mButtonOk;
    ArrayList<WorldPojo> catList;
    CoordinatorLayout coordinatelayout;

    @Override
    public void onItemClick(int position, View v) {
        // Toast.makeText(MainActivity.this, " Pos " + position, Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar
                .make(coordinatelayout, "" + catList.get(position).getCountry(), Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Database
        myDataBase = new Database(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditTextSearch = (EditText) findViewById(R.id.editText_search);
        mButtonOk = (Button) findViewById(R.id.button_ok);
        coordinatelayout = (CoordinatorLayout) findViewById(R.id.coordinatelayout);

        mButtonOk.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //hiding keypad by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Ids of all widgets
        editText = (EditText) findViewById(R.id.editText);
        //  editText.setHint("");
        verticalRecyclerView = (StatefulRecyclerView) findViewById(R.id.verticalRecyclerView);
        //Vertical RecyclerView

        verticalRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        Parcelable state = mLayoutManager.onSaveInstanceState();
        verticalRecyclerView.setLayoutManager(mLayoutManager);

        if (Utils.isNetworkAvailable(this)) {
            JsonResponse mainAsync = new JsonResponse(this);
            mainAsync.setListener(MainScreen.this);
            mainAsync.execute();
        } else {
            catList = new ArrayList<>();
            catList = myDataBase.getallData();
            if (catList.size() > 0) {
                verticalListAdapter = new ListAdapter(MainScreen.this, catList);
                verticalRecyclerView.setAdapter(verticalListAdapter);
                mLayoutManager.onRestoreInstanceState(state);
            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatelayout, "There is no data.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }

    }

    @Override
    public void CatList(ArrayList<WorldPojo> pojoArrayList) {
        catList = (ArrayList<WorldPojo>) pojoArrayList.clone();
        verticalListAdapter = new ListAdapter(MainScreen.this, pojoArrayList);
        verticalRecyclerView.setAdapter(verticalListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("country")) {
            //  editText.setText("");
            WorldPojo dataObject = new WorldPojo();
            dataObject.setRank("0");
            dataObject.setCountry(getIntent().getStringExtra("country"));
            dataObject.setPopulation("Not calculated");
            dataObject.setFlag_image("");
            catList.add(0, dataObject);
            verticalListAdapter = new ListAdapter(MainScreen.this, catList);
            verticalRecyclerView.setAdapter(verticalListAdapter);
        }
    }

    private void sendNotification(String titleMessage, String message) {
        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("country", titleMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_check))
                .setContentTitle(titleMessage)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m/* ID of notification */, notificationBuilder.build());
    }


    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // int color = 0x008000;
            // notificationBuilder.setColor(color);
            return R.drawable.icon_check;

        } else {
            return R.mipmap.ic_launcher;
        }
    }

    @Override
    public void onClick(View v) {
        String mItem = mEditTextSearch.getText().toString().trim();
        if (mItem.length() == 0) {
            Snackbar snackbar = Snackbar
                    .make(coordinatelayout, "Please enter a country name first.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            sendNotification(mItem, "This is notification");
        }
    }

}