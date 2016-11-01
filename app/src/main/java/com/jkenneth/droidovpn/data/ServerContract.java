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
final class ServerContract {
    public ServerContract() {}

    static abstract class ServerEntry implements BaseColumns {
        static final String TABLE_NAME = "server";
        static final String COLUMN_NAME_HOST_NAME = "host_name";
        static final String COLUMN_NAME_IP_ADDRESS = "ip_address";
        static final String COLUMN_NAME_SCORE = "score";
        static final String COLUMN_NAME_PING = "ping";
        static final String COLUMN_NAME_SPEED = "speed";
        static final String COLUMN_NAME_COUNTRY_LONG = "country_long";
        static final String COLUMN_NAME_COUNTRY_SHORT = "country_short";
        static final String COLUMN_NAME_VPN_SESSIONS = "vpn_sessions";
        static final String COLUMN_NAME_UPTIME = "uptime";
        static final String COLUMN_NAME_TOTAL_USERS = "total_users";
        static final String COLUMN_NAME_TOTAL_TRAFFIC = "total_traffic";
        static final String COLUMN_NAME_LOG_TYPE = "log_type";
        static final String COLUMN_NAME_OPERATOR = "operator_name";
        static final String COLUMN_NAME_OPERATOR_MESSAGE = "operator_message";
        static final String COLUMN_NAME_CONFIG_DATA = "config_data";
        static final String COLUMN_NAME_PORT = "port";
        static final String COLUMN_NAME_PROTOCOL = "protocol";
        static final String COLUMN_NAME_IS_OLD = "is_old";
        static final String COLUMN_NAME_IS_STARRED = "is_starred";

        static final String[] ALL_COLUMNS = {
                _ID,
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
