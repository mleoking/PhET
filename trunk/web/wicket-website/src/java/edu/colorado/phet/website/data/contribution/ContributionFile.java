package edu.colorado.phet.website.data.contribution;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class ContributionFile implements Serializable {

    private int id;
    private Contribution contribution;
    private String filename;
    private String location;
    private int size; // bytes

    private static Logger logger = Logger.getLogger( ContributionFile.class.getName() );

    public File getFileLocation() {
        return new File( ( (PhetWicketApplication) PhetWicketApplication.get() ).getActivitiesRoot(), getRelativeLocation() );
    }

    public String getRelativeLocation() {
        return contribution.getId() + "/" + filename.replace( '/', '_' );
    }

    public AbstractLinker getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                PhetWicketApplication app = (PhetWicketApplication) PhetWicketApplication.get();
                return app.getPhetDownloadLocation() + "/" + getRelativeLocation();
            }

            @Override
            public String getSubUrl( PageContext context ) {
                return null;
            }
        };
    }

    /**
     * Used for transferring files from the old data
     *
     * @param id
     * @return
     */
    public File getTmpFileLocation( String id ) {
        return new File( ( (PhetWicketApplication) PhetWicketApplication.get() ).getActivitiesRoot(), "tmp" + id + "/" + filename.replace( '/', '_' ) );
    }

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