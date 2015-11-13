package com.jkenneth.droidovpn.data;

import com.jkenneth.droidovpn.data.ServerContract.ServerEntry;

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
public class ServerDatabase {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_SERVER =
            "CREATE TABLE " + ServerEntry.TABLE_NAME + " (" +
            ServerEntry._ID + " INTEGER PRIMARY KEY," +
            ServerEntry.COLUMN_NAME_HOST_NAME + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_IP_ADDRESS + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_SCORE + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_PING + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_SPEED + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_COUNTRY_LONG + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_COUNTRY_SHORT + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_VPN_SESSIONS + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_UPTIME + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_TOTAL_USERS + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_TOTAL_TRAFFIC + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_LOG_TYPE + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_OPERATOR + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_OPERATOR_MESSAGE + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_CONFIG_DATA + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_PORT + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_PROTOCOL + TEXT_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_IS_OLD + INTEGER_TYPE + COMMA_SEP +
            ServerEntry.COLUMN_NAME_IS_STARRED + INTEGER_TYPE +
            " )";

    public static final String SQL_DELETE_SERVER =
            "DROP TABLE IF EXISTS " + ServerEntry.TABLE_NAME;
}
