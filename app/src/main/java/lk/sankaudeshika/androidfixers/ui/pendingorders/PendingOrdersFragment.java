package lk.sankaudeshika.androidfixers.ui.pendingorders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.sankaudeshika.androidfixers.R;
import lk.sankaudeshika.androidfixers.databinding.FragmentPendingOrdersBinding;
import lk.sankaudeshika.androidfixers.model.PendingOrders;


public class PendingOrdersFragment extends Fragment {

    private FragmentPendingOrdersBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        GetShare Preferences Data
        SharedPreferences sp = requireActivity().getSharedPreferences("lk.sankaudeshika.androidfixers",Context.MODE_PRIVATE);
        String vendor_mobile = sp.getString("Default_mobile","null");

        //        Load Pending Orders

        View view = inflater.inflate(R.layout.fragment_pending_orders, container, false);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("booking")
                .whereEqualTo("vendor_mobile", vendor_mobile) // Filter by Vendor ID
                .whereEqualTo("status", "pending")    // Filter by Pending Status
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<PendingOrders> pendingOrdersList = new ArrayList<>();


                        List<DocumentSnapshot> list = task.getResult().getDocuments();

//                        Log.i("appout", "adasda: "+vendor_mobile);

                        for (DocumentSnapshot documentSnapshot : list) {
                            // Get your booking details here
                            String bookingId = documentSnapshot.getId();
                            String customerName = documentSnapshot.getString("cusomier_id");
                            // Handle each booking as needed
                            Log.i("appout", "adasda: "+customerName);
                            pendingOrdersList.add(new PendingOrders("asdaiusd"));
                        }

                        RecyclerView pendingJosBox = view.findViewById(R.id.pendingOrdersRecycleView);
                        pendingJosBox.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
                        pendingJosBox.setAdapter(new OrderAdapter(pendingOrdersList, view.getContext()));
                    }
                });
//        pendingOrdersList.add(new PendingOrders("OK"));





        return view;
    }
}


class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.PendingOrderViewHolder>{


    class PendingOrderViewHolder extends RecyclerView.ViewHolder{
        TextView textExample ;

        public PendingOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textExample = itemView.findViewById(R.id.textView21);
        }
    }
    private ArrayList<PendingOrders> pendingOrdersArrayList;
    private Context context;

    public OrderAdapter(ArrayList<PendingOrders> orderArrayList,Context contextfrom){

        this.pendingOrdersArrayList = orderArrayList;
        this.context=contextfrom;
    }

    @NonNull
    @Override
    public PendingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater  = LayoutInflater.from(parent.getContext());
        View PendingItem = layoutInflater.inflate(R.layout.pending_order_layouts,parent,false);
        PendingOrderViewHolder pendingOrderViewHolder = new PendingOrderViewHolder(PendingItem);

        return pendingOrderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrderViewHolder holder, int position) {
        PendingOrders pendingOrdersObject = pendingOrdersArrayList.get(position);
        Log.i("appout", "onBindViewHolder: " +pendingOrdersArrayList.get(position).getOrderText());
        holder.textExample.setText(pendingOrdersArrayList.get(position).getOrderText());
    }

    @Override
    public int getItemCount() {
        return pendingOrdersArrayList.size();
    }
}