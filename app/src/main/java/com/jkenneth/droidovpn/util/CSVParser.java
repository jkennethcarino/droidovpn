package com.jkenneth.droidovpn.util;

import android.util.Base64;

import com.jkenneth.droidovpn.model.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Parse CSV from VPN Gate API
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
public class CSVParser {

    private static final int HOST_NAME = 0;
    private static final int IP_ADDRESS = 1;
    private static final int SCORE = 2;
    private static final int PING = 3;
    private static final int SPEED = 4;
    private static final int COUNTRY_LONG = 5;
    private static final int COUNTRY_SHORT = 6;
    private static final int VPN_SESSION = 7;
    private static final int UPTIME = 8;
    private static final int TOTAL_USERS = 9;
    private static final int TOTAL_TRAFFIC = 10;
    private static final int LOG_TYPE = 11;
    private static final int OPERATOR = 12;
    private static final int MESSAGE = 13;
    private static final int OVPN_CONFIG_DATA = 14;

    private static final int PORT_INDEX = 2;
    private static final int PROTOCOL_INDEX = 1;

    public static Server stringToServer(String line) {
        String[] vpn = line.split(",");

        Server server = new Server();
        server.hostName = vpn[HOST_NAME];
        server.ipAddress = vpn[IP_ADDRESS];
        server.score = Integer.parseInt(vpn[SCORE]);
        server.ping = vpn[PING];
        server.speed = Long.parseLong(vpn[SPEED]);
        server.countryLong = vpn[COUNTRY_LONG];
        server.countryShort = vpn[COUNTRY_SHORT];
        server.vpnSessions = Long.parseLong(vpn[VPN_SESSION]);
        server.uptime = Long.parseLong(vpn[UPTIME]);
        server.totalUsers = Long.parseLong(vpn[TOTAL_USERS]);
        server.totalTraffic = vpn[TOTAL_TRAFFIC];
        server.logType = vpn[LOG_TYPE];
        server.operator = vpn[OPERATOR];
        server.message = vpn[MESSAGE];
        server.ovpnConfigData = new String(Base64.decode(
                vpn[OVPN_CONFIG_DATA], Base64.DEFAULT));

        String[] lines = server.ovpnConfigData.split("[\\r\\n]+");
        server.port = getPort(lines);
        server.protocol = getProtocol(lines);

        return server;
    }

    public static List<Server> parse(Response response) {
        List<Server> servers = new ArrayList<>();
        InputStream in = null;
        BufferedReader reader = null;

        try {
            in = response.body().byteStream();
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("*") && !line.startsWith("#")) {
                    servers.add(stringToServer(line));
                }
            }

        } catch (IOException ignored) {
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
            }
        }

        return servers;
    }

    /**
     * @return Port used in OVPN file ("remote <HOSTNAME> <PORT>")
     * */
    private static int getPort(String[] lines) {
        int port = 0;
        for (String line : lines) {
            if (!line.startsWith("#")) {
                if (line.startsWith("remote")) {
                    port = Integer.parseInt(line.split(" ")[PORT_INDEX]);
                    break;
                }
            }
        }
        return port;
    }

    /**
     * @return Protocol used in OVPN file. ("proto <TCP/UDP>")
     * */
    private static String getProtocol(String[] lines) {
        String protocol = "";
        for (String line : lines) {
            if (!line.startsWith("#")) {
                if (line.startsWith("proto")) {
                    protocol = line.split(" ")[PROTOCOL_INDEX];
                    break;
                }
            }
        }
        return protocol;
    }
}
