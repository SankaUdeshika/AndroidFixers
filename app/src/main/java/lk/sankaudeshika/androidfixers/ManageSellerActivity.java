package lk.sankaudeshika.androidfixers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import lk.sankaudeshika.androidfixers.model.Seller;
import lk.sankaudeshika.androidfixers.model.ServerURL;

public class ManageSellerActivity extends AppCompatActivity {

    SellerAdapter sellerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_seller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("vendor").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                        ArrayList<Seller> sellersArraylist = new ArrayList<>();
                        for(DocumentSnapshot documentItem : documentList){
                            Seller seller = new Seller(
                                    documentItem.getId(),
                                    documentItem.getString("email"),
                                    documentItem.getString("mobile_1"),
                                    documentItem.getString("seller_company"),
                                    documentItem.getString("seller_name"),
                                    documentItem.getString("status"),
                                    documentItem.getString("sub_category")
                            );
                            sellersArraylist.add(seller);
                        }

                        sellerAdapter = new SellerAdapter(sellersArraylist,ManageSellerActivity.this);
                        RecyclerView recyclerView = findViewById(R.id.activitySellerReccleView);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageSellerActivity.this,LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        recyclerView.setAdapter(sellerAdapter);

                    }
                });





    }
}


class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.SellerViewHolder>{

    class SellerViewHolder extends RecyclerView.ViewHolder{
        TextView SellerName;
        TextView CompanyName;
        TextView Email;
        TextView Mobile;
        Button DeleteButton;
        ImageView imageView8;
        public SellerViewHolder(@NonNull View itemView){
            super(itemView);
            SellerName =  itemView.findViewById(R.id.SellerName);
            CompanyName = itemView.findViewById(R.id.CompanyName);
            Email = itemView.findViewById(R.id.Email);
            Mobile = itemView.findViewById(R.id.Mobile);
            imageView8 = itemView.findViewById(R.id.imageView8);
            DeleteButton  = itemView.findViewById(R.id.DeleteButton);
//            ViewServiceBtn = itemView.findViewById(R.id.ViewServiceBtn);
//            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }

    ArrayList<Seller> ServiceArraylist;
    Context context;

    public SellerAdapter(ArrayList serviceArraylist1, Context context) {
        ServiceArraylist = serviceArraylist1;
        this.context = context;
    }

    @NonNull
    @Override
    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View serviceView = layoutInflater.inflate(R.layout.manage_recycle_seller_item_view,parent,false);
        SellerViewHolder serviceViewHolder = new SellerViewHolder(serviceView);
        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerViewHolder holder, int position) {
        Seller service = ServiceArraylist.get(position);
        holder.SellerName.setText(service.getSeller_name());
        holder.CompanyName.setText(service.getSeller_company());
        holder.Email.setText(service.getEmail());
        holder.Mobile.setText(service.getMobile_1());


        holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("vendor").document(service.getId()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(view.getContext(),ManageSellerActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        });
            }
        });
        Picasso.get()
                .load(ServerURL.ServerImages+service.getId()+"seller_profileImage.jpg")
                .resize(500, 500)
                .centerCrop()
                .into(holder.imageView8);

//        Log.i("appout", "onBindViewHolder: "+service.getMobile_1());
    }

    @Override
    public int getItemCount() {
        return ServiceArraylist.size();
    }
}