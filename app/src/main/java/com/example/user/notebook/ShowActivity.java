package com.example.user.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShowActivity extends AppCompatActivity {
    TextView time_view, content_view;
    Bundle bundle;
    private DbAdapter dbAdapter;
    int cursor_id, index;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        bundle = this.getIntent().getExtras();
        cursor_id = bundle.getInt("item_id");//取得Intent參數, 此處取得的是id值
        dbAdapter = new DbAdapter(this); //設定dbAdapter
        Cursor cursor = dbAdapter.queryById(cursor_id);//透過dbAdapter的queryById方法取得資料
        index = cursor.getInt(0);//將資料顯示在畫面上
        time_view.setText(cursor.getString(1));
        content_view.setText(cursor.getString(2));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("item_id", index);
                i.putExtra("type", "edit")
                        .setClass(ShowActivity.this, EditActivity.class);
                startActivity(i);
            }
        });
    }
    private void initView() {
        time_view = findViewById(R.id.time_view);
        content_view = findViewById(R.id.content_view);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.del_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_del://刪除便條紙
                dialog = null;
                builder = null;
                builder = new AlertDialog.Builder(this);
                builder.setTitle("訊息")
                        .setMessage("確定刪除此筆資料嗎? 刪除後無法恢復!")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {//設定確定按鈕
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Boolean isDeleted = dbAdapter.deleteMemo(index);
                                if (isDeleted) {
                                    Toast.makeText(ShowActivity.this, "已刪除!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//設定取消按鈕
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}