/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AbstractToolOriginNode is a collection of nodes used to visually indicate
 * where the origin is on various measurement tools.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractToolOriginNode extends PComposite {

    private static final float SIZE = 10f;
    private static final Color FILL_COLOR = Color.WHITE;
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );
    
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    
    protected AbstractToolOriginNode( int direction ) {

        GeneralPath trianglePath = new GeneralPath();
        trianglePath.moveTo( 0f, 0f );
        
        switch ( direction ) {
        case LEFT:
            trianglePath.lineTo( SIZE, -SIZE / 2 );
            trianglePath.lineTo( SIZE, SIZE / 2 );
            break;
        case RIGHT:
            trianglePath.lineTo( -SIZE, -SIZE / 2 );
            trianglePath.lineTo( -SIZE, SIZE / 2 );
            break;
        case UP:
            trianglePath.lineTo( SIZE / 2, SIZE );
            trianglePath.lineTo( -SIZE / 2, SIZE );
            break;
        case DOWN:
            trianglePath.lineTo( SIZE / 2, -SIZE );
            trianglePath.lineTo( -SIZE / 2, -SIZE );
            break;
        default:
            throw new IllegalArgumentException( "unknown direction=" + direction );
        }
        
        trianglePath.closePath();
        PPath pathNode = new PPath( trianglePath );
        pathNode.setPaint( FILL_COLOR );
        pathNode.setStroke( STROKE );
        pathNode.setStrokePaint( STROKE_COLOR );
        addChild( pathNode );
        pathNode.setOffset( 0, 0 );
    }
    
    public static class LeftToolOriginNode extends AbstractToolOriginNode {
        public LeftToolOriginNode() {
            super( LEFT );
        }
    }
    
    public static class RightToolOriginNode extends AbstractToolOriginNode {
        public RightToolOriginNode() {
            super( RIGHT );
        }
    }
    
    public static class UpToolOriginNode extends AbstractToolOriginNode {
        public UpToolOriginNode() {
            super( UP );
        }
    }
    
    public static class DownToolOriginNode extends AbstractToolOriginNode {
        public DownToolOriginNode() {
           super( DOWN );
        }
    }
}
