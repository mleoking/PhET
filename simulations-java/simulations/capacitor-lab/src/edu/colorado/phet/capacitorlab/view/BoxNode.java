/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;
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

    private final TopNode topNode;
    private final FrontNode frontNode;
    private final SideNode sideNode;
    
    private double width, depth, height;
    
    public BoxNode( ModelViewTransform mvt, Color color ) {
        this( mvt, color, 1, 1, 1 );
    }
    
    public BoxNode( ModelViewTransform mvt, Color color, double width, double height, double depth ) {
        
        topNode = new TopNode( mvt, getTopColor( color ) );
        frontNode = new FrontNode( mvt, getFrontColor( color ) );
        sideNode = new SideNode( mvt, getSideColor( color ) );
        
        addChild( topNode );
        addChild( frontNode );
        addChild( sideNode );
        
        // force an update
        this.width = -1;
        this.depth = -1;
        this.height = -1;
        setSize( width, height, depth );
    }
    
    public void setSize( double width, double height, double depth ) {
        if ( width != this.width || height != this.height || depth != this.depth ) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            update();
        }
    }
    
    public void setColor( Color color ) {
        topNode.setPaint( getTopColor( color ) );
        frontNode.setPaint( getFrontColor( color ) );
        sideNode.setPaint( getSideColor( color ) );
    }
    
    private void update() {
        topNode.setSize( width, height, depth );
        frontNode.setSize( width, height, depth );
        sideNode.setSize( width, height, depth );
    }
    
    /*
     * Base class for all faces of the box.
     */
    private abstract static class FaceNode extends PPath {
        
        private final ModelViewTransform mvt;
        private final GeneralPath path;
        
        public FaceNode( ModelViewTransform mvt, Paint paint ) {
            this.mvt = mvt;
            this.path = new GeneralPath();
            setPaint( paint );
            setStroke( STROKE );
            setStrokePaint( STROKE_COLOR );
        }
        
        public abstract void setSize( double width, double height, double depth );
        
        protected ModelViewTransform getMvt() {
            return mvt;
        }
        
        protected void setPath( Point2D p0, Point2D p1, Point2D p2, Point2D p3 ) {
            path.reset();
            path.moveTo( (float) p0.getX(), (float) p0.getY() );
            path.lineTo( (float) p1.getX(), (float) p1.getY() );
            path.lineTo( (float) p2.getX(), (float) p2.getY() );
            path.lineTo( (float) p3.getX(), (float) p3.getY() );
            path.closePath();
            setPathTo( path );
        }
    }
    
    /*
     * Top of the box is a parallelogram.
     * Path specified using clockwise traversal.
     * 
     *          p1 -------------- p2
     *          /                /
     *         /                /
     *       p0 --------------p3
     */
    private static class TopNode extends FaceNode {
        
        public TopNode( ModelViewTransform mvt, Paint paint ) {
            super( mvt, paint );
        }
        
        public void setSize( double width, double height, double depth ) {
            // 3D model to 2D view transform
            Point2D p0 = getMvt().modelToView( -width / 2, 0, -depth / 2 );
            Point2D p1 = getMvt().modelToView( -width / 2, 0, depth / 2 );
            Point2D p2 = getMvt().modelToView( width / 2, 0, depth / 2 );
            Point2D p3 = getMvt().modelToView( width / 2, 0, -depth / 2 );
            // path
            setPath( p0, p1, p2, p3 );
        }
    }
    
    /*
     * Front of the box is a rectangle.
     * Path specified using clockwise traversal.
     * 
     *    p1 ----------- p2
     *     |              |
     *     |              |
     *    p0 ----------- p3
     */
    private static class FrontNode extends FaceNode {
        
        public FrontNode( ModelViewTransform mvt, Paint paint ) {
            super( mvt, paint );
        }
        
        public void setSize( double width, double height, double depth ) {
            // 3D model to 2D view transform
            Point2D p0 = getMvt().modelToView( -width / 2, height, -depth / 2 );
            Point2D p1 = getMvt().modelToView( -width / 2, 0, -depth / 2 );
            Point2D p2 = getMvt().modelToView( width / 2, 0, -depth / 2 );
            Point2D p3 = getMvt().modelToView( width / 2, height, -depth / 2 );
            // path
            setPath( p0, p1, p2, p3 );
        }
    }
    
    /*
     * Side of the box is a parallelogram.
     * Original at upper-left point. 
     * Path specified using clockwise traversal.
     * 
     *              p2
     *             / |
     *            /  |
     *           /   |
     *          /   p3
     *         p1   /
     *         |   /
     *         |  /
     *         | /
     *         p0
     */
    private static class SideNode extends FaceNode {
        
        public SideNode( ModelViewTransform mvt, Paint paint ) {
            super( mvt, paint );
        }
        
        public void setSize( double width, double height, double depth ) {
            // 3D model to 2D view transform
            Point2D p0 = getMvt().modelToView( width / 2, height, -depth / 2 );
            Point2D p1 = getMvt().modelToView( width / 2, 0, -depth / 2 );
            Point2D p2 = getMvt().modelToView( width / 2, 0, depth / 2 );
            Point2D p3 = getMvt().modelToView( width / 2, height, depth / 2 );
            // path
            setPath( p0, p1, p2, p3 );
        }
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
