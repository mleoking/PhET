package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.io.File;

public class Project implements Serializable {

    // TODO: add "live" object and properties

    private int id;
    private String name;
    private int versionMajor;
    private int versionMinor;
    private int versionDev;
    private int versionRevision;
    private long versionTimestamp;
    private boolean visible;

    public Project() {
    }

    public String getVersionString() {
        String ret = "";
        ret += getVersionMajor();
        ret += ".";
        if ( getVersionMinor() < 10 && getVersionMinor() >= 0 ) {
            ret += "0";
        }
        ret += getVersionMinor();
        if ( getVersionDev() != 0 ) {
            ret += ".";
            if ( getVersionDev() < 10 && getVersionDev() >= 0 ) {
                ret += "0";
            }
            ret += getVersionDev();
        }
        return ret;
    }

    public File getProjectProperties( File docRoot ) {
        return new File( docRoot, "sims/" + name + "/" + name + ".properties" );
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor( int versionMajor ) {
        this.versionMajor = versionMajor;
    }

    public int getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor( int versionMinor ) {
        this.versionMinor = versionMinor;
    }

    public int getVersionDev() {
        return versionDev;
    }

    public void setVersionDev( int versionDev ) {
        this.versionDev = versionDev;
    }

    public int getVersionRevision() {
        return versionRevision;
    }

    public void setVersionRevision( int versionRevision ) {
        this.versionRevision = versionRevision;
    }

    public long getVersionTimestamp() {
        return versionTimestamp;
    }

    public void setVersionTimestamp( long versionTimestamp ) {
        this.versionTimestamp = versionTimestamp;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
