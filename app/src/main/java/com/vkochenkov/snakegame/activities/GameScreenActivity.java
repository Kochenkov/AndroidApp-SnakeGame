package com.vkochenkov.snakegame.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vkochenkov.snakegame.R;
import com.vkochenkov.snakegame.enums.Direction;
import com.vkochenkov.snakegame.views.GameScreenView;

import java.util.ArrayList;
import java.util.List;

public class GameScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private GameScreenView gameScreenView;
    private LinearLayout mainLinearLayout;
    private Button btnRightMove;
    private Button btnLeftMove;
    private Button btnUpMove;
    private Button btnDownMove;

    private int multiple = 20;
    private int timeToRefreshDraw = 250;
    private boolean snakeIsAlive = true;
    private Direction direction = Direction.RIGHT;
    private int rectSize;
    private int startX;
    private int startY;
    private int gameScreenSize;
    private int phoneScreenWidth;

    private List<Rect> snake = new ArrayList<>();
    private Rect food;

    private SnakeMoving snakeMoving;

    //todo попробовать переписать на асинк таск, ибо не получается останавливать процесс при сворачивании или выходе из активити
    private class SnakeMoving extends Thread {
        @Override
        public void run() {
            while (snakeIsAlive) {
                try {
                    this.sleep(timeToRefreshDraw);
                    moveSnakeInDirection();
                } catch (InterruptedException e) {
                    System.out.println("haha");
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        btnRightMove = findViewById(R.id.btn_right);
        btnLeftMove = findViewById(R.id.btn_left);
        btnUpMove = findViewById(R.id.btn_up);
        btnDownMove = findViewById(R.id.btn_down);

        btnRightMove.setOnClickListener(this);
        btnLeftMove.setOnClickListener(this);
        btnUpMove.setOnClickListener(this);
        btnDownMove.setOnClickListener(this);

        findDisplaySize();
        initGameScreenParams();
        initSnakeArray();
        food = generateNewFood();
        createAndInitGameScreenView();

        //запускаем движение змеи в бэкграунде
        snakeMoving = new SnakeMoving();
        snakeMoving.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btn_right):
                if (direction != Direction.LEFT) {
                    direction = Direction.RIGHT;
                }
                break;
            case (R.id.btn_left):
                if (direction != Direction.RIGHT) {
                    direction = Direction.LEFT;
                }
                break;
            case (R.id.btn_up):
                if (direction != Direction.DOWN) {
                    direction = Direction.UP;
                }
                break;
            case (R.id.btn_down):
                if (direction != Direction.UP) {
                    direction = Direction.DOWN;
                }
                break;
        }
    }

    private void createAndInitGameScreenView() {
        gameScreenView = new GameScreenView(this, snake, food, rectSize, multiple);
        LayoutParams params = new LayoutParams(gameScreenSize, gameScreenSize);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = rectSize;
        params.bottomMargin = rectSize;
        gameScreenView.setLayoutParams(params);
        mainLinearLayout = findViewById(R.id.game_screen_layout);
        mainLinearLayout.addView(gameScreenView, 0);
    }

    private void initSnakeArray() {
        for (int i = 0; i < 3; i++) {
            snake.add(new Rect(startX - (rectSize * i), startY, (startX + rectSize) - (rectSize * i), (startY + rectSize)));
        }
    }

    private void initGameScreenParams() {
        rectSize = phoneScreenWidth / multiple;
        gameScreenSize = rectSize * (multiple - 2);
        startX = rectSize * (multiple / 2);
        startY = rectSize * (multiple / 2);
    }

    private void findDisplaySize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneScreenWidth = metrics.widthPixels;
    }

    //todo пофиксить баг с быстрым нажатием "вниз и в бок"
    private void moveSnakeInDirection() {
        Rect head = snake.get(0);
        switch (direction) {
            case RIGHT:
                snake.add(0, new Rect(head.left + rectSize, head.top, head.right + rectSize, head.bottom));
                break;
            case LEFT:
                snake.add(0, new Rect(head.left - rectSize, head.top, head.right - rectSize, head.bottom));
                break;
            case UP:
                snake.add(0, new Rect(head.left, head.top - rectSize, head.right, head.bottom - rectSize));
                break;
            case DOWN:
                snake.add(0, new Rect(head.left, head.top + rectSize, head.right, head.bottom + rectSize));
                break;
        }

        head = snake.get(0);

        borderClashValidation(head);
        foodClashValidation(head);

        gameScreenView.setSnake(snake);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameScreenView.invalidate();
            }
        });
    }

    private void foodClashValidation(Rect head) {
        if (head.left == food.left &&
                head.top == food.top &&
                head.right == food.right &&
                head.bottom == food.bottom
        ) {
            Log.d("food", "food has been eaten");
            food = generateNewFood();
            gameScreenView.setFood(food);
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private Rect generateNewFood() {
        //todo сделать так, что бы еда не появлялась внутри змеи
        int randomX = (int) ((Math.random()*gameScreenSize)/rectSize)*rectSize;
        int randomY = (int) ((Math.random()*gameScreenSize)/rectSize)*rectSize;
        return new Rect(randomX, randomY, randomX+rectSize, randomY+rectSize);
    }

    private void borderClashValidation(Rect head) {
        if (head.left < 0 ||
            head.top < 0 ||
            head.right > gameScreenSize ||
            head.bottom > gameScreenSize
        ) {
            //todo написать валидацию столкновения со своим телом
            snakeIsAlive = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(GameScreenActivity.this, "you are dead!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}