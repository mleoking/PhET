/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.controls.DoubleSlitCheckBox;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;

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
    private DoubleSlitCheckBox doubleSlitCheckBox;

    public DoubleSlitPanel( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
        this.horizontalDoubleSlit = discreteModel.getDoubleSlitPotential();
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Double Slit" ) );

        doubleSlitCheckBox = new DoubleSlitCheckBox( "Enabled", getDiscreteModel() );
        add( doubleSlitCheckBox );
        doubleSlitCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setControlsEnabled( doubleSlitCheckBox.isSelected() );
            }
        } );

        verticalPosition = createComponent( "Vertical Position", new Setter() {
            public void valueChanged( double val ) {
                horizontalDoubleSlit.setY( (int)val );
            }

            public double getValue( HorizontalDoubleSlit horizontalDoubleSlit ) {
                return horizontalDoubleSlit.getY();
            }

            public double getMin() {
                return 0;
            }

            public double getMax() {
                return 100;
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


        add( slitSize );
        add( slitSeparation );
        add( verticalPosition );

        setControlsEnabled( doubleSlitCheckBox.isSelected() );
    }

    private void setControlsEnabled( boolean selected ) {
        slitSize.setEnabled( selected );
        slitSeparation.setEnabled( selected );
        verticalPosition.setEnabled( selected );
    }

    private JComponent createComponent( String title, final Setter setter ) {
//        HorizontalLayoutPanel horizontalLayoutPanel = new HorizontalLayoutPanel();
//        horizontalLayoutPanel.add( new JLabel( title ) );
        double origValue = setter.getValue( horizontalDoubleSlit );
        final Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 100, setter.getMin(), setter.getMax() );

        final JSlider comp = new JSlider( 0, 100, (int)linearFunction.createInverse().evaluate( origValue ) );
        comp.setBorder( BorderFactory.createTitledBorder( title ) );
        comp.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double v = linearFunction.evaluate( comp.getValue() );
                setter.valueChanged( v );
            }
        } );
//        horizontalLayoutPanel.add( comp );
        return comp;
//        return horizontalLayoutPanel;
    }

    public void addDoubleSlitCheckBoxListener( ActionListener actionListener ) {
        doubleSlitCheckBox.addActionListener( actionListener );
    }

    public boolean isDoubleSlitEnabled() {
        return doubleSlitCheckBox.isSelected();
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
