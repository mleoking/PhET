// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.graphics.TriColorRoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PCanvas;

/**
 * PhET's visual representation of a shaded sphere.
 * It has a 3D look with a specular highlight at the upper left, and shadow towards the lower right.
 * Override getHighlightCenter if you want to move the highlight.
 * <p/>
 * This implementation uses SphericalNode via composition instead of subclassing,
 * because SphericalNode's interface uses Paint, and we're constrained to Color.
 * <p/>
 * Origin is at the center of the sphere.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShadedSphereNode extends PhetPNode {

    private static final Color DEFAULT_HIGHLIGHT = Color.WHITE;
    private static final Color DEFAULT_SHADOW = Color.BLACK;

    private final SphericalNode sphericalNode;
    private final Color highlightColor, mainColor, shadowColor; // save colors to regenerate gradient if diameter changes

    /**
     * Constructs an atom node with default highlight and shadow colors.
     *
     * @param diameter
     * @param color
     */
    public ShadedSphereNode( double diameter, Color color ) {
        this( diameter, color, DEFAULT_HIGHLIGHT, DEFAULT_SHADOW, false /* convertToImage */ );
    }

    /**
     * @param diameter
     * @param mainColor      main color of the atom
     * @param highlightColor color used for the specular highlight
     * @param shadowColor    color used for the shadow
     */
    public ShadedSphereNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor ) {
        this( diameter, mainColor, highlightColor, shadowColor, false /* convertToImage */ );
    }

    /**
     * @param diameter
     * @param mainColor      main color of the atom
     * @param highlightColor color used for the specular highlight
     * @param shadowColor    color used for the shadow
     * @param convertToImage gradient paint used herein is expensive, setting this to true converts to an image
     */
    public ShadedSphereNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor, boolean convertToImage ) {
        this.mainColor = mainColor;
        this.highlightColor = highlightColor;
        this.shadowColor = shadowColor;
        sphericalNode = new SphericalNode( diameter, createPaint( diameter, highlightColor, mainColor, shadowColor ), convertToImage );
        addChild( sphericalNode );
    }

    /**
     * Sets the diameter.
     * This requires adjusting the paint, so that the gradient matches the new diameter.
     *
     * @param diameter
     */
    public void setDiameter( double diameter ) {
        if ( diameter != sphericalNode.getDiameter() ) {
            sphericalNode.setPaint( createPaint( diameter, highlightColor, mainColor, shadowColor ) );
            sphericalNode.setDiameter( diameter );
        }
    }

    /**
     * Default highlight is at the upper-left.
     * If you want the highlight somewhere else, override this method.
     * If we find that we need to override this often, consider providing the
     * XY ratios for the highlight center as constructor parameters.
     *
     * @param diameter
     */
    protected Point2D getHighlightCenter( double diameter ) {
        return new Point2D.Double( -diameter / 6, -diameter / 6 );
    }

    /*
     * Creates a 3-color gradient paint for the SphereNode, to mimic a specular highlight and shadow.
     * SphereNode's origin is at the center of the sphere.
     */
    private Paint createPaint( double diameter, Color highlightColor, Color mainColor, Color shadowColor ) {
        Point2D highlightCenter = getHighlightCenter( diameter );
        double highlightMainSpan = diameter / 3; // distance for the gradient from highlightColor to mainColor
        double mainShadowSpan = 0.7 * diameter; // distance for the gradient from mainColor to shadowColor
        return new TriColorRoundGradientPaint( highlightColor, mainColor, shadowColor, highlightCenter.getX(), highlightCenter.getY(), highlightMainSpan, mainShadowSpan );
    }

    public static void main( String[] args ) {
        // node
        ShadedSphereNode node = new ShadedSphereNode( 300, new Color( 255, 127, 0 ) );
        node.setOffset( 250, 250 );
        // canvas
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 500, 500 ) );
        canvas.getLayer().addChild( node );
        // frame
        JFrame frame = new JFrame( "ShadedSphereNode" );
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
