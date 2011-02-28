// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.shapes.BoxShapeFactory;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
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
    private Dimension3D size;
    
    public BoxNode( CLModelViewTransform3D mvt, Color color, Dimension3D size ) {
        
        this.shapeFactory = new BoxShapeFactory( mvt );
        this.size = new Dimension3D( size );
        
        topNode = new PhetPPath( shapeFactory.createTopFace( size ), getTopColor( color ), STROKE, STROKE_COLOR );
        frontNode = new PhetPPath( shapeFactory.createFrontFace( size ), getFrontColor( color ), STROKE, STROKE_COLOR );
        sideNode = new PhetPPath( shapeFactory.createSideFace( size ), getSideColor( color ), STROKE, STROKE_COLOR );
        
        addChild( topNode );
        addChild( frontNode );
        addChild( sideNode );
    }
    
    public void setSize( Dimension3D size ) {
        if ( !size.equals( this.size ) ) {
            this.size = new Dimension3D( size );
            updateShapes();
        }
    }
    
    public void setColor( Color color ) {
        topNode.setPaint( getTopColor( color ) );
        frontNode.setPaint( getFrontColor( color ) );
        sideNode.setPaint( getSideColor( color ) );
    }
    
    private void updateShapes() {
        topNode.setPathTo( shapeFactory.createTopFace( size ) );
        frontNode.setPathTo( shapeFactory.createFrontFace( size ) );
        sideNode.setPathTo( shapeFactory.createSideFace( size ) );
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
