package com.jkenneth.droidovpn.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jkenneth.droidovpn.data.ServerContract.ServerEntry;
import com.jkenneth.droidovpn.model.Server;

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
 * Created by Jhon Kenneth Carino on 11/11/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "droidovpn.db";
    private static DBHelper sInstance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ServerDatabase.SQL_CREATE_SERVER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ServerDatabase.SQL_DELETE_SERVER);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void save(List<Server> servers) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        for (Server server : servers) {
            ContentValues values = getContentValues(server);

            int rows = db.update(ServerEntry.TABLE_NAME, values,
                    ServerEntry.COLUMN_NAME_IP_ADDRESS + " = ? AND " +
                            ServerEntry.COLUMN_NAME_PROTOCOL + " = ?",
                    new String[]{server.ipAddress, server.protocol});

            if (rows != 1) {
                // create new record
                db.insert(ServerEntry.TABLE_NAME, null, values);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        // now, delete those servers with is_old = 1
        deleteOld();
        db.close();
    }

    public void deleteOld() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ServerEntry.TABLE_NAME,
                ServerEntry.COLUMN_NAME_IS_OLD + " = 1", null);
    }

    public void setStarred(String ipAddress, boolean isStarred) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ServerEntry.COLUMN_NAME_IS_STARRED, isStarred ? 1 : 0);
        db.update(ServerEntry.TABLE_NAME, values,
                ServerEntry.COLUMN_NAME_IP_ADDRESS + " = ?", new String[]{ipAddress});
    }

    public List<Server> getAll() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Server> servers = new ArrayList<>();
        Cursor cursor = db.query(ServerEntry.TABLE_NAME,
                ServerEntry.ALL_COLUMNS, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                servers.add(cursorToServer(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();

        return servers;
    }

    private ContentValues getContentValues(Server server) {
        ContentValues values = new ContentValues();
        values.put(ServerEntry.COLUMN_NAME_HOST_NAME, server.hostName);
        values.put(ServerEntry.COLUMN_NAME_IP_ADDRESS, server.ipAddress);
        values.put(ServerEntry.COLUMN_NAME_SCORE, server.score);
        values.put(ServerEntry.COLUMN_NAME_PING, server.ping);
        values.put(ServerEntry.COLUMN_NAME_SPEED, server.speed);
        values.put(ServerEntry.COLUMN_NAME_COUNTRY_LONG, server.countryLong);
        values.put(ServerEntry.COLUMN_NAME_COUNTRY_SHORT, server.countryShort);
        values.put(ServerEntry.COLUMN_NAME_VPN_SESSIONS, server.vpnSessions);
        values.put(ServerEntry.COLUMN_NAME_UPTIME, server.uptime);
        values.put(ServerEntry.COLUMN_NAME_TOTAL_USERS, server.totalUsers);
        values.put(ServerEntry.COLUMN_NAME_TOTAL_TRAFFIC, server.totalTraffic);
        values.put(ServerEntry.COLUMN_NAME_LOG_TYPE, server.logType);
        values.put(ServerEntry.COLUMN_NAME_OPERATOR, server.operator);
        values.put(ServerEntry.COLUMN_NAME_OPERATOR_MESSAGE, server.message);
        values.put(ServerEntry.COLUMN_NAME_CONFIG_DATA, server.ovpnConfigData);
        values.put(ServerEntry.COLUMN_NAME_PORT, server.port);
        values.put(ServerEntry.COLUMN_NAME_PROTOCOL, server.protocol);
        values.put(ServerEntry.COLUMN_NAME_IS_OLD, 0);
        values.put(ServerEntry.COLUMN_NAME_IS_STARRED, server.isStarred ? 1 : 0);
        return values;
    }

    private Server cursorToServer(Cursor cursor) {
        Server server = new Server();
        server.hostName = cursor.getString(1);
        server.ipAddress = cursor.getString(2);
        server.score = cursor.getInt(3);
        server.ping = cursor.getString(4);
        server.speed = cursor.getLong(5);
        server.countryLong = cursor.getString(6);
        server.countryShort = cursor.getString(7);
        server.vpnSessions = cursor.getLong(8);
        server.uptime = cursor.getLong(9);
        server.totalUsers = cursor.getLong(10);
        server.totalTraffic = cursor.getString(11);
        server.logType = cursor.getString(12);
        server.operator = cursor.getString(13);
        server.message = cursor.getString(14);
        server.ovpnConfigData = cursor.getString(15);
        server.port = cursor.getInt(16);
        server.protocol = cursor.getString(17);
        server.isStarred = cursor.getInt(19) == 1;
        return server;
    }
}
