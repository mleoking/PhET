package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.util.IntId;

public class ContributionNomination implements Serializable, IntId {

    private int id;
    private Date dateCreated;
    private Contribution contribution;
    private PhetUser phetUser;
    private String reason;

    private static final Logger logger = Logger.getLogger( ContributionNomination.class.getName() );

    public ContributionNomination() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated( Date dateCreated ) {
        this.dateCreated = dateCreated;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }

    public PhetUser getPhetUser() {
        return phetUser;
    }

    public void setPhetUser( PhetUser user ) {
        this.phetUser = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason( String reason ) {
        this.reason = reason;
    }
}