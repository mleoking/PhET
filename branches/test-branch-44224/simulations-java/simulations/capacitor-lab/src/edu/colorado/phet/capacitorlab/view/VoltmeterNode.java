/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A voltmeter that is independent of any specific model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterNode extends PhetPNode {
    
    // body of the meter
    private static final Image BODY_IMAGE = CLImages.VOLTMETER;
    
    // digital display
    private static final double DISPLAY_DEFAULT_VALUE = Double.NaN;
    private static final String DISPLAY_LABEL = "";
    private static final NumberFormat DISPLAY_VALUE_FORMAT = new DecimalFormat( "0.00" );
    private static final String DISPLAY_PATTERN = CLStrings.PATTERN_LABEL_VALUE_UNITS;
    private static final String DISPLAY_UNITS = CLStrings.UNITS_VOLTS;
    private static final String DISPLAY_NOT_A_NUMBER = "???"; //XXX i18n
    private static final Font DISPLAY_FONT = new PhetFont( 22 );
    private static final Color DISPLAY_TEXT_COLOR = Color.BLACK;
    private static final Color DISPLAY_BACKGROUND_COLOR = Color.WHITE;
    private static final boolean DISPLAY_SHOW_SIGN = true;
    
    // relationship between meter image and digital display bounds
    private static final double DISPLAY_X_MARGIN_TO_IMAGE_WIDTH_RATIO = 0.10;
    private static final double DISPLAY_Y_MARGIN_TO_IMAGE_HEIGHT_RATIO = 0.08;
    private static final double DISPLAY_HEIGHT_TO_IMAGE_HEIGHT_RATION = 0.28;
    
    // probes
    private static final Image POSITIVE_PROBE_IMAGE = CLImages.RED_PROBE;
    private static final Image NEGATIVE_PROBE_IMAGE = CLImages.BLACK_PROBE;
    
    private final BodyNode bodyNode; // the body of the voltmeter
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    
    public VoltmeterNode() {
        this( DISPLAY_LABEL, DISPLAY_VALUE_FORMAT, DISPLAY_UNITS, DISPLAY_PATTERN );
    }
    
    public VoltmeterNode( String label, NumberFormat valueFormat, String units, String displayPattern ) {
        
        // nodes
        bodyNode = new BodyNode( DISPLAY_DEFAULT_VALUE, label, valueFormat, units, displayPattern, DISPLAY_NOT_A_NUMBER, DISPLAY_SHOW_SIGN );
        positiveProbeNode = new PositiveProbeNode();
        negativeProbeNode = new NegativeProbeNode();
        
        // rendering order
        addChild( bodyNode );
        addChild( positiveProbeNode );
        addChild( negativeProbeNode );
        
        // layout
        double x = 0;
        double y = 0;
        bodyNode.setOffset( x, y );
        x = bodyNode.getFullBoundsReference().getMaxX() + 20;
        positiveProbeNode.setOffset( x, y );
        x = bodyNode.getFullBoundsReference().getMinX() - negativeProbeNode.getFullBoundsReference().getWidth() - 20;
        negativeProbeNode.setOffset( x, y );
    }
    
    public void setValue( double value ) {
        bodyNode.setValue( value );
    }
    
    /*
     * Body of the meter.
     */
    private static class BodyNode extends PComposite {

        private final DoubleDisplayNode displayNode;
        private final PPath displayBackgroundNode;
        
        public BodyNode( double value, String label, NumberFormat valueFormat, String units, String displayPattern, String notANumber, boolean showSign ) {
            
            // body of the meter
            PImage bodyNode = new PImage( BODY_IMAGE );
            addChild( bodyNode );
            
            // display background, assumes display is horizontally centered in the meter
            double x = DISPLAY_X_MARGIN_TO_IMAGE_WIDTH_RATIO * bodyNode.getFullBoundsReference().getWidth();
            double y = DISPLAY_Y_MARGIN_TO_IMAGE_HEIGHT_RATIO * bodyNode.getFullBoundsReference().getHeight();
            double w = bodyNode.getFullBoundsReference().getWidth() - ( 2 * x );
            double h = ( DISPLAY_HEIGHT_TO_IMAGE_HEIGHT_RATION * bodyNode.getFullBoundsReference().getHeight() ) - ( 2 * y );
            displayBackgroundNode = new PPath( new Rectangle2D.Double( x, y, w, h ) );
            displayBackgroundNode.setStroke( null );
            displayBackgroundNode.setPaint( DISPLAY_BACKGROUND_COLOR );
            addChild( displayBackgroundNode );
            
            // digital display
            displayNode = new DoubleDisplayNode( value, label, valueFormat, units, displayPattern, notANumber, showSign );
            displayNode.setFont( DISPLAY_FONT );
            displayNode.setHTMLColor( DISPLAY_TEXT_COLOR );
            addChild( displayNode );
            
            // display right justified
            displayNode.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent event ) {
                    if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                        updateLayout();
                    }
                }
            });
            
            // interactivity
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() {
                @Override
                public void drag( PInputEvent event ) {
                    super.drag( event );
                    //XXX notify that body location changed
                }
            } );
            
            updateLayout();
        }
        
        public void setValue( double value ) {
            displayNode.setValue( value );
        }
        
        private void updateLayout() {
            double x = displayBackgroundNode.getFullBoundsReference().getMaxX() - displayNode.getFullBoundsReference().getWidth() - 2;
            double y = displayBackgroundNode.getFullBoundsReference().getMaxY() - displayNode.getFullBoundsReference().getHeight() - 1;
            displayNode.setOffset( x, y );
        }
    }
    
    /*
     * Base class for probes.
     */
    private abstract static class ProbeNode extends PImage {

        public ProbeNode( Image image ) {
            super( image );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() {
                @Override
                public void drag( PInputEvent event ) {
                    super.drag( event );
                    //XXX notify that probe location changed
                }
            } );
        }
    }

    /*
     * Positive probe.
     */
    private static class PositiveProbeNode extends ProbeNode {

        public PositiveProbeNode() {
            super( POSITIVE_PROBE_IMAGE );
        }
    }

    /*
     * Negative probe.
     */
    private static class NegativeProbeNode extends ProbeNode {

        public NegativeProbeNode() {
            super( NEGATIVE_PROBE_IMAGE );
        }
    }

    /*
     * Cable that connects a probe to the meter.
     */
    private static class CableNode extends PhetPNode {

        public CableNode() {
            //XXX
        }
    }
    
}
