/**
 * Class: AntennaControlPanel
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.command.SetMovementManualCmd;
import edu.colorado.phet.command.SetMovementSinusoidalCmd;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.command.StaticFieldIsEnabledCmd;
import edu.colorado.phet.emf.command.DynamicFieldIsEnabledCmd;
import edu.colorado.phet.emf.command.SetFieldCurveEnabledCmd;
import edu.colorado.phet.emf.command.SetFreqencyCmd;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class AntennaControlPanel extends JPanel implements Observer {

    public AntennaControlPanel() {
        EmfModel.instance().addObserver( this );
        createControls();
    }

    private void createControls() {
        this.setPreferredSize( new Dimension ( 150, 400 ));
        this.add( new MovementControlPanel() );
        this.add( new OptionControlPanel() );
    }

    public void update( Observable o, Object arg ) {
    }


    //
    // Inner classes
    //

    private class ParameterControlPanel extends JPanel {
        JSlider bSlider = new JSlider( 0, 10000, 500 );
        JSlider cSlider = new JSlider( 0, 10000, 500 );
    }

    /**
     * An inner class for the controls that enable, disable or set the values
     * of various options
     */
    private class OptionControlPanel extends JPanel {

        JCheckBox staticFieldCB = new JCheckBox( "Enable static field" );
        JCheckBox dynamicFieldCB = new JCheckBox( "Enable radiating field " );
        JCheckBox splineCurveCB = new JCheckBox( "Display curves" );
        JSlider freqSlider = new JSlider( 0, 300, 150 );

        OptionControlPanel() {
            this.setLayout( new GridLayout( 5, 1 ));

            this.add( staticFieldCB );
            staticFieldCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new StaticFieldIsEnabledCmd( staticFieldCB.isSelected() ).doIt();
                }
            } );

            this.add( dynamicFieldCB );
            dynamicFieldCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new DynamicFieldIsEnabledCmd( dynamicFieldCB.isSelected() ).doIt();
                }
            } );

            this.add( splineCurveCB );
            splineCurveCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new SetFieldCurveEnabledCmd( splineCurveCB.isSelected() ).doIt();
                }
            } );

            this.add( new JLabel( "Frequency" ));
            this.add( freqSlider );
            freqSlider.setPreferredSize( new Dimension( 50, 20 ));
            freqSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    new SetFreqencyCmd( (float)freqSlider.getValue() / 5000 ).doIt();
                }
            } );
        }
    }

    /**
     * An inner class for the radio buttons that control how the transmitting
     * electrons move
     */
    private class MovementControlPanel extends JPanel {

        JRadioButton sineRB = new JRadioButton( "Sinusoidal movement" );
        JRadioButton manualRB = new JRadioButton( "Manual control" );
        ButtonGroup rbGroup = new ButtonGroup();

        MovementControlPanel() {
            this.setLayout( new GridLayout( 2, 1 ));
            this.add( sineRB );
            this.add( manualRB );
            rbGroup.add( sineRB );
            rbGroup.add( manualRB );
            sineRB.setSelected( true );

            sineRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( sineRB.isSelected() ) {
                        new SetMovementSinusoidalCmd( 0.1f, 50 ).doIt();
                    }
                }
            } );

            manualRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( manualRB.isSelected() ) {
                        new SetMovementManualCmd().doIt();
                    }
                }
            } );
        }
    }
}
