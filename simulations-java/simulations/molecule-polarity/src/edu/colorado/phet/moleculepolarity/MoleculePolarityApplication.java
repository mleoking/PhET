// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
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
        addModule( new RealMoleculesModule( parentFrame ) );

        // Options menu
        parentFrame.addMenu( new OptionsMenu() {{
            add( new JCheckBoxMenuItem( MPStrings.RAINBOW_OPTION, JmolViewerNode.RAINBOW_MEP.get() ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        JmolViewerNode.RAINBOW_MEP.set( ( (JCheckBoxMenuItem) e.getSource() ).isSelected() );
                    }
                } );
            }} );
        }} );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, MPConstants.PROJECT_NAME, MoleculePolarityApplication.class );
    }
}
