package casariego.jorge.airport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.estimote.sdk.SystemRequirementsChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * The checkWithDefaultDialogs will use default system dialogs if it needs to ask the user
         * for anything—e.g., to turn Bluetooth or Location on, or … yes,
         * ask for ACCESS_COARSE_LOCATION permission.
         *
         * In production: in a production app, you might want to design your own UI, or even an
         * entire onboarding process, to explain why your app requires location and Bluetooth,
         * and what’s in it for the user.
         */
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
}
