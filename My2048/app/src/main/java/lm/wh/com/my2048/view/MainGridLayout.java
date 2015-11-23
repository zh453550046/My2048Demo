package lm.wh.com.my2048.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lm.wh.com.my2048.util.DisplayUtil;

/**
 * Created by Administrator on 2015/11/18.
 */
public class MainGridLayout extends GridLayout {

    private List<ItemLayout> list;

    private int count, length, maxScore, playerScore, times;

    private final int SPACE = 4;

    private Float downX, dowY, upX, upY;

    private Game2048Listener listener;

    private final int COUNT = 16;

    public MainGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (listener != null) {
            listener.onGameStart();
        } else {
            defaultStartGame(context);
        }
    }

    public void defaultStartGame(Context context) {
        maxScore = 2;
        setCount(context, COUNT);
        int number = 3;
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            do {
                ItemLayout itemLayout = ((list.get(Math.abs(random.nextInt()) % count)));
                if (itemLayout.getScore() == 0) {
                    itemLayout.setScore(2);
                    break;
                }
            } while (true);
        }
    }

    public void restartGame(Context context) {
        init(context);
    }

    /**
     * if gameListener did not initialize game, must call defaultStartGame() on onGameStart().
     *
     * @param gameListener
     */
    public void setGameListener(Game2048Listener gameListener) {
        listener = gameListener;
    }

    /**
     * call first.
     *
     * @param count
     */
    public void setCount(Context context, int count) {
        this.count = count;
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        for (int i = 0; i < count; i++) {
            list.add(new ItemLayout(context));
        }
    }

    /**
     * call after setCount().
     *
     * @param context
     * @param totalWidth
     * @param toatalHeight
     */
    public void setItemWidth(Context context, int totalWidth, int toatalHeight) {
        length = (int) Math.sqrt(count);
        setRowCount(length);
        setColumnCount(length);
        if (list == null) {
            list = new ArrayList<>();
        }
        for (int i = 1; i <= count; i++) {
            ItemLayout itemLayout = list.get(i - 1);
            if (itemLayout != null) {
                itemLayout.setWidth((totalWidth - (length - 1) * DisplayUtil.dip2px(context, SPACE)) / length);
                itemLayout.setHeight((toatalHeight - (length - 1) * DisplayUtil.dip2px(context, SPACE)) / length);
                addView(itemLayout);
                GridLayout.LayoutParams layoutParams = (LayoutParams) getChildAt(i - 1).getLayoutParams();
                if (i % length != 0) {
                    if (i <= (length - 1) * length) {
                        layoutParams.setMargins(0, 0, DisplayUtil.dip2px(context, SPACE), DisplayUtil.dip2px(context, SPACE));
                    } else {
                        layoutParams.setMargins(0, 0, DisplayUtil.dip2px(context, SPACE), 0);
                    }
                } else {
                    if (i <= (length - 1) * length) {
                        layoutParams.setMargins(0, 0, 0, DisplayUtil.dip2px(context, SPACE));
                    }
                }
                getChildAt(i - 1).setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                dowY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                times++;
                upX = ev.getX();
                upY = ev.getY();
                int orientation = Math.abs(upX - downX) > Math.abs(upY - dowY) ? 1 : 0;//1 for horizontal, 0 for vertical
                switch (orientation) {
                    case 1:
                        if (Math.abs(upX - downX) > 20) {
                            if (upX - downX > 0) {
                                doMove(1);
                            } else {
                                doMove(2);
                            }
                        }
                        break;
                    case 0:
                        if (Math.abs(upY - dowY) > 20) {
                            if (upY - dowY < 0) {
                                doMove(3);
                            } else {
                                doMove(4);
                            }
                        }
                        break;
                    default:
                        break;
                }
        }
        return true;
    }

    /**
     * @param i 1:rght 2:left 3:top 4:bottom
     */
    private void doMove(int i) {
        boolean shoulAddItem = false, canAdd = false;
        int number = 0;
        switch (i) {
            case 1:
                List<Integer> right = new ArrayList<>();
                for (int j = 1; j <= length; j++) {
                    right.clear();
                    for (int k = 1; k <= length; k++) {
                        int score = ((ItemLayout) getChildAt(k + (j - 1) * length - 1)).getScore();
                        if (score > 0) {
                            if (score > maxScore) {
                                maxScore = score;
                            }
                            right.add(score);
                        } else {
                            if (number < 2) {
                                number++;
                            }
                            if (!canAdd) {
                                canAdd = true;
                            }
                        }
                    }
                    if (right.size() > 0) {
                        if (right.size() == 1) {
                            for (int k = 1; k <= length; k++) {
                                ItemLayout itemLayout = ((ItemLayout) getChildAt(k + (j - 1) * length - 1));
                                if (k == length) {
                                    if (itemLayout.getScore() == 0) {
                                        itemLayout.setScore(right.get(0));
                                    }
                                } else {
                                    if (itemLayout.getScore() != 0) {
                                        itemLayout.setScore(0);
                                    }
                                }
                            }
                        } else {
                            for (int l = right.size() - 1; l > 0; l--) {
                                if (right.get(l - 1).equals(right.get(l))) {
                                    shoulAddItem = true;
                                    playerScore += right.get(l);
                                    right.set(l, right.get(l) * 2);
                                    right.set(l - 1, 0);
                                }
                            }
                            if (shoulAddItem && listener != null) {
                                listener.onGameScore(playerScore);
                            }
                            for (int k = length; k >= 1; k--) {
                                ItemLayout itemLayout = ((ItemLayout) getChildAt(k + (j - 1) * length - 1));
                                if (right.size() > 0) {
                                    if (right.get(0) == 0) {
                                        right.remove(0);
                                    }
                                    if (right.size() > 0) {
                                        itemLayout.setScore(right.get(right.size() - 1));
                                        right.remove(right.size() - 1);
                                    } else {
                                        itemLayout.setScore(0);
                                    }
                                } else {
                                    itemLayout.setScore(0);
                                }
                            }
                        }
                    }
                }
                break;
            case 2:
                List<Integer> left = new ArrayList<>();
                for (int j = 1; j <= length; j++) {
                    left.clear();
                    for (int k = 1; k <= length; k++) {
                        int score = ((ItemLayout) getChildAt(k + (j - 1) * length - 1)).getScore();
                        if (score > 0) {
                            if (score > maxScore) {
                                maxScore = score;
                            }
                            left.add(score);
                        } else {
                            if (number < 2) {
                                number++;
                            }
                            if (!canAdd) {
                                canAdd = true;
                            }
                        }
                    }

                    if (left.size() > 0) {
                        if (left.size() == 1) {
                            for (int k = 1; k <= length; k++) {
                                ItemLayout itemLayout = ((ItemLayout) getChildAt(k + (j - 1) * length - 1));
                                if (k == 1) {
                                    if (itemLayout.getScore() == 0) {
                                        itemLayout.setScore(left.get(0));
                                    }
                                } else {
                                    if (itemLayout.getScore() != 0) {
                                        itemLayout.setScore(0);
                                    }
                                }
                            }
                        } else {
                            for (int l = 1; l < left.size(); l++) {
                                if (left.get(l - 1).equals(left.get(l))) {
                                    shoulAddItem = true;
                                    playerScore += left.get(l - 1);
                                    left.set(l - 1, left.get(l - 1) * 2);
                                    left.set(l, 0);
                                }
                            }
                            if (shoulAddItem && listener != null) {
                                listener.onGameScore(playerScore);
                            }
                            for (int k = 1; k <= length; k++) {
                                ItemLayout itemLayout = ((ItemLayout) getChildAt(k + (j - 1) * length - 1));
                                if (left.size() > 0) {
                                    if (left.get(0) == 0) {
                                        left.remove(0);
                                    }
                                    if (left.size() > 0) {
                                        itemLayout.setScore(left.get(0));
                                        left.remove(0);
                                    } else {
                                        itemLayout.setScore(0);
                                    }
                                } else {
                                    itemLayout.setScore(0);
                                }
                            }
                        }
                    }
                }
                break;
            case 3:
                List<Integer> top = new ArrayList<>();
                for (int j = 1; j <= length; j++) {
                    top.clear();
                    for (int k = 1; k <= count; k++) {
                        if (k % length == j % length) {
                            int score = ((ItemLayout) getChildAt(k - 1)).getScore();
                            if (score > 0) {
                                if (score > maxScore) {
                                    maxScore = score;
                                }
                                top.add(score);
                            } else {
                                if (number < 2) {
                                    number++;
                                }
                                if (!canAdd) {
                                    canAdd = true;
                                }
                            }
                        }
                    }
                    if (top.size() > 0) {
                        if (top.size() == 1) {
                            ((ItemLayout) getChildAt(j - 1)).setScore(top.get(0));
                            for (int k = length + j; k <= count; k++) {
                                if (k % length == j % length) {
                                    ItemLayout itemLayout = ((ItemLayout) getChildAt(k - 1));
                                    if (itemLayout.getScore() != 0) {
                                        itemLayout.setScore(0);
                                    }
                                }
                            }
                        } else {
                            for (int l = 1; l < top.size(); l++) {
                                if (top.get(l - 1).equals(top.get(l))) {
                                    shoulAddItem = true;
                                    playerScore += top.get(l - 1);
                                    top.set(l - 1, top.get(l - 1) * 2);
                                    top.set(l, 0);
                                }
                            }
                            if (shoulAddItem && listener != null) {
                                listener.onGameScore(playerScore);
                            }
                            for (int k = 1; k <= count; k++) {
                                ItemLayout itemLayout = (ItemLayout) getChildAt(k - 1);
                                if (k % length == j % length) {
                                    if (top.size() > 0) {
                                        if (top.get(0) == 0) {
                                            top.remove(0);
                                        }
                                        if (top.size() > 0) {
                                            itemLayout.setScore(top.get(0));
                                            top.remove(0);
                                        } else {
                                            itemLayout.setScore(0);
                                        }
                                    } else {
                                        itemLayout.setScore(0);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 4:
                List<Integer> bottom = new ArrayList<>();
                for (int j = 1; j <= length; j++) {
                    bottom.clear();
                    for (int k = 1; k <= count; k++) {
                        if (k % length == j % length) {
                            int score = ((ItemLayout) getChildAt(k - 1)).getScore();
                            if (score > 0) {
                                if (score > maxScore) {
                                    maxScore = score;
                                }
                                bottom.add(score);
                            } else {
                                if (number < 2) {
                                    number++;
                                }
                                if (!canAdd) {
                                    canAdd = true;
                                }
                            }
                        }
                    }
                    if (bottom.size() > 0) {
                        if (bottom.size() == 1) {
                            ((ItemLayout) getChildAt(count - 1 - (length - j))).setScore(bottom.get(0));
                            for (int k = count - length - (length - j); k >= 1; k--) {
                                if (k % length == j % length) {
                                    ItemLayout itemLayout = ((ItemLayout) getChildAt(k - 1));
                                    if (itemLayout.getScore() != 0) {
                                        itemLayout.setScore(0);
                                    }
                                }
                            }
                        } else {
                            for (int l = bottom.size() - 1; l >= 1; l--) {
                                if (bottom.get(l - 1).equals(bottom.get(l))) {
                                    shoulAddItem = true;
                                    playerScore += bottom.get(l - 1);
                                    bottom.set(l, bottom.get(l - 1) * 2);
                                    bottom.set(l - 1, 0);
                                }
                            }
                            if (shoulAddItem && listener != null) {
                                listener.onGameScore(playerScore);
                            }
                            for (int k = count - (length - j); k >= 1; k--) {
                                ItemLayout itemLayout = (ItemLayout) getChildAt(k - 1);
                                if (k % length == j % length) {
                                    if (bottom.size() > 0) {
                                        if (bottom.get(0) == 0) {
                                            bottom.remove(0);
                                        }
                                        if (bottom.size() > 0) {
                                            itemLayout.setScore(bottom.get(bottom.size() - 1));
                                            bottom.remove(bottom.size() - 1);
                                        } else {
                                            itemLayout.setScore(0);
                                        }
                                    } else {
                                        itemLayout.setScore(0);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }

        if (!canAdd && !shoulAddItem) {
            if (listener != null) {
                listener.onGameOver(playerScore);
            }
        } else {
            if (number >= 2) {
                addItem(shoulAddItem);
            } else {
                addItem(shoulAddItem, 1);
            }
        }
    }

    private void addItem(boolean shoulAddItem) {
        if (shoulAddItem || times >= 2) {
            Random random = new Random();
            int number;
            number = 1;
            for (int i = 0; i < number; i++) {
                do {
                    ItemLayout itemLayout = (ItemLayout) getChildAt(Math.abs(random.nextInt()) % count);
                    if (itemLayout.getScore() == 0) {
                        Random rd = new Random();
                        int newScore = 0;
                        switch (rd.nextInt(4)) {
                            case 0:
                                newScore = 2;
                                break;
                            case 1:
                                newScore = 4;
                                break;
                            case 2:
                                newScore = 2;
                                break;
                            case 3:
                                newScore = maxScore / 2 > 1 ? maxScore / 2 : 2;
                                break;
                            default:
                                break;
                        }
                        itemLayout.setScore(newScore);
                        break;
                    }
                } while (true);
            }
            times = 0;
        }
    }

    private void addItem(boolean shoulAddItem, int number) {
        if (shoulAddItem || times >= 2) {
            Random random = new Random();
            for (int i = 0; i < number; i++) {
                do {
                    ItemLayout itemLayout = (ItemLayout) getChildAt(Math.abs(random.nextInt()) % count);
                    if (itemLayout.getScore() == 0) {
                        Random rd = new Random();
                        int newScore = 0;
                        switch (rd.nextInt(4)) {
                            case 0:
                                newScore = 2;
                                break;
                            case 1:
                                newScore = 4;
                                break;
                            case 2:
                                newScore = 2;
                                break;
                            case 3:
                                newScore = maxScore / 2 > 1 ? maxScore / 2 : 2;
                                break;
                            default:
                                break;
                        }
                        itemLayout.setScore(newScore);
                        break;
                    }
                } while (true);
            }
            times = 0;
        }
    }

    public void clearAll() {
        for (int i = 0; i < count; i++) {
            ((ItemLayout) getChildAt(i)).setScore(0);
        }
    }


    public interface Game2048Listener {
        void onGameStart();

        void onGameScore(int score);

        void onGameOver(int playScore);
    }
}
