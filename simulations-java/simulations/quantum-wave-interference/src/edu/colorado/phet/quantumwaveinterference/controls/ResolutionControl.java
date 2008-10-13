/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.QWIScreenNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 29, 2005
 * Time: 9:34:53 AM
 */

public class ResolutionControl extends AdvancedPanel {
    public static final int DEFAULT_WAVE_SIZE = 60;
    private QWIModule qwiModule;
    private final int WAVE_GRAPHIC_SIZE_1024x768 = 360;
    public static int INIT_WAVE_SIZE = 0;

    public static class ResolutionSetup {
        int value;
        String name;
        double timeFudgeFactorForLight;//see SchrodingerScreenNode
        private double timeFudgeFactorForParticles;

        public ResolutionSetup( int value, String name, double timeFudgeFactorForLight, double timeFudgeFactorForParticles ) {
            this.value = value;
            this.name = name;
            this.timeFudgeFactorForLight = timeFudgeFactorForLight;
            this.timeFudgeFactorForParticles = timeFudgeFactorForParticles;
        }

        public String toString() {
            return name;
        }

        public int intValue() {
            return value;
        }

        public double getTimeFudgeFactorForLight() {
            return timeFudgeFactorForLight;
        }

        public double getTimeFudgeFactorForParticles() {
            return timeFudgeFactorForParticles;
        }
    }

    public ResolutionControl( final QWIModule qwiModule ) {
        super( QWIResources.getString( "resolution1" ), QWIResources.getString( "resolution2" ) );
        this.qwiModule = qwiModule;

//        JLabel screenSizeLabel = new JLabel( "Grid Resolution" );
//        addControl( screenSizeLabel );

        final JSpinner screenSize = new JSpinner( new SpinnerNumberModel( DEFAULT_WAVE_SIZE, 10, 1024, 5 ) );
        getSchrodingerModule().setWaveSize( DEFAULT_WAVE_SIZE );
        screenSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer)screenSize.getValue();
                getSchrodingerModule().setWaveSize( value.intValue() );
            }
        } );
//        addControl( screenSize );

//        int[]configFor1280x760 = new int[]{3, 4, 5, 6, 8, 10, 12, 16, 32};
//        int[]values = new int[]{3, 4, 5, 6, 8, 10, 12, 16,32};
//        int[]configFor1024x768 = new int[]{2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 18};

        final JComboBox jComboBox = new JComboBox( QWIModule.getResolutionSetups() );
        jComboBox.setSelectedItem( new Integer( qwiModule.getSchrodingerPanel().getSchrodingerScreenNode().getCellSize() ) );
//        addControl( new JLabel( "Pixels per lattice cell." ) );
        addControl( new JLabel( QWIResources.getString( "menus.resolution" ) ) );
        addControl( jComboBox );
        jComboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ResolutionSetup value = (ResolutionSetup)jComboBox.getSelectedItem();
                getSchrodingerModule().setResolution( value );//has to happen before event notification: see SSN for setUnits
                qwiModule.setCellSize( value.intValue() );
                int waveSize = WAVE_GRAPHIC_SIZE_1024x768 / value.intValue();
                getSchrodingerModule().setWaveSize( waveSize );
            }
        } );
        ResolutionControl.INIT_WAVE_SIZE = WAVE_GRAPHIC_SIZE_1024x768 /
                                           qwiModule.getSchrodingerPanel().getSchrodingerScreenNode().getCellSize();
        getSchrodingerModule().setWaveSize( INIT_WAVE_SIZE );

        JLabel numSkip = new JLabel( QWIResources.getString( "menus.time-step" ) );
        addControl( numSkip );
        final JSpinner frameSkip = new JSpinner( new SpinnerNumberModel( QWIScreenNode.numIterationsBetwenScreenUpdate, 1, 20, 1 ) );
        frameSkip.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer val = (Integer)frameSkip.getValue();
                QWIScreenNode.numIterationsBetwenScreenUpdate = val.intValue();
            }
        } );
        addControl( frameSkip );
    }

    private QWIModule getSchrodingerModule() {
        return qwiModule;
    }
}
