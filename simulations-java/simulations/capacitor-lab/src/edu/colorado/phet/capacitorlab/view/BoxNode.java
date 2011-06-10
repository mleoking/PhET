// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.shapes.BoxShapeCreator;
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

    private final BoxShapeCreator shapeCreator; // creates shapes for the visible faces
    private final PPath topNode, frontNode, sideNode; // the visible faces
    private Dimension3D size;

    /**
     * Constructor
     *
     * @param mvt   model-view transform
     * @param color
     * @param size
     */
    public BoxNode( CLModelViewTransform3D mvt, Color color, Dimension3D size ) {

        this.shapeCreator = new BoxShapeCreator( mvt );
        this.size = new Dimension3D( size );

        topNode = new PhetPPath( shapeCreator.createTopFace( size ), getTopColor( color ), STROKE, STROKE_COLOR );
        frontNode = new PhetPPath( shapeCreator.createFrontFace( size ), getFrontColor( color ), STROKE, STROKE_COLOR );
        sideNode = new PhetPPath( shapeCreator.createSideFace( size ), getSideColor( color ), STROKE, STROKE_COLOR );

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
        topNode.setPathTo( shapeCreator.createTopFace( size ) );
        frontNode.setPathTo( shapeCreator.createFrontFace( size ) );
        sideNode.setPathTo( shapeCreator.createSideFace( size ) );
    }

    // top color is the base color
    private Color getTopColor( Color baseColor ) {
        return baseColor;
    }

    // front color is one shade darker
    private Color getFrontColor( Color baseColor ) {
        return getDarkerColor( getTopColor( baseColor ) );
    }

    // side color is two shades darker
    private Color getSideColor( Color baseColor ) {
        return getDarkerColor( getDarkerColor( baseColor ) );
    }

    /*
     * Color.darker doesn't preserve alpha, so we need our own method.
     * Note that this results in the allocation of 2 Colors,
     * but that's not an issue for this sim.
     */
    private Color getDarkerColor( Color color ) {
        Color c = color.darker();
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), color.getAlpha() );
    }
}
