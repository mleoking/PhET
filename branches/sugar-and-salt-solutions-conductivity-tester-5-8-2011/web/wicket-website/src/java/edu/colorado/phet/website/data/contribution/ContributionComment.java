package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.util.IntId;

public class ContributionComment implements Serializable, IntId {

    private int id;
    private String text;
    private Date dateCreated;
    private Date dateUpdated;
    private Contribution contribution;
    private PhetUser phetUser;

    private static final Logger logger = Logger.getLogger( ContributionComment.class.getName() );

    public ContributionComment() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated( Date dateCreated ) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated( Date dateUpdated ) {
        this.dateUpdated = dateUpdated;
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
}
