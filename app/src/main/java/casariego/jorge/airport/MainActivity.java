package casariego.jorge.airport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *      1. What is beacon monitoring: You can think of beacon monitoring as a geofence, i.e., a virtual
 *      barrier that’s usually defined using a set of geographic coordinates. Moving and out of
 *      the area it encloses triggers “enter” and “exit” events, which the app can react to.
 *
 *      2. With beacon regions, you can say, “I’m only interested in beacons with UUID ‘ABC’
 *      and major ‘XYZ’.” Or you can be more specific, adding a minor requirement to the mix
 *
 *      3. When monitoring a region that spans multiple beacons, there will be a single “enter”
 *      event when the first matching beacon is detected; and a single “exit” event when none of
 *      the matching beacons can be detected anymore.
 *
 *      4. Our monitoring use case was quite trivial—just show a notification.
 *      With ranging, our goal is to show nearby options to a grab a snack, which involves tying
 *      two pieces of data together: identifiers of nearby beacons, and a list of food options.
 *
 *          First, we need a data structure to hold the food options and beacons.
 *          Second, we need to code up a simple algorithm:
 *              - Take the closest beacon.
 *              - Look up all the food places closest to that beacon.
 */
public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;
    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("64160:33963", new ArrayList<String>() {{
            add("Doña Angela");
            // read as: "Heavenly Sandwiches" is closest
            // to the beacon with major 22504 and minor 48827
            add("Campo Alto");
            // "Green & Green Salads" is the next closest
            add("Edificio Alexia");
            // "Mini Panini" is the furthest away
        }});
        placesByBeacons.put("45082:34365", new ArrayList<String>() {{
            add("La Vienesa");
            add("Lavadero de Autos");
            add("Ñemityra");
        }});

        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    Log.d("Airport", "Nearest places: " + places);
                }
            }
        });


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

        // Code to start and stop ranging as the activity appears and disappears on screen.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                //beaconManager.startMonitoring(region);
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        //beaconManager.stopMonitoring(region);
        beaconManager.stopRanging(region);
        super.onPause();

    }

    private List<String> placesNearBeacon(Beacon beacon){
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());

        if(PLACES_BY_BEACONS.containsKey(beaconKey)){
            return PLACES_BY_BEACONS.get(beaconKey);
        }

        return Collections.emptyList();
    }

}
