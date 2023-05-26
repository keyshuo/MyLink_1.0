/*package com.example.mylink_10.gameRelated;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class GameView extends View {
    private Piece selected; // 已经选中的piece
    private Game g;
    private Paint paint; // 画线的画笔
    private List<Point> path; // 两个piece连接路径上的转折点

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void start(Game g) {
        this.g = g;
        setPaint();
        this.setOnTouchListener(new OnTouchListener() {
            // 触摸事件
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    selPiece(motionEvent); // 选中
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    performClick();
                    postInvalidate(); // 刷新
                }
                return true;
            }
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void setPaint() {
        paint = new Paint();
        paint.setColor(Color.rgb(88,203,162));
        paint.setTextSize(34);
        paint.setStrokeWidth(3);
    } // 设置画笔

    private void selPiece(MotionEvent me) {
        Piece temp = g.getDst(me.getX(), me.getY()); // 根据触摸位置计算出选中哪个piece
        if(temp != null && !temp.isDel()) { // 如果能找到目的方块并选中的piece没被删掉
            if (selected == null) {
                selected = temp;
            } else {
                path = g.getAccess(selected, temp); // 判断连接是否成功
                if (path != null) {
                    selected = null;
                    postInvalidate();
                } else {
                    selected = temp;
                }
            }
        }
    }

    private void drawLine(List<Point> path, Canvas canvas) { // 根据转折点画线
        for(int i = 0; i < path.size() - 1; i++) {
            TurningPoint s = getCenter(path.get(i));
            TurningPoint e = getCenter(path.get(i + 1));
            float x1 = s.getX(), y1 = s.getY();
            float x2 = e.getX(), y2 = e.getY();
            canvas.drawLine(x1, y1, x2, y2, paint); // 调用画线的函数
        }
    }

    private TurningPoint getCenter(Point p) { // 根据board中的逻辑坐标获取实际像素中心坐标
        float x = GameConf.START_X + p.x * GameConf.PIECE_WIDTH - (float)(GameConf.PIECE_WIDTH / 2);
        float y = GameConf.START_Y + p.y * GameConf.PIECE_HEIGHT - (float)(GameConf.PIECE_HEIGHT / 2);
        return new TurningPoint(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Piece> p = g.getBoard().getPieces();
        for(Piece pi: p) { // 遍历pieces
            if(!pi.isDel()) { // 查看每个piece是否被删除，存在则画出来
                canvas.drawBitmap(pi.getPieceImage().getBm(), pi.getLuX(), pi.getLuY(), null);
            }
        }
        if(selected != null) {
            canvas.drawBitmap(ImageUtil.getCheck(), selected.getLuX(), selected.getLuY(), null);
        }
        if(path != null) {
            drawLine(path, canvas);
            path = null;
        }
    }
}*/

package com.example.mylink_10.gameRelated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mylink_10.R;

import java.util.List;

public class GameView extends View {
    private Piece selected; // 已经选中的piece
    private Game g;
    private Paint paint; // 画线的画笔
    private List<Point> path; // 两个piece连接路径上的转折点

    private boolean scoreFlag;
    private TextView sco;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void start(Game g, boolean scoreFlag) {
        this.g = g;
        this.scoreFlag = scoreFlag;
        setPaint();
        this.setOnTouchListener(new OnTouchListener() {
            // 触摸事件
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    selPiece(motionEvent); // 选中
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    performClick();
                    postInvalidate(); // 刷新
                    if(g.getBoard().getTot() == 0) { // 检查游戏是否结束
                        Intent intent = new Intent("action_win");
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        Log.i("broadcast", "send ok");
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void setPaint() {
        paint = new Paint();
        paint.setColor(Color.rgb(88,203,162));
        paint.setTextSize(34);
        paint.setStrokeWidth(3);
    } // 设置画笔

    private void selPiece(MotionEvent me) {
        Piece temp = g.getDst(me.getX(), me.getY()); // 根据触摸位置计算出选中哪个piece
        if(temp != null && !temp.isDel()) { // 如果能找到目的方块并选中的piece没被删掉
            if (selected == null) {
                selected = temp;
            } else {
                Log.i("Selected", String.format("p1=(%d,%d), p2=(%d, %d)", selected.getX(), selected.getY(), temp.getX(), temp.getY()));
                path = g.getAccess(selected, temp); // 判断连接是否成功
                if (path != null) {
                    selected.del();
                    temp.del();
                    g.getBoard().adjTot(-2);
                    if(scoreFlag == true) {
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent("action_score"));
                    }
                    selected = null;
                    postInvalidate();
                } else {
                    selected = temp;
                }
            }
        }
        if(selected != null) {
            Log.i("Selected", String.format("pre=(%d,%d)", selected.getX(), selected.getY()));
        }
    }

    private void drawLine(List<Point> path, Canvas canvas) { // 根据转折点画线
        for(int i = 0; i < path.size() - 1; i++) {
            MyPoint s = getCenter(path.get(i));
            MyPoint e = getCenter(path.get(i + 1));
            float x1 = s.getX(), y1 = s.getY();
            float x2 = e.getX(), y2 = e.getY();
            canvas.drawLine(x1, y1, x2, y2, paint); // 调用画线的函数
        }
    }

    private MyPoint getCenter(Point p) { // 根据board中的逻辑坐标获取实际像素中心坐标
        float x = GameConf.START_X + p.x * GameConf.PIECE_WIDTH - (float)(GameConf.PIECE_WIDTH / 2);
        float y = GameConf.START_Y + p.y * GameConf.PIECE_HEIGHT - (float)(GameConf.PIECE_HEIGHT / 2);
        return new MyPoint(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Piece> p = g.getBoard().getPieces();
        for(Piece pi: p) { // 遍历pieces
            if(!pi.isDel()) { // 查看每个piece是否被删除，存在则画出来
                canvas.drawBitmap(pi.getPieceImage().getBm(), pi.getLuX(), pi.getLuY(), null);
            }
        }
        if(selected != null) {
            canvas.drawBitmap(ImageUtil.getCheck(), selected.getLuX(), selected.getLuY(), null);
        }
        if(path != null) {
            drawLine(path, canvas);
            path = null;
        }
    }
}

