package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:35:09 AM
 * Copyright (c) Jul 18, 2006 by Sam Reid
 */

public abstract class ConstrainedSliderControl extends VerticalLayoutPanel {
    private Font titleFont = new Font( "Lucida Sans", Font.BOLD, 12 );
    private JSlider slider;
    private ConstrainedSliderControl.TextReadout textReadout;

    private ConstrainedSliderControl.CoordinateFrame modelFrame;
    private ConstrainedSliderControl.CoordinateFrame viewFrame;
    private ConstrainedSliderControl.CoordinateFrame sliderFrame;
    private DecimalFormat format;

    public void init( String title, DecimalFormat format, CoordinateFrame modelFrame, CoordinateFrame viewFrame, CoordinateFrame sliderFrame ) {
        this.modelFrame = modelFrame;
        this.viewFrame = viewFrame;
        this.sliderFrame = sliderFrame;
        this.format = format;
        JLabel titleLabel = new JLabel( title ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        titleLabel.setFont( titleFont );
        setAnchor( GridBagConstraints.CENTER );
        setFillNone();
        add( titleLabel );
        slider = new JSlider( 0, getNumSliderValues(), (int)transform( getModelValue(), modelFrame, sliderFrame ) );
        slider.setPaintLabels( true );
        slider.setPaintTrack( true );
        slider.setPaintTicks( true );
        slider.setMajorTickSpacing( 4 );
        slider.setMinorTickSpacing( 1 );
        slider.setSnapToTicks( true );
        Hashtable labels = new Hashtable();
        System.out.println( "getNumSliderValues() = " + getNumSliderValues() );
        for( int i = 0; i <= getNumSliderValues(); i ++ ) {
            labels.put( new Integer( i ), new JLabel( format.format( transform( i, sliderFrame, viewFrame ) ) ) );
        }
        slider.setLabelTable( labels );
        addFullWidth( slider );
        setBorder( BorderFactory.createEtchedBorder() );
        setFillNone();
        textReadout = new ConstrainedSliderControl.TextReadout();
        add( textReadout );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                changeValue();
            }
        } );
        update();
        /*
        several coordinate frames here
        1. slider: 0..10 or so; dependent on component
         2. view (display): what the user sees.
         3. model (code): what value is used for computation. 
        */
    }

    protected double determineModelValue() {
        return transform( getSlider().getValue(), getSliderFrame(), getModelFrame() );
    }

    private int getNumSliderValues() {
        return (int)Math.round( sliderFrame.getRange() );
    }

    public JSlider getSlider() {
        return slider;
    }

    public static class CoordinateFrame {
        double min;
        double max;

        public CoordinateFrame( double min, double max ) {
            this.min = min;
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getRange() {
            return max - min;
        }

        public boolean contains( double value ) {
            return value >= min && value <= max;
        }

        public String toString() {
            return "min=" + min + ", max=" + max;
        }
    }

    protected double transform( double value, ConstrainedSliderControl.CoordinateFrame sourceFrame, ConstrainedSliderControl.CoordinateFrame dstFrame ) {
        if( sourceFrame.contains( value ) ) {
            return new Function.LinearFunction( sourceFrame.getMin(), sourceFrame.getMax(), dstFrame.getMin(), dstFrame.getMax() ).evaluate( value );
        }
        else {
            throw new RuntimeException( "Model frame doesn't contain value: " + value + ", sourceFrame=" + sourceFrame );
        }
    }

    public abstract double getModelValue();

    public abstract void setModelValue( double modelValue );

    private void changeValue() {
        setModelValue( determineModelValue() );
    }

    public void update() {
        slider.setValue( (int)Math.round( transform( getModelValue(), modelFrame, sliderFrame ) ) );
        textReadout.setText( "" + format.format( transform( getModelValue(), modelFrame, viewFrame ) ) + " nm" );
    }

    private static class TextReadout extends HorizontalLayoutPanel {
        private JTextField textField;

        public TextReadout() {
            textField = new JTextField( 10 );
            add( textField );
            textField.setEditable( false );
            textField.setBorder( null );
        }

        public void setText( String s ) {
            textField.setText( s );
        }
    }

    public CoordinateFrame getModelFrame() {
        return modelFrame;
    }

    public CoordinateFrame getViewFrame() {
        return viewFrame;
    }

    public CoordinateFrame getSliderFrame() {
        return sliderFrame;
    }
}
