package com.example.android.budgety;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BottomSheetListDialogFragment extends BottomSheetDialogFragment {

    int y, m, d;
    public static int counter = 1;

    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    String UserId;
    String category;
    String desc;
    double amount;
    Date date;
    int method = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        UserId = fAuth.getCurrentUser().getUid();


        View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);
        final TextInputEditText textInputEditText = view.findViewById(R.id.date_holder);
        final Spinner TransSpinner = view.findViewById(R.id.TransactionSpinner);
        final Calendar calendar = Calendar.getInstance();
        final ChipGroup TranschipGroup = (ChipGroup) view.findViewById(R.id.TransChip_group);


        //make the list of category depend on Transaction method
        TranschipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                String[] list;
                ArrayAdapter<String> spinnerArrayAdapter;

                if (TranschipGroup.getCheckedChipId() == R.id.income) {
                    list = getResources().getStringArray(R.array.incom);
                    method = 1;
                } else if (TranschipGroup.getCheckedChipId() == R.id.expenses) {
                    list = getResources().getStringArray(R.array.expenses);
                    method = 2;
                } else {
                    ArrayList<Budget> budgets = MainActivity.account.getBudgets();
                    list = new String[budgets.size()];
                    method = 3;
                    for (int i = 0; i < list.length; i++) {
                        list[i] = budgets.get(i).getbName();
                    }
                }
                spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item
                        , list);
                TransSpinner.setAdapter(spinnerArrayAdapter);
            }
        });


        //
        final TextView decsText = view.findViewById(R.id.desc_holder);
        final TextView amountText = view.findViewById(R.id.amount_holder);
        final ChipGroup DatechipGroup = (ChipGroup) view.findViewById(R.id.Datechip_group);
        final TextView dateText = view.findViewById(R.id.date_holder);
        final MaterialButton b = view.findViewById(R.id.AddTransactionButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int Trans_chip_id = TranschipGroup.getCheckedChipId();
                category = TransSpinner.getSelectedItem().toString();
                desc = decsText.getText().toString();
                String textAmount = amountText.getText().toString();
                if (TextUtils.isEmpty(textAmount)) {
                    amountText.setError("Amount is required!");
                    return;
                }
                amount = Double.parseDouble(amountText.getText().toString());

                int Date_chip_id = DatechipGroup.getCheckedChipId();


                date = null;
                if (Date_chip_id == R.id.today) {
                    date = new Date();
                } else if (Date_chip_id == R.id.yasterday) {
                    date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                } else {
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        date = formatter1.parse(dateText.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                MainActivity.account.makeTransaction(Trans_chip_id, amount, category, desc, date);
                Date currentMonth = new Date();
                if (currentMonth.getMonth() == date.getMonth()) {
                    MainActivity.account.setCurrentList(currentMonth.getMonth());
                    home.myAdapter.notifyItemInserted(0);

                }
                addTransaction(category, desc, amount, Trans_chip_id, date);
                FragmentManager fm = getFragmentManager();
                if ((fm.findFragmentById(R.id.fragment_container) instanceof home)) {
                    home home = (home) fm.findFragmentById(R.id.fragment_container);
                    home.updateHeader(home.getView());
                } else {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container,
                            new home()).commit();
                }
                dismiss();
                Home_page.bottomNav.getMenu().getItem(0).setChecked(true);
            }
        });

        DatechipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.date) {
                    y = calendar.get(Calendar.YEAR);
                    m = calendar.get(Calendar.MONTH);
                    d = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            textInputEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        }
                    }, y, m, d);
                    datePickerDialog.show();
                }
            }


        });
        return view;
    }


    public void addTransaction(String category, String desc, double amount, int method, Date date) {

        DocumentReference documentReference = firestore.collection("users").document(UserId).collection("transactions").document();
        counter++;
        // set the variables inside the user and set it in the database in firebase
        Map<String, Object> user = new HashMap<>();

        user.put("Category", category);
        user.put("desc", desc);
        user.put("amount", amount);
        user.put("date", date.toString());
        user.put("method", method);


        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(null, " success" + UserId);
            }
        });


    }

}

