// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;
import edu.colorado.phet.capacitorlab.module.introduction.IntroductionModule;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorLabApplication extends PiccoloPhetApplication {

    public static final PhetResources RESOURCES = new PhetResources( CLConstants.PROJECT_NAME );

    public CapacitorLabApplication( PhetApplicationConfig config ) {
        super( config );
        CLGlobalProperties globalProperties = new CLGlobalProperties( getPhetFrame(), isDeveloperControlsEnabled() );
        initModules( globalProperties );
        initMenuBar( globalProperties );
    }

    private void initModules( CLGlobalProperties globalProperties ) {

        // add modules
        addModule( new IntroductionModule( globalProperties ) );
        addModule( new DielectricModule( globalProperties ) );
        addModule( new MultipleCapacitorsModule( globalProperties ) );

        //TODO: #2941, consider moving this to common code
        int maxWidth = 0;
        for ( Module module : getModules() ) {
            maxWidth = Math.max( maxWidth, module.getControlPanel().getPreferredSize().width );
        }
        for ( Module module : getModules() ) {
            module.getControlPanel().addControlFullWidth( Box.createHorizontalStrut( maxWidth ) );
        }
    }

    private void initMenuBar( CLGlobalProperties globalProperties ) {
        // Developer menu items, no i18n
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show E-Field measurement shapes", globalProperties.eFieldShapesVisibleProperty ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show voltage measurement shapes", globalProperties.voltageShapesVisibleProperty ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, CLConstants.PROJECT_NAME, CapacitorLabApplication.class );
    }
}
