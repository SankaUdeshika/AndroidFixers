package lk.sankaudeshika.androidfixers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //        Global Map
        HashMap<String,String> sellerDetails = new HashMap<>();

        //      Add register_sub_component
        LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
        View RegisterView = inflater.inflate(R.layout.regsiter_sub_component, null, false);
        View RegisterView2 = inflater.inflate(R.layout.register_part2, null, false);

//        Add Liner Layouts
        LinearLayout linearLayout = findViewById(R.id.registerLienarLayout);
        linearLayout.addView(RegisterView);

        //        Load Spiner
        Spinner category_spinner = RegisterView2.findViewById(R.id.category_spinner);
        List<String> categories = new LinkedList<>();
        categories.add("Select You Service Category");

//        search category from Firebase
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("service_category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                        for (DocumentSnapshot item: list ) {
                            Log.i("appout", item.getString("sub_category").toString());
                            categories.add(item.getString("sub_category").toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("appout", "onFailure: Error");
                        new AlertDialog.Builder(RegisterView2.getContext()).setTitle("Error").setMessage("Something Worng PLease Try again later");

                    }
                });

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categories);
        categoryAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        category_spinner.setAdapter(categoryAdapter);
        //set OnlcickLister to spinner
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                Log.i("appout", "onItemSelected: "+i+" "+l+" "+selectedItem);
                sellerDetails.put("sub_category",selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });




//        Link Next Button


        Button button  =  RegisterView.findViewById(R.id.nextButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EditText registerMobile = RegisterView.findViewById(R.id.RegisterMainMobile);
                EditText registerCompanyMobile = RegisterView.findViewById(R.id.RegisterCompanyMobile);

//                mobile 1 Validation

                if (registerMobile.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }else if (registerMobile.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "mobile Number must have 10 characters", Toast.LENGTH_SHORT).show();
                }else if (!registerMobile.getText().toString().matches("^(?:\\+94|0)7[1245678]\\d{7}$")) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Mobile Number ", Toast.LENGTH_SHORT).show();
                }else
//                mobile 2 validation
                if (registerCompanyMobile.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }else if (registerCompanyMobile.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "mobile Number must have 10 characters", Toast.LENGTH_SHORT).show();
                }else if (!registerCompanyMobile.getText().toString().matches("^(?:\\+94|0)7[1245678]\\d{7}$")) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Mobile Number ", Toast.LENGTH_SHORT).show();
                }else{
                    sellerDetails.put("mobile_1",registerMobile.getText().toString());
                    sellerDetails.put("mobile_2",registerMobile.getText().toString());
                    setContentView(RegisterView2);
                }
            }
        });



//        Open Add new Category Popup
        Button newCategoryPopupBtn = RegisterView2.findViewById(R.id.addNewCategoryPopup);
        View AlertView =  inflater.inflate(R.layout.add_new_category,null,false);

        newCategoryPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext()).setView(AlertView).show();
            }
        });
//        New Category Type and Search
        EditText newCategoryInput = AlertView.findViewById(R.id.newCategory);
        newCategoryInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        firestore.collection("service_category")
                                .whereEqualTo("sub_category",newCategoryInput.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        int count = task.getResult().size();

                                        Log.i("appout", "onComplete: "+count);

                                        if(count == 0){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                     TextView CategoryStatus = AlertView.findViewById(R.id.textView17);
                                                    CategoryStatus.setTextColor(getColor(R.color.SuccessText));
                                                    CategoryStatus.setText("acceptable");
                                                }
                                            });
                                        }else if(count>0){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TextView CategoryStatus = AlertView.findViewById(R.id.textView17);
                                                    CategoryStatus.setTextColor(getColor(R.color.WarningText));
                                                    CategoryStatus.setText("Already Have a Category");
                                                }
                                            });
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }
                }).start();

                Log.i("appout", "onKey: "+newCategoryInput.getText().toString());
                return false;
            }
        });



//        Add new Category Process
        ImageButton ConfirmCategory =  AlertView.findViewById(R.id.ConfirmCategory);
        ConfirmCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("service_category")
                        .whereEqualTo("sub_category",newCategoryInput.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int count = task.getResult().size();

                                Log.i("appout", "onComplete: "+count);

                                if(count == 0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HashMap<String, Object> newCategory = new HashMap<>();
                                            newCategory.put("category","Indore");
                                            newCategory.put("sub_category",newCategoryInput.getText().toString());
                                            firestore.collection("service_category").add(newCategory)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                           sellerDetails.put("sub_category",newCategoryInput.getText().toString());
                                                           Toast.makeText(RegisterActivity.this, "Category Added Success", Toast.LENGTH_SHORT).show();
                                                           newCategoryPopupBtn.setVisibility(RegisterView2.GONE);
                                                           TextView textView18 = RegisterView2.findViewById(R.id.textView18);
                                                           textView18.setText("Your Category is Setted");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                           Toast.makeText(RegisterActivity.this, "Something wrong, please Try again later", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    });
                                }else if(count>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this, "Please Enter Valid Category", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


            }
        });


//      Register Process
        Button registerButton = RegisterView2.findViewById(R.id.RegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText registerName = RegisterView2.findViewById(R.id.RegisterName);
                EditText registerCompanyName = RegisterView2.findViewById(R.id.RegistercompmnyName);
                EditText registerEmail = RegisterView2.findViewById(R.id.RegisterEmail);
                EditText registerpassword = RegisterView2.findViewById(R.id.Registerpassword);

//                Validation
                if (registerName.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                }else if(registerCompanyName.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please Enter Comapany Name", Toast.LENGTH_SHORT).show();
                }else if(registerEmail.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }else if(!registerEmail.getText().toString().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                }else if(registerpassword.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
                }else{



                    firestore.collection("vendor")
                            .whereEqualTo("mobile_1",sellerDetails.get("mobile_1"))
                            .whereEqualTo("seller_company", registerCompanyName.getText().toString())
                            .whereEqualTo("status", "active")
                            .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.i("appout", "onSuccess: "+queryDocumentSnapshots.size());

                                            if(queryDocumentSnapshots.size() == 0){

                                                sellerDetails.put("seller_name",registerName.getText().toString());
                                                sellerDetails.put("seller_company",registerCompanyName.getText().toString());
                                                sellerDetails.put("password",registerpassword.getText().toString());
                                                sellerDetails.put("email",registerEmail.getText().toString());
                                                sellerDetails.put("location","none");
                                                sellerDetails.put("status","acive");





                                                firestore.collection("vendor").add(sellerDetails)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                                                                i.putExtra("RegisterMessage",1);
                                                                startActivity(i);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }else{
                                                new AlertDialog.Builder(RegisterActivity.this).setTitle("Already Have a Account").setMessage("already Have a Account,Please Sign in ").show();
                                            }

                                        }
                                    })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });



                }


            }
        });
    }
}