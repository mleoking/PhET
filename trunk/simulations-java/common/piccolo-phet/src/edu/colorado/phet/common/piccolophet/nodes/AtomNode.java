/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.graphics.TriColorRoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PCanvas;

/**
 * PhET's visual representation of an atom.
 * It has a 3D look with a specular highlight at the upper left.
 * <p>
 * This implementation uses SphericalNode via composition instead of subclassing,
 * because SphericalNode's interface uses Paint, and we're constrained to Color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends PhetPNode {

    private static final Color DEFAULT_HIGHLIGHT = Color.WHITE;
    private static final Color DEFAULT_SHADOW = Color.BLACK;

    private final SphericalNode sphericalNode;
    private final Color highlightColor, mainColor, shadowColor; // save colors to regenerate gradient if diameter changes

    /**
     * Constructs an atom node with default highlight and shadow colors.
     * @param diameter
     * @param color
     */
    public AtomNode( double diameter, Color color ) {
        this( diameter, color, DEFAULT_HIGHLIGHT, DEFAULT_SHADOW, false /* convertToImage */ );
    }

    /**
     * @param diameter
     * @param mainColor main color of the atom
     * @param highlightColor color used for the specular highlight
     * @param shadowColor color used for the shadow
     */
    public AtomNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor ) {
        this( diameter, mainColor, highlightColor, shadowColor, false /* convertToImage */ );
    }

    /**
     * @param diameter
     * @param mainColor main color of the atom
     * @param highlightColor color used for the specular highlight
     * @param shadowColor color used for the shadow
     * @param convertToImage gradient paint used herein is expensive, setting this to true converts to an image
     */
    public AtomNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor, boolean convertToImage ) {
        this.mainColor = mainColor;
        this.highlightColor = highlightColor;
        this.shadowColor = shadowColor;
        sphericalNode = new SphericalNode( diameter, createPaint( diameter, highlightColor, mainColor, shadowColor ), convertToImage );
        addChild( sphericalNode );
    }

    /**
     * Sets the diameter.
     * This requires adjusting the paint, so that the gradient matches the new diameter.
     * @param diameter
     */
    public void setDiameter( double diameter ) {
        if ( diameter != sphericalNode.getDiameter() ) {
            sphericalNode.setPaint( createPaint( diameter, highlightColor, mainColor, shadowColor ) );
            sphericalNode.setDiameter( diameter );
        }
    }

    /*
     * Creates a 3-color gradient paint for the SphereNode, to mimic a specular highlight and shadow.
     * SphereNode's origin is at the center of the sphere.
     */
    private static Paint createPaint( double diameter, Color highlightColor, Color mainColor, Color shadowColor ) {
        double centerX = -diameter/6; // upper-left corner of the sphere
        double centerY = centerX;
        double highlightMainSpan = diameter/4; // distance for the gradient from highlightColor to mainColor
        double mainShadowSpan = 0.8 * diameter; // distance for the gradient from mainColor to shadowColor
        return new TriColorRoundGradientPaint( highlightColor, mainColor, shadowColor, centerX, centerY, highlightMainSpan, mainShadowSpan );
    }

    public static void main( String[] args ) {
        // node
        AtomNode node = new AtomNode( 50, Color.RED );
        node.setOffset( 100, 100 );
        // canvas
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 400, 400 ) );
        canvas.getLayer().addChild( node );
        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
