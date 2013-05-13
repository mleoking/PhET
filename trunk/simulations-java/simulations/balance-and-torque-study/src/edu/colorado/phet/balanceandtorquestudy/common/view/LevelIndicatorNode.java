// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.common.model.Plank;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * The level indicator shows triangles to the side of the plank to help indicate whether the plank is at exactly 0 degrees.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class LevelIndicatorNode extends PNode {
    public LevelIndicatorNode( ModelViewTransform mvt, final Plank plank ) {

        // Locations for left and right edge
        final Point2D leftEdgeOfPlank = mvt.modelToView( new Point2D.Double( plank.getPivotPoint().getX() - Plank.LENGTH / 2,
                                                                             plank.getPlankSurfaceCenter().getY() ) );
        final Point2D rightEdgeOfPlank = mvt.modelToView( new Point2D.Double( plank.getPivotPoint().getX() + Plank.LENGTH / 2,
                                                                              plank.getPlankSurfaceCenter().getY() ) );

        // Draw a sort of arrow head shape.
        Shape leftIndicatorShape = new DoubleGeneralPath() {{
            moveTo( 0, 0 );
            lineTo( -40, -15 );
            lineTo( -30, 0 );
            lineTo( -40, 15 );
            closePath();
        }}.getGeneralPath();

        //Create paths for left and right side
        final PPath leftLevelIndicatorNode = new PhetPPath( leftIndicatorShape, new BasicStroke( 1f ), Color.BLACK );
        leftLevelIndicatorNode.setOffset( leftEdgeOfPlank.getX() - 5, leftEdgeOfPlank.getY() );
        addChild( leftLevelIndicatorNode );

        Shape rightIndicatorShape = AffineTransform.getScaleInstance( -1, 1 ).createTransformedShape( leftIndicatorShape );
        final PPath rightLevelIndicatorNode = new PhetPPath( rightIndicatorShape, new BasicStroke( 1f ), Color.BLACK );
        rightLevelIndicatorNode.setOffset( rightEdgeOfPlank.getX() + 5, rightEdgeOfPlank.getY() );
        addChild( rightLevelIndicatorNode );

        //Highlight if the plank is level
        plank.addShapeObserver( new SimpleObserver() {
            public void update() {
                if ( Math.abs( plank.getTiltAngle() ) < Math.PI / 1000 ) {
                    leftLevelIndicatorNode.setPaint( new Color( 173, 255, 47 ) );
                    rightLevelIndicatorNode.setPaint( new Color( 173, 255, 47 ) );
                }
                else {
                    leftLevelIndicatorNode.setPaint( Color.LIGHT_GRAY );
                    rightLevelIndicatorNode.setPaint( Color.LIGHT_GRAY );
                }
            }
        } );
    }
}