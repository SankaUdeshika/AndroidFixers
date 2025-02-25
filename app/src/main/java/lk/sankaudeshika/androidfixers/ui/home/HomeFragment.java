package lk.sankaudeshika.androidfixers.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.sankaudeshika.androidfixers.R;
import lk.sankaudeshika.androidfixers.ShopLocatoinActivity;
import lk.sankaudeshika.androidfixers.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    Intent i;
    //        pichart Variables
    float DoneCount ;
    float PendingCount ;
    String vendor_id;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //        SharedPreferences Details
        SharedPreferences sp = requireActivity().getSharedPreferences("lk.sankaudeshika.androidfixers", Context.MODE_PRIVATE);
        vendor_id = sp.getString("Default_vendor_id","null");
        Log.i("appout", "onCreateView: "+vendor_id);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        //        Load Pie Chart
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
                                        PieChart pieChart = requireActivity().findViewById(R.id.HomepieChart);
                                        ArrayList<PieEntry> pieEntryList = new ArrayList<>();
                                        pieEntryList.add(new PieEntry(50, "Orders"));
                                        pieEntryList.add(new PieEntry(30, "Pending Orders"));

                                        for (PieEntry item: pieEntryList) {
                                            Log.i("appout", "o"+String.valueOf(item.getValue()));
                                        }

                                        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "Your Progress");
                                        ArrayList<Integer> colorArray = new ArrayList<>();
                                        colorArray.add(getResources().getColor(R.color.OrderColors));
                                        colorArray.add(getResources().getColor(R.color.CompletionColors));
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


//        Send Shop Location Activity
//        SharedPreferences sp = requireActivity().getSharedPreferences("lk.sankaudeshika.androidfixers", Context.MODE_PRIVATE);
        String mobile = sp.getString("Default_mobile","null");
        firestore.collection("vendor")
                .whereEqualTo("mobile_1",mobile)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> snapShots = task.getResult().getDocuments();
                        for (DocumentSnapshot item: snapShots) {
                            if(item.getString("locaiton").equals("none")){
                                Log.i("appout", "onComplete: ");

                                Intent intent = new Intent(root.getContext(), ShopLocatoinActivity.class);
                                i = intent;
                                startActivity(intent);
                            }
                        }
                    }
                });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
//      Load Pie Chart
        SharedPreferences sp = requireActivity().getSharedPreferences("lk.sankaudeshika.androidfixers", Context.MODE_PRIVATE);
        String mobile = sp.getString("Default_mobile","null");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("vendor")
                .whereEqualTo("mobile_1",mobile)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> snapShots = task.getResult().getDocuments();
                        for (DocumentSnapshot item: snapShots) {
                            if(item.getString("locaiton").equals("none")){
                                Log.i("appout", "onComplete: ");
                                startActivity(i);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}