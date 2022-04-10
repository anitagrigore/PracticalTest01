package ro.pub.cs.systems.eim.practicaltest01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PracticalTest01SecondaryActivity extends AppCompatActivity {
    Button okButton, cancelButton;
    TextView numberOfClicks;

    ButtonClickListener buttonClickListener = new ButtonClickListener();
    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ok:
                    setResult(RESULT_OK, null);
                    break;
                case R.id.cancel:
                    setResult(RESULT_CANCELED, null);
                    break;
            }
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_secondary);

        numberOfClicks = findViewById(R.id.number_of_clicks);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras().containsKey(Constants.NUMBER_OF_CLICKS)) {
            int numberOfClicksValue = intent.getIntExtra(Constants.NUMBER_OF_CLICKS, -1);
            numberOfClicks.setText(String.valueOf(numberOfClicksValue));
        }

        okButton = findViewById(R.id.ok);
        okButton.setOnClickListener(buttonClickListener);

        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(buttonClickListener);
    }
}