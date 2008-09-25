/**
 * Class: EmfApplication Package: edu.colorado.phet.emf Author: Another Guy
 * Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.radiowaves.view.WaveMediumGraphic;

public class RadioWavesApplication extends PhetApplication {

    public static double s_speedOfLight = 6;
    
    public RadioWavesApplication( PhetApplicationConfig config ) {
        super( config );
        
        ConstantDtClock clock = new ConstantDtClock( 40, 0.5 );
        final EmfModule antennaModule = new EmfModule( clock );
        addModule( antennaModule );
        
        // Add an options menu
        JMenu optionsMenu = new JMenu( "Options" ); //XXX i18n
        final JCheckBoxMenuItem fadeScalarRepCB = new JCheckBoxMenuItem( "Fade scalar representation" ); //XXX i18n
        fadeScalarRepCB.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                WaveMediumGraphic.Y_GRADIENT = fadeScalarRepCB.isSelected();
                antennaModule.setFieldSense( antennaModule.getFieldSense() );
            }
        } );
        optionsMenu.add( fadeScalarRepCB );
    }

    public static class RadioWavesApplicationConfig extends PhetApplicationConfig {
        public RadioWavesApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new FrameSetup.CenteredWithSize( 1024, 768 ), RadioWavesResources.getResourceLoader() );
            super.setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new RadioWavesApplication( config );
                }
            } );
            super.setLookAndFeel( new PhetLookAndFeel() );//XXX delete this, not needed
        }
    }
    
    public static void main( String[] args ) {
        new RadioWavesApplicationConfig( args ).launchSim();
    }

}
