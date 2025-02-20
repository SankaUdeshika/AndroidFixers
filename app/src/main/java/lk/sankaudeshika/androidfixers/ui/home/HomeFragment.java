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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import lk.sankaudeshika.androidfixers.ShopLocatoinActivity;
import lk.sankaudeshika.androidfixers.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    Intent i;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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