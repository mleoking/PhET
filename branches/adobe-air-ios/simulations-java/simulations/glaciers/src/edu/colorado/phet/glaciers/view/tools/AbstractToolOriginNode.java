// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.view.tools;

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
    
    protected AbstractToolOriginNode( double rotation ) {
        // arrow pointing to the left
        GeneralPath trianglePath = new GeneralPath();
        trianglePath.moveTo( 0f, 0f );
        trianglePath.lineTo( SIZE, -SIZE / 2 );
        trianglePath.lineTo( SIZE, SIZE / 2 );
        trianglePath.closePath();
        PPath pathNode = new PPath( trianglePath );
        pathNode.setPaint( FILL_COLOR );
        pathNode.setStroke( STROKE );
        pathNode.setStrokePaint( STROKE_COLOR );
        addChild( pathNode );
        pathNode.setOffset( 0, 0 );
        // rotate
        rotate( rotation );
    }
    
    public static class LeftToolOriginNode extends AbstractToolOriginNode {
        public LeftToolOriginNode() {
            super( 0 );
        }
    }
    
    public static class RightToolOriginNode extends AbstractToolOriginNode {
        public RightToolOriginNode() {
            super( Math.PI );
        }
    }
    
    public static class UpToolOriginNode extends AbstractToolOriginNode {
        public UpToolOriginNode() {
            super( Math.PI / 2 );
        }
    }
    
    public static class DownToolOriginNode extends AbstractToolOriginNode {
        public DownToolOriginNode() {
           super( Math.PI / 2 );
        }
    }
}
