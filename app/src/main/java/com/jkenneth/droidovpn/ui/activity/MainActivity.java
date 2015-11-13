package com.jkenneth.droidovpn.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jkenneth.droidovpn.Config;
import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.data.DBHelper;
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

/**
 * Shows the list of parsed servers from VPN Gate CSV
 *
 * Copyright (C) 2015  Jhon Kenneth Carino
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by Jhon Kenneth Carino on 10/18/15.
 */
public class MainActivity extends AppCompatActivity {

    private OkHttpClient mClient = new OkHttpClient();

    private Call mCall;

    private List<Server> mServers = new ArrayList<>();

    private ServerAdapter mAdapter;

    private DBHelper mDatabase;

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
        mDatabase = DBHelper.getInstance(this.getApplicationContext());

        // display cached server list
        setServerList();

        // get updated server list
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

    private void setServerList() {
        mServers.clear();
        mServers.addAll(mDatabase.getAll());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.getDispatcher().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                mCall.cancel();
            }
        });
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
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    List<Server> servers = CSVParser.parse(response);
                    mServers.clear();
                    mServers.addAll(servers);
                    mDatabase.save(servers);

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
