package pl.marekjedrzejewski.rgbtohsv;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    NumberPicker numberR, numberG, numberB;
    NumberPicker numberH, numberS, numberV;
    TextView colorPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ustawienie wszystkich numberpickerów na zakres 0-255 i ustawienie listenera
        numberR = initNumberPickerRGB(R.id.numberR);
        numberG = initNumberPickerRGB(R.id.numberG);
        numberB = initNumberPickerRGB(R.id.numberB);

        // ustawienie pickera dla H na 0-359, S i V na 0-100 i ustawienie listenera
        numberH = initNumberPickerH(R.id.numberH);
        numberS = initNumberPickerSV(R.id.numberS);
        numberV = initNumberPickerSV(R.id.numberV);

        colorPreview = (TextView)findViewById(R.id.colorPreview);
    }

    // Inicjalizowanie numberpickerów
    private NumberPicker initNumberPickerRGB(int id) {
        NumberPicker numberPicker = (NumberPicker)findViewById(id);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(255);
        numberPicker.setOnValueChangedListener(rgbChangeListener);
        return numberPicker;
    }

    private NumberPicker initNumberPickerH(int id) {
        NumberPicker numberPicker = (NumberPicker)findViewById(id);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(359);
        numberPicker.setOnValueChangedListener(hsvChangeListener);
        return numberPicker;
    }

    private NumberPicker initNumberPickerSV(int id) {
        NumberPicker numberPicker = (NumberPicker)findViewById(id);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);

        // Przedstawienie wartości jako float z zakresu 0-1
        String floatstrings[] = new String[101];
        for (int i = 0; i < 101; i++) {
            floatstrings[i] = String.valueOf((float)(i) / 100.f);
        }
        numberPicker.setDisplayedValues(floatstrings);

        numberPicker.setOnValueChangedListener(hsvChangeListener);
        return numberPicker;
    }

    // Listenery
    private NumberPicker.OnValueChangeListener rgbChangeListener =
            new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    convertToHSVandChangePreview();
                }
            };

    private NumberPicker.OnValueChangeListener hsvChangeListener =
            new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    convertToRGBandChangePreview();
                }
            };

    private void convertToHSVandChangePreview() {
        int col = Color.rgb(numberR.getValue(), numberG.getValue(), numberB.getValue());
        float[] hsv = new float[3];
        Color.colorToHSV(col, hsv);
        numberH.setValue((int) hsv[0]);
        numberS.setValue((int) (100*hsv[1]) );
        numberV.setValue((int) (100 * hsv[2]));

        setColorPreview(col);
    }

    private void convertToRGBandChangePreview() {
        float[] hsv = new float[3];
        hsv[0] = numberH.getValue();
        hsv[1] = (float)numberS.getValue() / 100;
        hsv[2] = (float)numberV.getValue() / 100;
        int col = Color.HSVToColor(hsv);
        numberR.setValue(Color.red(col));
        numberG.setValue(Color.green(col));
        numberB.setValue(Color.blue(col));

        setColorPreview(col);
    }

    private void setColorPreview(int col) {
        colorPreview.setBackgroundColor(col);
        // .substring(2), żeby wyświetlić RGB, nie ARGB
        colorPreview.setText("#" + Integer.toHexString(col).toUpperCase().substring(2));
        colorPreview.setTextColor(contrast(col));
    }

    // ustawienie kontrastującego koloru, żeby kod koloru był widoczny
    private int contrast(int col) {
        float[] hsv = new float[3];
        Color.colorToHSV(col, hsv);
        if (hsv[2] > 0.5)
            hsv[2] = 0;
        else
            hsv[2] = 1;
        hsv[1] = 0;
        return Color.HSVToColor(hsv);
    }
}
