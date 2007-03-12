/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * SphericalNode draws a sphere, with origin at the center of the sphere.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SphericalNode extends PhetPNode {
    
    private PPath _pathNode;
    private PImage _imageNode;
    private boolean _convertToImage;
    
    /*
     * For use by subclass constructors.
     * 
     * @param convertToImage
     */
    protected SphericalNode( boolean convertToImage ) {
        this( 1, null, null, null, convertToImage );
    }
    
    /**
     * Constructs a spherical node with no stroke.
     * @param diameter
     * @param fillPaint
     * @param convertToImage
     */
    public SphericalNode( double diameter, Paint fillPaint, boolean convertToImage ) {
        this( diameter, fillPaint, null, null, convertToImage );
    }
       
    /**
     * Constructor.
     * @param diameter
     * @param fillPaint
     * @param stroke
     * @param strokePaint
     * @param convertToImage
     */
    public SphericalNode( double diameter, Paint fillPaint, Stroke stroke, Paint strokePaint, boolean convertToImage ) {
        super();
        
        _convertToImage = convertToImage;

        _pathNode = new PPath();
        _pathNode.setPaint( fillPaint );
        _pathNode.setStroke( stroke );
        _pathNode.setStrokePaint( strokePaint );
        
        if ( convertToImage ) {
            _imageNode = new PImage();
             addChild( _imageNode );
        }
        else {
            addChild( _pathNode );
        }
        
        setDiameter( diameter );
    }
    
    /**
     * Gets the diameter of the node.
     * @return double
     */
    public double getDiameter() {
        return getFullBounds().getWidth();
    }
    
    public void setDiameter( double diameter ) {
        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        _pathNode.setPathTo( shape );
        if ( _convertToImage ) {
            convertToImage();
        }
    }
    
    public void setPaint( Paint paint ) {
        _pathNode.setPaint( paint );
        if ( _convertToImage ) {
            convertToImage();
        }
    }
    
    public void setStroke( Stroke stroke ) {
        _pathNode.setStroke( stroke );
        if ( _convertToImage ) {
            convertToImage();
        }
    }
    
    public void setStrokePaint( Paint paint ) {
        _pathNode.setStrokePaint( paint );
        if ( _convertToImage ) {
            convertToImage();
        }
    }
    
    private void convertToImage() {
        _imageNode.setImage( _pathNode.toImage() );
        // Move origin to center
        _imageNode.setOffset( -_imageNode.getFullBounds().getWidth() / 2, -_imageNode.getFullBounds().getHeight() / 2 );
    }
}
