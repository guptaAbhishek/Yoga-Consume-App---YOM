package com.example.siddarthshikhar.yogaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class MyScheduleActivity extends ActionBarActivity implements PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone, ClassAdapter.classUpdateTask {

    TextView showSelectedDate;
    CaldroidFragment caldroidFragment;
    Date savedDate;
    HashMap<Date,String> map;
    private SlidingUpPanelLayout mLayout;
    String currDate;
    ArrayList<YogaClass> listOfWholeMonth,listOfSpecificDate;
    ListView lv;
    final Context c=this;
    ClassAdapter adapter;
    ImageView up1,up2;
    ProgressDialog dlg;
    int check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        listOfWholeMonth=new ArrayList<YogaClass>();
        listOfSpecificDate=new ArrayList<YogaClass>();
        lv =(ListView)findViewById(R.id.yoga_class_list);

        up1=(ImageView)findViewById(R.id.up_icon_1);
        up2=(ImageView)findViewById(R.id.up_icon_2);
        check=0;

        map=new HashMap<Date,String>();
        savedDate=null;
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelExpanded(View panel) {
                up1.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                up2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                up1.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                up2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBar bar=getSupportActionBar();
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("MY SCHEDULE");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        caldroidFragment = new CaldroidFragment();

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, true);
        caldroidFragment.setArguments(args);
        caldroidFragment.setCaldroidListener(listener);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        showSelectedDate=(TextView)findViewById(R.id.date_selected);
        showSelectedDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
    }
    public void populateClassList(){
        listOfSpecificDate.clear();
        for(int i=0;i<listOfWholeMonth.size();i++){
            String date = listOfWholeMonth.get(i).date;
            String startTime =listOfWholeMonth.get(i).startTime ;
            String endTime =listOfWholeMonth.get(i).endTime;
            String scheduleId =listOfWholeMonth.get(i).scheduleID;
            String classStatus=listOfWholeMonth.get(i).currStatus;
            String venue=listOfWholeMonth.get(i).venue;
            if(date.equals(currDate))
                listOfSpecificDate.add(new YogaClass(date,startTime,endTime,scheduleId,classStatus,venue));
        }
        String temp=currDate+" 23:59";
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US);
        Date dateFromTemp=null;
        try {
            dateFromTemp=format.parse(temp);
        } catch (ParseException e) {
        }
        if(listOfSpecificDate.size()==0 && dateFromTemp.getTime()<System.currentTimeMillis()){
            listOfSpecificDate.add(new YogaClass(currDate,"00:00","00:00","bogus","-1",""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
    public  void setDatesWithColours(int month,int lastDayOfMonth,int year){
        CustomErrorDialog dialog=new CustomErrorDialog();
        dialog.listener=this;
        dlg = new ProgressDialog(MyScheduleActivity.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Setting Up Calendar....");
        dlg.show();

        Calendar cal = Calendar.getInstance();
        int currDayOfMonth=1;
        String startDate="",endDate="";
        startDate=""+year+"-"+month+"-"+currDayOfMonth;
        endDate=""+year+"-"+month+"-"+lastDayOfMonth;

        JSONObject toBePosted=new JSONObject();
        try {
            toBePosted.put("phone",getSharedPreferences("profilephone", 0).getString("phone", null));
            toBePosted.put("authKey",getSharedPreferences("Authorization", 0).getString("Auth_key", null));
            toBePosted.put("start_date", startDate);
            toBePosted.put("end_date",endDate);
        } catch (JSONException e) {
        }
        PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
        puttingTask.toBePosted=toBePosted;
        puttingTask.listener=this;
        puttingTask.execute("http://"+Constants.IP_ADDRESS+"getmyschedule");
    }
    final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            String newformat=format.format(date);
            format=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US);
            try {
                date=format.parse(newformat+" 23:59");
            } catch (ParseException e) {
            }
            format=new SimpleDateFormat("dd MMMM yyyy",Locale.US);
            showSelectedDate.setText(format.format(date));
            if(savedDate!=null){
                if(map.containsKey(savedDate)){
                    if(map.get(savedDate).equals("Red")){
                        caldroidFragment.setBackgroundResourceForDate(R.color.Red,savedDate);
                    }else if(map.get(savedDate).equals("Yellow")){
                        caldroidFragment.setBackgroundResourceForDate(R.color.Yellow,savedDate);
                    }else if(map.get(savedDate).equals("Green")){
                        caldroidFragment.setBackgroundResourceForDate(R.color.Green,savedDate);
                    }
                }
                else
                    caldroidFragment.clearBackgroundResourceForDate(savedDate);
            }
            savedDate=date;
            caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_sky_blue, savedDate);
            caldroidFragment.refreshView();
            currDate="";
            format = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            try {
                Date temp=format.parse(showSelectedDate.getText().toString());
                format=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                currDate=format.format(temp);
            } catch (ParseException e) {
            }
            populateClassList();

            adapter = new ClassAdapter(c, 0, listOfSpecificDate, MyScheduleActivity.this.getLayoutInflater(), MyScheduleActivity.this.getFragmentManager(),MyScheduleActivity.this);
            adapter.callingClass=MyScheduleActivity.this;
            lv.setAdapter(adapter);
        }
        @Override
        public void onChangeMonth(int month, int year) {
            Calendar cal=Calendar.getInstance();
            cal.set(year, month, 1);
            setDatesWithColours(cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR));
        }
        @Override
        public void onLongClickDate(Date date, View view) {
            onSelectDate(date, view);
        }
        @Override
        public void onCaldroidViewCreated() {
            Date temp=new Date(System.currentTimeMillis());
            savedDate=temp;
            SimpleDateFormat format=new SimpleDateFormat("dd MMMM yyyy",Locale.US);
            showSelectedDate.setText(format.format(temp));
            caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_sky_blue, temp);
            caldroidFragment.refreshView();
            listOfSpecificDate.clear();
            currDate="";
            format=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            currDate=format.format(temp);
            populateClassList();

            adapter = new ClassAdapter(c, 0, listOfSpecificDate, MyScheduleActivity.this.getLayoutInflater(), MyScheduleActivity.this.getFragmentManager(), MyScheduleActivity.this);
            adapter.callingClass=MyScheduleActivity.this;
            lv.setAdapter(adapter);

            up1.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            up2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
    };

    @Override
    public void processEnquiries(Boolean ifExecuted, String output, int typeOfError) {
        if(check==1){
            if(ifExecuted==true){
                JSONObject obj = null;
                String responseType="",responseMessage="";
                try {
                    obj = new JSONObject(output);
                    responseType=obj.getString("Response_Type");
                    responseMessage=obj.getString("Response_Message");
                } catch (JSONException e) {
                }
                if(responseType.equals("Success")){
                    CustomErrorDialog dialog=new CustomErrorDialog();
                    dialog.listener=MyScheduleActivity.this;
                    dialog.dialogString="Schedule successfully Updated";
                    dialog.show(getFragmentManager(), "");
                }else{
                    CustomErrorDialog dialog=new CustomErrorDialog();
                    dialog.listener=MyScheduleActivity.this;
                    dialog.dialogString="We are experiencing some Technical difficulties.Please contact us";
                    dialog.show(getFragmentManager(), "");
                }
            }else{
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=MyScheduleActivity.this;
                if(typeOfError==1){
                    dialog.dialogString="No Internet Connection!";
                    dialog.show(getFragmentManager(), "");
                }
                else{
                    dialog.dialogString="Oops! Server Error! Please try after some time";
                    dialog.show(getFragmentManager(),"");
                }
            }
        }else{
            dlg.dismiss();
            if(ifExecuted==true){
                JSONArray arr=null;
                JSONObject obj = null;
                String responseMessage="";
                try {
                    arr = new JSONArray(output);
                } catch (JSONException e) {
                    responseMessage="Error";
                }
                if(responseMessage.equals("Error")==false){
                    listOfWholeMonth.clear();
                    for(int i=0;i<arr.length();i++) {
                        try {
                            obj=arr.getJSONObject(i);
                        } catch (JSONException e) {
                        }
                        String scheduleID="",startTime="",endTime="",date="",currStatus="",venue="";
                        try {
                            scheduleID = obj.getString("SCHEDULE_DATE_ID");
                            startTime = obj.getString("START_TIME");
                            endTime = obj.getString("END_TIME");
                            date = obj.getString("SCHEDULE_DATE");
                            currStatus = obj.getString("CLASS_STATUS");
                            venue=obj.getString("VENUE");
                        } catch (JSONException e) {
                        }
                        listOfWholeMonth.add(new YogaClass(date,startTime,endTime,scheduleID,currStatus,venue));
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                        Date currDate=null;
                        try {
                            currDate=format.parse(date+" 23:59");
                        } catch (ParseException e) {
                        }
                        if(map.containsKey(currDate)){
                            if(map.get(currDate).equals("Yellow")){
                                continue;
                            }
                            else if(map.get(currDate).equals("Green")){
                                if(currStatus.equals("1")==false) {
                                    caldroidFragment.setBackgroundResourceForDate(R.color.Yellow, currDate);
                                    map.remove(currDate);
                                    map.put(currDate,"Yellow");
                                }else
                                    continue;
                            }
                            else{
                                if(currStatus.equals("0")==false){
                                    caldroidFragment.setBackgroundResourceForDate(R.color.Yellow, currDate);
                                    map.remove(currDate);
                                    map.put(currDate,"Yellow");
                                }else
                                    continue;
                            }
                        }else{
                            if(currDate.getTime()>System.currentTimeMillis()){
                                map.put(currDate,"Yellow");
                                caldroidFragment.setBackgroundResourceForDate(R.color.Yellow,currDate);
                            }else if(currStatus.equals("1")){
                                map.put(currDate,"Green");
                                caldroidFragment.setBackgroundResourceForDate(R.color.Green,currDate);
                            }else if(currStatus.equals("0")){
                                map.put(currDate,"Red");
                                caldroidFragment.setBackgroundResourceForDate(R.color.Red,currDate);
                            }
                        }
                    }
                    caldroidFragment.refreshView();
                }else{
                    CustomErrorDialog dialog=new CustomErrorDialog();
                    dialog.listener=this;
                    dialog.dialogString="Error! No Schedule Found.";
                    dialog.show(getFragmentManager(), "");
                }
            }else{
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                if(typeOfError==1){
                    dialog.dialogString="No Internet Connection!";
                    dialog.show(getFragmentManager(), "");
                }
                else{
                    dialog.dialogString="Oops! Server Error! Please try after some time";
                    dialog.show(getFragmentManager(),"");
                }
            }
        }
        check=0;
    }
    @Override
    public void DialogEnded(String dialogString) {
        if(dialogString.equals("Schedule successfully Updated")){
            Intent mainIntent = new Intent(MyScheduleActivity.this,MyScheduleActivity.class);
            MyScheduleActivity.this.startActivity(mainIntent);
            MyScheduleActivity.this.finish();
        }else if(dialogString.equals("We are experiencing some Technical difficulties.Please contact us")){
            Intent mainIntent = new Intent(MyScheduleActivity.this,ContactUsActivity.class);
            MyScheduleActivity.this.startActivity(mainIntent);
            MyScheduleActivity.this.finish();
        }
    }

    @Override
    public void classUpdateTaskToBeDone(String status,String scheduleDateID, YogaClass cur) {
            JSONObject toBePosted=new JSONObject();
            try {
                toBePosted.put("scheduleDateId",scheduleDateID);
                toBePosted.put("scheduleStatus",status);
                toBePosted.put("phone", getSharedPreferences("profilephone", 0).getString("phone", null));
                toBePosted.put("authKey",getSharedPreferences("Authorization", 0).getString("Auth_key", null));
                if(scheduleDateID!=null)
                    toBePosted.put("date", cur.date);
                else
                    toBePosted.put("scheduleDate", cur.date);
            } catch (JSONException e) {
            }
            PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
            puttingTask.toBePosted=toBePosted;
            puttingTask.listener=this;
            check=1;
            puttingTask.execute("http://"+Constants.IP_ADDRESS+"updateClassStatus");
    }
}
