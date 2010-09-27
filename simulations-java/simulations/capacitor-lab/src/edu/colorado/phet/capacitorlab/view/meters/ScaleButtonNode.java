/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Button used for changing the scale on the bar meters.
 * The button can be either disabled or enabled.
 * When disabled, it appears grayed out.
 * When enabled, it shows either a '+' or '-' to indicate how the scale will be changed.
 * <p>
 * Origin is at the upper-left corner of the bounding rectangle.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ScaleButtonNode extends PComposite {
    
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
    
    private final PPath glassNode, handleNode, minusNode;
    private final PlusNode plusNode;
    
    private boolean enabled, plusVisible;
    
    public ScaleButtonNode() {
        
        glassNode = new PPath( new Ellipse2D.Double( 0, 0, GLASS_DIAMETER, GLASS_DIAMETER ) );
        glassNode.setPaint( new Color( 0, 0, 0, 0 ) ); // transparent, so that clicking in the center of the glass works
        glassNode.setStroke( GLASS_STROKE );
        glassNode.setStrokePaint( ENABLED_COLOR );
        addChild( glassNode );
        
        handleNode = new PPath( new Rectangle2D.Double( 0, 0, HANDLE_WIDTH, HANDLE_HEIGHT ) );
        handleNode.setStroke( null );
        handleNode.setPaint( ENABLED_COLOR );
        addChild( handleNode );
        
        plusNode = new PlusNode();
        addChild( plusNode );
        
        // Minus sign, created using PPaths because PText("-") can be accurately centered.
        minusNode = new PPath( new Rectangle2D.Double( 0, 0, PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT ) );
        minusNode.setStroke( null );
        minusNode.setPaint( ENABLED_COLOR );
        addChild( minusNode );
        
        // layout
        double x = 0;
        double y = 0;
        glassNode.setOffset( x, y );
        x = glassNode.getFullBoundsReference().getCenterX() - ( handleNode.getFullBoundsReference().getWidth() / 2 );
        y = glassNode.getFullBoundsReference().getMaxY() - ( GLASS_STROKE_WIDTH / 2 );
        handleNode.setOffset( x, y );
        x = glassNode.getFullBoundsReference().getCenterX() - ( minusNode.getFullBoundsReference().getWidth() / 2 );
        y = glassNode.getFullBoundsReference().getCenterY() - ( minusNode.getFullBoundsReference().getHeight() / 2 );
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
    
    /*
     * Plus sign, created using PPaths because PText("+") can be accurately centered.
     * Origin at geometric center.
     */
    private static class PlusNode extends PComposite {
        
        private final PPath horizontalNode, verticalNode;
        
        public PlusNode() {
            
            horizontalNode = new PPath( new Rectangle2D.Double( -PLUS_MINUS_WIDTH/2, -PLUS_MINUS_HEIGHT/2, PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT ) );
            horizontalNode.setStroke( null );
            horizontalNode.setPaint( ENABLED_COLOR );
            addChild( horizontalNode );
            
            verticalNode = new PPath( new Rectangle2D.Double( -PLUS_MINUS_HEIGHT/2, -PLUS_MINUS_WIDTH/2, PLUS_MINUS_HEIGHT, PLUS_MINUS_WIDTH ) );
            verticalNode.setStroke( null );
            verticalNode.setPaint( ENABLED_COLOR );
            addChild( verticalNode );
        }
    }
}
