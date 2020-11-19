package com.vkochenkov.snakegame;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GameScreenActivity extends AppCompatActivity {

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

    //other
    private DisplayMetrics metrics;
    private int phoneScreenHeight;
    private int phoneScreenWidth;

    private int gameScreenSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //find display size
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneScreenHeight = metrics.heightPixels;
        phoneScreenWidth = metrics.widthPixels;

        //init game screen params
        rectSize = phoneScreenWidth/multiple;
        gameScreenSize = boardSize + (rectSize+boardSize)*(multiple-1);
        startX = rectSize + boardSize;
        startY = rectSize + boardSize;

        //init snake array
        for (int i=0; i<3; i++) {
            snake.add(new Rect(startX-((rectSize +boardSize)*i), startY, (startX+ rectSize)-((rectSize +boardSize)*i), (startY+ rectSize)));
        }

        //create gameScreenView
        gameScreenView = new GameScreenView(this, snake);
        LayoutParams params = new LayoutParams(gameScreenSize, gameScreenSize);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        gameScreenView.setLayoutParams(params);
        linearLayout = findViewById(R.id.game_screen_layout);
        linearLayout.addView(gameScreenView);

        //todo сделать 4 кнопки
        //test button
        btnRightMove = findViewById(R.id.btn_move);
        btnRightMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rect first = snake.get(0);
                snake.add(0, new Rect(first.left+ rectSize +boardSize, first.top, first.right+ rectSize +boardSize, first.bottom));
                snake.remove(snake.size()-1);
                gameScreenView.setSnake(snake);
                gameScreenView.invalidate();

                Log.d("gameScreenHeight", String.valueOf(gameScreenView.getMeasuredHeight()));
                Log.d("gameScreenWidth", String.valueOf(gameScreenView.getMeasuredWidth()));
                //Log.d("snakeLength", String.valueOf(snake.size()));
                //Log.d("lastElem", snake.get(0).left + " " + snake.get(0).top + " " + snake.get(0).right + " " + snake.get(0).bottom);
            }
        });
    }
}

