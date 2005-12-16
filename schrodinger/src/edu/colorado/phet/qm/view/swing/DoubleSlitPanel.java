/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.swing;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.controls.ResolutionControl;
import edu.colorado.phet.qm.controls.SlitDetectorPanel;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 9:37:07 AM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class DoubleSlitPanel extends VerticalLayoutPanel {
    private DiscreteModel discreteModel;
    private HorizontalDoubleSlit horizontalDoubleSlit;
    private JComponent slitSize;
    private JComponent slitSeparation;
    private JComponent verticalPosition;
    private SlitDetectorPanel slitDetectorPanel;
    private SchrodingerModule module;
//    private DoubleSlitCheckBox doubleSlitCheckBox;

    public DoubleSlitPanel( final DiscreteModel discreteModel, SchrodingerModule intensityModule ) {
        this.discreteModel = discreteModel;
        this.module = intensityModule;
        this.horizontalDoubleSlit = discreteModel.getDoubleSlitPotential();
//        setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Double Slit" ) );
        setBorder( BorderFactory.createRaisedBevelBorder() );

//        doubleSlitCheckBox = new DoubleSlitCheckBox( "Enabled", getDiscreteModel() );
//        add( doubleSlitCheckBox );
//        doubleSlitCheckBox.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setControlsEnabled( doubleSlitCheckBox.isSelected() );
//            }
//        } );

        verticalPosition = createComponent( "Vertical Position", new Setter() {
            public void valueChanged( double val ) {
//                int y = (int)val;
////                int y = ResolutionControl.DEFAULT_WAVE_SIZE - (int)val;
//                System.out.println( "y = " + y );
                horizontalDoubleSlit.setY( (int)val );
            }

            public double getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getY();
            }

            public double getMin() {
                return 0;
            }

            public double getMax() {
                return ResolutionControl.DEFAULT_WAVE_SIZE;
            }
        } );


        slitSize = createComponent( "Slit Width ", new Setter() {
            public void valueChanged( double val ) {
                horizontalDoubleSlit.setSlitSize( (int)val );
            }

            public double getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getSlitSize();
            }

            public double getMin() {
                return 0;
            }

            public double getMax() {
                return 50;
            }
        } );

        slitSeparation = createComponent( "Slit Separation", new Setter() {
            public void valueChanged( double val ) {
                horizontalDoubleSlit.setSlitSeparation( (int)val );
            }

            public double getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getSlitSeparation();
            }

            public double getMin() {
                return 0;
            }

            public double getMax() {
                return 100;
            }
        } );

        final JCheckBox absorbtiveSlit = new JCheckBox( "Slit Absorption", getDiscreteModel().isSlitAbsorptive() );
        absorbtiveSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setSlitAbsorptive( absorbtiveSlit.isSelected() );
            }
        } );
        add( absorbtiveSlit );


        add( slitSize );
        add( slitSeparation );
        add( verticalPosition );

        if( intensityModule instanceof IntensityModule ) {//todo use polymorphism here
            slitDetectorPanel = new SlitDetectorPanel( (IntensityModule)intensityModule );
            addFullWidth( slitDetectorPanel );
        }
//        setControlsEnabled( doubleSlitCheckBox.isSelected() );
        setControlsEnabled( true );

    }

    public SlitDetectorPanel getSlitDetectorPanel() {
        return slitDetectorPanel;
    }

    private void setControlsEnabled( boolean selected ) {
        slitSize.setEnabled( selected );
        slitSeparation.setEnabled( selected );
        verticalPosition.setEnabled( selected );
    }

    private JComponent createComponent( String title, final Setter setter ) {
        final Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 100, setter.getMin(), setter.getMax() );
        final JSlider comp = new JSlider( 0, 100, (int)linearFunction.createInverse().evaluate( setter.getValue( horizontalDoubleSlit ) ) );
        comp.setBorder( BorderFactory.createTitledBorder( title ) );
        comp.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double v = linearFunction.evaluate( comp.getValue() );
                setter.valueChanged( v );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                comp.setValue( (int)linearFunction.createInverse().evaluate( setter.getValue( horizontalDoubleSlit ) ) );
            }
        } );
        return comp;
    }

    private static interface Setter {
        void valueChanged( double val );

        double getValue( HorizontalDoubleSlit horizontalDoubleSlit );

        double getMin();

        double getMax();
    }

    private DiscreteModel getDiscreteModel() {
        return discreteModel;
    }
}
