package com.jkenneth.droidovpn.data;

import android.provider.BaseColumns;

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
 * Created by Jhon Kenneth Carino on 11/9/2015.
 */
public final class ServerContract {
    public ServerContract() {}

    public static abstract class ServerEntry implements BaseColumns {
        public static final String TABLE_NAME = "server";
        public static final String COLUMN_NAME_HOST_NAME = "host_name";
        public static final String COLUMN_NAME_IP_ADDRESS = "ip_address";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_PING = "ping";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_COUNTRY_LONG = "country_long";
        public static final String COLUMN_NAME_COUNTRY_SHORT = "country_short";
        public static final String COLUMN_NAME_VPN_SESSIONS = "vpn_sessions";
        public static final String COLUMN_NAME_UPTIME = "uptime";
        public static final String COLUMN_NAME_TOTAL_USERS = "total_users";
        public static final String COLUMN_NAME_TOTAL_TRAFFIC = "total_traffic";
        public static final String COLUMN_NAME_LOG_TYPE = "log_type";
        public static final String COLUMN_NAME_OPERATOR = "operator_name";
        public static final String COLUMN_NAME_OPERATOR_MESSAGE = "operator_message";
        public static final String COLUMN_NAME_CONFIG_DATA = "config_data";
        public static final String COLUMN_NAME_PORT = "port";
        public static final String COLUMN_NAME_PROTOCOL = "protocol";
        public static final String COLUMN_NAME_IS_OLD = "is_old";
        public static final String COLUMN_NAME_IS_STARRED = "is_starred";

        public static final String[] ALL_COLUMNS = {
                ServerEntry._ID,
                COLUMN_NAME_HOST_NAME,
                COLUMN_NAME_IP_ADDRESS,
                COLUMN_NAME_SCORE,
                COLUMN_NAME_PING,
                COLUMN_NAME_SPEED,
                COLUMN_NAME_COUNTRY_LONG,
                COLUMN_NAME_COUNTRY_SHORT,
                COLUMN_NAME_VPN_SESSIONS,
                COLUMN_NAME_UPTIME,
                COLUMN_NAME_TOTAL_USERS,
                COLUMN_NAME_TOTAL_TRAFFIC,
                COLUMN_NAME_LOG_TYPE,
                COLUMN_NAME_OPERATOR,
                COLUMN_NAME_OPERATOR_MESSAGE,
                COLUMN_NAME_CONFIG_DATA,
                COLUMN_NAME_PORT,
                COLUMN_NAME_PROTOCOL,
                COLUMN_NAME_IS_OLD,
                COLUMN_NAME_IS_STARRED
        };
    }
}
