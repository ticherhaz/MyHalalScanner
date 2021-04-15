package com.hafis.myhalalscanner.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hafis.myhalalscanner.R;
import com.hafis.myhalalscanner.tool.Utils;

import java.util.List;

public class AboutUsActivity extends AppCompatActivity {

    private final boolean readyKa = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        TextView tvPT = findViewById(R.id.tv_about_pt);
        TextView tvLongDesc = findViewById(R.id.tv_about_long_desc);
        // tvPT.setOnClickListener(v -> startActivity(new Intent(AboutUsActivity.this, privpol.class)));
        tvLongDesc.setText(Html.fromHtml(getString(R.string.app_long_description)));

        ImageView ivDealDebt = findViewById(R.id.iv_about_dealdebt);
        ivDealDebt.setOnClickListener(v -> {
            if (readyKa)
                Utils.DialogComingSoon(AboutUsActivity.this);
            else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.dealdebt.com.my"));
                startActivity(intent);
            }
        });
        ImageView ivTwitter = findViewById(R.id.iv_about_twitter);
        ivTwitter.setOnClickListener(v -> {
            if (readyKa)
                Utils.DialogComingSoon(AboutUsActivity.this);
            else {
                Intent intent;
                try {
                    // get the Twitter app if possible
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=DealDebtmy"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/DealDebtmy"));
                }
                startActivity(intent);
            }
        });
        ImageView ivFacebook = findViewById(R.id.iv_about_facebook);
        ivFacebook.setOnClickListener(v -> {
            if (readyKa)
                Utils.DialogComingSoon(AboutUsActivity.this);
            else {
                Intent facebookAppIntent;
                try {
                    facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/dealdebt"));
                    startActivity(facebookAppIntent);
                } catch (ActivityNotFoundException e) {
                    facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/dealdebt"));
                    startActivity(facebookAppIntent);
                }
            }
        });
        ImageView ivInstagram = findViewById(R.id.iv_about_instagram);
        ivInstagram.setOnClickListener(v -> {
            if (readyKa)
                Utils.DialogComingSoon(AboutUsActivity.this);
            else {
                Uri uri = Uri.parse("http://instagram.com/_u/dealdebt");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");
                if (isIntentAvailable(AboutUsActivity.this, insta)) {
                    startActivity(insta);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/dealdebt")));
                }
            }
        });
        ImageView ivYoutube = findViewById(R.id.iv_about_youtube);
        ivYoutube.setOnClickListener(v -> {
            if (readyKa)
                Utils.DialogComingSoon(AboutUsActivity.this);
            else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCidOzvPO4sIEv8qU0ZFH6Mw")));
            }
        });

    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}