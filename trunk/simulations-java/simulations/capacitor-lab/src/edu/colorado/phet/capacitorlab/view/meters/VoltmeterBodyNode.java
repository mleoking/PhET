/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.capacitorlab.view.DoubleDisplayNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Body of the voltmeter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterBodyNode extends PComposite {
    
    // body of the meter
    private static final Image BODY_IMAGE = CLImages.VOLTMETER;
    
    // digital display
    private static final double DISPLAY_DEFAULT_VALUE = Double.NaN;
    private static final String DISPLAY_LABEL = "";
    private static final NumberFormat DISPLAY_VALUE_FORMAT = new DecimalFormat( "0.00" );
    private static final String DISPLAY_PATTERN = CLStrings.PATTERN_LABEL_VALUE_UNITS;
    private static final String DISPLAY_UNITS = CLStrings.VOLTS;
    private static final String DISPLAY_NOT_A_NUMBER = "???"; //XXX i18n
    private static final Font DISPLAY_FONT = new PhetFont( 16 );
    private static final Color DISPLAY_TEXT_COLOR = Color.BLACK;
    private static final Color DISPLAY_BACKGROUND_COLOR = Color.WHITE;
    private static final boolean DISPLAY_SHOW_SIGN = true;
    
    // relationship between meter image and digital display bounds
    private static final double DISPLAY_X_MARGIN_TO_IMAGE_WIDTH_RATIO = 0.10;
    private static final double DISPLAY_Y_MARGIN_TO_IMAGE_HEIGHT_RATIO = 0.08;
    private static final double DISPLAY_HEIGHT_TO_IMAGE_HEIGHT_RATION = 0.28;

    private final DoubleDisplayNode displayNode;
    private final PPath displayBackgroundNode;
    private final Point2D positiveConnectionOffset, negativeConnectionOffset; // offsets for connection points of wire that attach probes to body
    
    public VoltmeterBodyNode( final Voltmeter voltmeter, PNode dragBoundsNode ) {
        
        // body of the meter
        PImage imageNode = new PImage( BODY_IMAGE );
        addChild( imageNode );
        
        // display background, assumes display is horizontally centered in the meter
        double x = DISPLAY_X_MARGIN_TO_IMAGE_WIDTH_RATIO * imageNode.getFullBoundsReference().getWidth();
        double y = DISPLAY_Y_MARGIN_TO_IMAGE_HEIGHT_RATIO * imageNode.getFullBoundsReference().getHeight();
        double w = imageNode.getFullBoundsReference().getWidth() - ( 2 * x );
        double h = ( DISPLAY_HEIGHT_TO_IMAGE_HEIGHT_RATION * imageNode.getFullBoundsReference().getHeight() ) - ( 2 * y );
        displayBackgroundNode = new PPath( new Rectangle2D.Double( x, y, w, h ) );
        displayBackgroundNode.setStroke( null );
        displayBackgroundNode.setPaint( DISPLAY_BACKGROUND_COLOR );
        addChild( displayBackgroundNode );
        
        // digital display
        displayNode = new DoubleDisplayNode( DISPLAY_DEFAULT_VALUE, DISPLAY_LABEL, DISPLAY_VALUE_FORMAT, DISPLAY_UNITS, DISPLAY_PATTERN, DISPLAY_NOT_A_NUMBER, DISPLAY_SHOW_SIGN );
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
        
        PBounds b = imageNode.getFullBounds();
        positiveConnectionOffset = new Point2D.Double( b.getWidth() / 4, b.getMaxY() ); // bottom left
        negativeConnectionOffset = new Point2D.Double( 3 * b.getWidth() / 4, b.getMaxY() ); // bottom right
        
        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
        
        // observe model value
        voltmeter.addValueObserver( new SimpleObserver() {
            public void update() {
                displayNode.setValue( voltmeter.getValue() );
                updateLayout();
            }
        } );
    }
    
    public Point2D getPositiveConnectionOffset() {
        return new Point2D.Double( positiveConnectionOffset.getX(), positiveConnectionOffset.getY() );
    }
    
    public Point2D getNegativeConnectionOffset() {
        return new Point2D.Double( negativeConnectionOffset.getX(), negativeConnectionOffset.getY() );
    }
    
    private void updateLayout() {
        double x = displayBackgroundNode.getFullBoundsReference().getMaxX() - displayNode.getFullBoundsReference().getWidth() - 4;
        double y = displayBackgroundNode.getFullBoundsReference().getMaxY() - displayNode.getFullBoundsReference().getHeight() - 1;
        displayNode.setOffset( x, y );
    }
}