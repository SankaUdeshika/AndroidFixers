package lk.sankaudeshika.androidfixers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.sankaudeshika.androidfixers.model.Customer;
import lk.sankaudeshika.androidfixers.model.Seller;
import lk.sankaudeshika.androidfixers.model.ServerURL;

public class ManageCustomerActivity extends AppCompatActivity {

    CustomerAdapter customerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                        ArrayList<Customer> customerArraylist = new ArrayList<>();
                        for(DocumentSnapshot documentItem : documentList){
                            Customer customer = new Customer(
                                    documentItem.getId(),
                                    documentItem.getString("name"),
                                    documentItem.getString("email"),
                                    documentItem.getString("mobile"),
                                    documentItem.getString("status"),
                                    documentItem.getString("address")
                            );
                            customerArraylist.add(customer);
                        }

                        customerAdapter = new CustomerAdapter(customerArraylist,ManageCustomerActivity.this);
                        RecyclerView recyclerView = findViewById(R.id.customerRecycleView);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageCustomerActivity.this,LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(customerAdapter);
                    }
                });


    }
}


class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>{

    class CustomerViewHolder extends RecyclerView.ViewHolder{
        TextView customeradminName;
        TextView customerNameEmail;
        TextView customerNameMobile;
        TextView customerMobileAddress;
        TextView customerStatus;
        Button CutomerDelete;
        ImageView customerImage;
        public CustomerViewHolder(@NonNull View itemView){
            super(itemView);
            customeradminName =  itemView.findViewById(R.id.customeradminName);
            customerNameEmail = itemView.findViewById(R.id.customerNameEmail);
            customerNameMobile = itemView.findViewById(R.id.customerNameMobile);
            customerMobileAddress = itemView.findViewById(R.id.customerMobileAddress);
            customerStatus = itemView.findViewById(R.id.customerStatus);
            CutomerDelete = itemView.findViewById(R.id.CutomerDelete);
            customerImage  = itemView.findViewById(R.id.customerImage);
        }
    }

    ArrayList<Customer> CustomerArraylist;
    Context context;

    public CustomerAdapter(ArrayList customerArraylist1, Context context) {
        CustomerArraylist = customerArraylist1;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View customerView = layoutInflater.inflate(R.layout.customer_details_item,parent,false);
        CustomerViewHolder customerViewHolder = new CustomerViewHolder(customerView);
        return customerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = CustomerArraylist.get(position);
        holder.customeradminName.setText(customer.getName());
        holder.customerNameEmail.setText(customer.getEmail());
        holder.customerNameMobile.setText(customer.getMobile());
        holder.customerMobileAddress.setText(customer.getAddress());
        holder.customerStatus.setText(customer.getStatus());

        holder.CutomerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("user").document(customer.getId()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(view.getContext(),ManageCustomerActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        });
            }
        });
        try {
            Picasso.get()
                    .load(ServerURL.ServerImages+customer.getId()+"_profileImage.jpg")
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.customerImage);
        } catch (Exception e) {
            Log.e("appout", "onBindViewHolder: "+e.toString() );
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return CustomerArraylist.size();
    }
}

