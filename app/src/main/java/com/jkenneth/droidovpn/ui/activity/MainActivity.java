package com.jkenneth.droidovpn.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jkenneth.droidovpn.Config;
import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.model.Server;
import com.jkenneth.droidovpn.ui.adapter.ServerAdapter;
import com.jkenneth.droidovpn.ui.fragment.LicensesDialogFragment;
import com.jkenneth.droidovpn.ui.widget.DividerItemDecoration;
import com.jkenneth.droidovpn.util.CSVParser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient mClient = new OkHttpClient();
    private Call mCall;

    private List<Server> mServers = new ArrayList<>();
    private ServerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecyclerView();

        Request request = new Request.Builder()
                .url(Config.VPN_GATE_API)
                .build();

        mCall = mClient.newCall(request);

        getServerList();
    }

    private void setupRecyclerView() {
        mAdapter = new ServerAdapter(this, mServers);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCall.cancel();

        mClient = null;
        mCall = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_licenses) {
            LicensesDialogFragment licensesDialog = new LicensesDialogFragment();
            licensesDialog.show(getSupportFragmentManager(), "licenses_dialog");
            return true;
        } else if (id == R.id.action_settings) {
            //Intent intent = new Intent(this, SettingsActivity.class);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getServerList() {
        mCall.enqueue(new Callback() {
            Handler mHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("onFailure", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    List<Server> servers = CSVParser.parse(response);
                    mServers.clear();
                    mServers.addAll(servers);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
