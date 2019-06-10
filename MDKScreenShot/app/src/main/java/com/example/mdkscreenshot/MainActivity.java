package com.example.mdkscreenshot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mdkscreenshot.db.HttpOrmHelper;
import com.example.mdkscreenshot.db.InformationTable;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_1)
    Button btn1;
    @BindView(R.id.btn_2)
    Button btn2;
    @BindView(R.id.btn_3)
    Button btn3;
    @BindView(R.id.btn_4)
    Button btn4;
    @BindView(R.id.btn_5)
    Button btn5;
    @BindView(R.id.btn_6)
    Button btn6;
    @BindView(R.id.show_view)
    LinearLayout showView;
    private HttpOrmHelper httpOrmHelper;
    private Dao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        httpOrmHelper = HttpOrmHelper.getOrmliteHelper(getApplicationContext());
        try {
            dao = httpOrmHelper.getDao(InformationTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }



    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                InformationTable infor = new InformationTable(1,"04/29 11:25");
                try {
                    dao.create(infor);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d("MainActivity", "onClick: 异常了....");
                }
                Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_2:
                break;
            case R.id.btn_3:
                break;
            case R.id.btn_4:
                break;
            case R.id.btn_5:
                break;
            case R.id.btn_6:
                break;
        }
    }
}
