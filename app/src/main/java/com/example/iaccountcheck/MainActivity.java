package com.example.iaccountcheck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBClass DB;
    private ListView listView;
    private ImageButton Add;
    private List<costList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        list=new ArrayList<>();
        SQLiteDatabase db=DB.getReadableDatabase();
        Cursor cursor=db.query("account",null,null,null,null,
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
        listView.setAdapter(new ListAdapter(this,list));
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
}