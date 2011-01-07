// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.capacitorlab.view.MinusNode;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * "Zoom" button used for changing the scale on the bar meters.
 * The button can be either disabled or enabled.
 * When disabled, it appears grayed out.
 * When enabled, it shows either a '+' or '-' to indicate whether we'll be zooming in or out.
 * <p>
 * Origin is at the upper-left corner of the bounding rectangle.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZoomButtonNode extends PComposite {
    
    // all other dimensions are derived from the glass diameter
    private static final double GLASS_DIAMETER = 9;
    
    private static final Color ENABLED_COLOR = Color.BLACK;
    private static final Color DISABLED_COLOR = new Color( 180, 180, 180 ); // grey
    
    private static final float GLASS_STROKE_WIDTH = (float)( GLASS_DIAMETER / 7f );
    private static final Stroke GLASS_STROKE = new BasicStroke( GLASS_STROKE_WIDTH );
    
    private static final double HANDLE_WIDTH = 0.2 * GLASS_DIAMETER;
    private static final double HANDLE_HEIGHT = 0.65 * GLASS_DIAMETER;
    
    private static final double PLUS_MINUS_WIDTH = 0.6 * GLASS_DIAMETER;
    private static final double PLUS_MINUS_HEIGHT = 0.1 * GLASS_DIAMETER;
    
    private final PPath glassNode, handleNode;
    private final PlusNode plusNode;
    private final MinusNode minusNode;
    
    private boolean enabled, plusVisible;
    
    public ZoomButtonNode() {
        
        glassNode = new PPath( new Ellipse2D.Double( 0, 0, GLASS_DIAMETER, GLASS_DIAMETER ) );
        glassNode.setPaint( new Color( 0, 0, 0, 0 ) ); // transparent, so that clicking in the center of the glass works
        glassNode.setStroke( GLASS_STROKE );
        glassNode.setStrokePaint( ENABLED_COLOR );
        addChild( glassNode );
        
        handleNode = new PPath( new Rectangle2D.Double( 0, 0, HANDLE_WIDTH, HANDLE_HEIGHT ) );
        handleNode.setStroke( null );
        handleNode.setPaint( ENABLED_COLOR );
        addChild( handleNode );
        
        plusNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, ENABLED_COLOR );
        addChild( plusNode );
        
        // Minus sign, created using PPaths because PText("-") can be accurately centered.
        minusNode = new MinusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, ENABLED_COLOR );
        addChild( minusNode );
        
        // layout
        double x = 0;
        double y = 0;
        glassNode.setOffset( x, y );
        x = glassNode.getFullBoundsReference().getCenterX() - ( handleNode.getFullBoundsReference().getWidth() / 2 );
        y = glassNode.getFullBoundsReference().getMaxY() - ( GLASS_STROKE_WIDTH / 2 );
        handleNode.setOffset( x, y );
        x = glassNode.getFullBoundsReference().getCenterX();
        y = glassNode.getFullBoundsReference().getCenterY();
        minusNode.setOffset( x, y );
        x = glassNode.getFullBoundsReference().getCenterX();
        y = glassNode.getFullBoundsReference().getCenterY();
        plusNode.setOffset( x, y );
        
        // initial state
        enabled = false;
        plusVisible = true;
        update();
    }
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != this.enabled ) {
            this.enabled = enabled;
            update();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setPlusVisible( boolean plusVisible ) {
        if ( plusVisible != this.plusVisible ) {
            this.plusVisible = plusVisible;
            update();
        }
    }
    
    private void update() {
        // color
        Paint paint = ( enabled ? ENABLED_COLOR : DISABLED_COLOR );
        glassNode.setStrokePaint( paint );
        handleNode.setPaint( paint );
        // visibility
        plusNode.setVisible( enabled && plusVisible );
        minusNode.setVisible( enabled && !plusVisible );
    }
    
    public static Icon getZoomInIcon( double scale ) {
        return getZoomIcon( true /* plusVisible */, scale );
    }
    
    public static Icon getZoomOutIcon( double scale ) {
        return getZoomIcon( false /* plusVisible */, scale );
    }
    
    private static Icon getZoomIcon( boolean plusVisible, double scale ) {
        ZoomButtonNode node = new ZoomButtonNode();
        node.setEnabled( true );
        node.setPlusVisible( plusVisible );
        node.scale( scale );
        return new ImageIcon( node.toImage() );
    }
}
