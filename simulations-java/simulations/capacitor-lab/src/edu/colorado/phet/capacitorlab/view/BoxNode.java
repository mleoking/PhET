/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Pseudo-3D representation of a box, using parallelograms.
 * The top and side faces are foreshortened to give the illusion of distance between front and back planes.
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
    
    public BoxNode( Paint topPaint, Paint frontPaint, Paint sidePaint ) {
        this( 1, 1, 1, topPaint, frontPaint, sidePaint );
    }
    
    public BoxNode( double width, double depth, double height, Paint topPaint, Paint frontPaint, Paint sidePaint ) {
        
        topNode = new TopNode( topPaint );
        frontNode = new FrontNode( frontPaint );
        sideNode = new SideNode( sidePaint );
        
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
    
    public void setTopPaint( Paint paint ) {
        topNode.setPaint( paint );
    }
    
    public void setFrontPaint( Paint paint ) {
        frontNode.setPaint( paint );
    }
    
    public void setSidePaint( Paint paint ) {
        sideNode.setPaint( paint );
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
        x = frontNode.getFullBoundsReference().getWidth();
        y = frontNode.getYOffset();
        sideNode.setOffset( x, y );
        x = frontNode.getXOffset();
        y = frontNode.getYOffset();
        topNode.setOffset( x, y );

    }
    
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
    }
    
    private static class TopNode extends FaceNode {
        
        public TopNode( Paint paint ) {
            super( paint );
        }
        
        // parallelogram, origin at lower-left point
        public void setWidthAndDepth( double width, double depth ) {
            double xOffset = CLConstants.FORESHORTENING_FACTOR * depth * Math.cos( CLConstants.VIEWING_ANGLE );
            double yOffset = CLConstants.FORESHORTENING_FACTOR * depth * Math.sin( CLConstants.VIEWING_ANGLE );
            GeneralPath path = getPath();
            path.reset();
            path.moveTo( 0, 0 );
            path.lineTo( (float) xOffset, (float) -yOffset );
            path.lineTo( (float) ( width + xOffset ), (float) -yOffset );
            path.lineTo( (float) width, 0 );
            path.closePath();
            setPathTo( path );
        }
    }
    
    private static class FrontNode extends FaceNode {
        
        public FrontNode( Paint paint ) {
            super( paint );
        }
        
        // rectangle, origin at upper-left point
        public void setWidthAndHeight( double width, double height ) {
            GeneralPath path = getPath();
            path.reset();
            path.moveTo( 0, 0 );
            path.lineTo( (float) width, 0 );
            path.lineTo( (float) width, (float) height );
            path.lineTo( 0, (float) height );
            path.closePath();
            setPathTo( path );
        }
    }
    
    private static class SideNode extends FaceNode {
        
        public SideNode( Paint paint ) {
            super( paint );
        }
        
        // original at upper-left point
        public void setDepthAndHeight( double depth, double height ) {
            //XXX refactor, duplicate of code in TopNode.setWidthAndDepth
            double xOffset = CLConstants.FORESHORTENING_FACTOR * depth * Math.cos( CLConstants.VIEWING_ANGLE ); 
            double yOffset = CLConstants.FORESHORTENING_FACTOR * depth * Math.sin( CLConstants.VIEWING_ANGLE );
            GeneralPath path = getPath();
            path.reset();
            path.moveTo( 0, 0 );
            path.lineTo( (float) xOffset, (float) -yOffset );
            path.lineTo( (float) xOffset, (float) ( -yOffset + height ) );
            path.lineTo( 0, (float) height );
            path.closePath();
            setPathTo( path );
        }
    }
}
