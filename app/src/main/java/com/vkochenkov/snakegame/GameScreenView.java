package com.vkochenkov.snakegame;

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
    private int boardSize;
    private int viewSize;

    public void setSnake(List<Rect> snake) {
        this.snake = snake;
    }

    public GameScreenView(Context context, List<Rect> snake, int rectSize, int boardSize, int multiple) {
        this(context);
        this.snake = snake;
        this.boardSize = boardSize;
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
        drawTable(canvas);
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
        paint.setStrokeWidth(1);
        Rect rect = new Rect(0, 0, viewSize, viewSize);
        canvas.drawRect(rect, paint);
    }

    private void drawTable(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        for (int i = 0; i < multiple; i++) {
            canvas.drawLine(0, (rectSize + boardSize) * i, viewSize, (rectSize + boardSize) * i, paint);
            canvas.drawLine((rectSize + boardSize) * i, 0, (rectSize + boardSize) * i, viewSize, paint);
        }
    }
}
