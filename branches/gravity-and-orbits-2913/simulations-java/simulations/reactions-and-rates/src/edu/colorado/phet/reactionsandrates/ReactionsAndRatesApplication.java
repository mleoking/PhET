// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates;

import java.lang.reflect.InvocationTargetException;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.reactionsandrates.modules.ComplexModule;
import edu.colorado.phet.reactionsandrates.modules.RateExperimentsModule;
import edu.colorado.phet.reactionsandrates.modules.SimpleModule;

/**
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionsAndRatesApplication extends PiccoloPhetApplication {

    public ReactionsAndRatesApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new SimpleModule() );
        addModule( new ComplexModule() );
        addModule( new RateExperimentsModule() );
    }


    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.setFont( MRConfig.CONTROL_FONT );
        phetLookAndFeel.setTitledBorderFont( MRConfig.CONTROL_FONT );

        PhetApplicationConfig config = new PhetApplicationConfig( args, "reactions-and-rates" );
        config.setFrameSetup( new FrameSetup.CenteredWithSize( 1000, 700 ) );
        config.setLookAndFeel( phetLookAndFeel );
        new PhetApplicationLauncher().launchSim( config, ReactionsAndRatesApplication.class );
    }
}
