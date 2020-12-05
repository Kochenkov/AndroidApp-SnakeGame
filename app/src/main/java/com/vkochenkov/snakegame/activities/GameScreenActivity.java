package com.vkochenkov.snakegame.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vkochenkov.snakegame.R;
import com.vkochenkov.snakegame.enums.Direction;
import com.vkochenkov.snakegame.views.GameScreenView;

import java.util.LinkedList;
import java.util.List;

public class GameScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BEST_SCORE = "BEST_SCORE";
    private static final int initialSnakeSize = 3;
    private static final int multiple = 10;
    private static final int timeToRefreshDraw = 500;

    //texts
    private String LOSE_TITLE;
    private String WIN_TITLE;

    private SharedPreferences preferences;
    private GameScreenView gameScreenView;
    private LinearLayout gameBackgroundLayout;
    private TextView tvScore, tvBest;
    private Button btnBack;
    private ImageButton btnRightMove, btnLeftMove, btnUpMove, btnDownMove;
    private ToggleButton btnStartStop;
    private Direction direction;

    private boolean gameIsRunning;
    private boolean snakeIsAlive;
    private boolean alertIsShowed;
    private boolean startStopBtnPressed;

    private int rectSize;
    private int startX, startY;
    private int gameScreenSize;
    private int phoneScreenWidth;

    private int score;
    private int bestScore;

    private List<Rect> snake = new LinkedList<>();
    private Rect food;

    private SnakeMoving snakeMoving;

    private class SnakeMoving extends Thread {
        @Override
        public void run() {
            while (gameIsRunning) {
                try {
                    this.sleep(timeToRefreshDraw);
                    if (gameIsRunning && snakeIsAlive) {
                        moveAndValidateSnake();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getCause());
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LOSE_TITLE = getResources().getString(R.string.lose_title);
        WIN_TITLE = getResources().getString(R.string.win_title);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        btnRightMove = findViewById(R.id.btn_right);
        btnLeftMove = findViewById(R.id.btn_left);
        btnUpMove = findViewById(R.id.btn_up);
        btnDownMove = findViewById(R.id.btn_down);
        btnBack = findViewById(R.id.btn_back);
        btnStartStop = findViewById(R.id.btn_start_stop);
        tvScore = findViewById(R.id.tv_score);
        tvBest = findViewById(R.id.tv_best);

        btnRightMove.setOnClickListener(this);
        btnLeftMove.setOnClickListener(this);
        btnUpMove.setOnClickListener(this);
        btnDownMove.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnStartStop.setOnClickListener(this);

        findDisplaySize();
        initGameScreenParams();
        snake = generateSnakeArray();
        food = generateNewFood();
        createAndInitGameScreenView();

        //инициализируем начальные параметры
        score = snake.size();
        direction = Direction.RIGHT;
        gameIsRunning = true;
        snakeIsAlive = true;
        alertIsShowed = false;

        //достаем и отображаем рекордный результат из преференций
        preferences = getPreferences(MODE_PRIVATE);
        bestScore = preferences.getInt(BEST_SCORE, score);
        tvBest.setText(scoreToString(bestScore));

        //запускаем движение змеи в отдельном потоке
        snakeMoving = new SnakeMoving();
        snakeMoving.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopGame();
    }
    @Override
    protected void onResume() {
        super.onResume();
        resumeGame();
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
            case (R.id.btn_back):
                onBackPressed();
                break;
            case (R.id.btn_start_stop):
                if (btnStartStop.isChecked()) {
                    stopGame();
                } else {
                    resumeGame();
                }
                break;
        }
    }

    private void stopGame() {
        gameIsRunning = false;
        btnStartStop.setChecked(true);
    }

    private void resumeGame() {
        if (!gameIsRunning && snakeIsAlive && !alertIsShowed) {
            gameIsRunning = true;
            snakeMoving = new SnakeMoving();
            snakeMoving.start();
            btnStartStop.setChecked(false);
        }
    }

    private void createAndInitGameScreenView() {
        gameScreenView = new GameScreenView(this, snake, food, rectSize, multiple);
        LayoutParams params = new LayoutParams(gameScreenSize, gameScreenSize);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(rectSize / 10, rectSize / 10, rectSize / 10, rectSize / 10);
        gameScreenView.setLayoutParams(params);
        gameBackgroundLayout = findViewById(R.id.game_background_layout);
        gameBackgroundLayout.addView(gameScreenView, 0);
    }

    private List<Rect> generateSnakeArray() {
        List<Rect> snake = new LinkedList<>();
        for (int i = 0; i < initialSnakeSize; i++) {
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
    private void moveAndValidateSnake() {
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
        while (score / 10 > 0) {
            score = score / 10;
            size++;
        }
        String strScore = "";
        for (int i = 0; i < (initialSize - size); i++) {
            strScore += "0";
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
                if (snake.size() < ((multiple - 2) * (multiple - 2))) {
                    Log.d("generateNewFood", "RECURSION!!!");
                    food = generateNewFood();
                } else {
                    //победа
                    endGame(WIN_TITLE);
                }
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
            //в генерацию еды зашита победа -__-
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
            endGame(LOSE_TITLE);
        }
    }

    private void snakeBodyClashValidation(Rect head) {
        for (int i = 1; i < snake.size(); i++) {
            Rect currentElem = snake.get(i);
            if (head.left == currentElem.left && head.top == currentElem.top) {
                endGame(LOSE_TITLE);
            }
        }
    }

    private void endGame(final String title) {
        gameIsRunning = false;
        snakeIsAlive = false;
        if (score > bestScore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(BEST_SCORE, score);
            editor.apply();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showEndGameAlert(title, getString(R.string.alert_ok));
            }
        });
    }

    private void showEndGameAlert(String alertTitle, String alertButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameScreenActivity.this);
        builder.setTitle(alertTitle)
                .setMessage(getString(R.string.your_score) + score)
                .setCancelable(false)
                .setNegativeButton(alertButtonText,
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(GameScreenActivity.this);
        quitDialog.setTitle(R.string.get_out_title)
                  .setMessage(R.string.get_out_description)
                  .setCancelable(false);
        quitDialog.setPositiveButton(R.string.yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertIsShowed = false;
                finish();
            }
        });

        quitDialog.setNegativeButton(R.string.no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertIsShowed = false;
                resumeGame();
            }
        });

        quitDialog.show();
        alertIsShowed = true;
        stopGame();
    }
}