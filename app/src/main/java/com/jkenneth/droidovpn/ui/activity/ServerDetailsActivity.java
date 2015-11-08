package com.jkenneth.droidovpn.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.model.Server;
import com.jkenneth.droidovpn.util.OVPNUtils;

/**
 * Shows Server Details
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
 * Created by Jhon Kenneth Carino on 10/26/2015.
 */
public class ServerDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_DETAILS = "server_details";

    private Server mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() == null) {
            Toast.makeText(this.getApplicationContext(),
                    getString(R.string.invalid_server),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Bundle extras = getIntent().getExtras();
        mServer = (Server) extras.getSerializable(EXTRA_DETAILS);

        initViews();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_server_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_share) {
            OVPNUtils.shareOVPNFile(this, mServer);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        Button importProfile = (Button) findViewById(R.id.btn_import);
        importProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OVPNUtils.importToOpenVPN(ServerDetailsActivity.this, mServer);
            }
        });
    }

    private void updateUI() {
        setText(R.id.tv_country_name, mServer.countryLong);
        setText(R.id.tv_host_name, mServer.hostName);
        setText(R.id.tv_ip_address, mServer.ipAddress);
        setText(R.id.tv_port, String.valueOf(mServer.port));
        setText(R.id.tv_protocol, mServer.protocol.toUpperCase());
        setText(R.id.tv_speed, OVPNUtils.humanReadableCount(mServer.speed, true));
        setText(R.id.tv_ping, String.format(getString(R.string.format_ping), mServer.ping));
        setText(R.id.tv_vpn_sessions, String.valueOf(mServer.vpnSessions));
        setText(R.id.tv_uptime, String.valueOf(mServer.uptime));
        setText(R.id.tv_total_users, String.valueOf(mServer.totalUsers));
        setText(R.id.tv_total_traffic, OVPNUtils.humanReadableCount(
                Long.valueOf(mServer.totalTraffic), false));
        setText(R.id.tv_logging_policy, mServer.logType);
        setText(R.id.tv_operator_name, mServer.operator);
        setText(R.id.tv_operator_message, mServer.message);
    }

    private void setText(int textView, String text) {
        text = !TextUtils.isEmpty(text) ? text : "-";
        ((TextView) findViewById(textView)).setText(text);
    }
}
