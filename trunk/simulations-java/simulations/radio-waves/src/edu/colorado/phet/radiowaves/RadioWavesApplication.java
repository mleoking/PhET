// Copyright 2002-2011, University of Colorado

/**
 * Class: EmfApplication Package: edu.colorado.phet.emf Author: Another Guy
 * Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
        //XXX this is not actually added to the menu bar, if added in the future it requires i18n
    }

    public static void main( String[] args ) {
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new RadioWavesApplication( config );
            }
        };
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, RadioWavesConstants.PROJECT_NAME );
        appConfig.getLookAndFeel().setBackgroundColor( new Color( 255, 255, 220 ) ); // Unfuddle #959
        appConfig.getLookAndFeel().setFont( new PhetFont(PhetFont.getDefaultFontSize(),true) ); //Unfuddle #960
        appConfig.getLookAndFeel().setTitledBorderTitleColor( Color.BLACK ); // Unfuddle #960
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

}
