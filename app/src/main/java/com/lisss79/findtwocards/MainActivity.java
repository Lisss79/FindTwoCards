package com.lisss79.findtwocards;

import static com.lisss79.findtwocards.Values.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements OnCardSelectedListener{
    private GameView gameView;
    private CardsData cardsData;
    private int numOfCol = 4;
    private int numOfRow = 5;
    private int numOfSymbols = numOfCol * numOfRow / 2;

    private int openCards;
    private boolean noCardsLeft;
    private int[] firstCard = new int[4];
    private int numOfClicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNumOfCards();

        cardsData = new CardsData(numOfCol, numOfRow, numOfSymbols);
        gameView = new GameView(this, cardsData);
        gameView.setOnCardSelectListener(this);
        setContentView(gameView);

        openCards = 0;
        noCardsLeft = false;
        numOfClicks = 0;
        gameView.setTopText("Number of clicks: " + numOfClicks);

    }

    private void getNumOfCards() {
        Intent data = getIntent();
        int numOfCards = data.getIntExtra(NUM_OF_CARDS_KEY, 16);
        numOfCol = 4;
        numOfRow = numOfCards / numOfCol;
        numOfSymbols = numOfCol * numOfRow / 2;
    }

    @Override
    public void onCardSelected(int col, int row, int value) {
        numOfClicks++;
        gameView.setTopText("Number of clicks: " + numOfClicks);
        if(openCards == 0) {
            openCards = 1;
            firstCard[CARD_VALUE] = value;
            firstCard[CARD_COL] = col;
            firstCard[CARD_ROW] = row;
            gameView.setAnimationChangeState(col, row);
        } else if(openCards == 1) {
            openCards = 2;
            gameView.setAnimationChangeState(col, row);
            if(value == firstCard[CARD_VALUE]) {
                // открыты две одинаковые карты
                gameView.removeCard(firstCard[CARD_COL], firstCard[CARD_ROW]);
                gameView.removeCard(col, row);
                gameView.setAnimationDisappearance(col, row);
                gameView.setAnimationDisappearance(firstCard[CARD_COL], firstCard[CARD_ROW]);
            } else {
                // открыты две разные карты
            }
        }
        else if(openCards == 2) {
            openCards = 1;
            firstCard[CARD_VALUE] = value;
            firstCard[CARD_COL] = col;
            firstCard[CARD_ROW] = row;
            gameView.setAnimationChangeState(col, row);
            gameView.closeCardsExceptOne(col, row);
        }

    }

    @Override
    public void onCardsRemoved(boolean noCardsLeft) {
        openCards--;
        this.noCardsLeft = noCardsLeft;
    }

    @Override
    public void onCardsClosedExceptOne(int value) {
        openCards = 1;
        firstCard[CARD_VALUE] = value;
    }

    @Override
    public void onCardsChangeAnimFinished(int col, int row, int state) {
        if(state == CARD_OPEN_TO_BE_REMOVED) {
            //gameView.setAnimationDisappearance(col, row);
            //gameView.setAnimationDisappearance(firstCard[CARD_COL], firstCard[CARD_ROW]);
        }
    }

    @Override
    public void onCardsDisappearanceFinished(int col, int row, int state) {
        gameView.changeToAbsentIfNeed(col, row);
        if(noCardsLeft) {
            putBestResult(numOfCol * numOfRow, numOfClicks);
            gameView.postDelayed(() ->
                            gameView.showGreetingMessage("Congratulations!|You found all tiles!"),
                    (long) (1500 * (ANIMATION_DURATION + DISAPPEARANCE_DURATION)));
        }
    }

    @Override
    public void onEnd() {
        finish();
    }

    public void putBestResult(int numOfCards, int result) {
        int i = (numOfCards - 8) / 4;
        int bestResult = sp.getInt(BEST_RESULT_KEY[i], 100);
        if(result < bestResult) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(BEST_RESULT_KEY[i], result);
            editor.apply();
        }
    }

}