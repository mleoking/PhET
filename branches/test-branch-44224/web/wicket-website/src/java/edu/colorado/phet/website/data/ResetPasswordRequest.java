package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.util.IntId;

public class ResetPasswordRequest implements Serializable, IntId {
    private static final Logger logger = Logger.getLogger( ResetPasswordRequest.class.getName() );
    private PhetUser phetUser;
    private Date timestamp;
    private String key; //unique identifier for this forgotten password request
    private int id; //automatically incremented, see the hbm file

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest( PhetUser phetUser, Date timestamp, String key) {
        this.phetUser = phetUser;
        this.timestamp = timestamp;
        this.key = key;
    }

    public PhetUser getPhetUser() {
        return phetUser;
    }

    public void setPhetUser( PhetUser phetUser ) {
        this.phetUser = phetUser;
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