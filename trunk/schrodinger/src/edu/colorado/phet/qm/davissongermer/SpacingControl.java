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
    private Function.LinearFunction modelViewTransform;
    private Font titleFont = new Font( "Lucida Sans", Font.BOLD, 12 );
    private DGModel dgModel;
    private double scale;
    private JSlider slider;
    private TextReadout textReadout;

    public SpacingControl( DGModel dgModel, double scale ) {
        this.dgModel = dgModel;
        this.scale = scale;
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
        int numValues = getNumValues();
        modelViewTransform = new Function.LinearFunction( getModelMin(), getModelMax(), 0, numValues );
        slider = new JSlider( 0, numValues, 5 );
        slider.setPaintLabels( true );
        slider.setPaintTrack( true );
        slider.setPaintTicks( true );
        slider.setMajorTickSpacing( 4 );
        slider.setMinorTickSpacing( 1 );
        slider.setSnapToTicks( true );
        Hashtable labels = new Hashtable();
        for( int i = 0; i <= numValues; i++ ) {
            labels.put( new Integer( i ), new JLabel( new DecimalFormat( "0.0" ).format( sliderValueToModel( i ) ) ) );
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

    private void changeValue() {
        dgModel.setFractionalSpacing( sliderValueToModel( slider.getValue() ) / scale );
    }

    public double fractionalToModelValue( double fractional ) {
        return fractional * dgModel.getWavefunction().getWidth() * scale;
    }

    public double modelToFractionalValue( double model ) {
        return model / dgModel.getWavefunction().getWidth() / scale;
    }

    public void update() {
        double value = dgModel.getFractionalSpacing() * dgModel.getWavefunction().getWidth() / scale;
        slider.setValue( modelToSliderValue( value ) );
        textReadout.setText( "" + new DecimalFormat( "0.0" ).format( value ) + " nm" );
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

    private int getNumValues() {
        double increment = 0.1;
        return (int)( ( getModelRange() ) / increment );
    }

    private double getModelRange() {
        return getModelMax() - getModelMin();
    }

    private double getModelMax() {
        return 1.2;
    }

    private double getModelMin() {
        return 0.4;
    }

    public double sliderValueToModel( int view ) {
        return modelViewTransform.createInverse().evaluate( view );
    }

    public int modelToSliderValue( double model ) {
        return (int)modelViewTransform.evaluate( model );
    }
}
