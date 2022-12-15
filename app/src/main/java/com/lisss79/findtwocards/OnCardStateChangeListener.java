package com.lisss79.findtwocards;

/**
 * Интерфейс, методы которого вызывает {@link CardsData}
 */
public interface OnCardStateChangeListener {
    /**
     * Вызывается, когда произошло изменение состояния карты
     * <p>
     *     Например, была открыта, а стала закрыта
     * </p>
     * @param col колонка карты
     * @param row ряд карты
     */
    void onCardStateChange(int col, int row);

    /**
     * Вызывается, когда произошло удаление карты с поля
     * @param col колонка карты
     * @param row ряд карты
     */
    void onCardStateRemove(int col, int row);
}
