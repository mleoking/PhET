// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.util.Date;

/**
 * @author Sam Reid
 */
public class StudentInfo {
    public final String classID;
    public final String userID;
    public final Date startTime;
    public final long upTime;
    public final boolean online;
    public final String simName;
    public final long latency;
    public final int sessionID;
    public final boolean earthCrashed;
    public final double sunMass;

    public StudentInfo( String classID, String userID, Date startTime, long upTime, boolean online, String simName, long latency, int sessionID, boolean earthCrashed, double sunMass ) {
        this.classID = classID;
        this.userID = userID;
        this.startTime = startTime;
        this.upTime = upTime;
        this.online = online;
        this.simName = simName;
        this.latency = latency;
        this.sessionID = sessionID;
        this.earthCrashed = earthCrashed;
        this.sunMass = sunMass;
    }

    public Object getValue( int columnIndex ) {
        switch( columnIndex ) {
            case 0:
                return classID;
            case 1:
                return userID;
            case 2:
                return startTime;
            case 3:
                return upTime;
            case 4:
                return online;
            case 5:
                return simName;
            case 6:
                return latency;
            case 7:
                return sessionID;
            case 8:
                return earthCrashed;
            case 9:
                return sunMass;
        }
        return null;
    }
}
