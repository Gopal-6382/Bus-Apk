package com.example.bus;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.bus.databinding.ActivityMainBinding;
import com.example.bus.ui.Search_Bus.SearchOnline;
import com.example.bus.ui.Search_Bus.Search_Bus;
import com.example.bus.ui.busTiming.Bus_timing;
import com.example.bus.ui.dbs.AppDatabase;
import com.example.bus.ui.dbs.DatabaseSeeder;
import com.example.bus.ui.dbs.FirestoreUploader;
import com.example.bus.ui.feedback.FeedbackFragment;
import com.example.bus.ui.home.HomeFragment;
import com.example.bus.ui.login.LoginActivity;
import com.example.bus.ui.login.SignupActivity;
import com.example.bus.ui.prediction.BusTimingPredictor;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MainActivity";
    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // ✅ More efficient
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // ❌ Disable offline mode
                .build();
        // Call prediction
        // Call the prediction function
//BusTimingPredictor predictor = new BusTimingPredictor(this);
  //      predictor.runPrediction();

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // 🔹 Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.bus_timing,
                R.id.search_bus,
                R.id.nav_feedback,
                R.id.nav_settings,
                R.id.search_bus_online
        ).setOpenableLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // ✅ Ensure database is initialized before seeding
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            DatabaseSeeder.populateDatabase(this);
            verifyDatabaseData(db);
        });
        // ✅ Navigation Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            if (item.getItemId() == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.nav_feedback) {
                loadFragment(new FeedbackFragment());
            } else if (item.getItemId() == R.id.search_bus) {
                loadFragment(new Search_Bus());
            } else if (item.getItemId() == R.id.search_bus_online) {
                loadFragment(new SearchOnline());
            }else if (item.getItemId() == R.id.bus_timing) {
                loadFragment(new Bus_timing());
            } else if (item.getItemId() == R.id.nav_add_account) {
                addNewAccount();
            } else if (item.getItemId() == R.id.nav_logout) {
                logoutUser();
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
            }

            drawer.closeDrawers();
            return true;

        });

        // ✅ Fetch Firestore Data
    }


    // 🔹 Load Fragment Helper
    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .commit();
    }

    // ✅ Verify Database
    private void verifyDatabaseData(AppDatabase db) {
        executorService.execute(() -> {
            logData("Districts", db.daoInterfaces().getAllDistricts());
            logData("Taluks", db.daoInterfaces().getAllTaluks());
            logData("Bus Stands", db.daoInterfaces().getAllBusStands());
            logData("Routes", db.daoInterfaces().getAllRoutes());
            logData("Buses", db.daoInterfaces().getAllBuses());
            logData("Stops", db.daoInterfaces().getAllStops());
            logData("Timings", db.daoInterfaces().getAllTimings());
            Log.d(TAG, "✅ Database Verification Complete!");
        });
    }

    // ✅ Log Data Helper
    private <T> void logData(String label, List<T> data) {
        Log.d(TAG, "📌 " + label + ":");
        for (T item : data) {
            Log.d(TAG, "   ➡ " + item.toString());
        }
    }

    // ✅ Check Internet
    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }

    // ✅ Logout
    private void logoutUser() {
        SharedPreferences.Editor editor = getSharedPreferences("UserSession", MODE_PRIVATE).edit();
        editor.clear().apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ✅ Add Account
    private void addNewAccount() {
        startActivity(new Intent(MainActivity.this, SignupActivity.class));
    }

    // ✅ Back Navigation
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // ✅ Shutdown Executor
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }


}
