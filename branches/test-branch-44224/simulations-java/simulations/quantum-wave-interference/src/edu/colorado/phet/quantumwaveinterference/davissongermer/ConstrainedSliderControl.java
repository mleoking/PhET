package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.quantumwaveinterference.QWIResources;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:35:09 AM
 */

public abstract class ConstrainedSliderControl extends VerticalLayoutPanel {
    private Font titleFont = new PhetFont( Font.BOLD, 12 );
    private JSlider slider;
//    private ConstrainedSliderControl.TextReadout textReadout;

    private CoordinateFrame modelFrame;
    private CoordinateFrame viewFrame;
    private CoordinateFrame sliderFrame;
    private DecimalFormat format;
    private JLabel titleLabel;
    private String title;

    public void init( String title, DecimalFormat format, CoordinateFrame modelFrame, CoordinateFrame viewFrame, CoordinateFrame sliderFrame ) {
        this.title = title;
        this.modelFrame = modelFrame;
        this.viewFrame = viewFrame;
        this.sliderFrame = sliderFrame;
        this.format = format;
        titleLabel = new JLabel( title ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        titleLabel.setFont( titleFont );
        setAnchor( GridBagConstraints.WEST );
        setFillNone();
        add( titleLabel );
        slider = new JSlider( 0, getNumSliderValues() - 1, (int)transform( getModelValue(), modelFrame, sliderFrame ) );
        slider.setPaintLabels( true );
        slider.setPaintTrack( true );
        slider.setPaintTicks( true );
        slider.setMajorTickSpacing( 4 );
        slider.setMinorTickSpacing( 1 );
        slider.setSnapToTicks( true );
        Hashtable labels = new Hashtable();
        System.out.println( "getNumSliderValues() = " + getNumSliderValues() );
        for( int i = 0; i <= getNumSliderValues() - 1; i++ ) {
            labels.put( new Integer( i ), new JLabel( format.format( transform( i, sliderFrame, viewFrame ) ) ) );
        }
        slider.setLabelTable( labels );
        addFullWidth( slider );
        setBorder( BorderFactory.createEtchedBorder() );
        setFillNone();
//        textReadout = new ConstrainedSliderControl.TextReadout();
//        add( textReadout );
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
        return (int)Math.round( sliderFrame.getRange() ) + 1;
    }

    public JSlider getSlider() {
        return slider;
    }

    protected double transform( double value, CoordinateFrame sourceFrame, CoordinateFrame dstFrame ) {
        return sourceFrame.transform( value, dstFrame );
    }

    public abstract double getModelValue();

    public abstract void setModelValue( double modelValue );

    private void changeValue() {
        setModelValue( determineModelValue() );
    }

    public void update() {
        slider.setValue( (int)Math.round( transform( getModelValue(), modelFrame, sliderFrame ) ) );
        String text = "" + format.format( transform( getModelValue(), modelFrame, viewFrame ) );
//        textReadout.setText( text + " nm" );
        titleLabel.setText( MessageFormat.format( QWIResources.getString( "0.1.nm" ), new Object[]{title, text} ) );
    }

//    private static class TextReadout extends HorizontalLayoutPanel {
//        private JTextField textField;
//
//        public TextReadout() {
//            textField = new JTextField( 10 );
//            add( textField );
//            textField.setEditable( false );
//            textField.setBorder( null );
//        }
//
//        public void setText( String s ) {
//            textField.setText( s );
//        }
//    }
//

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
