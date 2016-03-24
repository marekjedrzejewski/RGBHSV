package pl.marekjedrzejewski.rgbtohsv;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
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

        // setting the proper range for pickers and value change listeners
        numberR = initNumberPickerRGB(R.id.numberR);
        numberG = initNumberPickerRGB(R.id.numberG);
        numberB = initNumberPickerRGB(R.id.numberB);

        numberH = initNumberPickerH(R.id.numberH);
        numberS = initNumberPickerSV(R.id.numberS);
        numberV = initNumberPickerSV(R.id.numberV);

        colorPreview = (TextView)findViewById(R.id.colorPreview);
    }

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

        // Representation on numberpicker as float in range 0-1, step 0.01
        String floatstrings[] = new String[101];
        for (int i = 0; i < 101; i++) {
            floatstrings[i] = String.valueOf((float)(i) / 100.f);
        }
        numberPicker.setDisplayedValues(floatstrings);

        // display numeric keyboard instead of text for it
        ((EditText)numberPicker.getChildAt(0)).setRawInputType(InputType.TYPE_CLASS_NUMBER);

        numberPicker.setOnValueChangedListener(hsvChangeListener);
        return numberPicker;
    }

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
        // .substring(2), because we want RGB instead of ARGB
        colorPreview.setText("#" + Integer.toHexString(col).toUpperCase().substring(2));
        colorPreview.setTextColor(contrast(col));
    }

    // That's necessary for the color code to be visible on the preview
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
