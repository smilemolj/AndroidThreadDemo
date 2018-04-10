package com.smilemolj.androidthreaddemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TimerTask;
import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {
    private Button button;
    private EditText input;
    private Button countBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.defaultThreadFactory().newThread(new Runnable() {
                    @Override
                    public void run() {

//                        MainActivity2.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                button.setText("在线程中更新成功");
//                            }
//                        });

//                        button.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                button.setText("在线程中更新成功");
//                            }
//                        });

                        button.postDelayed(new Runnable() {
                            long startTime = System.currentTimeMillis();

                            @Override
                            public void run() {
                                button.setText("在线程中更新成功" + (System.currentTimeMillis() - startTime));
                            }
                        }, 1000);

                    }
                }).start();
            }
        });
        input = findViewById(R.id.editText);
        countBtn = findViewById(R.id.button2);
        countBtn.setOnClickListener(new View.OnClickListener() {
            TimeCountTask timeCountTask;

            @Override
            public void onClick(View v) {
                String inputText = input.getText().toString();

                if (countBtn.getTag()==null){
                    timeCountTask = new TimeCountTask();
                    if (!TextUtils.isEmpty(inputText)) {
                        timeCountTask.execute(Long.valueOf(inputText));
                    } else {
                        timeCountTask.execute();
                    }
                }else {
                    timeCountTask.cancel(true);
                }

            }
        });
    }

    class TimeCountTask extends AsyncTask<Object, String, String> {
        TextView showText;

        @Override
        protected void onPreExecute() {
            showText = findViewById(R.id.textView);
            input.setEnabled(false);
            countBtn.setTag(0);
            countBtn.setText("取消");
        }

        @Override
        protected String doInBackground(Object... objects) {
            if (objects.length == 1 && objects[0] instanceof Long) {
                long inputTime = (long) objects[0];
                for (; inputTime > 0; inputTime--) {
                    publishProgress("剩余：" + inputTime + "秒");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            } else {
                MainActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity2.this, "参数错误", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            showText.setText(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            showText.setText("计时已完成");
            input.setEnabled(true);
            countBtn.setTag(null);
            countBtn.setText("计时");
        }

        @Override
        protected void onCancelled() {
            showText.setText("计时已取消");
            input.setEnabled(true);
            countBtn.setTag(null);
            countBtn.setText("计时");
        }
    }
}
