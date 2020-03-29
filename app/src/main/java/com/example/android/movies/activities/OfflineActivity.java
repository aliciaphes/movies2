package com.example.android.movies.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.movies.R;
import com.example.android.movies.databinding.ActivityOfflineBinding;
import com.example.android.movies.utils.Utilities;


public class OfflineActivity extends AppCompatActivity {

    private Context mContext;
    private ActivityOfflineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        binding = ActivityOfflineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isNetworkAvailable(mContext) && Utilities.isOnline()) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, R.string.check_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
