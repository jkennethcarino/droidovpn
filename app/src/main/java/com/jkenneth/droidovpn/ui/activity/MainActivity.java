package com.jkenneth.droidovpn.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.badoo.mobile.util.WeakHandler;
import com.jkenneth.droidovpn.BuildConfig;
import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.data.DbHelper;
import com.jkenneth.droidovpn.model.Server;
import com.jkenneth.droidovpn.ui.adapter.ServerAdapter;
import com.jkenneth.droidovpn.ui.fragment.LicensesDialogFragment;
import com.jkenneth.droidovpn.ui.widget.EmptyRecyclerView;
import com.jkenneth.droidovpn.util.CsvParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    private static final int SORT_COUNTRY = 1;
    private static final int SORT_SPEED = 2;
    private static final int SORT_PING = 3;

    private static final String DIALOG_LICENSES_TAG = "licenses-dialog";

    private SwipeRefreshLayout swipeRefreshLayout;

    private WeakHandler handler;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private List<Server> servers = new ArrayList<>();

    private Request request;

    private Call mCall;

    private ServerAdapter adapter;

    private DbHelper dbHelper;

    private int sortedBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new WeakHandler();
        dbHelper = DbHelper.getInstance(this.getApplicationContext());

        // Retrieve all cached servers
        servers.addAll(dbHelper.getAll());

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the other views
        setupSwipeRefreshLayout();
        setupRecyclerView();

        if (request == null) {
            request = new Request.Builder()
                    .url(BuildConfig.VPN_GATE_API)
                    .build();
        }

        if (servers.isEmpty()) {
            populateServerList();
        }
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateServerList();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ServerAdapter(servers, serverClickCallback);
        EmptyRecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setEmptyView(findViewById(android.R.id.empty));
        recyclerView.setAdapter(adapter);
    }

    private final ServerAdapter.ServerClickCallback serverClickCallback =
            new ServerAdapter.ServerClickCallback() {
                @Override
                public void onItemClick(@NonNull Server server) {
                    Intent intent = new Intent(MainActivity.this, ServerDetailsActivity.class);
                    intent.putExtra(ServerDetailsActivity.EXTRA_DETAILS, server);
                    startActivity(intent);
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_country:
                item.setChecked(item.isChecked());
                sort(SORT_COUNTRY);
                break;
            case R.id.sort_speed:
                item.setChecked(item.isChecked());
                sort(SORT_SPEED);
                break;
            case R.id.sort_ping:
                item.setChecked(item.isChecked());
                sort(SORT_PING);
                break;
            case R.id.action_licenses:
                LicensesDialogFragment licensesDialog = new LicensesDialogFragment();
                licensesDialog.show(getSupportFragmentManager(), DIALOG_LICENSES_TAG);
                break;
            // case R.id.action_settings:
            //    break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sort(final int sortBy) {
        sortedBy = sortBy;

        Collections.sort(servers, new Comparator<Server>() {
            @Override
            public int compare(Server server, Server server2) {
                int compareTo = 0;
                if (sortBy == SORT_COUNTRY) {
                    compareTo = server.countryLong.compareTo(server2.countryLong);

                } else if (sortBy == SORT_SPEED) {
                    compareTo = Long.valueOf(server2.speed)
                            .compareTo(server.speed);

                } else if (sortBy == SORT_PING) {
                    Long ping = !server.ping.equals("-") ?
                            Long.valueOf(server.ping) : 0L;
                    Long ping2 = !server2.ping.equals("-") ?
                            Long.valueOf(server2.ping) : 0L;

                    compareTo = ping2.compareTo(ping);
                }
                return compareTo;
            }
        });
        adapter.setServerList(servers);
    }

    private void loadServerList(List<Server> serverList) {
        adapter.setServerList(serverList);
        dbHelper.save(servers);

        sort(sortedBy);
    }

    /** Displays the updated list of VPN servers */
    private void populateServerList() {
        swipeRefreshLayout.setRefreshing(true);

        mCall = okHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final List<Server> servers = CsvParser.parse(response);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadServerList(servers);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }
}
