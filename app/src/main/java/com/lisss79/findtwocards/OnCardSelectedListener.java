package com.lisss79.findtwocards;

import android.view.View;

/**
 * Интерфейс, методы которого вызывает {@link GameView}
 */
public interface OnCardSelectedListener {

    /**
     * Вызывается, когда произошел клик по карте
     * @param col колонка выбранной карты
     * @param row ряд выбранной карты
     * @param value значение колонка выбранной карты
     */
    void onCardSelected(int col, int row, int value);

    /**
     * Вызывается, когда произошло удаление карты с поля
     * @param noCardsLeft true, если это была последняя карта
     */
    void onCardsRemoved(boolean noCardsLeft);

    /**
     * Вызывается, когда карты закрываются, кроме одной
     * <p>
     *     Случай, когда две разные карты открыты, и происходит клик по третьей
     * </p>
     * @param value значение этой карты
     */
    void onCardsClosedExceptOne(int value);

    /**
     * Вызывается, когда закончилась анимация изменения состояния
     */
    void onCardsChangeAnimFinished(int col, int row, int state);

    /**
     * Вызывается, когда закончилась анимация иcчезновения
     */
    void onCardsDisappearanceFinished(int col, int row, int state);

    /**
     * Вызывается, когда закончилась игра
     */
    void onEnd();

}
