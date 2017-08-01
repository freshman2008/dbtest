package com.example.gd001.dbtest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private DBAdapter adapter;
    private ListView listView;
    private List<Person> personList = new ArrayList<Person>();
    private Button insertBtn;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        insertBtn = (Button) findViewById(R.id.btn_insert);
        listView = (ListView)findViewById(R.id.person_list);

        //实例化DBHelper
        dbHelper = new DBHelper(this);
        personList = queryData();
        //实例化DbAdapter
        adapter = new DBAdapter(getApplication(), personList);
        listView.setAdapter(adapter);

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //实例化一个ContentValues， ContentValues是以键值对的形式，键是数据库的列名，值是要插入的值
                ContentValues values = new ContentValues();
                values.put("name", "Edward");
                values.put("age", 30);
                values.put("sex", "mail");

                //调用insert插入数据库
                dbHelper.insert(values);
                updateUI();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Person p = personList.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("真的要删除该记录？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //调用delete（）删除某条数据
                        dbHelper.delete(p.get_id());
                        updateUI();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();


                // 关闭数据库
                dbHelper.close();
            }
        });
    }

    //查询数据库，将每一行的数据封装成一个person 对象，然后将对象添加到List中
    private List<Person> queryData() {
        personList.clear();
        dbHelper = new DBHelper(this);

        //调用query()获取Cursor
        Cursor c = dbHelper.query();
        while (c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            int age = c.getInt(c.getColumnIndex("age"));
            String sex = c.getString(c.getColumnIndex("sex"));

            //用一个Person对象来封装查询出来的数据
            Person p = new Person();
            p.set_id(_id);
            p.setName(name);
            p.setAge(age);
            p.setSex(sex);

            personList.add(p);
        }
        return personList;
    }

    public void updateUI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //重新刷新适配器
                adapter.refresh(queryData());
            }
        });
    }
}
