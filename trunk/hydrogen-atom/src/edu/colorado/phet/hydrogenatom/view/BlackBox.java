/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BlackBox is a 3D-looking black box with a "window" on the front face 
 * that can be opened and closed.  Opening the window allows you to see 
 * what's happening in the space behind the front of the box.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BlackBox {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color FRONT_COLOR = Color.BLACK;
    private static final Color TOP_COLOR_FRONT = Color.DARK_GRAY;
    private static final Color TOP_COLOR_BACK = Color.GRAY;
    private static final Color INSIDE_COLOR = Color.DARK_GRAY;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final float BACK_OFFSET = 0.15f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FrontNode _frontNode;
    private BackNode _backNode;
    
    //----------------------------------------------------------------------------
    //  Constructors
    //----------------------------------------------------------------------------
    
    public BlackBox( double width, double height, double depth ) {
        
        _frontNode = new FrontNode( width, height );
        _backNode = new BackNode( width, height, depth );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public PNode getFrontNode() {
        return _frontNode;
    }
    
    public PNode getBackNode() {
        return _backNode;
    }
    
    public void setOpen( boolean flag ) {
        if ( flag ) {
            open();
        }
        else {
            close();
        }
    }
    
    public void open() {
        _frontNode.open();
    }
    
    public void close() {
        _frontNode.close();
    }
    
    public void setOffset( double x, double y ) {
        _frontNode.setOffset( x, y );
        _backNode.setOffset( x, y );
    }

    public void setTransform( AffineTransform transform ) {
        _frontNode.setTransform( transform );
        _backNode.setTransform( transform );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Front of the box, with a window that can be opened to see inside.
     */
    private static class FrontNode extends PhetPNode {

        private PPath _insideNode;
        private Shape _shapeNoWindow;
        private Shape _shapeWithWindow;

        public FrontNode( double width, double height ) {
            super();

            setPickable( false );
            setChildrenPickable( false );

            _insideNode = new PPath();
            _insideNode.setPaint( FRONT_COLOR );
            _insideNode.setStroke( STROKE );
            _insideNode.setStrokePaint( STROKE_COLOR );
            addChild( _insideNode );

            _shapeNoWindow = new Rectangle2D.Double( 0, 0, width, height );

            // Use constructive geometry to punch a window in a rectangle.
            {
                Shape frontShape = new Rectangle2D.Double( 0, 0, width, height );
                Shape windowShape = new Rectangle2D.Double( 0.1 * width, 0.1 * height, width - ( 0.2 * width ), height - ( 0.2 * height ) );
                Area frontArea = new Area( frontShape );
                Area windowArea = new Area( windowShape );
                frontArea.subtract( windowArea );
                _shapeWithWindow = frontArea;
            }

            // Default state
            close();
        }
        
        public void open() {
            _insideNode.setPathTo( _shapeWithWindow );
        }

        public void close() {
            _insideNode.setPathTo( _shapeNoWindow );
        }
    }
    
    /* 
     * Back, side, top and bottom of the box.
     */
    private static class BackNode extends PhetPNode {

        public BackNode( double width, double height, double depth ) {
            super();
            
            setPickable( false );
            setChildrenPickable( false );
            
            PPath insideNode = new PPath();
            insideNode.setPathTo(  new Rectangle2D.Double( 0, 0, width, height ) );
            insideNode.setPaint( INSIDE_COLOR );
            insideNode.setStroke( STROKE );
            insideNode.setStrokePaint( STROKE_COLOR );
            
            final float w = (float)width;
            final float d = (float)depth;
            GeneralPath topPath = new GeneralPath();
            topPath.moveTo( 0, 0 ); 
            topPath.lineTo( BACK_OFFSET * w, -d );
            topPath.lineTo( ( 1 - BACK_OFFSET ) * w, -d );
            topPath.lineTo( w, 0f );
            topPath.closePath();
            PPath topNode = new PPath();
            topNode.setPathTo( topPath );
            topNode.setPaint( new GradientPaint( 0f, 0f, TOP_COLOR_FRONT, 0f, -d, TOP_COLOR_BACK ) );
            topNode.setStroke( STROKE );
            topNode.setStrokePaint( STROKE_COLOR );
            
            addChild( insideNode );
            addChild( topNode );
        }
    }
}
