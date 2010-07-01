package edu.colorado.phet.website.data;

import java.util.Date;

import org.apache.log4j.Logger;

public class ResetPasswordRequest {
    private static final Logger logger = Logger.getLogger( ResetPasswordRequest.class.getName() );
    private PhetUser user;
    private Date timestamp;
    private String key; //unique identifier for this forgotten password request
    private int id;

    public ResetPasswordRequest() {
    }

    public PhetUser getUser() {
        return user;
    }

    public void setUser( PhetUser user ) {
        this.user = user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( Date timestamp ) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }
}