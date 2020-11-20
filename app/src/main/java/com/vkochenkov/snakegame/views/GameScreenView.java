package com.vkochenkov.snakegame.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.List;

public class GameScreenView extends View {

    private List<Rect> snake;
    private int multiple;
    private int rectSize;
    private int viewSize;

    public void setSnake(List<Rect> snake) {
        this.snake = snake;
    }

    public GameScreenView(Context context, List<Rect> snake, int rectSize, int multiple) {
        this(context);
        this.snake = snake;
        this.rectSize = rectSize;
        this.multiple = multiple;
    }

    public GameScreenView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewSize == 0) {
            viewSize = this.getMeasuredWidth();
        }

        drawFrame(canvas);
        drawSnake(canvas);
    }

    private void drawSnake(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1);
        for (int i = 0; i < snake.size(); i++) {
            canvas.drawRect(snake.get(i), paint);
        }
    }

    private void drawFrame(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        Rect rect = new Rect(0, 0, viewSize, viewSize);
        canvas.drawRect(rect, paint);
    }
}
