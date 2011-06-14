// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * SphericalNode draws a sphere, with origin at the center of the sphere.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SphericalNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final PPath _pathNode;
    private final PImage _imageNode;
    private boolean _convertToImage;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param diameter
     * @param fillPaint
     * @param stroke
     * @param strokePaint
     * @param convertToImage
     */
    public SphericalNode( double diameter, Paint fillPaint, Stroke stroke, Paint strokePaint, boolean convertToImage ) {
        super();

        _convertToImage = convertToImage;

        _pathNode = new PhetPPath();
        _pathNode.setPaint( fillPaint );
        _pathNode.setStroke( stroke );
        _pathNode.setStrokePaint( strokePaint );

        _imageNode = new PImage();

        if ( convertToImage ) {
            addChild( _imageNode );
        }
        else {
            addChild( _pathNode );
        }

        setDiameter( diameter );
    }

    /**
     * Constructs a spherical node with no stroke.
     *
     * @param diameter
     * @param fillPaint
     * @param convertToImage
     */
    public SphericalNode( double diameter, Paint fillPaint, boolean convertToImage ) {
        this( diameter, fillPaint, null, null, convertToImage );
    }

    /*
    * Convenience constructor, for use by subclass constructors.
    *
    * @param convertToImage
    */
    protected SphericalNode( boolean convertToImage ) {
        this( 1, null, null, null, convertToImage );
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    public double getDiameter() {
        return getFullBoundsReference().getWidth();
    }

    public void setDiameter( double diameter ) {
        Shape shape = new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter ); // origin at center
        _pathNode.setPathTo( shape );
        update();
    }

    public void setPaint( Paint paint ) {
        _pathNode.setPaint( paint );
        update();
    }

    public void setStroke( Stroke stroke ) {
        _pathNode.setStroke( stroke );
        update();
    }

    public void setStrokePaint( Paint paint ) {
        _pathNode.setStrokePaint( paint );
        update();
    }

    public void setConvertToImage( boolean convertToImage ) {
        if ( convertToImage != _convertToImage ) {
            _convertToImage = convertToImage;
            if ( convertToImage ) {
                addChild( _imageNode );
                removeChild( _pathNode );
            }
            else {
                addChild( _pathNode );
                removeChild( _imageNode );
            }
            update();
        }
    }

    public boolean isConvertToImage() {
        return _convertToImage;
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    private void update() {
        if ( _convertToImage ) {
            _imageNode.setImage( _pathNode.toImage() );
            // Move origin to center
            PBounds imageBounds = _imageNode.getFullBoundsReference();
            _imageNode.setOffset( -imageBounds.getWidth() / 2, -imageBounds.getHeight() / 2 );
        }
    }
}
