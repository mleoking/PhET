package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class ContributionFile implements Serializable {

    private int id;
    private Contribution contribution;
    private String filename;
    private String location;
    private int size; // bytes

    // TODO: decide on implementation options, particularly how to avoid XSS vulnerabilities!

    private static Logger logger = Logger.getLogger( ContributionFile.class.getName() );

    public ContributionFile() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename( String filename ) {
        this.filename = filename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation( String location ) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize( int size ) {
        this.size = size;
    }
}