/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hafis.myhalalscanner.textdetector;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.hafis.myhalalscanner.R;
import com.hafis.myhalalscanner.enumarator.Status;
import com.hafis.myhalalscanner.ml.GraphicOverlay;
import com.hafis.myhalalscanner.ml.VisionProcessorBase;
import com.hafis.myhalalscanner.model.Ingredient;
import com.hafis.myhalalscanner.tool.Constant;
import com.hafis.myhalalscanner.tool.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor for the text detector demo.
 */
public class TextRecognitionProcessor extends VisionProcessorBase<Text> {

    private static final String TAG = "TextRecProcessor";

    private final TextRecognizer textRecognizer;
    private final Context context;

    public TextRecognitionProcessor(Context context) {
        super(context);
        this.context = context;
        textRecognizer = TextRecognition.getClient();
    }

    private static void logExtrasForTesting(Text text) {
        if (text != null) {
            Log.v(MANUAL_TESTING_LOG, "Detected text has : " + text.getTextBlocks().size() + " blocks");
            for (int i = 0; i < text.getTextBlocks().size(); ++i) {
                List<Line> lines = text.getTextBlocks().get(i).getLines();
                Log.v(
                        MANUAL_TESTING_LOG,
                        String.format("Detected text block %d has %d lines", i, lines.size()));
                for (int j = 0; j < lines.size(); ++j) {
                    List<Element> elements = lines.get(j).getElements();
                    Log.v(
                            MANUAL_TESTING_LOG,
                            String.format("Detected text line %d has %d elements", j, elements.size()));
                    for (int k = 0; k < elements.size(); ++k) {
                        Element element = elements.get(k);
                        Log.v(
                                MANUAL_TESTING_LOG,
                                String.format("Detected text element %d says: %s", k, element.getText()));
                        Log.v(
                                MANUAL_TESTING_LOG,
                                String.format(
                                        "Detected text element %d has a bounding box: %s",
                                        k, element.getBoundingBox().flattenToString()));
                        Log.v(
                                MANUAL_TESTING_LOG,
                                String.format(
                                        "Expected corner point size is 4, get %d", element.getCornerPoints().length));
                        for (Point point : element.getCornerPoints()) {
                            Log.v(
                                    MANUAL_TESTING_LOG,
                                    String.format(
                                            "Corner point for element %d is located at: x - %d, y = %d",
                                            k, point.x, point.y));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        textRecognizer.close();
    }

    @Override
    protected Task<Text> detectInImage(InputImage image) {
        return textRecognizer.process(image);
    }

    @Override
    protected void onSuccess(@NonNull Text text, @NonNull GraphicOverlay graphicOverlay) {
        Log.d(TAG, "On-device Text detection successful");
        //logExtrasForTesting(text);
        //graphicOverlay.add(new TextGraphic(graphicOverlay, text));
        comparisonIngredientCode(text);
    }

    private void comparisonIngredientCode(final Text text) {
        Utils.ShowProgressDialog(context);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(Constant.INGREDIENT);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Status isHalal = Status.No_RECORD_FOUND;
                    boolean isDataAda = false;
                    List<Ingredient> iiList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.i("???", "DataSnapshot");
                        final Ingredient i = dataSnapshot.getValue(Ingredient.class);
                        if (i != null && text.getText().toUpperCase().contains(i.getId().trim().toUpperCase())) {
                            Log.i("???", "halal");
                            final String status = i.getStatus().trim();
                            if (status.equalsIgnoreCase("Halal")) {
                                isHalal = Status.HALAL;
                            } else if (status.equalsIgnoreCase("Doubtful")) {
                                isHalal = Status.DOUBTFUL;
                            }
                            iiList.add(i);
                            isDataAda = true;
                        }
                    }

                    if (!isDataAda) {
                        iiList.add(new Ingredient("Code not found", "-", "-", "NO RECORD FOUND"));
                    }

                    Utils.DismissProgressDialog();
                    dialogShow(isHalal, iiList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void dialogShow(final Status isHalal, final List<Ingredient> ii) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_result);
        dialog.setCancelable(true);

        if (ii != null) {
            if (ii.size() == 1) {
                //Here we want to change the colour and the icon
                if (ii.get(0).getStatus().equalsIgnoreCase(Status.HALAL.name())) {
                    ((TextView) dialog.findViewById(R.id.tv_title)).setText("HALAL");
                    ((ImageView) dialog.findViewById(R.id.iv_icon)).setImageDrawable(context.getDrawable(R.drawable.ic_baseline_smile_24));
                    dialog.findViewById(R.id.ll_dialog).setBackgroundResource(R.color.green_400);

                } else if (isHalal.equals(Status.DOUBTFUL)) {
                    ((TextView) dialog.findViewById(R.id.tv_title)).setText("DOUBTFUL");
                    ((ImageView) dialog.findViewById(R.id.iv_icon)).setImageDrawable(context.getDrawable(R.drawable.ic_baseline_not_smile_24));
                    dialog.findViewById(R.id.ll_dialog).setBackgroundResource(R.color.red_400);
                } else {
                    ((TextView) dialog.findViewById(R.id.tv_title)).setText("NO RECORD FOUND");
                    ((ImageView) dialog.findViewById(R.id.iv_icon)).setImageDrawable(context.getDrawable(R.drawable.ic_baseline_confused_24));
                    dialog.findViewById(R.id.ll_dialog).setBackgroundResource(R.color.grey_400);
                }

                ((TextView) dialog.findViewById(R.id.tv_title)).setText(ii.get(0).getStatus());
                final String description = "Code Food: " + ii.get(0).getId() + "\nName: " + ii.get(0).getName() + "\nDescription: " + ii.get(0).getDescription();
                ((TextView) dialog.findViewById(R.id.tv_description)).setText(description);
            } else {
                int halal = 0;
                StringBuilder halalFoodCode = new StringBuilder();
                StringBuilder doubtfulFoodCode = new StringBuilder();
                for (int x = 0; x < ii.size(); x++) {
                    if (ii.get(x).getStatus().equalsIgnoreCase(Status.HALAL.name())) {
                        halal++;
                        halalFoodCode.append(", ").append(ii.get(x).getId());
                    } else {
                        doubtfulFoodCode.append(", ").append(ii.get(x).getId());
                    }
                }
                String hhh = "";
                if (halalFoodCode.length() > 0)
                    hhh = halalFoodCode.substring(1);
                String jjj = "";
                if (doubtfulFoodCode.length() > 0)
                    jjj = doubtfulFoodCode.substring(1);

                final int percentageHalal = (halal / ii.size()) * 100;
                final String strPercentageHalal = percentageHalal + "%";
                if (percentageHalal > 50) {
                    ((TextView) dialog.findViewById(R.id.tv_title)).setText("HALAL " + strPercentageHalal);
                    ((ImageView) dialog.findViewById(R.id.iv_icon)).setImageDrawable(context.getDrawable(R.drawable.ic_baseline_smile_24));
                    dialog.findViewById(R.id.ll_dialog).setBackgroundResource(R.color.green_400);
                } else {
                    ((TextView) dialog.findViewById(R.id.tv_title)).setText("DOUBTFUL");
                    ((ImageView) dialog.findViewById(R.id.iv_icon)).setImageDrawable(context.getDrawable(R.drawable.ic_baseline_not_smile_24));
                    dialog.findViewById(R.id.ll_dialog).setBackgroundResource(R.color.red_400);
                }
                final String totalCode = "Total code found: " + ii.size() + "\n";
                final String description = totalCode + "Halal Code Food: " + hhh + "\nDoubtful Food Code: " + jjj;
                ((TextView) dialog.findViewById(R.id.tv_description)).setText(description);
            }
        }

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v12 -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Text detection failed." + e);
    }
}
