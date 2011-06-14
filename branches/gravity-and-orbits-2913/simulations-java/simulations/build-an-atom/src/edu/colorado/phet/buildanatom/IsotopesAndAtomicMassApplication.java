// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.buildanatom.developer.DeveloperConfiguration;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.MakeIsotopesModule;
import edu.colorado.phet.buildanatom.modules.isotopemixture.MixIsotopesModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for this simulation.
 */
public class IsotopesAndAtomicMassApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public IsotopesAndAtomicMassApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {
        addModule( new MakeIsotopesModule() );
        addModule( new MixIsotopesModule() );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();

        // Add an item for controlling whether unstable nuclei are animated.
        final JCheckBoxMenuItem animateNucleusCheckBox = new JCheckBoxMenuItem( "Animate Unstable Nucleus" ) {{
            setSelected( DeveloperConfiguration.ANIMATE_UNSTABLE_NUCLEUS_PROPERTY.get() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    DeveloperConfiguration.ANIMATE_UNSTABLE_NUCLEUS_PROPERTY.set( isSelected() );
                }
            } );
        }};

        developerMenu.add( animateNucleusCheckBox );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAnAtomConstants.PROJECT_NAME,
                BuildAnAtomConstants.FLAVOR_NAME_ISOTOPES_AND_ATOMIC_MASS, IsotopesAndAtomicMassApplication.class );
    }
}
