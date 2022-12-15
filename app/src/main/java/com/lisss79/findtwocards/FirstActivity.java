package com.lisss79.findtwocards;

import static com.lisss79.findtwocards.Values.BEST_RESULT_KEY;
import static com.lisss79.findtwocards.Values.NUM_OF_CARDS_KEY;
import static com.lisss79.findtwocards.Values.sp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {
    private int numOfCards = 16;
    private int[] bestResults = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        Button buttonGo = findViewById(R.id.buttonGo);
        buttonGo.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(NUM_OF_CARDS_KEY, numOfCards);
            startActivity(intent);
        });

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton8:
                        numOfCards = 8;
                        break;
                    case R.id.radioButton12:
                        numOfCards = 12;
                        break;
                    case R.id.radioButton16:
                        numOfCards = 16;
                        break;
                    case R.id.radioButton20:
                        numOfCards = 20;
                        break;
                    default:
                        numOfCards = 16;
                        break;
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(NUM_OF_CARDS_KEY, numOfCards);
                editor.apply();
            }
        });
    }

    private void setSelectedRadioButton() {
        int buttonId;
        switch(numOfCards) {
            case 8:
                buttonId = R.id.radioButton8;
                break;
            case 12:
                buttonId = R.id.radioButton12;
                break;
            case 16:
                buttonId = R.id.radioButton16;
                break;
            case 20:
                buttonId = R.id.radioButton20;
                break;
            default:
                buttonId = R.id.radioButton16;
                break;
        }
        RadioButton button = findViewById(buttonId);
        button.setChecked(true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        numOfCards = sp.getInt(NUM_OF_CARDS_KEY, 16);
        for(int i = 0; i < 4; i++) {
            bestResults[i] = sp.getInt(BEST_RESULT_KEY[i], 100);
        }
        ((TextView) findViewById(R.id.textViewRes8)).setText(bestResults[0] + " clicks");
        ((TextView) findViewById(R.id.textViewRes12)).setText(bestResults[1] + " clicks");
        ((TextView) findViewById(R.id.textViewRes16)).setText(bestResults[2] + " clicks");
        ((TextView) findViewById(R.id.textViewRes20)).setText(bestResults[3] + " clicks");
        setSelectedRadioButton();
    }
}