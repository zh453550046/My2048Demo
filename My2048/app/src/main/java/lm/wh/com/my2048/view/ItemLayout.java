package lm.wh.com.my2048.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import lm.wh.com.my2048.util.DisplayUtil;

/**
 * Created by Administrator on 2015/11/18.
 */
public class ItemLayout extends FrameLayout {

    private int score, width, height;

    private Paint paint;

    private final String TAG = getClass().getSimpleName();

    public ItemLayout(Context context) {
        super(context);
        init(context);
    }

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setTextSize(DisplayUtil.sp2px(context, 22));
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStrokeWidth(3);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        int background;
        Rect targetRect = new Rect(0, 0, width, height);
        if (score == 0) {
            background = getResources().getColor(android.R.color.darker_gray);
            canvas.drawColor(background);
        } else {
            background = getResources().getColor(android.R.color.holo_orange_dark);
            canvas.drawColor(background);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(score), targetRect.centerX(), baseline, paint);
        }


        super.dispatchDraw(canvas);
    }

    public void setScore(int score) {
        if (this.score != score) {
            this.score = score;
            invalidate();
        }
    }

    public int getScore() {
        return score;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
