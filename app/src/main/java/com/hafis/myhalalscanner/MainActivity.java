package com.hafis.myhalalscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hafis.myhalalscanner.activity.AboutUsActivity;
import com.hafis.myhalalscanner.activity.ChooserActivity;
import com.hafis.myhalalscanner.activity.bmi.BmiActivity;
import com.hafis.myhalalscanner.model.Ingredient;
import com.hafis.myhalalscanner.tool.Constant;
import com.hafis.myhalalscanner.tool.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onScannerClick(View view) {
        startActivity(new Intent(MainActivity.this, ChooserActivity.class));
    }

    public void onAboutUsClick(View view) {
        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
    }

    public void onUpdateClick(View view) {
        Utils.ShowProgressDialog(MainActivity.this);
        view.setEnabled(false);
        //Delete the old one
        FastSave.getInstance().deleteValue(Constant.FAST_SAVE_INGREDIENT_LIST);
        new Thread(() -> {
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(Constant.INGREDIENT);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        List<Ingredient> ll = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            final Ingredient i = dataSnapshot.getValue(Ingredient.class);
                            if (i != null) {
                                final String id = i.getId().trim();
                                final String status = i.getStatus().trim();
                                final String name = i.getName().trim();
                                final String description = i.getDescription().trim();
                                ll.add(new Ingredient(id, name, description, status));
                            }
                        }
                        runOnUiThread(() -> {
                            //And then here we store the new value inside the fastSave which is SharedPreference
                            FastSave.getInstance().saveObjectsList(Constant.FAST_SAVE_INGREDIENT_LIST, ll);
                            Utils.DismissProgressDialog();
                            view.setEnabled(true);
                            Utils.ShowToast(MainActivity.this, "Data updated successfully");
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }).start();
    }

    public void onBMIClick(View view) {
        startActivity(new Intent(MainActivity.this, BmiActivity.class));
    }
}