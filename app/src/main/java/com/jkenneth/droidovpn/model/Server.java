package com.jkenneth.droidovpn.model;

import java.io.Serializable;

/**
 * Created by Jhon Kenneth Carino on 10/18/15.
 */
public class Server implements Serializable {

    public String hostName;
    public String ipAddress;
    public int score;
    public String ping;
    public long speed;
    public String countryLong;
    public String countryShort;
    public long vpnSessions;
    public long uptime;
    public long totalUsers;
    public String totalTraffic;
    public String logType;
    public String operator;
    public String message;
    public String ovpnConfigData;
    public int port;
    public String protocol;

}
