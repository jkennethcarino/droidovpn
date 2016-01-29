package com.jkenneth.droidovpn.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.jkenneth.droidovpn.ui.widget.EmptyRecyclerView;
import com.jkenneth.droidovpn.util.CSVParser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private static final int SORT_COUNTRY = 1;
    private static final int SORT_SPEED = 2;
    private static final int SORT_PING = 3;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OkHttpClient mClient = new OkHttpClient();

    private List<Server> mServers = new ArrayList<>();

    private Request mRequest;

    private Call mCall;

    private ServerAdapter mAdapter;

    private DBHelper mDatabase;

    private int mSortedBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = DBHelper.getInstance(this.getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add all cached servers
        mServers.addAll(mDatabase.getAll());

        setupSwipeRefreshLayout();
        setupRecyclerView();

        if (mRequest == null) {
            mRequest = new Request.Builder()
                    .url(Config.VPN_GATE_API)
                    .build();
        }

        if (mServers.isEmpty()) {
            fetchServers();
        }
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getServerList();
            }
        });
    }

    private void setupRecyclerView() {
        mAdapter = new ServerAdapter(this, mServers);
        EmptyRecyclerView recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.setEmptyView(findViewById(android.R.id.empty));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    private void fetchServers() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getServerList();
            }
        });
    }

    private void cancelRequest() {
        mClient.getDispatcher().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                if (mCall != null) {
                    mCall.cancel();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelRequest();
        mSwipeRefreshLayout.setOnRefreshListener(null);
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
                licensesDialog.show(getSupportFragmentManager(), "licenses_dialog");
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sort(final int sortBy) {
        Collections.sort(mServers, new Comparator<Server>() {
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
        mAdapter.notifyDataSetChanged();
        mSortedBy = sortBy;
    }

    /** Displays the updated list of VPN servers */
    private void getServerList() {
        mCall = mClient.newCall(mRequest);
        mCall.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    final List<Server> servers = CSVParser.parse(response);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mServers.clear();
                            mServers.addAll(servers);
                            mDatabase.save(servers);

                            sort(mSortedBy);

                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }
}
