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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vkochenkov.snakegame.R;
import com.vkochenkov.snakegame.enums.Direction;
import com.vkochenkov.snakegame.views.GameScreenView;

import java.util.LinkedList;
import java.util.List;

public class GameScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int multiple = 7;
    private static final int timeToRefreshDraw = 500;

    private GameScreenView gameScreenView;
    private LinearLayout mainLinearLayout;
    private TextView tvScore;

    private Button btnRightMove;
    private Button btnLeftMove;
    private Button btnUpMove;
    private Button btnDownMove;

    private Direction direction;

    private boolean gameIsRunning;

    private int rectSize;
    private int startX;
    private int startY;
    private int gameScreenSize;
    private int phoneScreenWidth;

    private int score;
    private List<Rect> snake = new LinkedList<>();
    private Rect food;

    private SnakeMoving snakeMoving;

    private class SnakeMoving extends Thread {
        @Override
        public void run() {
            while (gameIsRunning) {
                try {
                    this.sleep(timeToRefreshDraw);
                    if (gameIsRunning) {
                        moveSnakeInDirection();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getCause());
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
        tvScore = findViewById(R.id.tv_score);

        btnRightMove.setOnClickListener(this);
        btnLeftMove.setOnClickListener(this);
        btnUpMove.setOnClickListener(this);
        btnDownMove.setOnClickListener(this);

        findDisplaySize();
        initGameScreenParams();
        snake = generateSnakeArray();
        food = generateNewFood();
        createAndInitGameScreenView();

        //инициализируем начальные параметры
        score = snake.size();
        direction = Direction.RIGHT;
        gameIsRunning = true;
        //запускаем движение змеи в отдельном потоке
        snakeMoving = new SnakeMoving();
        snakeMoving.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameIsRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameIsRunning) {
            gameIsRunning = true;
            snakeMoving = new SnakeMoving();
            snakeMoving.start();
        }
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
        params.topMargin = rectSize/2;
        params.bottomMargin = rectSize/2;
        gameScreenView.setLayoutParams(params);
        mainLinearLayout = findViewById(R.id.game_screen_layout);
        mainLinearLayout.addView(gameScreenView, 1);
    }

    private List<Rect> generateSnakeArray() {
        List<Rect> snake = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            snake.add(new Rect(startX - (rectSize * i), startY, (startX + rectSize) - (rectSize * i), (startY + rectSize)));
        }
        return snake;
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
    //или это не баг? В классической змейке на тетрисе так и сделано
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
        eatFoodOrDeleteTailElement(head);
        borderClashValidation(head);
        snakeBodyClashValidation(head);
        gameScreenView.setSnake(snake);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvScore.setText(scoreToString(score));
                gameScreenView.invalidate();
            }
        });
    }

    private String scoreToString(int score) {
        final int initialScore = score;
        //0000
        final int initialSize = 4;
        int size = 1;
        while (score/10>0) {
           score = score/10;
           size++;
        }
        String strScore = "";
        for (int i=0; i<(initialSize-size); i++) {
            strScore  += "0";
        }
        strScore += initialScore;
        return strScore;
    }

    private Rect generateNewFood() {
        int randomX = generateRandomCoordinate();
        int randomY = generateRandomCoordinate();

        Rect food = new Rect(randomX, randomY, randomX + rectSize, randomY + rectSize);

        //проверка на то, что бы еда не появлялась внутри змеи с последующим рекурсивным вызовом ф-ции
        for (int i = 0; i < snake.size(); i++) {
            Rect snakeElem = snake.get(i);
            if (food.left == snakeElem.left && food.top == snakeElem.top) {
                food = generateNewFood();
                Log.d("generateNewFood", "RECURSION!!!");
                break;
            }
            Log.d("generateNewFood", "no recursion");
        }

        return food;
    }

    private int generateRandomCoordinate() {
        return (int) ((Math.random() * gameScreenSize) / rectSize) * rectSize;
    }

    private void eatFoodOrDeleteTailElement(Rect head) {
        if (head.left == food.left && head.top == food.top) {
            score = snake.size();
            food = generateNewFood();
            gameScreenView.setFood(food);
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void borderClashValidation(Rect head) {
        if (head.left < 0 ||
                head.top < 0 ||
                head.right > gameScreenSize ||
                head.bottom > gameScreenSize
        ) {
            endGame();
        }
    }

    private void snakeBodyClashValidation(Rect head) {
        for (int i = 1; i < snake.size(); i++) {
            Rect currentElem = snake.get(i);
            if (head.left == currentElem.left && head.top == currentElem.top) {
                endGame();
            }
        }
    }

    private void endGame() {
        gameIsRunning = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(GameScreenActivity.this, "you are dead!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}