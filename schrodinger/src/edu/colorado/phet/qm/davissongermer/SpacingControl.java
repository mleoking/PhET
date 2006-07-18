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

public class SpacingControl extends VerticalLayoutPanel {
    private Font titleFont = new Font( "Lucida Sans", Font.BOLD, 12 );
    private DGModel dgModel;
    private JSlider slider;
    private TextReadout textReadout;

    private double viewMin = 0.4;
    private double viewMax = 1.2;
//    private double modelMin = 0.04;
//    private double modelMax = 0.12;

    double scaleTx = 10.0 * 1.0 / 45;
    private CoordinateFrame modelFrame = new CoordinateFrame( viewMin * scaleTx, viewMax * scaleTx );
    private CoordinateFrame viewFrame = new CoordinateFrame( viewMin, viewMax );
    private CoordinateFrame sliderFrame = new CoordinateFrame( 0, getNumSliderValues() );

    private int getNumSliderValues() {
        double viewIncrement = 0.1;
        return (int)( Math.round( viewFrame.getRange() / viewIncrement ) + 1 );
    }

    public SpacingControl( DGModel dgModel, double scale ) {
        this.dgModel = dgModel;
        JLabel title = new JLabel( "Atom Separation (D)" ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        title.setFont( titleFont );
        setAnchor( GridBagConstraints.CENTER );
        setFillNone();
        add( title );
        slider = new JSlider( 0, getNumSliderValues(), (int)transform( dgModel.getFractionalSpacing(), modelFrame, sliderFrame ) );
        slider.setPaintLabels( true );
        slider.setPaintTrack( true );
        slider.setPaintTicks( true );
        slider.setMajorTickSpacing( 4 );
        slider.setMinorTickSpacing( 1 );
        slider.setSnapToTicks( true );
        Hashtable labels = new Hashtable();
        System.out.println( "getNumSliderValues() = " + getNumSliderValues() );
        for( int i = 0; i <= getNumSliderValues(); i ++ ) {
            labels.put( new Integer( i ), new JLabel( new DecimalFormat( "0.0" ).format( transform( i, sliderFrame, viewFrame ) ) ) );
        }
        slider.setLabelTable( labels );
        addFullWidth( slider );
        setBorder( BorderFactory.createEtchedBorder() );
        setFillNone();
        textReadout = new TextReadout();
        add( textReadout );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                changeValue();
            }
        } );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                update();
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
    }

    private void changeValue() {
        double spacing = transform( slider.getValue(), sliderFrame, modelFrame );
//        System.out.println( "spacing = " + spacing );
        dgModel.setFractionalSpacing( spacing );
    }

    private double transform( double value, CoordinateFrame sourceFrame, CoordinateFrame dstFrame ) {
        return new Function.LinearFunction( sourceFrame.getMin(), sourceFrame.getMax(), dstFrame.getMin(), dstFrame.getMax() ).evaluate( value );
    }

    public void update() {
        slider.setValue( (int)Math.round( transform( dgModel.getFractionalSpacing(), modelFrame, sliderFrame ) ) );
        textReadout.setText( "" + new DecimalFormat( "0.0" ).format( transform( dgModel.getFractionalSpacing(), modelFrame, viewFrame ) ) + " nm" );
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

}
