/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.SchrodingerModule;
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

public class DoubleSlitControlPanel extends VerticalLayoutPanel {
    private DiscreteModel discreteModel;
    private HorizontalDoubleSlit horizontalDoubleSlit;
    private JComponent slitSize;
    private JComponent slitSeparation;
    private JComponent verticalPosition;
    private SlitDetectorPanel slitDetectorPanel;
    private SchrodingerModule module;

    public DoubleSlitControlPanel( final DiscreteModel discreteModel, SchrodingerModule intensityModule ) {
        this.discreteModel = discreteModel;
        this.module = intensityModule;
        this.horizontalDoubleSlit = discreteModel.getDoubleSlitPotential();
//        setBorder( BorderFactory.createRaisedBevelBorder() );

        verticalPosition = createComponent( "Vertical Position", new Setter() {
            int insetY = 10;

            public void valueChanged( int val ) {
                horizontalDoubleSlit.setY( val );
            }

            public int getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getY();
            }

            public int getMin() {
                return 0 + insetY;
            }

            public int getMax() {
                return ResolutionControl.DEFAULT_WAVE_SIZE - insetY;
            }
        } );


        slitSize = createComponent( "Slit Width ", new Setter() {
            public void valueChanged( int val ) {
                horizontalDoubleSlit.setSlitSize( (int)val );
            }

            public int getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getSlitSize();
            }

            public int getMin() {
                return 4;
            }

            public int getMax() {
                return 25;
            }
        } );

        slitSeparation = createComponent( "Slit Separation", new Setter() {
            public void valueChanged( int val ) {
                horizontalDoubleSlit.setSlitSeparation( (int)val );
            }

            public int getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getSlitSeparation();
            }

            public int getMin() {
                return 0;
            }

            public int getMax() {
                return 30;
            }
        } );

        final JCheckBox absorbtiveSlit = new JCheckBox( "Absorbing Barriers", getDiscreteModel().isSlitAbsorptive() );
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
        final JSlider comp = new JSlider( setter.getMin(), setter.getMax(), setter.getValue( horizontalDoubleSlit ) );
        comp.setBorder( BorderFactory.createTitledBorder( title ) );
        comp.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setter.valueChanged( comp.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                comp.setValue( setter.getValue( horizontalDoubleSlit ) );
            }
        } );
        return comp;
    }

//    private JComponent createComponentORIG( String title, final Setter setter ) {
//        final Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 100, setter.getMin(), setter.getMax() );
//        final JSlider comp = new JSlider( 0, 100, (int)linearFunction.createInverse().evaluate( setter.getValue( horizontalDoubleSlit ) ) );
//        comp.setBorder( BorderFactory.createTitledBorder( title ) );
//        comp.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                double v = linearFunction.evaluate( comp.getValue() );
//                setter.valueChanged( v );
//            }
//        } );
//        module.getClock().addClockListener( new ClockAdapter() {
//            public void clockTicked( ClockEvent event ) {
//                comp.setValue( (int)linearFunction.createInverse().evaluate( setter.getValue( horizontalDoubleSlit ) ) );
//            }
//        } );
//        return comp;
//    }

    private static interface Setter {
        void valueChanged( int val );

        int getValue( HorizontalDoubleSlit horizontalDoubleSlit );

        int getMin();

        int getMax();
    }

    private DiscreteModel getDiscreteModel() {
        return discreteModel;
    }
}
