package com.jkenneth.droidovpn.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.model.Server;
import com.jkenneth.droidovpn.ui.activity.ServerDetailsActivity;
import com.jkenneth.droidovpn.util.OvpnUtils;

import java.util.List;

/**
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
 * Created by Jhon Kenneth Carino on 10/25/15.
 */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {

    private Context mContext;
    private int mBackground;
    private List<Server> mServers;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCountry;
        public final TextView mProtocol;
        public final TextView mIpAddress;
        public final TextView mSpeed;
        public final TextView mPing;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCountry = (TextView) view.findViewById(R.id.tv_country_name);
            mProtocol = (TextView) view.findViewById(R.id.tv_protocol);
            mIpAddress = (TextView) view.findViewById(R.id.tv_ip_address);
            mSpeed = (TextView) view.findViewById(R.id.tv_speed);
            mPing = (TextView) view.findViewById(R.id.tv_ping);
        }
    }

    public ServerAdapter(Context context, List<Server> servers) {
        TypedValue mTypedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        this.mContext = context;
        this.mBackground = mTypedValue.resourceId;
        this.mServers = servers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.server_list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Server server = mServers.get(position);
        holder.mCountry.setText(server.countryLong);
        holder.mProtocol.setText(server.protocol.toUpperCase());
        holder.mIpAddress.setText(String.format(
                mContext.getString(R.string.format_ip_address),
                server.ipAddress, server.port));
        holder.mSpeed.setText(OvpnUtils.humanReadableCount(server.speed, true));
        holder.mPing.setText(String.format(
                mContext.getString(R.string.format_ping), server.ping));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ServerDetailsActivity.class);
                intent.putExtra(ServerDetailsActivity.EXTRA_DETAILS, server);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mServers.size();
    }
}
