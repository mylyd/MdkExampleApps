package com.example.xyidentification;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.xyidentification.adapter.DrawerRecyclerViewAdapter;
import com.example.xyidentification.fragment.CardRecognitionFragment;
import com.example.xyidentification.fragment.CardWitnessFragment;
import com.example.xyidentification.fragment.SeriousContrastFragment;

import com.example.xyidentification.utils.ListItem;
import com.example.xyidentification.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.tl_custom)
    Toolbar toolbar;
    @BindView(R.id.dl_left)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.recycler_itemImg)
    ImageView recyclerItemImg;
    @BindView(R.id.rv_left_menu)
    RecyclerView rvLeftMenu;

    List<ListItem> mlist = new ArrayList<>();
    @BindView(R.id.list_setup)
    LinearLayout listSetup;
    private ActionBarDrawerToggle mDrawerToggle;
    boolean mdlopen = true;
    private DrawerRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showDrawer();
        //设置菜单列表
        setDataList();
        //recyclerItemImg.setImageResource(R.drawable.img_itembg);
        addFragment(new CardRecognitionFragment(), false);
    }

    private void setDataList() {
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem1)));
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem2)));
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem3)));
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem4)));
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem5)));
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem6)));
        mlist.add(new ListItem(R.mipmap.list_module, getString(R.string.listItem7)));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adapter = new DrawerRecyclerViewAdapter(mlist);
        rvLeftMenu.setLayoutManager(llm);
        rvLeftMenu.setAdapter(adapter);
        adapter.OnItemListener(new DrawerRecyclerViewAdapter.OnItemListener() {
            @Override
            public void onItemListener(int position) {
                switch (position) {
                    case 0:
                        addFragment(new CardRecognitionFragment(), false);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 1:
                        addFragment(new SeriousContrastFragment(), false);
                        break;
                    case 3:
                        addFragment(new CardWitnessFragment(),false);
                        break;
                  /*case 2:
                        addFragment(new SeriousContrastFragment(), false);
                        break;
                    case 3:
                        addFragment(new EndFragment(), false);
                        break;*/
                    default:
                        ToastUtils.showToast(getApplicationContext(),"没有访问权限...");
                        break;
                }
            }
        });
    }

    private void showDrawer() {
        toolbar.setTitle(getString(R.string.actionbar_string));//设置Toolbar标题
        toolbar.setTitleTextColor(getResources().getColor(R.color.actionbarTextColor)); //设置标题颜色

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mdlopen = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mdlopen = true;
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
       // mDrawerLayout.setScrimColor(Color.argb(1, 0, 0, 0));
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                View content = mDrawerLayout.getChildAt(0);
                int offset = (int) (view.getWidth() * v);
                content.setTranslationX(offset);//+ 右 ，- 左
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected: " + mdlopen);
            if (mdlopen) {
                mDrawerLayout.openDrawer(Gravity.START);
            } else {
                mDrawerLayout.closeDrawer(Gravity.START);
            }
        }
        return true;
    }

    private void addFragment(Fragment fragment, boolean backStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.home_frameLayout, fragment);
        if (backStack) {
            ft.addToBackStack("fragment");
        }
        ft.commit();
    }

    @OnClick(R.id.list_setup)
    public void onClick() {
        ToastUtils.showToast(this,"此模块正在维护中...");
    }
}
