package com.example.iaccountcheck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DBClass DB;
    private ListView listView;
    private ImageButton Add;
    private List<costList> list;
    private ListAdapter mAdapter;
    int gg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toastDisplay();
        Log.i(TAG,"WHITCH=!!!!!!!!!!!!!!!!!!!");
        initView();
        initData();

    }
    private void toastDisplay(){
        // 首次使用显示提示控制
        SharedPreferences preferences1 = getSharedPreferences("count2", 0); // 存在则打开它，否则创建新的Preferences
        //int count2 = preferences1.getInt("count2", 0); // 取出数据
        gg = 0;
        int count2 = gg;
        Log.i(TAG,"WHITCH=!!!!!!!!!!!!!!!!!!!"+count2);
        if (count2 == 0) { // 判断程序与第几次运行，如果是第一次运行则跳转到引导页面
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Log.i(TAG,"WHITCH=!!!!!!!!!!!!!!!!!!!");
            toastInit();
            SharedPreferences.Editor editor = preferences1.edit(); // 让preferences处于编辑状态
            //gg = 1;
            editor.putInt("count2", 1); // 存入数据
            editor.commit(); // 提交修改
        }
    }
    private void toastInit(){
        // 初始使用显示toast提示设置手势
        Toast toast = Toast.makeText(this,
                "首次启动，请登录/注册", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setMargin(0f, 0.1f);
        toast.show();
    }

    private void initData() {

        list=new ArrayList<>();
        SQLiteDatabase db=DB.getReadableDatabase();
        final Cursor cursor=db.query("account",null,null,null,null,
                null,null);
        while (cursor.moveToNext()){
            costList clist=new costList();//构造实例
            clist.set_id(cursor.getString(cursor.getColumnIndex("_id")));
            clist.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
            clist.setDate(cursor.getString(cursor.getColumnIndex("Date")));
            clist.setMoney(cursor.getString(cursor.getColumnIndex("Money")));
            list.add(clist);
        }
        //绑定适配器
        mAdapter = new ListAdapter(this,list);
        listView.setAdapter(mAdapter);
        //listView.setAdapter(new ListAdapter(this,list));
        //长按删除功能
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除？");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int whitch){
                        //Log.i(TAG,"WHITCH="+i);
                        int index = 0;
                        index = i;
                        String deleteText = list.get(index).getTitle().toString();
                        SQLiteDatabase db = DB.getReadableDatabase();
                        list.remove(i);
                        Cursor cursor1 = db.query("account",null,null,null,null,null,null);
                        while(cursor1.moveToNext()){
                              String Title = cursor1.getString(cursor1.getColumnIndex("Title"));
                              if (deleteText.equals(Title)){
                                  deleteData(cursor1.getInt(0));
                                  Log.i(TAG,"WHITCH=!!!!!!!!!!!!!!!!!!!");
                                  break;
                              }
                              db.close();
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(),"该条记录已被删除",Toast.LENGTH_SHORT).show();

                    }
                }
                    private void deleteData(int index) {
                        SQLiteDatabase db = DB.getReadableDatabase();
                        // 从表中删除指定的一条数据
                        db.execSQL("DELETE FROM " + "account" + " WHERE _id="
                                + Integer.toString(index));
                        db.close();
                    }});
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whitch){
                    }
                });
                builder.create().show();
                return false;
            }
        });
        db.close();

    }

    private void initView() {
        DB=new DBClass(MainActivity.this);
        listView = findViewById(R.id.list_view);
        Add=findViewById(R.id.add);
    }

    public void addAccount(View view){//跳转
        Intent intent=new Intent(MainActivity.this,new_cost.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==1)
        {
            this.initData();
        }
    }

    //点击两次返回即可退出程序
    private long mExitTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                            Object mHelperUtils;
                            Toast.makeText(this, "再按一次退出APP", Toast.LENGTH_SHORT).show();
                            mExitTime = System.currentTimeMillis();
                        } else {
                            finish();
                        }
                    return true;
                }
            return super.onKeyDown(keyCode, event);
        }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);//加载menu文件到布局
        return true;
    }
    //菜单点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除？");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int whitch){
                        SQLiteDatabase db = DB.getReadableDatabase();
                        //全部删除
                        db.delete("account",null,null);
                        db.close();
                        list.clear();
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(),"全部记录已被删除",Toast.LENGTH_SHORT).show();
                    }});
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whitch){
                    }
                });
                builder.create().show();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
}