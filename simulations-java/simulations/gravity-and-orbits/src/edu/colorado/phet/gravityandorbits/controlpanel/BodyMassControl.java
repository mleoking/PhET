package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.DefaultLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.gravityandorbits.model.Body;

/**
 * @author Sam Reid
 */
public class BodyMassControl extends VerticalLayoutPanel {

    public BodyMassControl( final Body body, double min, double max ) {
        final Function.LinearFunction modelToView = new Function.LinearFunction( min, max, 0, 100 );

        add(new JLabel(body.getName()){{
            setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
            setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
            setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        }});
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
        setBackground( GravityAndOrbitsControlPanel.BACKGROUND );

        add(new JSlider(){{
            setMinorTickSpacing( 0 );
            setMajorTickSpacing( 0 );
            setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
            setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
        }});

        //todo: add icon for the object
        final LinearValueControl control = new LinearValueControl( min, max, body.getMass(), body.getName(), "0.00", "", new DefaultLayoutStrategy() {
            //TODO: fix LinearValueControl or use a different component
            //I used this override so I could specify the background and foreground of the intermediate valuePanel component
            @Override
            public void doLayout( AbstractValueControl valueControl ) {
                // Get the components that will be part of the layout
                JComponent slider = valueControl.getSlider();
                JComponent textField = valueControl.getTextField();
                JComponent valueLabel = valueControl.getValueLabel();
                JComponent unitsLabel = valueControl.getUnitsLabel();

                // Label+textfield+units in a panel.
                JPanel valuePanel = new JPanel();
                EasyGridBagLayout valueLayout = new EasyGridBagLayout( valuePanel );
                valueLayout.setAnchor( GridBagConstraints.WEST );
                valuePanel.setLayout( valueLayout );
                valueLayout.addComponent( valueLabel, 0, 0 );
                valueLayout.addComponent( textField, 0, 1 );
                valueLayout.addComponent( unitsLabel, 0, 2 );
                valuePanel.setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
                valuePanel.setForeground( GravityAndOrbitsControlPanel.FOREGROUND );

                // Label+textfield+units above slider
                EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
                valueControl.setLayout( layout );
//                int anchor = justificationToAnchor( _justification );
//                layout.setAnchor( anchor );
                layout.addComponent( valuePanel, 0, 0 );
                layout.addComponent( slider, 1, 0 );
            }
        } );
        control.setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        control.setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
        control.getSlider().setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        control.getSlider().setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
        control.setMajorTicksVisible( false );
        control.setMinorTicksVisible( false );
        control.getSlider().setBorder( null );
        control.getValueLabel().setOpaque( true );
        control.getValueLabel().setBorder( null );
        control.getValueLabel().setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        control.getValueLabel().setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
        control.getValueLabel().setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
        body.getDiameterProperty().addObserver( new SimpleObserver() {
            public void update() {
                control.setValue( ( body.getMass() ) );
            }
        } );
        control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                body.setMass( ( control.getValue() ) );
            }
        } );
        control.setTextFieldVisible( false );
//        add( control );
    }
}
