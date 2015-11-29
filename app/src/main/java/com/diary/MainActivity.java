package com.diary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView tv_date;
    private Button btn_save;
    private EditText et_contents;
    private View dialogView;

    private int year,month, day;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_date = (TextView) findViewById(R.id.textView_date);
        btn_save = (Button) findViewById(R.id.button_save);
        et_contents = (EditText) findViewById(R.id.editText_contents);
        et_contents.setTextSize(16);
        ClickListener listener = new ClickListener();
        tv_date.setOnClickListener(listener);
        btn_save.setOnClickListener(listener);

        File dir =  getExternalFilesDir("mydiary") ;
        path = dir.getPath();

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        changeDate(year,month,day);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.item_reroad: changeDiary(year,month,day); return true;
            case R.id.item_delete:AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                String fileName = year + "_" + month + "_" + day + ".txt";
                final File diary = new File(path, fileName);
                if(!diary.exists()) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.noDiary), Toast.LENGTH_SHORT).show();
                    return true;
                }
                dialog.setTitle(year + "년 " + month + "월 " + day + "일 일기를 삭제하시겠습니까?");
                dialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        diary.delete();
                        changeDiary(year, month, day);
                    }
                });
                dialog.setNegativeButton(getResources().getString(R.string.cancle), null);
                dialog.show();
                return true;
            case R.id.large: et_contents.setTextSize(22);return true;
            case R.id.medium: et_contents.setTextSize(16);return true;
            case R.id.small: et_contents.setTextSize(10);return true;
        }
        return false;
    }

    public void changeDate(int year, int month, int day) {
        tv_date.setText(year+"년 "+month+"월 "+day+"일");
        changeDiary(year,month,day);
    }
    public void changeDiary(int year,int month, int day) {
        File diary = new File(path,year+"_"+month+"_"+day+".txt");
        try {
            FileInputStream fi = new FileInputStream(diary);
            byte[] txt = new byte[500];
            String contents = null;
            fi.read(txt);
            fi.close();
            contents = (new String(txt)).trim();
            et_contents.setText(contents);
        }catch(FileNotFoundException e) {
            et_contents.setText("");
            et_contents.setHint(getResources().getString(R.string.emptyContents));
        }catch(IOException e) {
            Toast.makeText(getApplicationContext(), "IOException!", Toast.LENGTH_SHORT).show();
        }
    }

    class ClickListener implements View.OnClickListener {
        DatePicker dp;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textView_date: dialogView = (View)View.inflate(MainActivity.this,R.layout.datepicker_dialog,null);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(getResources().getString(R.string.dialogTitle));
                    dialog.setView(dialogView);

                    dp = (DatePicker)dialogView.findViewById(R.id.datePicker);
                    dp.init(year,month-1,day,null);

                    dialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            year = dp.getYear();
                            month = dp.getMonth()+1;
                            day = dp.getDayOfMonth();

                            changeDate(year,month,day);
                        }
                    });
                    dialog.setNegativeButton(getResources().getString(R.string.cancle), null);
                    dialog.show();
                    break;
                case R.id.button_save:try{
                    String fileName = year+"_"+month+"_"+day;
                    FileOutputStream fo = new FileOutputStream(path+"/"+fileName+".txt");
                    String str = et_contents.getText().toString();
                    fo.write(str.getBytes());
                    fo.close();;
                    Toast.makeText(getApplicationContext(), fileName+".txt" + " 이 저장됨", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Toast.makeText(getApplicationContext(), "IOException!", Toast.LENGTH_SHORT).show();
                }break;
            }
        }
    }
}
