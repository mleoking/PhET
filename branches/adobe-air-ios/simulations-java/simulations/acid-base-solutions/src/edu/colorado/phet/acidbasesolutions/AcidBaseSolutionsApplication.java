// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions;

import javax.swing.Box;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.module.CustomSolutionModule;
import edu.colorado.phet.acidbasesolutions.module.IntroductionModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class AcidBaseSolutionsApplication extends PiccoloPhetApplication {

    public AcidBaseSolutionsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
    }

    private void initModules() {
        
        boolean dev = isDeveloperControlsEnabled();
        addModule( new IntroductionModule( dev ) );
        addModule( new CustomSolutionModule( dev ) );
        
        // set color of control panels after adding all modules
        setControlPanelBackground( ABSColors.CONTROL_PANEL_BACKGROUND );
        
        // make all control panels the same width
        int maxWidth = 0;
        for ( Module module : getModules() ) {
            maxWidth = Math.max( maxWidth, module.getControlPanel().getPreferredSize().width );
        }
        for ( Module module : getModules() ) {
            module.getControlPanel().addControlFullWidth( Box.createHorizontalStrut( maxWidth ) );
        }
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT, AcidBaseSolutionsApplication.class );
    }
}
