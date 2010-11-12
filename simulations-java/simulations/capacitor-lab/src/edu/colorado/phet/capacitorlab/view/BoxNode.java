/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.shapes.BoxShapeFactory;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Pseudo-3D representation of a box, using parallelograms.
 * Three sides are visible: top, front, right.
 * The top and right-side faces are foreshortened to give the illusion of distance between front and back planes.
 * Origin is at the center of the top face.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BoxNode extends PhetPNode {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;

    private final BoxShapeFactory shapeFactory;
    private final PPath topNode, frontNode, sideNode;
    private double width, depth, height;
    
    public BoxNode( ModelViewTransform mvt, Color color ) {
        this( mvt, color, 1, 1, 1 );
    }
    
    public BoxNode( ModelViewTransform mvt, Color color, double width, double height, double depth ) {
        
        this.shapeFactory = new BoxShapeFactory( mvt );
        
        topNode = new PhetPPath( shapeFactory.createTopFace( width, height, depth ), getTopColor( color ), STROKE, STROKE_COLOR );
        frontNode = new PhetPPath( shapeFactory.createFrontFace( width, height, depth ), getFrontColor( color ), STROKE, STROKE_COLOR );
        sideNode = new PhetPPath( shapeFactory.createSideFace( width, height, depth ), getSideColor( color ), STROKE, STROKE_COLOR );
        
        addChild( topNode );
        addChild( frontNode );
        addChild( sideNode );
    }
    
    public void setSize( double width, double height, double depth ) {
        if ( width != this.width || height != this.height || depth != this.depth ) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            updateShapes();
        }
    }
    
    public void setColor( Color color ) {
        topNode.setPaint( getTopColor( color ) );
        frontNode.setPaint( getFrontColor( color ) );
        sideNode.setPaint( getSideColor( color ) );
    }
    
    private void updateShapes() {
        topNode.setPathTo( shapeFactory.createTopFace( width, height, depth ) );
        frontNode.setPathTo( shapeFactory.createFrontFace( width, height, depth ) );
        sideNode.setPathTo( shapeFactory.createSideFace( width, height, depth ) );
    }
    
    private Color getTopColor( Color baseColor ) {
        return baseColor;
    }

    private Color getFrontColor( Color baseColor ) {
        return getDarkerColor( getTopColor( baseColor ) );
    }

    private Color getSideColor( Color baseColor ) {
        return getDarkerColor( getFrontColor( baseColor ) );
    }
    
    /*
     * Color.darker doesn't preserve alpha, so we need our own method.
     * Notes that this results in the allocation of 2 Colors,
     * but that's not an issue for this sim.
     */
    private Color getDarkerColor( Color color ) {
        Color c = color.darker();
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), color.getAlpha() );
    }
}
