package ro.pub.cs.systems.eim.practicaltest01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.BreakIterator;

public class PracticalTest01MainActivity extends AppCompatActivity {
    Button pressMeButton;
    Button nextActivityButton;
    Button pressMeTooButton;
    EditText pressMeText;
    EditText pressMeTooText;

    private int serviceStatus = Constants.SERVICE_STOPPED;
    private IntentFilter intentFilter = new IntentFilter();

    ButtonClickListener buttonClickListener = new ButtonClickListener();
    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int leftNumberOfCLicks = Integer.parseInt(pressMeText.getText().toString());
            int rightNumberOfCLicks = Integer.parseInt(pressMeTooText.getText().toString());
            switch (view.getId()) {
                case R.id.press_me:
                    leftNumberOfCLicks++;
                    pressMeText.setText(String.valueOf(leftNumberOfCLicks));
                    break;
                case R.id.press_me_again:
                    rightNumberOfCLicks++;
                    pressMeTooText.setText(String.valueOf(rightNumberOfCLicks));
                    break;

                case R.id.secondaryActivity:
                    Intent intent = new Intent(getApplicationContext(), PracticalTest01SecondaryActivity.class);
                    int numberOfClicks = Integer.parseInt(pressMeText.getText().toString()) +
                            Integer.parseInt(pressMeTooText.getText().toString());
                    intent.putExtra(Constants.NUMBER_OF_CLICKS, numberOfClicks);
                    startActivity(intent);
                    break;
            }

            if (leftNumberOfCLicks + rightNumberOfCLicks > Constants.NUMBER_OF_CLICKS_THRESHOLD
                    && serviceStatus != Constants.SERVICE_STOPPED) {
                Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
                intent.putExtra(Constants.FIRST_NUMBER, leftNumberOfCLicks);
                intent.putExtra(Constants.SECOND_NUMBER, rightNumberOfCLicks);
                getApplicationContext().startService(intent);
                serviceStatus = Constants.SERVICE_STARTED;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_main);

        pressMeButton = findViewById(R.id.press_me);
        pressMeButton.setOnClickListener(buttonClickListener);

        nextActivityButton = findViewById(R.id.secondaryActivity);
        nextActivityButton.setOnClickListener(buttonClickListener);

        pressMeTooButton = findViewById(R.id.press_me_again);
        pressMeTooButton.setOnClickListener(buttonClickListener);

        pressMeText = findViewById(R.id.press_me_text);
        pressMeTooText = findViewById(R.id.press_me_again_text);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.LEFT_COUNT)) {
                pressMeText.setText(savedInstanceState.getString(Constants.LEFT_COUNT));
            } else {
                pressMeText.setText(String.valueOf(0));
            }
            if (savedInstanceState.containsKey(Constants.RIGHT_COUNT)) {
                pressMeTooText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
            } else {
                pressMeTooText.setText(String.valueOf(0));
            }
        } else {
            pressMeText.setText(String.valueOf(0));
            pressMeTooText.setText(String.valueOf(0));
        }

        for (int index = 0; index < Constants.actionTypes.length; index++) {
            intentFilter.addAction(Constants.actionTypes[index]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.LEFT_COUNT, pressMeText.getText().toString());
        savedInstanceState.putString(Constants.RIGHT_COUNT, pressMeTooText.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(Constants.LEFT_COUNT)) {
            pressMeText.setText(savedInstanceState.getString(Constants.LEFT_COUNT));
        } else {
            pressMeText.setText(String.valueOf(0));
        }
        if (savedInstanceState.containsKey(Constants.RIGHT_COUNT)) {
            pressMeTooText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
        } else {
            pressMeTooText.setText(String.valueOf(0));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constants.SECONDARY_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "The activity returned with result " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.BROADCAST_RECEIVER_TAG, intent.getStringExtra(Constants.BROADCAST_RECEIVER_EXTRA));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(messageBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, PracticalTest01Service.class);
        stopService(intent);
        super.onDestroy();
    }
}