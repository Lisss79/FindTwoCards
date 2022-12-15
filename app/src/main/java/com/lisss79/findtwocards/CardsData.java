package com.lisss79.findtwocards;

import static com.lisss79.findtwocards.Values.*;

import android.content.Context;

import java.util.Random;

/**
 * Создание и обработка массива карт
 */
public class CardsData {
    private int[][][] cards;
    private final int numOfCol;
    private final int numOfRow;
    private final int numOfSymbols;
    private final int[] symbols = new int[10]; // содержит ссылки на картинки карт
    private OnCardStateChangeListener listener; // используется для событий удаления карты и смены состояния
    private final Random random = new Random();

    public CardsData(int numOfCol, int numOfRow, int numOfSymbols) {
        cards = new int[numOfCol][numOfRow][2];
        this.numOfCol = numOfCol;
        this.numOfRow = numOfRow;
        this.numOfSymbols = numOfSymbols;
        initArray();
        fillCards();
    }

    /**
     * Заполнить массив карт случайными значениями так, чтобы каждого вида карт было по две
     */
    private void fillCards() {
        for(int symb = 0; symb < numOfSymbols; symb++) {
            for(int n = 0; n < 2; n++) {
                int x = random.nextInt(numOfCol);
                int y = random.nextInt(numOfRow);
                int[] indexes = findFirstEmpty(x, y);
                x = indexes[0];
                y = indexes[1];
                cards[x][y][CARD_VALUE] = symb;
                cards[x][y][CARD_STATE] = CARD_CLOSE;
            }
        }
    }

    /**
     * Ищет первое свободное поле в массиве карт
     * (вспомогателньый метод для) {@link CardsData#fillCards}
     * @param x колонка
     * @param y ряд
     * @return индекс первого свободного поля
     */
    private int[] findFirstEmpty(int x, int y) {
        int x1 = x;
        int y1 = y;
        while(cards[x1][y1][CARD_STATE] != CARD_ABSENT) {
            x1++;
            if(x1 >= numOfCol) {
                x1 = 0;
                y1++;
                if(y1 >= numOfRow) {
                    y1 = 0;
                    x1 = 0;
                }
            }
        }
        return new int[] {x1, y1};
    }

    /**
     * Заполняет массив ссылок на картинки карт
     */
    private void initArray() {
        symbols[0] = R.drawable.symbol1;
        symbols[1] = R.drawable.symbol2;
        symbols[2] = R.drawable.symbol3;
        symbols[3] = R.drawable.symbol4;
        symbols[4] = R.drawable.symbol5;
        symbols[5] = R.drawable.symbol6;
        symbols[6] = R.drawable.symbol7;
        symbols[7] = R.drawable.symbol8;
        symbols[8] = R.drawable.symbol9;
        symbols[9] = R.drawable.symbol10;
    }

    public int[] getSymbols() {
        return symbols;
    }

    public int[][][] getCards() {
        return cards;
    }

    public void setCards(int[][][] cards) {
        this.cards = cards;
    }

    public int getNumOfCol() {
        return numOfCol;
    }

    public int getNumOfRow() {
        return numOfRow;
    }

    public int getNumOfSymbols() {
        return numOfSymbols;
    }

    public void closeCards() {
        for(int x = 0; x < numOfCol; x++) {
            for(int y = 0; y < numOfRow; y++) {
                if(cards[x][y][CARD_STATE] == CARD_OPEN) {
                    cards[x][y][CARD_STATE] = CARD_CLOSE;
                }
            }
        }
    }

    /**
     * Закрыть все карты, кроме одной
     * @param col колонка карты, которую оставить открытой
     * @param row ряд карты, которую оставить открытой
     */
    public void closeCardsExceptOne(int col, int row) {
        for(int x = 0; x < numOfCol; x++) {
            for(int y = 0; y < numOfRow; y++) {
                if(cards[x][y][CARD_STATE] == CARD_OPEN && (x != col || y != row)) {
                    cards[x][y][CARD_STATE] = CARD_CLOSE;
                    if(listener != null) listener.onCardStateChange(x, y);
                }
            }
        }
    }

    /**
     * Удалить карту с поля
     * @return true, если больше карт не осталось
     */
    public boolean removeCards(int col, int row) {
        int counter = 0;
        for(int x = 0; x < numOfCol; x++) {
            for(int y = 0; y < numOfRow; y++) {
                if(x == col && y == row && cards[x][y][CARD_STATE] == CARD_OPEN) {
                    cards[x][y][CARD_STATE] = CARD_OPEN_TO_BE_REMOVED;
                }
                if(x == col && y == row && cards[x][y][CARD_STATE] == CARD_CLOSE) {
                    cards[x][y][CARD_STATE] = CARD_CLOSE_TO_BE_REMOVED;
                }
                if(listener != null) listener.onCardStateRemove(x, y);
                if(cards[x][y][CARD_STATE] == CARD_CLOSE) counter++;
            }
        }
        return counter == 0;
    }

    /**
     * Изменить статус карты на ABSENT, если она была TO_BE_REMOVED
     * @param col
     * @param row
     */
    public void changeToAbsentIfNeed(int col, int row) {
        if(cards[col][row][CARD_STATE] == CARD_OPEN_TO_BE_REMOVED ||
                cards[col][row][CARD_STATE] == CARD_CLOSE_TO_BE_REMOVED)
            cards[col][row][CARD_STATE] = CARD_ABSENT;
    }

    /**
     * Находит в массиве карт одинаковую пару
     * @param value значение карты, для которого надо искать пару
     * @return массив из координат первой карты и второй
     */
    public int[] cardsToBeRemoved(int value) {
        int[] res = new int[4];
        int i = 0;
        for(int x = 0; x < numOfCol; x++) {
            for(int y = 0; y < numOfRow; y++) {
                if(cards[x][y][CARD_VALUE] == value) {
                    res[i] = x;
                    res[i + 1] = y;
                    i += 2;
                    cards[x][y][CARD_STATE] = CARD_ABSENT;
                }
            }
        }
        return res;
    }

    public void setOnCardsStateChangeListener(OnCardStateChangeListener listener) {
        this.listener = listener;
    }


}
