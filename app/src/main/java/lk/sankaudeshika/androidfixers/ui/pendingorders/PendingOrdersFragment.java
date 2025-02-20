package lk.sankaudeshika.androidfixers.ui.pendingorders;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.sankaudeshika.androidfixers.R;
import lk.sankaudeshika.androidfixers.databinding.FragmentPendingOrdersBinding;


public class PendingOrdersFragment extends Fragment {

    private FragmentPendingOrdersBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_pending_orders, container, false);
    }
}