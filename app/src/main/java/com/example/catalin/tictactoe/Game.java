package com.example.catalin.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.example.catalin.tictactoe.ButtonInfo.BUTTON_STATE_X;
import static com.example.catalin.tictactoe.ButtonInfo.BUTTON_STATE_0;
import static com.example.catalin.tictactoe.ButtonInfo.BUTTON_STATE_UNUSED;

public class Game implements View.OnClickListener {

    public static final int Linii = 3;
    public static final int Coloane = 3;
    public static final int P1Turn = 0;
    public static final int P2Turn = 1;
    private int P1Score;
    private int P2Score;
    private int PTurn;
    private Context ctx;
    private TextView pturnTextView;
    private TextView p1ScoreTextView;
    private TextView p2ScoreTextView;
    private GameProcessor gameProcessor = new GameProcessor();
    private FrameLayout winMessageLayout;
    private TextView winMessageTextView;

    public static final Integer[][] buttonsIds = {
            {R.id.button1, R.id.button2, R.id.button3},
            {R.id.button4, R.id.button5, R.id.button6},
            {R.id.button7, R.id.button8, R.id.button9}
    };

    private ButtonInfo[][] buttons = new ButtonInfo[Linii][Coloane];
    private Button resetButton;

    public Game(Context context, int firstPlayer){
        P1Score = 0;
        P2Score = 0;
        ctx = context;

        winMessageTextView = ((Activity) context).findViewById(R.id.winTextView);
        winMessageLayout = ((Activity) context).findViewById(R.id.winView);
        winMessageLayout.setVisibility(View.INVISIBLE);
        resetButton = ((Activity) context).findViewById(R.id.buttonReset);
        pturnTextView = ((Activity) ctx).findViewById(R.id.textViewPlayerTurn);
        p1ScoreTextView = ((Activity) ctx).findViewById(R.id.p1Score);
        p2ScoreTextView = ((Activity) ctx).findViewById(R.id.p2Score);
        setPTurn(firstPlayer);
        resetButton.setOnClickListener(this);

        for(int i = 0; i < Linii; i++)
        {
            for(int j = 0; j < Coloane; j++)
            {
                buttons[i][j] = new ButtonInfo();
                buttons[i][j].b = ((Activity) context).findViewById(buttonsIds[i][j]);
                buttons[i][j].b.setText(" ");
                buttons[i][j].b.setBackgroundColor(ContextCompat.getColor(context, R.color.colorButtonUnused));
                buttons[i][j].b.setOnClickListener(this);
                buttons[i][j].state = BUTTON_STATE_UNUSED;
            }
        }
    }

    @Override
    public void onClick(View v) {
        //Log.i("Tets", "Haha");
        Button b = ((Activity) ctx).findViewById(v.getId());

        ReturnIndexes ret = findButtonIndex(b);
        int i = ret.geti();
        int j = ret.getj();
        if(i != -1 && j != -1)
        {
            if (buttons[i][j].state == BUTTON_STATE_UNUSED)
            {
                Log.i("state", String.valueOf(buttons[i][j].state));

                switch (PTurn) {
                    case P1Turn: {
                        gameProcessor.markButtonAsX(b);
                        break;
                    }
                    case P2Turn: {
                        gameProcessor.markButtonAs0(b);
                        break;
                    }
                }
            }
        }
        else if(v.getId() == R.id.buttonReset)
        {
            resetGame();
        }
    }

    public ReturnIndexes findButtonIndex(Button b)
    {
        for(int i = 0; i < Linii; i++)
        {
            for(int j = 0; j < Coloane; j++)
            {
                ReturnIndexes ret = new ReturnIndexes(i, j);
                if(buttons[i][j].b == b) return ret;
            }
        }
        return new ReturnIndexes(-1, -1);
    }

    public void resetGame()
    {
        restartGame();
        P1Score = 0;
        P2Score = 0;
        PTurn = P1Turn;
        String s = "Player 1: 0 points";
        p1ScoreTextView.setText(s);
        s = "Player 2: 0 points";
        p2ScoreTextView.setText(s);
        s = "Player 1 (X) turn";
        pturnTextView.setText(s);
    }

    public void restartGame()
    {
        for(int i = 0; i < Linii; i++)
        {
            for(int j = 0; j < Coloane; j++)
            {
                buttons[i][j].b.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorButtonUnused));
                buttons[i][j].b.setText(" ");
                buttons[i][j].state = BUTTON_STATE_UNUSED;
                gameProcessor.reset();
            }
        }
    }

    public void showWinner(int Player)
    {

        final int interval = 1000;
        Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            public void run() {
                winMessageLayout.setVisibility(View.INVISIBLE);
                resetButton.setClickable(true);
                SetButtonsClickable(true);
                restartGame();
            }
        };

        String s = "Null";
        switch (Player)
        {
            case 0:
            {
                s = "No-one won!";
                break;
            }
            case 1:
            {
                P1Score++;
                s = "Player 1: " + P1Score + " points";
                p1ScoreTextView.setText(s);
                s = "Player 2 (O) turn";
                pturnTextView.setText(s);
                s = "Player 1 won!";
                break;
            }
            case 2:
            {
                P2Score++;
                s = "Player 2: " + P2Score + " points";
                p2ScoreTextView.setText(s);
                s = "Player 1 (X) turn";
                pturnTextView.setText(s);
                s = "Player 2 won!";
                break;
            }
        }
        winMessageTextView.setText(s);
        winMessageLayout.setVisibility(View.VISIBLE);
        SetButtonsClickable(false);
        resetButton.setClickable(false);
        handler.postDelayed(runnable, interval);
    }

    public void SetButtonsClickable(boolean toggle)
    {
        for(int i = 0; i < Linii; i++)
        {
            for(int j = 0; j < Coloane; j++)
            {
                buttons[i][j].b.setClickable(toggle);
            }
        }

    }

    public class GameProcessor {

        private int[][] val = new int[Linii][Coloane];
        private static final int VAL_0 = 2;
        private static final int VAL_X = 1;
        private static final int VAL_NONE = 0;

        private void CheckWinner()
        {
            boolean x = false;

            for(int i = 0; i < Linii; i++) {
                for (int j = 0; j < Coloane; j++) {
                    //Log.i("Game", val[i][j] + ", " + val[i][j + 1] + ", " + val[i][j + 2]);
                    if (j+2 < Coloane && val[i][j] != VAL_NONE && val[i][j] == val[i][j + 1] && val[i][j + 1] == val[i][j + 2]) {
                        x = true;
                    } else if (i+2 < Linii && val[i][j] != VAL_NONE && val[i][j] == val[i + 1][j] && val[i + 2][j] == val[i + 1][j]) {
                        x = true;
                    } else if (i+2 < Linii && j+2 < Coloane && val[i][j] != VAL_NONE && val[i + 1][j + 1] == val[i][j] && val[i + 2][j + 2] == val[i + 1][j + 1]) {
                        x = true;
                    } else if (i+2 < Linii && j+2 < Coloane && val[i][j + 2] != VAL_NONE && val[i][j + 2] == val[i + 1][j + 1] && val[i + 1][j + 1] == val[i + 2][j]) {
                        x = true;
                    }
                }
            }
            if(x)
            {
                /*String s;
                if (PTurn == P2Turn) {
                    s = "Player 1 won!";
                    P1Score ++;
                } else s = "Player 2 won!"; P2Score ++;
                pturnTextView.setText(s);
                */
                if(PTurn == P2Turn) showWinner(1);
                else showWinner(2);
                //resetGame();
            }
            else
            {
                boolean y = true;
                for(int i = 0; i < Linii; i++) {
                    for (int j = 0; j < Coloane; j++) {
                        if(val[i][j] == BUTTON_STATE_UNUSED) y = false;
                    }
                }
                if(y)
                {
                    //String s = "No-one won!";
                    //pturnTextView.setText(s);
                    showWinner(0);
                }
            }
        }

        public GameProcessor()
        {
            for(int i = 0; i < Linii; i++)
            {
                for(int j = 0; j < Coloane; j++)
                {
                    val[i][j] = VAL_NONE;
                }
            }
        }

        public void reset()
        {
            for(int i = 0; i < Linii; i++) {
                for (int j = 0; j < Coloane; j++) {
                    val[i][j] = VAL_NONE;
                }
            }
        }

        private void markButtonAsX(Button b)
        {
            b.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorButtonUsed));
            b.setText("X");

            ReturnIndexes ret = findButtonIndex(b);
            int i = ret.geti();
            int j = ret.getj();
            buttons[i][j].state = BUTTON_STATE_X;
            val[i][j] = VAL_X;
            setPTurn(P2Turn);
            CheckWinner();
        }

        private void markButtonAs0(Button b)
        {
            b.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorButtonUsed));
            b.setText("O");

            ReturnIndexes ret = findButtonIndex(b);
            int i = ret.geti();
            int j = ret.getj();
            buttons[i][j].state = BUTTON_STATE_0;
            val[i][j] = VAL_0;
            setPTurn(P1Turn);
            CheckWinner();
        }
    }

    public void setPTurn(int PTurn) {
        String s;
        this.PTurn = PTurn;
        if(PTurn == P1Turn)
        {
            s = "Player 1 (X) turn";
        }
        else s = "Player 2 (O) turn";
        pturnTextView.setText(s);
    }

    public void setP1Score(int p1Score) {
        P1Score = p1Score;
    }

    public void setP2Score(int p2Score) {
        P2Score = p2Score;
    }

    public int getP1Score() {
        return P1Score;
    }

    public int getP2Score() {
        return P2Score;
    }


}
