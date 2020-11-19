package com.vkochenkov.snakegame;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GameScreenActivity extends AppCompatActivity implements View.OnClickListener {

    //views
    private GameScreenView gameScreenView;
    private LinearLayout linearLayout;
    private Button btnRightMove;
    private Button btnLeftMove;
    private Button btnUpMove;
    private Button btnDownMove;

    //snake data
    private int multiple = 15;
    private int rectSize;
    private int startX;
    private int startY;
    private int boardSize = 1;
    private List<Rect> snake = new ArrayList<>();
    private int gameScreenSize;

    private int phoneScreenWidth;

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
        createAndShowGameScreenView();
    }

    private void createAndShowGameScreenView() {
        gameScreenView = new GameScreenView(this, snake, rectSize, boardSize, multiple);
        LayoutParams params = new LayoutParams(gameScreenSize, gameScreenSize);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        gameScreenView.setLayoutParams(params);
        linearLayout = findViewById(R.id.game_screen_layout);
        linearLayout.addView(gameScreenView);
    }

    private void initSnakeArray() {
        for (int i = 0; i < 3; i++) {
            snake.add(new Rect(startX - ((rectSize + boardSize) * i), startY, (startX + rectSize) - ((rectSize + boardSize) * i), (startY + rectSize)));
        }
    }

    private void initGameScreenParams() {
        rectSize = phoneScreenWidth / multiple;
        gameScreenSize = boardSize + (rectSize + boardSize) * (multiple - 1);
        startX = boardSize + (rectSize + boardSize) * (multiple / 2);
        startY = boardSize + (rectSize + boardSize) * (multiple / 2);
    }

    private void findDisplaySize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //phoneScreenHeight = metrics.heightPixels;
        phoneScreenWidth = metrics.widthPixels;
    }

    @Override
    public void onClick(View view) {
        Rect first = snake.get(0);
        switch (view.getId()) {
            case (R.id.btn_right):
                snake.add(0, new Rect(first.left + rectSize + boardSize, first.top, first.right + rectSize + boardSize, first.bottom));
                break;
            case (R.id.btn_left):
                snake.add(0, new Rect(first.left - rectSize - boardSize, first.top, first.right - rectSize - boardSize, first.bottom));
                break;
            case (R.id.btn_up):
                snake.add(0, new Rect(first.left, first.top  - rectSize - boardSize, first.right, first.bottom - rectSize - boardSize));
                break;
            case (R.id.btn_down):
                snake.add(0, new Rect(first.left, first.top  + rectSize + boardSize, first.right, first.bottom + rectSize + boardSize));
                break;
        }
        snake.remove(snake.size() - 1);
        gameScreenView.setSnake(snake);
        gameScreenView.invalidate();
    }
}

