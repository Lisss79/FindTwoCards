package com.lisss79.findtwocards;

import static com.lisss79.findtwocards.Values.ANIMATION_DURATION;
import static com.lisss79.findtwocards.Values.ANIMATION_STEP;
import static com.lisss79.findtwocards.Values.CARD_ABSENT;
import static com.lisss79.findtwocards.Values.CARD_CLOSE;
import static com.lisss79.findtwocards.Values.CARD_CLOSE_TO_BE_REMOVED;
import static com.lisss79.findtwocards.Values.CARD_OPEN;
import static com.lisss79.findtwocards.Values.CARD_OPEN_TO_BE_REMOVED;
import static com.lisss79.findtwocards.Values.CARD_STATE;
import static com.lisss79.findtwocards.Values.CARD_VALUE;
import static com.lisss79.findtwocards.Values.DISAPPEARANCE_DURATION;
import static com.lisss79.findtwocards.Values.DISAPPEARANCE_STEP;
import static com.lisss79.findtwocards.Values.END_OF_ANIMATION;
import static com.lisss79.findtwocards.Values.NO_ANIMATION;
import static com.lisss79.findtwocards.Values.SECOND_PART_OF_ANIMATION;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class GameView extends View implements OnCardStateChangeListener {
    CardsData cardsData;
    private int numOfCol;
    private int numOfRow;
    private int numOfSymbols;

    private Context context;
    private RectF rect1;
    private RectF rect2;
    private RectF rect1Anim;
    private RectF rect2Anim;
    private Rect textRect;
    private Paint paint;
    private Paint backgroundPaint;
    private Paint cardPaint;
    private Paint textPaint;
    private Path cardPath;
    private Path cardPathAnim;
    private Bitmap cardPic;
    private Bitmap backgroundPic;
    private BitmapFactory.Options options;
    private int backgroundColor;
    private int strokeColor;
    private int cardsOffset;
    private int picOffset;
    private int extraTopOffset;

    private int width;
    private int height;
    private int cardWidth;
    private int cardHeight;
    private int cardRadius;
    private int textHeight;

    private int[] coordsX;
    private int[] coordsY;
    private int[][][] cards;
    private int[] symbols;
    private Bitmap[] symbolsBmp;

    private double[][] anim;
    private double[][] disapp;
    private long timeAnim;
    private long timeDisapp;
    private boolean needToRefresh;

    private boolean showGreetingMessage = false;
    private String greetingMessage = "Congratulations!|You found all tiles!";
    private String topText = "";

    private OnCardSelectedListener listener;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, CardsData cardsData) {
        super(context);
        this.cardsData = cardsData;
        this.numOfCol = cardsData.getNumOfCol();
        this.numOfRow = cardsData.getNumOfRow();
        this.cards = cardsData.getCards();
        this.symbols = cardsData.getSymbols();
        this.numOfSymbols = cardsData.getNumOfSymbols();
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        anim = new double[numOfCol][numOfRow];
        disapp = new double[numOfCol][numOfRow];
        cardsData.setOnCardsStateChangeListener(this);
        timeDisapp = 0;
        timeAnim = 0;
        textHeight = 0;

        textRect = new Rect();
        paint = new Paint();
        cardPaint = new Paint();
        textPaint = new Paint();
        cardPath = new Path();
        cardPathAnim = new Path();
        backgroundColor = context.getColor(R.color.opaque_grey_light);
        strokeColor = context.getColor(R.color.black);

        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(3);

        extraTopOffset = 0;
        cardsOffset = (int) getResources().getDimension(R.dimen.cardsOffset);
        picOffset = (int) getResources().getDimension(R.dimen.picOffset);
        cardRadius = (int) getResources().getDimension(R.dimen.cardsRadius);
        coordsX = new int[numOfCol];
        coordsY = new int[numOfRow];

        options = new BitmapFactory.Options();
        options.inMutable = true;
        backgroundPic = BitmapFactory.decodeResource(getResources(), R.drawable.background, options);
        cardPic = BitmapFactory.decodeResource(getResources(), R.drawable.card, options);

        symbolsBmp = new Bitmap[numOfSymbols];

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showGreetingMessage) {
            String text1 = greetingMessage.split("\\|")[0];
            String text2 = greetingMessage.split("\\|")[1];
            Path path1 = new Path();
            path1.addCircle(width / 2, height / 2, (float) (0.75 * width / 2), Path.Direction.CW);
            canvas.drawRect(0, 0, width, height, backgroundPaint);
            paint.reset();
            paint.setTextSize(100);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setTypeface(Typeface.SERIF);
            paint.setColor(Color.BLUE);
            canvas.drawTextOnPath(text1, path1, 1520, 0, paint);
            path1.rewind();
            path1.addCircle(width / 2, height / 2, (float) (0.75 * width / 2), Path.Direction.CCW);
            canvas.drawTextOnPath(text2, path1, 1450, 0, paint);
        } else {
            canvas.drawRect(0, 0, width, height, backgroundPaint);
            if(!topText.isEmpty()) {
                textPaint.getTextBounds(topText,0, topText.length(), textRect);
                textHeight = 50;
                canvas.drawText(topText, 5, textHeight, textPaint);
            }
            drawAllCards(canvas);
        }
    }

    public void setTopText(String text) {
        topText = text;
        invalidate();
    }

    /**
     * Рисует все карты на поле
     *
     * @param canvas холст из {@link #onDraw}
     */
    private void drawAllCards(Canvas canvas) {
        needToRefresh = false;
        for (int x = 0; x < numOfCol; x++) {
            for (int y = 0; y < numOfRow; y++) {
                canvas.save();
                int currX = cardsOffset + x * (cardWidth + cardsOffset);
                int currY = extraTopOffset + textHeight + cardsOffset +
                        y * (cardHeight + cardsOffset);
                coordsX[x] = currX;
                coordsY[y] = currY;
                int cardState = cards[x][y][CARD_STATE];
                if (cardState != CARD_ABSENT && cardState != CARD_OPEN_TO_BE_REMOVED &&
                        cardState != CARD_CLOSE_TO_BE_REMOVED) drawCard(canvas,
                        currX, currY, cardState, cards[x][y][CARD_VALUE], x, y);
                else if (cardState == CARD_OPEN_TO_BE_REMOVED || cardState == CARD_CLOSE_TO_BE_REMOVED) {
                    drawCard(canvas, currX, currY, cardState, cards[x][y][CARD_VALUE], x, y);
                } else if (cardState == CARD_ABSENT) disappearanceCard(canvas, currX, currY,
                        cardState, cards[x][y][CARD_VALUE], x, y);
                canvas.restore();
            }
        }
        if (needToRefresh) invalidate();
    }

    private void disappearanceCard(Canvas canvas, int x, int y, int state, int value, int col, int row) {
        double disappearance = disapp[col][row];
        if (anim[col][row] == 0 && (disappearance > NO_ANIMATION)) {
            // Если нужно анимировать ирсчезновение карты
            needToRefresh = true;
            double deltaTime = ANIMATION_STEP;
            if (timeDisapp == 0) {
                timeDisapp = System.currentTimeMillis();
            } else {
                deltaTime = (double) (System.currentTimeMillis() - timeDisapp) / 1000;
                timeDisapp = System.currentTimeMillis();
            }
            if (deltaTime < 0.006 || deltaTime > 0.033) deltaTime = ANIMATION_STEP;
            deltaTime = ANIMATION_STEP;
            double disappStep = deltaTime / DISAPPEARANCE_DURATION;

            int alpha = (int) ((1 - disappearance) * 0xFF);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(20f);
            int newStrokeColor = (strokeColor & 0x00FFFFFF) | (alpha << 24);
            paint.setColor(newStrokeColor);

            canvas.translate(x, y);
            cardPathAnim.rewind();
            cardPathAnim.addRoundRect(rect1, cardRadius, cardRadius, Path.Direction.CW);
            canvas.clipPath(cardPathAnim);

            int newBackgroundColor = (backgroundColor & 0x00FFFFFF) | (alpha << 24);
            canvas.drawColor(newBackgroundColor);
            cardPaint.setAlpha(alpha);
            canvas.drawBitmap(symbolsBmp[value], null, rect2, cardPaint);
            disapp[col][row] += disappStep;
            if (disapp[col][row] >= END_OF_ANIMATION) {
                disapp[col][row] = NO_ANIMATION;
                listener.onCardsDisappearanceFinished(col, row, state);
            }
            canvas.drawPath(cardPath, paint);
        }

    }

    /**
     * Нарисовать одну карту
     *
     * @param canvas холст из {@link #onDraw}
     * @param x      позиция карты на холсте
     * @param y      позиция карты на холсте
     * @param state  состояние карты
     * @param value  значение карты
     * @param col    колонка карты
     * @param row    ряд карты
     */
    private void drawCard(Canvas canvas, int x, int y, int state, int value, int col, int row) {
        double animation = anim[col][row];
        double disappearance = disapp[col][row];
        boolean firstPartOfAnim = true;
        if (animation > NO_ANIMATION) {
            if (animation <= SECOND_PART_OF_ANIMATION) {
                firstPartOfAnim = true;
            } else {
                firstPartOfAnim = false;
                animation = 1 - animation;
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20f);
        paint.setColor(strokeColor);
        if (animation == NO_ANIMATION && disappearance > NO_ANIMATION) {
            disappearanceCard(canvas, x, y, state, value, col, row);
        } else if (animation == NO_ANIMATION) {
            // Если анимация не нужна, просто рисуем карту
            canvas.translate(x, y);
            canvas.clipPath(cardPath);
            if (state == CARD_CLOSE || state == CARD_CLOSE_TO_BE_REMOVED) {
                // закрытая карта
                canvas.drawBitmap(cardPic, null, rect1, null);
            } else {
                // открытая карта
                canvas.drawColor(backgroundColor);
                canvas.drawBitmap(symbolsBmp[value], null, rect2, null);
            }
        } else {
            // Если нужно анимировать переворот карты
            needToRefresh = true;
            double deltaTime = ANIMATION_STEP;
            if (timeAnim == 0) {
                timeAnim = System.currentTimeMillis();
            } else {
                deltaTime = (double) (System.currentTimeMillis() - timeAnim) / 1000;
                timeAnim = System.currentTimeMillis();
            }
            if (deltaTime < 0.006 || deltaTime > 0.033) deltaTime = ANIMATION_STEP;
            deltaTime = ANIMATION_STEP;
            double animStep = deltaTime / ANIMATION_DURATION;

            canvas.translate(x, y);
            rect1Anim.set((float) (cardWidth * animation), 0,
                    (float) (cardWidth * (1 - animation)), cardHeight);
            rect2Anim.set((float) (picOffset + cardWidth * animation),
                    (float) (picOffset + (cardHeight - cardWidth) / 2),
                    (float) (cardWidth * (1 - animation) - picOffset),
                    (float) ((cardHeight + cardWidth) / 2 - picOffset));
            cardPathAnim.rewind();
            cardPathAnim.addRoundRect(rect1Anim, cardRadius, cardRadius, Path.Direction.CW);
            canvas.clipPath(cardPathAnim);
            if (((state == CARD_CLOSE || state == CARD_CLOSE_TO_BE_REMOVED) && !firstPartOfAnim) ||
                    ((state == CARD_OPEN || state == CARD_OPEN_TO_BE_REMOVED) && firstPartOfAnim)) {
                canvas.drawBitmap(cardPic, null, rect1Anim, null);
            } else {
                canvas.drawColor(backgroundColor);
                canvas.drawBitmap(symbolsBmp[value], null, rect2Anim, null);
            }
            anim[col][row] += animStep;
            if (anim[col][row] >= END_OF_ANIMATION) {
                anim[col][row] = NO_ANIMATION;
                listener.onCardsChangeAnimFinished(col, row, state);
            }
        }
        canvas.drawPath(cardPath, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        double ratioHW = 1.5f;
        cardWidth = (width - numOfCol * cardsOffset - cardsOffset) / numOfCol;
        double maxCardHeight = (height - numOfRow * cardsOffset - cardsOffset) * 0.85 / numOfRow;
        if(maxCardHeight < cardWidth * ratioHW) ratioHW = maxCardHeight / cardWidth;
        cardHeight = (int) (cardWidth * ratioHW);
        int allCardsHeight = (cardHeight + cardsOffset) * numOfRow;
        extraTopOffset = (int) ((h * 0.85 - allCardsHeight) / 2);

        rect1 = new RectF(0, 0, cardWidth, cardHeight);
        rect1Anim = new RectF(rect1);
        cardPath.rewind();
        cardPath.addRoundRect(rect1, cardRadius, cardRadius, Path.Direction.CW);
        rect2 = new RectF(picOffset, (float) (picOffset + (cardHeight - cardWidth) / 2),
                cardWidth - picOffset,
                (float) ((cardHeight + cardWidth) / 2 - picOffset));
        rect2Anim = new RectF(rect2);

        backgroundPic = Bitmap.createScaledBitmap(backgroundPic, width / 3,
                width / 3, true);
        BitmapShader backgroundShader = new BitmapShader(backgroundPic,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setShader(backgroundShader);

        for (int i = 0; i < numOfSymbols; i++) {
            Bitmap pic = BitmapFactory.decodeResource(getResources(), symbols[i], options);
            pic = Bitmap.createScaledBitmap(pic, cardWidth, cardWidth, true);
            symbolsBmp[i] = pic;
        }

    }

    /**
     * Обработка нажания на карту
     *
     * @param event событие нажания с данными
     * @return true, если было нажание на карту
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (listener != null && !showGreetingMessage) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int xRes = Arrays.binarySearch(coordsX, x);
                if (xRes < 0) xRes = -2 - xRes;
                if (xRes < 0) xRes = 0;
                int yRes = -2 - Arrays.binarySearch(coordsY, y);
                if (yRes < 0) yRes = -2 - yRes;
                if (yRes < 0) yRes = 0;
                int x1 = coordsX[xRes];
                int x2 = coordsX[xRes] + cardWidth;
                int y1 = coordsY[yRes];
                int y2 = coordsY[yRes] + cardHeight;
                if (x > x1 && x < x2 && y > y1 && y < y2 &&
                        cards[xRes][yRes][CARD_STATE] == CARD_CLOSE) {
                    result = true;
                    cards[xRes][yRes][CARD_STATE] = CARD_OPEN;
                    listener.onCardSelected(xRes, yRes, cards[xRes][yRes][CARD_VALUE]);
                    invalidate();
                }
            }
        } else if(listener != null) {
            listener.onEnd();
            result = true;
        }
            return result;
    }

    /**
     * Запустить анимацию изменения состояния для карты
     *
     * @param col колонка карты
     * @param row ряд карты
     */
    public void setAnimationChangeState(int col, int row) {
        anim[col][row] = ANIMATION_STEP;
        invalidate();
    }

    /**
     * Запустить анимацию исчезновения карты
     *
     * @param col колонка карты
     * @param row ряд карты
     */
    public void setAnimationDisappearance(int col, int row) {
        disapp[col][row] = DISAPPEARANCE_STEP;
        invalidate();
    }


    public void setOnCardSelectListener(OnCardSelectedListener listener) {
        this.listener = listener;
    }

    public void closeCardsExceptOne(int col, int row) {
        cardsData.setCards(cards);
        cardsData.closeCardsExceptOne(col, row);
        cards = cardsData.getCards();
        listener.onCardsClosedExceptOne(cards[col][row][CARD_VALUE]);

    }

    /**
     * Удалить карту с экрана
     *
     * @param col колонка карты
     * @param row ряд карты
     */
    public void removeCard(int col, int row) {
        cardsData.setCards(cards);
        boolean noCardsLeft = cardsData.removeCards(col, row);
        cards = cardsData.getCards();
        listener.onCardsRemoved(noCardsLeft);
    }

    public void showGreetingMessage(String greetingMessage) {
        this.greetingMessage = greetingMessage;
        showGreetingMessage = true;
        invalidate();
    }

    @Override
    public void onCardStateChange(int col, int row) {
        anim[col][row] = ANIMATION_STEP;
    }

    @Override
    public void onCardStateRemove(int col, int row) {

    }

    public void changeToAbsentIfNeed(int col, int row) {
        cardsData.setCards(cards);
        cardsData.changeToAbsentIfNeed(col, row);
        cardsData.getCards();
    }
}
