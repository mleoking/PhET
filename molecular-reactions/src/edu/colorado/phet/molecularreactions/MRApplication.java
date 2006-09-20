package edu.colorado.phet.molecularreactions;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.modules.TestModule;
import edu.colorado.phet.molecularreactions.modules.SimpleModule;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import java.io.IOException;
import java.awt.event.ContainerAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * edu.colorado.phet.molecularreactions.MRApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRApplication extends PhetApplication {
//public class MRApplication extends PiccoloPhetApplication {

    public MRApplication( String[] args ) {
        super( args, SimStrings.get( "Application.title" ),
               SimStrings.get( "Application.description" ),
               MRConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1000, 700 ) );

        SimpleModule simpleModule = new SimpleModule();
        addModule( simpleModule );
        addModule( new ComplexModule() );
        addModule( new TestModule() );

        setActiveModule( simpleModule );
    }


    public static void main( String[] args ) {

        // Standard initializations
        SimStrings.init( args, MRConfig.LOCALIZATION_BUNDLE );
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.initLookAndFeel();

        MRApplication application = new MRApplication( args );

        // Set the iconized logo for the simulation
        try {
            PhetUtilities.getPhetFrame().setIconImage( ImageLoader.loadBufferedImage( "images/Phet-logo-24x24.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Let 'er rip
        application.startApplication();
    }
}
