package edu.colorado.phet.website.data.contribution;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.util.DataListener;
import edu.colorado.phet.website.data.util.IntId;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.links.RawLinkable;
import edu.colorado.phet.website.util.links.RawLinker;

public class Contribution implements Serializable, DataListener, IntId {

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
    private boolean fromPhet;
    private boolean goldStar;
    private int oldId;
    private Locale locale;
    private Set files = new HashSet();
    private Set comments = new HashSet();
    private Set levels = new HashSet();
    private Set subjects = new HashSet();
    private Set flags = new HashSet();
    private Set nominations = new HashSet();
    private Set types = new HashSet();
    private Set simulations = new HashSet();

    // standards stored as booleans for efficient storage and HQL queries

    // Science as Inquiry
    private boolean standardK4A;
    private boolean standard58A;
    private boolean standard912A;

    // Physical Science
    private boolean standardK4B;
    private boolean standard58B;
    private boolean standard912B;

    // Life Science
    private boolean standardK4C;
    private boolean standard58C;
    private boolean standard912C;

    // Earth and Space Science
    private boolean standardK4D;
    private boolean standard58D;
    private boolean standard912D;

    // Science and Technology
    private boolean standardK4E;
    private boolean standard58E;
    private boolean standard912E;

    // Science in Personal and Social Perspective
    private boolean standardK4F;
    private boolean standard58F;
    private boolean standard912F;

    // History and Nature of Science
    private boolean standardK4G;
    private boolean standard58G;
    private boolean standard912G;

    private static final Logger logger = Logger.getLogger( Contribution.class.getName() );

    public Contribution() {
    }

    /*---------------------------------------------------------------------------*
    * public instance functions
    *----------------------------------------------------------------------------*/

    public boolean isVisible() {
        return isApproved();
    }

    public boolean hasStandards() {
        return standard58A || standard58B || standard58C || standard58D || standard58E || standard58F || standard58G ||
               standard912A || standard912B || standard912C || standard912D || standard912E || standard912F || standard912G ||
               standardK4A || standardK4B || standardK4C || standardK4D || standardK4E || standardK4F || standardK4G;
    }

    public File getZipFile() {
        return new File( PhetWicketApplication.get().getActivitiesRoot(), "/zip/" + getZipName() + ".zip" );
    }

    public String getZipName() {
        return id + "-" + StringUtils.escapeFileString( title );
    }

    public String getZipLocation() {
        return PhetWicketApplication.get().getActivitiesLocation() + "/zip/" + getZipName() + ".zip";
    }

    public RawLinkable getZipLinker() {
        return new RawLinker( getZipLocation() );
    }

    public void createZipFile() throws IOException {
        File zipFile = getZipFile();
        zipFile.getParentFile().mkdirs();
        if ( zipFile.exists() ) {
            zipFile.delete();
        }
        if ( getFiles().isEmpty() ) {
            // can't write a zip file with no files inside it. bail out if that is the case
            logger.warn( "bailing out, will not attempt to write a zip file without any files" );
            return;
        }
        ZipOutputStream zout = new ZipOutputStream( new FileOutputStream( zipFile ) );

        String zipName = getZipName();

        Set<String> usedNames = new HashSet<String>();

        for ( Object o : getFiles() ) {
            ContributionFile cfile = (ContributionFile) o;
            File file = cfile.getFileLocation();
            if ( usedNames.contains( file.getName() ) ) {
                logger.warn( "duplicate file of name " + file.getName() + " for contribution " + getId() );
                continue;
            }
            usedNames.add( file.getName() );

            zout.putNextEntry( new ZipEntry( zipName + "/" + cfile.getFilename() ) );

            FileInputStream fin = new FileInputStream( file );
            final int bufsize = 1024;
            byte[] buf = new byte[bufsize];
            int len;
            while ( ( len = fin.read( buf ) ) != -1 ) {
                zout.write( buf, 0, len );
            }
        }

        zout.finish();
        zout.flush();
        zout.close();
    }

    /**
     * Used to sort contributions for display
     *
     * @param other         The other contribution
     * @param currentLocale The current locale to use for sorting.
     * @return
     */
    public int displayCompareTo( Contribution other, Locale currentLocale ) {
        if ( other.getId() == getId() ) {
            // check for equality
            return 0;
        }
        if ( !getLocale().equals( other.getLocale() ) ) {
            // sort by locale
            if ( getLocale().equals( currentLocale ) ) {
                return -1;
            }
            if ( other.getLocale().equals( currentLocale ) ) {
                return 1;
            }
            return getLocale().getLanguage().compareTo( other.getLocale().getLanguage() );
        }
        if ( isGoldStar() != other.isGoldStar() ) {
            // sort by gold star
            return isGoldStar() ? -1 : 1;
        }

        // sort by creation date
        return getDateCreated().compareTo( other.getDateCreated() );
    }

    /**
     * Should be wrapped within a transaction
     *
     * @param session The hibernate session
     */
    public void deleteMe( Session session ) {
        for ( Object o : comments ) {
            session.delete( o );
        }
        for ( Object o : files ) {
            // for now, leave the actual files in place. the ID will remain unique
            session.delete( o );
        }
        for ( Object o : flags ) {
            session.delete( o );
        }
        for ( Object o : levels ) {
            session.delete( o );
        }
        for ( Object o : nominations ) {
            session.delete( o );
        }
        for ( Object o : subjects ) {
            session.delete( o );
        }
        for ( Object o : types ) {
            session.delete( o );
        }
        for ( Object o : simulations ) {
            Simulation simulation = (Simulation) o;
            simulation.getContributions().remove( this );
            // no update needed since the data is stored within the contribution
        }
        session.delete( this );
    }

    public void onUpdate() {
        logger.info( "contribution #" + id + " updated" );

        try {
            createZipFile();
        }
        catch( IOException e ) {
            e.printStackTrace();
            logger.error( "unable to recreate zip file for contribution " + id, e );
        }
    }

    /*---------------------------------------------------------------------------*
    * utilities to add related data
    *----------------------------------------------------------------------------*/

    public void addComment( ContributionComment comment ) {
        comment.setContribution( this );
        comments.add( comment );
    }

    public void addFile( ContributionFile file ) {
        file.setContribution( this );
        files.add( file );
    }

    public void addFlag( ContributionFlag flag ) {
        flag.setContribution( this );
        flags.add( flag );
    }

    public void addLevel( ContributionLevel level ) {
        level.setContribution( this );
        levels.add( level );
    }

    public void addNomination( ContributionNomination nomination ) {
        nomination.setContribution( this );
        nominations.add( nomination );
    }

    public void addSubject( ContributionSubject subject ) {
        subject.setContribution( this );
        subjects.add( subject );
    }

    public void addType( ContributionType type ) {
        type.setContribution( this );
        types.add( type );
    }

    public void addSimulation( Simulation simulation ) {
        simulation.getContributions().add( this );
        simulations.add( simulation );
    }

    /*---------------------------------------------------------------------------*
    * utilities to remove related data
    *----------------------------------------------------------------------------*/

    public void removeSimulation( Simulation simulation ) {
        simulation.getContributions().remove( this );
        simulations.remove( simulation );
    }

    /*---------------------------------------------------------------------------*
    * getters and setters
    *----------------------------------------------------------------------------*/

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

    /**
     * Yes I also cringe when I see this function name, but it makes things simpler!
     *
     * @return Whether answers are included!
     */
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

    public Set getTypes() {
        return types;
    }

    public void setTypes( Set types ) {
        this.types = types;
    }

    public Set getSimulations() {
        return simulations;
    }

    public void setSimulations( Set simulations ) {
        this.simulations = simulations;
    }

    public boolean isStandardK4A() {
        return standardK4A;
    }

    public void setStandardK4A( boolean standardK4A ) {
        this.standardK4A = standardK4A;
    }

    public boolean isStandard58A() {
        return standard58A;
    }

    public void setStandard58A( boolean standard58A ) {
        this.standard58A = standard58A;
    }

    public boolean isStandard912A() {
        return standard912A;
    }

    public void setStandard912A( boolean standard912A ) {
        this.standard912A = standard912A;
    }

    public boolean isStandardK4B() {
        return standardK4B;
    }

    public void setStandardK4B( boolean standardK4B ) {
        this.standardK4B = standardK4B;
    }

    public boolean isStandard58B() {
        return standard58B;
    }

    public void setStandard58B( boolean standard58B ) {
        this.standard58B = standard58B;
    }

    public boolean isStandard912B() {
        return standard912B;
    }

    public void setStandard912B( boolean standard912B ) {
        this.standard912B = standard912B;
    }

    public boolean isStandardK4C() {
        return standardK4C;
    }

    public void setStandardK4C( boolean standardK4C ) {
        this.standardK4C = standardK4C;
    }

    public boolean isStandard58C() {
        return standard58C;
    }

    public void setStandard58C( boolean standard58C ) {
        this.standard58C = standard58C;
    }

    public boolean isStandard912C() {
        return standard912C;
    }

    public void setStandard912C( boolean standard912C ) {
        this.standard912C = standard912C;
    }

    public boolean isStandardK4D() {
        return standardK4D;
    }

    public void setStandardK4D( boolean standardK4D ) {
        this.standardK4D = standardK4D;
    }

    public boolean isStandard58D() {
        return standard58D;
    }

    public void setStandard58D( boolean standard58D ) {
        this.standard58D = standard58D;
    }

    public boolean isStandard912D() {
        return standard912D;
    }

    public void setStandard912D( boolean standard912D ) {
        this.standard912D = standard912D;
    }

    public boolean isStandardK4E() {
        return standardK4E;
    }

    public void setStandardK4E( boolean standardK4E ) {
        this.standardK4E = standardK4E;
    }

    public boolean isStandard58E() {
        return standard58E;
    }

    public void setStandard58E( boolean standard58E ) {
        this.standard58E = standard58E;
    }

    public boolean isStandard912E() {
        return standard912E;
    }

    public void setStandard912E( boolean standard912E ) {
        this.standard912E = standard912E;
    }

    public boolean isStandardK4F() {
        return standardK4F;
    }

    public void setStandardK4F( boolean standardK4F ) {
        this.standardK4F = standardK4F;
    }

    public boolean isStandard58F() {
        return standard58F;
    }

    public void setStandard58F( boolean standard58F ) {
        this.standard58F = standard58F;
    }

    public boolean isStandard912F() {
        return standard912F;
    }

    public void setStandard912F( boolean standard912F ) {
        this.standard912F = standard912F;
    }

    public boolean isStandardK4G() {
        return standardK4G;
    }

    public void setStandardK4G( boolean standardK4G ) {
        this.standardK4G = standardK4G;
    }

    public boolean isStandard58G() {
        return standard58G;
    }

    public void setStandard58G( boolean standard58G ) {
        this.standard58G = standard58G;
    }

    public boolean isStandard912G() {
        return standard912G;
    }

    public void setStandard912G( boolean standard912G ) {
        this.standard912G = standard912G;
    }

    public int getOldId() {
        return oldId;
    }

    public void setOldId( int oldId ) {
        this.oldId = oldId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

}
