package lm.wh.com.my2048.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import lm.wh.com.my2048.R;
import lm.wh.com.my2048.util.DisplayUtil;
import lm.wh.com.my2048.util.SPUtil;
import lm.wh.com.my2048.view.MainGridLayout;

public class ActivityMainActivity extends Activity {

    private MainGridLayout gl;
    private TextView tv_top_score, tv_score;
    private boolean isMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        tv_top_score = (TextView) findViewById(R.id.tv_top_score);
        tv_score = (TextView) findViewById(R.id.tv_score);
        gl = (MainGridLayout) findViewById(R.id.gl);
        gl.setGameListener(new MainGridLayout.Game2048Listener() {
            @Override
            public void onGameStart() {
                tv_top_score.setText("最高得分:" + SPUtil.getPlayScore(ActivityMainActivity.this) + "分");
                gl.defaultStartGame(ActivityMainActivity.this);
            }

            @Override
            public void onGameScore(int score) {
                tv_score.setText("得分:" + score + "分");
            }

            @Override
            public void onGameOver(int playScore) {
                SPUtil.savePlayScore(ActivityMainActivity.this, playScore);
                showGameOverDialog(playScore);
            }
        });
    }

    private void showGameOverDialog(int playScore) {
        final Dialog dialog = new Dialog(ActivityMainActivity.this, R.style.game_over_dialog);
        View view = LayoutInflater.from(ActivityMainActivity.this).inflate(R.layout.dialog, null);
        TextView tv_get_score = (TextView) view.findViewById(R.id.tv_get_score);
        TextView tv_start = (TextView) view.findViewById(R.id.tv_start);
        TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        tv_get_score.setText(tv_get_score.getText().toString() + playScore + "分");
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                tv_score.setText("得分:0分");
                gl.clearAll();
                gl.restartGame(ActivityMainActivity.this);
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.getWindow().setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isMeasure) {
            gl.setItemWidth(ActivityMainActivity.this, DisplayUtil.getWidth(ActivityMainActivity.this), gl.getHeight());
            isMeasure = true;
        }

    }
}