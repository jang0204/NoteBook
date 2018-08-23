package com.example.user.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DbAdapter dbAdapter;
    TextView no_memo;
    ListView list_memos;
    private Intent intent;
    private ListAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter = new DbAdapter(this);
        Log.i("dbCount=",String.valueOf(dbAdapter.listMemos().getCount()));
        no_memo = findViewById(R.id.no_memo);
        list_memos = findViewById(R.id.list_memos);
        //判斷目前是否有聯絡人資料並設定顯示元件，如果是0，就顯示「目前無資料」
        if(dbAdapter.listMemos().getCount() == 0){
            list_memos.setVisibility(View.INVISIBLE);
            no_memo.setVisibility(View.VISIBLE);
        }else{
            list_memos.setVisibility(View.VISIBLE);
            no_memo.setVisibility(View.INVISIBLE);
        }
        displayList();
    }
    private void displayList(){
        Cursor cursor = dbAdapter.listMemos();
        dataAdapter = new ListAdapter(this, cursor);
        list_memos.setAdapter(dataAdapter);
        list_memos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor current_cursor = (Cursor) list_memos.getItemAtPosition(position);
                int item_id = current_cursor.getInt(current_cursor.getColumnIndexOrThrow("_id"));
                intent = new Intent();
                intent.putExtra("item_id", item_id);
                intent.setClass(MainActivity.this, ShowActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//menu的元件置入到這裡
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//跳轉新增畫面 Edit_Activity
        switch (item.getItemId()){
            case R.id.action_add:
                Intent i = new Intent(this, EditActivity.class);
                i.putExtra("type","add");
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("離開視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.mipmap.img)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());//關閉應用程式
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
        return true;
    }
}
