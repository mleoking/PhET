package edu.colorado.phet.wickettest.data;

public class Project {

    // TODO: add "live" object and properties

    private int id;
    private String name;
    private int versionMajor;
    private int versionMinor;
    private int versionDev;
    private int versionRevision;
    private long versionTimestamp;

    public Project() {
    }

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
}
