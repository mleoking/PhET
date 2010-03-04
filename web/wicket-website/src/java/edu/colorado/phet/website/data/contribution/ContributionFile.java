package edu.colorado.phet.website.data.contribution;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.IntId;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class ContributionFile implements Serializable, IntId {

    private int id;
    private Contribution contribution;
    private String filename;
    private String location;
    private int size; // bytes

    private static Logger logger = Logger.getLogger( ContributionFile.class.getName() );

    public File getFileLocation() {
        return new File( PhetWicketApplication.get().getActivitiesRoot(), getRelativeLocation() );
    }

    public String getRelativeLocation() {
        // added a few sanity checks on filename to prevent files being written lower down
        return contribution.getId() + "/" + filename.replace( '/', '_' ).replace( '\\', '_' );
    }

    public AbstractLinker getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                PhetWicketApplication app = PhetWicketApplication.get();
                return app.getActivitiesLocation() + "/" + getRelativeLocation();
            }

            @Override
            public String getSubUrl( PageContext context ) {
                return null;
            }
        };
    }

    public static void orderFiles( List<ContributionFile> files ) {
        Collections.sort( files, new Comparator<ContributionFile>() {
            public int compare( ContributionFile a, ContributionFile b ) {
                return a.getFilename().compareToIgnoreCase( b.getFilename() );
            }
        } );
    }

    public static void orderFilesCast( List files ) {
        Collections.sort( files, new Comparator() {
            public int compare( Object a, Object b ) {
                return ( (ContributionFile) a ).getFilename().compareToIgnoreCase( ( (ContributionFile) b ).getFilename() );
            }
        } );
    }

    /**
     * Used for transferring files from the old data
     *
     * @param id
     * @return
     */
    public File getTmpFileLocation( String id ) {
        return new File( PhetWicketApplication.get().getActivitiesRoot(), "tmp" + getRelativeLocation() );
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