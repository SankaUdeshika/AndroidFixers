package lk.sankaudeshika.androidfixers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import lk.sankaudeshika.androidfixers.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    //        pichart Variables
    float DoneCount ;
    float PendingCount ;
    String vendor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        SharedPreferences Details
        SharedPreferences sp = getSharedPreferences("lk.sankaudeshika.androidfixers",Context.MODE_PRIVATE);
        vendor_id = sp.getString("Default_vendor_id","null");





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Load Pie Chart
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("booking")
                .whereEqualTo("vendor_id",vendor_id)
                .whereEqualTo("status","pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        PendingCount = task.getResult().size();
                        Log.i("appout", "onComplete: "+task.getResult().size());

                        firestore.collection("booking")
                                .whereEqualTo("vendor_id",vendor_id)
                                .whereEqualTo("status","done")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        DoneCount = task.getResult().size();
                                        Log.i("appout", "onComplete: "+task.getResult().size());

                                        Log.i("appout", "onComplete: "+PendingCount);

                                        PieChart pieChart = findViewById(R.id.HomepieChart);
                                        ArrayList<PieEntry> pieEntryList = new ArrayList<>();
                                        pieEntryList.add(new PieEntry(DoneCount, "Orders"));
                                        pieEntryList.add(new PieEntry(PendingCount, "Pending Orders"));


                                        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "Your Progress");
                                        ArrayList<Integer> colorArray = new ArrayList<>();
                                        colorArray.add(getColor(R.color.OrderColors));
                                        colorArray.add(getColor(R.color.CompletionColors));
                                        pieDataSet.setColors(colorArray);

                                        PieData pieData = new PieData();
                                        pieData.setDataSet(pieDataSet);
                                        pieData.setValueTextSize(18);
                                        pieChart.setData(pieData);
                                        pieChart.invalidate();


                                    }
                                });


                    }
                });



    }
}