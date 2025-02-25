package lk.sankaudeshika.androidfixers.ui.pendingorders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

                        for (DocumentSnapshot documentSnapshot : list) {

                            String bookingId = documentSnapshot.getId();
                            String date = documentSnapshot.getString("date");
                            String time = documentSnapshot.getString("time");
                            String cutomer_mobile = documentSnapshot.getString("cusomier_id");
                            pendingOrdersList.add(new PendingOrders(cutomer_mobile,date,time,bookingId));

                        }


                        for (PendingOrders pendingItem: pendingOrdersList) {
                            Log.i("appout", "listIS: "+pendingItem.getBookingID());
                        }


                        RecyclerView pendingJosBox = view.findViewById(R.id.pendingOrdersRecycleView);
                        pendingJosBox.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
                        pendingJosBox.setAdapter(new OrderAdapter(pendingOrdersList, view.getContext()));

                    }
                });





        return view;
    }
}


class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.PendingOrderViewHolder>{


    class PendingOrderViewHolder extends RecyclerView.ViewHolder{
        TextView customerMobile ;
        TextView customerName;
        TextView bookingId;
        TextView date;
        Button approvedBtn;
        Button orderCancel;
        public PendingOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.CustomerName);
            customerMobile = itemView.findViewById(R.id.customerMobile);
            date = itemView.findViewById(R.id.datetime);
            bookingId = itemView.findViewById(R.id.bookingID);
            approvedBtn = itemView.findViewById(R.id.approvedBtn);
            orderCancel = itemView.findViewById(R.id.orderCancel);

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
        Log.i("appout", "onBindViewHolder: " +pendingOrdersArrayList.get(position).getBookingID());
        holder.customerName.setText(pendingOrdersArrayList.get(position).getBookingID());
        holder.customerMobile.setText(pendingOrdersArrayList.get(position).getCustomerMobile());
        holder.date.setText(pendingOrdersArrayList.get(position).getDate());
        holder.bookingId.setText(pendingOrdersArrayList.get(position).getBookingID());
        holder.approvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookingId = holder.bookingId.getText().toString();
                Log.i("appout", "onClicked"+bookingId);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                HashMap<String, Object> updateStatusMap = new HashMap<>();
                updateStatusMap.put("status","active");
                firestore.collection("booking").document(bookingId).update(updateStatusMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               RecyclerView recyclerView = view.findViewById(R.id.pendingOrdersRecycleView);
                                new AlertDialog.Builder(view.getContext()).setTitle("Update Success").setMessage("Order Appoinment is Successfully Active.").show();

                            }
                        });
            }
        });

        holder.orderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookingId = holder.bookingId.getText().toString();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                HashMap<String, Object> updateStatusMap = new HashMap<>();
                updateStatusMap.put("status","cancel");
                firestore.collection("booking").document(bookingId).update(updateStatusMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                RecyclerView recyclerView = view.findViewById(R.id.pendingOrdersRecycleView);
                                new AlertDialog.Builder(view.getContext()).setTitle("Order Canceled").setMessage("Order Appoinment is Successfully Canceled.").show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingOrdersArrayList.size();
    }
}