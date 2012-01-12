// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

/**
 * @author Sam Reid
 */
public class InsideMagnetsControlPanel extends VerticalLayoutPanel {
    public InsideMagnetsControlPanel( final InsideMagnetsModule module ) {
        final Property<ImmutableVector2D> field = module.getInsideMagnetsModel().getExternalMagneticField();
        add( new LinearValueControl( -5, 5, field.get().getX(), "Bx", "0.00", "T" ) {{
            field.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( field.get().getX() );
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    field.set( new ImmutableVector2D( getValue(), field.get().getY() ) );
                }
            } );
        }} );
        add( new LinearValueControl( -5, 5, field.get().getY(), "By", "0.00", "T" ) {{
            field.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( field.get().getY() );
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    field.set( new ImmutableVector2D( field.get().getX(), getValue() ) );
                }
            } );
        }} );

        final Property<Double> temperature = module.getInsideMagnetsModel().getTemperature();
        add( new LinearValueControl( 0.01, 2, temperature.get(), "Temperature", "0.00", "K" ) {{
            temperature.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( temperature.get() );
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    temperature.set( getValue() );
                }
            } );
        }} );

        add( new JCheckBox( "Show Magnetization", module.getShowMagnetizationProperty().get() ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getShowMagnetizationProperty().set( isSelected() );
                }
            } );
            module.getShowMagnetizationProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( module.getShowMagnetizationProperty().get() );
                }
            } );
        }} );
    }
}
