/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.view.IBoxColorStrategy.ThreeColorStrategy;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Pseudo-3D representation of a box, using parallelograms.
 * Three sides are visible: top, front, right.
 * The top and right-side faces are foreshortened to give the illusion of distance between front and back planes.
 * Origin is at the upper-left corner of the front face.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BoxNode extends PhetPNode {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;

    private final TopNode topNode;
    private final FrontNode frontNode;
    private final SideNode sideNode;
    private final IBoxColorStrategy colorStrategy;
    
    private double width, depth, height;
    
    public BoxNode( Color color ) {
        this( 1, 1, 1, color );
    }
    
    public BoxNode( double width, double depth, double height, Color color ) {
        
        colorStrategy = new ThreeColorStrategy();
        
        topNode = new TopNode( colorStrategy.getTopColor( color ) );
        frontNode = new FrontNode( colorStrategy.getFrontColor( color ) );
        sideNode = new SideNode( colorStrategy.getSideColor( color ) );
        
        addChild( topNode );
        addChild( frontNode );
        addChild( sideNode );
        
        // force an update
        this.width = -1;
        this.depth = -1;
        this.height = -1;
        setShape( width, depth, height );
    }
    
    public void setShape( double width, double depth, double height ) {
        if ( width != this.width || depth != this.depth || height != this.height ) {
            this.width = width;
            this.depth = depth;
            this.height = height;
            update();
        }
    }
    
    public void setColor( Color color ) {
        topNode.setPaint( colorStrategy.getTopColor( color ) );
        frontNode.setPaint( colorStrategy.getFrontColor( color ) );
        sideNode.setPaint( colorStrategy.getSideColor( color ) );
    }
    
    private void update() {
        
        // geometry
        topNode.setWidthAndDepth( width, depth );
        frontNode.setWidthAndHeight( width, height );
        sideNode.setDepthAndHeight( depth, height );

        // layout
        double x = 0;
        double y = 0;
        frontNode.setOffset( x, y );
        topNode.setOffset( x, y );
        x = frontNode.getFullBoundsReference().getMaxX();
        y = frontNode.getYOffset();
        sideNode.setOffset( x, y );
    }
    
    /*
     * Base class for all faces of the box.
     */
    private abstract static class FaceNode extends PPath {
        
        private final GeneralPath path;
        
        public FaceNode( Paint paint ) {
            path = new GeneralPath();
            setPaint( paint );
            setStroke( STROKE );
            setStrokePaint( STROKE_COLOR );
        }
        
        public GeneralPath getPath() {
            return path;
        }
        
        /*
         * Gets the x-offset required to create a pseudo-3D perspective between the front and back edges of a face.
         */
        protected static double getXOffsetForDepth( double depth ) {
            return depth * Math.sin( CLConstants.PITCH_VIEWING_ANGLE ) * Math.cos( Math.PI/2 + CLConstants.YAW_VIEWING_ANGLE );
        }
        
        /*
         * Gets the y-offset required to create a pseudo-3D perspective between the front and back edges of a face.
         */
        protected static double getYOffsetForDepth( double depth ) {
            return depth * Math.sin( CLConstants.PITCH_VIEWING_ANGLE ) * Math.sin( Math.PI/2 + CLConstants.YAW_VIEWING_ANGLE );
        }
    }
    
    /*
     * Top of the box is a parallelogram.
     * Origin at lower-left point.
     * Path specified using clockwise traversal.
     * 
     *        (x1,y1)----------(x2,y2)
     *          /                /
     *         /                /
     *     (0,0)------------(x3,y3)
     */
    private static class TopNode extends FaceNode {
        
        public TopNode( Paint paint ) {
            super( paint );
        }
        

        public void setWidthAndDepth( double width, double depth ) {
            float xOffset = (float) getXOffsetForDepth( depth );
            float yOffset = (float) getYOffsetForDepth( depth );
            GeneralPath path = getPath();
            path.reset();
            path.moveTo( 0f, 0f );
            path.lineTo( xOffset, -yOffset );
            path.lineTo( (float) ( width + xOffset ), -yOffset );
            path.lineTo( (float) width, 0f );
            path.closePath();
            setPathTo( path );
        }
    }
    
    /*
     * Front of the box is a rectangle.
     * Origin at upper-left point.
     * Path specified using clockwise traversal.
     * 
     *   (0,0)-----------(x1,y1)
     *     |                |
     *     |                |
     *  (x3,y3)----------(x2,y2)
     */
    private static class FrontNode extends FaceNode {
        
        public FrontNode( Paint paint ) {
            super( paint );
        }
        
        public void setWidthAndHeight( double width, double height ) {
            GeneralPath path = getPath();
            path.reset();
            path.moveTo( 0f, 0f );
            path.lineTo( (float) width, 0f );
            path.lineTo( (float) width, (float) height );
            path.lineTo( 0f, (float) height );
            path.closePath();
            setPathTo( path );
        }
    }
    
    /*
     * Side of the box is a parallelogram.
     * Original at upper-left point. 
     * Path specified using clockwise traversal.
     * 
     *           (x1,y1)
     *             / |
     *            /  |
     *           /   |
     *          /   (x2,y2)
     *       (0,0)  /
     *         |   /
     *         |  /
     *         | /
     *      (x3,y3)
     */
    private static class SideNode extends FaceNode {
        
        public SideNode( Paint paint ) {
            super( paint );
        }
        
        public void setDepthAndHeight( double depth, double height ) {
            float xOffset = (float) getXOffsetForDepth( depth );
            float yOffset = (float) getYOffsetForDepth( depth );
            GeneralPath path = getPath();
            path.reset();
            path.moveTo( 0f, 0f );
            path.lineTo( xOffset, -yOffset );
            path.lineTo( xOffset, (float) ( -yOffset + height ) );
            path.lineTo( 0f, (float) height );
            path.closePath();
            setPathTo( path );
        }
    }
}
