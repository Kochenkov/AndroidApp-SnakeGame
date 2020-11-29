package com.vkochenkov.snakegame.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.List;

public class GameScreenView extends View {

    private static final int SNAKE_COLOR = Color.GREEN;
    private static final int FOOD_COLOR = Color.RED;
    private static final int BOARD_COLOR = Color.DKGRAY;

    private List<Rect> snake;
    private Rect food;

    private int multiple;
    private int rectSize;
    private int viewSize;

    public GameScreenView(Context context) {
        super(context);
    }

    public GameScreenView(Context context, List<Rect> snake, Rect food, int rectSize, int multiple) {
        this(context);
        this.snake = snake;
        this.rectSize = rectSize;
        this.multiple = multiple;
        this.food = food;
    }

    public void setSnake(List<Rect> snake) {
        this.snake = snake;
    }

    public void setFood(Rect food) {
        this.food = food;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (viewSize == 0) {
            viewSize = this.getMeasuredWidth();
        }
        drawFrame(canvas);
        drawFood(canvas);
        drawSnake(canvas);
    }

    private void drawSnake(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(SNAKE_COLOR);
        paint.setStrokeWidth(1);
        for (int i = 0; i < snake.size(); i++) {
            canvas.drawRect(snake.get(i), paint);
        }
    }

    private void drawFrame(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(BOARD_COLOR);
        Rect rect = new Rect(0, 0, viewSize, viewSize);
        canvas.drawRect(rect, paint);
    }

    private void drawFood(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(FOOD_COLOR);
        canvas.drawRect(food, paint);
    }
}
