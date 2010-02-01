package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.PhetUser;

public class Contribution implements Serializable {

    private int id;
    private PhetUser phetUser;
    private String title;
    private String authors;
    private String keywords;
    private boolean approved;
    private String description;
    private int duration; // minutes
    private boolean answersIncluded;
    private String contactEmail;
    private String authorOrganization;
    private Date dateCreated;
    private Date dateUpdated;
    private String standardsCompliance;
    private boolean fromPhet;
    private boolean goldStar;
    private Set files;
    private Set comments;
    private Set levels;
    private Set subjects;
    private Set flags;
    private Set nominations;

    private static Logger logger = Logger.getLogger( Contribution.class.getName() );

    public Contribution() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public PhetUser getPhetUser() {
        return phetUser;
    }

    public void setPhetUser( PhetUser user ) {
        this.phetUser = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors( String authors ) {
        this.authors = authors;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords( String keywords ) {
        this.keywords = keywords;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved( boolean approved ) {
        this.approved = approved;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration( int duration ) {
        this.duration = duration;
    }

    public boolean isAnswersIncluded() {
        return answersIncluded;
    }

    public void setAnswersIncluded( boolean answersIncluded ) {
        this.answersIncluded = answersIncluded;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail( String contactEmail ) {
        this.contactEmail = contactEmail;
    }

    public String getAuthorOrganization() {
        return authorOrganization;
    }

    public void setAuthorOrganization( String authorOrganization ) {
        this.authorOrganization = authorOrganization;
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

    public String getStandardsCompliance() {
        return standardsCompliance;
    }

    public void setStandardsCompliance( String standardsCompliance ) {
        this.standardsCompliance = standardsCompliance;
    }

    public boolean isFromPhet() {
        return fromPhet;
    }

    public void setFromPhet( boolean fromPhet ) {
        this.fromPhet = fromPhet;
    }

    public boolean isGoldStar() {
        return goldStar;
    }

    public void setGoldStar( boolean goldStar ) {
        this.goldStar = goldStar;
    }

    public Set getFiles() {
        return files;
    }

    public void setFiles( Set files ) {
        this.files = files;
    }

    public Set getComments() {
        return comments;
    }

    public void setComments( Set comments ) {
        this.comments = comments;
    }

    public Set getLevels() {
        return levels;
    }

    public void setLevels( Set levels ) {
        this.levels = levels;
    }

    public Set getSubjects() {
        return subjects;
    }

    public void setSubjects( Set subjects ) {
        this.subjects = subjects;
    }

    public Set getFlags() {
        return flags;
    }

    public void setFlags( Set flags ) {
        this.flags = flags;
    }

    public Set getNominations() {
        return nominations;
    }

    public void setNominations( Set nominations ) {
        this.nominations = nominations;
    }
}
