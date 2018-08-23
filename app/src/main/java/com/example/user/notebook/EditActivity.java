package com.example.user.notebook;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtTitle;
    EditText edt_time, edt_memo;
    Button btn_ok, btn_back;
    Spinner sp_color;
    String new_time, new_memo, selected_color;
    Bundle bundle;
    String[] colors;
    SpinnerAdapter spinnerAdapter;
    int index;
    private int mYear, mMonth, mDay;
    private DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();
        dbAdapter = new DbAdapter(this);
        bundle = this.getIntent().getExtras();
        //判斷目前是否為編輯狀態
        if (bundle.getString("type").equals("edit")) {
            txtTitle.setText("編輯便條");
            index = bundle.getInt("item_id");
            Cursor cursor = dbAdapter.queryById(index);
            edt_memo.setText(cursor.getString(2));
        }
    }
    private void initView() {
        txtTitle = findViewById(R.id.txtTitle);
        edt_time = findViewById(R.id.edtTime);
        edt_time.setOnClickListener(this);
        edt_memo = findViewById(R.id.edtMemo);
        edt_memo.setOnClickListener(this);
        sp_color = findViewById(R.id.sp_colors);
        colors = getResources().getStringArray(R.array.colors);
        Log.i("color=", String.valueOf(colors));
        LinearLayout container = new LinearLayout(this);
        final ArrayList<ItemData> color_list = new ArrayList<ItemData>();
        color_list.add(new ItemData("紅色", "#f8bab7"));
        color_list.add(new ItemData("綠色", "#c6ffc6"));
        color_list.add(new ItemData("紫色", "#c992ff"));
        color_list.add(new ItemData("藍色", "#b2d5fb"));
        color_list.add(new ItemData("黃色", "#fff6bc"));
        color_list.add(new ItemData("粉色", "#f5d4f5"));
        color_list.add(new ItemData("橘色", "#f7dab1"));
        spinnerAdapter = new SpinnerAdapter(this, color_list);
        sp_color.setAdapter(spinnerAdapter);
        sp_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView img = ((view.findViewById(R.id.ticket)));
                ColorDrawable drawable = (ColorDrawable) img.getBackground();
                selected_color = Integer.toHexString(drawable.getColor()).substring(2);
                Log.i("selected_color=", selected_color);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtTime:
                if (bundle.getString("type").equals("add")) edt_time.setText("");
                //取得今天日期
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //將選定日期設定至edt_time
                        edt_time.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.edtMemo:
                if (bundle.getString("type").equals("add")) edt_memo.setText("");
                break;
            case R.id.btn_ok:
                //取得edit資料
                new_time = edt_time.getText().toString();
                Log.i("time=", new_time);
                new_memo = edt_memo.getText().toString();
                Log.i("memo=", new_memo);
                if (bundle.getString("type").equals("edit")) {
                    try {
                        //更新資料庫中的資料
                        dbAdapter.updateMemo(index, new_time, new_memo, null, selected_color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //回到MainActivity,若是要去ShowActivity則需要給它個值
                        Intent i = new Intent(this, MainActivity.class);
                        //i.putExtra("item_id",index);
                        startActivity(i);
                    }
                } else {
                    //測試是否有被log出來
                    Log.i("new_time=", new_time);
                    Log.i("new_memo=", new_memo);
                    Log.i("selected_color=", selected_color);
                    try {
                        //呼叫adapter的方法處理新增
                        dbAdapter.createMemo(new_time, new_memo, null, selected_color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //回到列表
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                    }
                }
                break;
            case R.id.btn_back:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }
    }
}