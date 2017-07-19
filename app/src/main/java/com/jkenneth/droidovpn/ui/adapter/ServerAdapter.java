package com.jkenneth.droidovpn.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.model.Server;
import com.jkenneth.droidovpn.util.OvpnUtils;

import java.util.ArrayList;
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

    private List<Server> servers = new ArrayList<>();

    private ServerClickCallback callback;

    public ServerAdapter(List<Server> servers, @NonNull ServerClickCallback callback) {
        this.servers.clear();
        this.servers.addAll(servers);
        this.callback = callback;
    }

    public void setServerList(@NonNull final List<Server> serverList) {
        if (servers.isEmpty()) {
            servers.clear();
            servers.addAll(serverList);
            notifyItemRangeInserted(0, serverList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return servers.size();
                }

                @Override
                public int getNewListSize() {
                    return serverList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Server old = servers.get(oldItemPosition);
                    Server server = serverList.get(newItemPosition);
                    return old.hostName.equals(server.hostName);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Server old = servers.get(oldItemPosition);
                    Server server = serverList.get(newItemPosition);
                    return old.hostName.equals(server.hostName)
                            && old.ipAddress.equals(server.ipAddress)
                            && old.countryLong.equals(server.countryLong);
                }
            });
            servers.clear();
            servers.addAll(serverList);
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.server_list_item, parent, false);
        return new ViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(servers.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return servers == null ? 0 : servers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View rootView;
        final TextView countryView;
        final TextView protocolView;
        final TextView ipAddressView;
        final TextView speedView;
        final TextView pingView;

        final ServerClickCallback callback;

        public ViewHolder(View view, ServerClickCallback callback) {
            super(view);
            rootView = view;
            countryView = view.findViewById(R.id.tv_country_name);
            protocolView = view.findViewById(R.id.tv_protocol);
            ipAddressView = view.findViewById(R.id.tv_ip_address);
            speedView = view.findViewById(R.id.tv_speed);
            pingView = view.findViewById(R.id.tv_ping);

            this.callback = callback;
        }

        public void bind(@NonNull final Server server) {
            final Context context = rootView.getContext();

            countryView.setText(server.countryLong);
            protocolView.setText(server.protocol.toUpperCase());
            ipAddressView.setText(context.getString(R.string.format_ip_address,
                    server.ipAddress, server.port));
            speedView.setText(context.getString(R.string.format_speed,
                    OvpnUtils.humanReadableCount(server.speed, true)));
            pingView.setText(context.getString(R.string.format_ping, server.ping));
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClick(server);
                }
            });
        }
    }

    public interface ServerClickCallback {
        void onItemClick(@NonNull Server server);
    }
}
