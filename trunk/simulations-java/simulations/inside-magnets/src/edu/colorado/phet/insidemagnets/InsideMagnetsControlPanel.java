// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

/**
 * @author Sam Reid
 */
public class InsideMagnetsControlPanel extends VerticalLayoutPanel {
    public InsideMagnetsControlPanel( final InsideMagnetsModule module ) {
        final Property<ImmutableVector2D> field = module.getInsideMagnetsModel().getExternalMagneticField();
        add( new LinearValueControl( -5, 5, field.getValue().getX(), "Bx", "0.00", "T" ) {{
            field.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( field.getValue().getX() );
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    field.setValue( new ImmutableVector2D( getValue(), field.getValue().getY() ) );
                }
            } );
        }} );
        add( new LinearValueControl( -5, 5, field.getValue().getY(), "By", "0.00", "T" ) {{
            field.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( field.getValue().getY() );
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    field.setValue( new ImmutableVector2D( field.getValue().getX(), getValue() ) );
                }
            } );
        }} );

        final Property<Double> temperature = module.getInsideMagnetsModel().getTemperature();
        add( new LinearValueControl( 0.01, 2, temperature.getValue(), "Temperature", "0.00", "K" ) {{
            temperature.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( temperature.getValue() );
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    temperature.setValue( getValue() );
                }
            } );
        }} );

        add( new JCheckBox( "Show Magnetization", module.getShowMagnetizationProperty().getValue() ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getShowMagnetizationProperty().setValue( isSelected() );
                }
            } );
            module.getShowMagnetizationProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( module.getShowMagnetizationProperty().getValue() );
                }
            } );
        }} );
    }
}
