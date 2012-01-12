// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.jmolphet.JmolConsole;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.DialogCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode;
import edu.colorado.phet.moleculepolarity.realmolecules.RealMoleculesModule;
import edu.colorado.phet.moleculepolarity.threeatoms.ThreeAtomsModule;
import edu.colorado.phet.moleculepolarity.twoatoms.TwoAtomsModule;

/**
 * Main class for the "Molecule Polarity" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculePolarityApplication extends PiccoloPhetApplication {

    public MoleculePolarityApplication( PhetApplicationConfig config ) {
        super( config );

        PhetFrame parentFrame = getPhetFrame();

        // modules
        addModule( new TwoAtomsModule( parentFrame ) );
        addModule( new ThreeAtomsModule( parentFrame ) );
        final RealMoleculesModule realMoleculesModule = new RealMoleculesModule( parentFrame );
        addModule( realMoleculesModule );

        // Options menu
        parentFrame.addMenu( new OptionsMenu() {{

            //Report on student usage for menus and menu items, see #3144
            add( new SimSharingJCheckBoxMenuItem( UserComponents.rainbowMenuItem, MPStrings.RAINBOW_OPTION, JmolViewerNode.RAINBOW_MEP.get() ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        JmolViewerNode.RAINBOW_MEP.set( ( (JCheckBoxMenuItem) e.getSource() ).isSelected() );
                    }
                } );
            }} );
        }} );

        // Developer menu item for Jmol Console, i18n not required
        parentFrame.getDeveloperMenu().add( new DialogCheckBoxMenuItem( "Jmol Console...", "Jmol Console", getPhetFrame(), new Function0<Container>() {
            public Container apply() {
                return new JmolConsole( realMoleculesModule.getJmolViewer() );
            }
        } ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, MPConstants.PROJECT_NAME, MoleculePolarityApplication.class );
    }
}
