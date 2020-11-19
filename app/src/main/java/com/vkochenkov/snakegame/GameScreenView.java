package com.vkochenkov.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class GameScreenView extends View {

    private List<Rect> snake;

    public void setSnake(List<Rect> snake) {
        this.snake = snake;
    }

    public GameScreenView(Context context, List<Rect> snake) {
        this(context);
        this.snake = snake;
    }

    public GameScreenView(Context context) {
        super(context);
    }

    public GameScreenView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSnake(canvas);
    }

    private void drawSnake(Canvas canvas) {
        drawFrame(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        for (int i=0; i<snake.size(); i++) {
            canvas.drawRect(snake.get(i), paint);
        }
    }

    private void drawFrame(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1);
        Rect rect = new Rect(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
        canvas.drawRect(rect, paint);
    }

    //todo drowTable(Canvas canvas) {}
    //нариисовать таблицу в цикле, для удобства
}
