/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.dischargelamps.MigrationModule;
import edu.colorado.phet.dischargelamps.PhotoelectricModule;

/**
 * PhotoelectricApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricApplication extends PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private String title = "The Photoelectric Effect";
    static private String description = "An exploration of the photoelectric effect";
    static private String version = "0.01";
    static private FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 680 );

    // Clock specification
    public static final double DT = 12;
    public static final int FPS = 25;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------


    public PhotoelectricApplication( String[] args ) {
        super( args,
               title,
               description,
               version,
               new SwingTimerClock( DT, FPS, AbstractClock.FRAMES_PER_SECOND),
               true,
               frameSetup );

        setModules( new Module[] { new MigrationModule( "miration", this.getClock() ),
            new PhotoelectricModule( this.getClock() ) } );
    }


    public static void main( String[] args ) {
        new PhotoelectricApplication( args ).startApplication();
    }
}
