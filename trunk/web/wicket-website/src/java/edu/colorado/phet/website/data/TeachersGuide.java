package edu.colorado.phet.website.data;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.util.IntId;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;

/**
 * DB data for the teachers guides that are produced by the PhET team. Designed for the current 0 or 1 guides per
 * simulation that is current, but can be extended later
 * <p/>
 * Transfer assumes teacher guides are PDF
 */
public class TeachersGuide implements Serializable, IntId {

    private int id;
    private Simulation simulation;
    private String filename;
    private int size;

    private int oldId;

    // in the future it would be possible to add the locale. not done now, since it should be doable later with
    // minimal effort

    private static final Logger logger = Logger.getLogger( TeachersGuide.class.getName() );

    public TeachersGuide() {
    }

    /*---------------------------------------------------------------------------*
    * public methods
    *----------------------------------------------------------------------------*/

    /**
     * Always make sure there is a filename before calling this
     *
     * @return
     */
    public File getFileLocation() {
        return new File( PhetWicketApplication.get().getTeachersGuideRoot(), filename );
    }

    public AbstractLinker getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                return PhetWicketApplication.get().getTeachersGuideLocation() + "/" + filename;
            }

            @Override
            public String getSubUrl( PageContext context ) {
                return null;
            }
        };
    }

    public static String createFilename( Simulation simulation ) {
        return simulation.getName() + "-guide.pdf";
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

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation( Simulation simulation ) {
        this.simulation = simulation;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename( String filename ) {
        this.filename = filename;
    }

    public int getSize() {
        return size;
    }

    public void setSize( int size ) {
        this.size = size;
    }

    public int getOldId() {
        return oldId;
    }

    public void setOldId( int oldId ) {
        this.oldId = oldId;
    }
}
