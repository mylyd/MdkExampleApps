package com.example.mdkfastprintingapp.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.utils.logs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 类说明：重要提示用户dialog类
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/19 17:12
 */
@SuppressLint("ValidFragment")
public class DialogHintActivity extends DialogFragment {
    private String TAG = "DialogHintActivity log";
    Unbinder unbinder;
    String tx_Hint = "";
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.dialog_true)
    Button btn_ok;


    public DialogHintActivity(String tx_Hint) {
        this.tx_Hint = tx_Hint;
    }


    //定义回掉函数
    public interface OnDialogHintText {
        void OnChooseDialog();
    }

    private OnDialogHintText onDialogHintText;

    public void setOnDialogHintText(OnDialogHintText dialogHintText) {
        this.onDialogHintText = dialogHintText;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_hint_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        unbinder = ButterKnife.bind(this, view);
        if (tx_Hint == null) {
            tvInfo.setText("提示信息错误...");
        } else {
            tvInfo.setText(tx_Hint);
        }

        if (getDialog().isShowing() && getDialog() != null) {
            getDialog().dismiss();
            logs.d(TAG, "onCreateView: dismiss");
        }else {
            logs.d(TAG, "onCreateView: no dismiss");
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.dialog_true)
    public void onClick() {
        onDialogHintText.OnChooseDialog();
        getDialog().dismiss();
    }
}
