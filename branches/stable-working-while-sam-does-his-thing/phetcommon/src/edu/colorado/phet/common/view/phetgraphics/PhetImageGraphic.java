/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PhetImageGraphic
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    private Point location;
    private AffineTransform relativeTx;
    private AffineTransform absoluteTx = new AffineTransform();
    private boolean shapeDirty = true;
    private Shape shape;

    public PhetImageGraphic( Component component ) {
        this( component, (BufferedImage)null );
    }

    public PhetImageGraphic( Component component, String imageResourceName ) {
        this( component, (BufferedImage)null );

        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Image resource not found: " + imageResourceName );
        }
        setImage( bufferedImage );
    }

    public PhetImageGraphic( Component component, BufferedImage image ) {
        this( component, image, new AffineTransform() );
    }

    public PhetImageGraphic( Component component, BufferedImage image, int x, int y ) {
        super( component );
        this.image = image;
        this.location = new Point( x, y );
        this.relativeTx = new AffineTransform();
        updateAbsoluteTx();
    }

    public PhetImageGraphic( Component component, BufferedImage image, AffineTransform transform ) {
        super( component );
        this.image = image;
        this.location = new Point( 0, 0 );
        this.relativeTx = transform;
        updateAbsoluteTx();
    }

    public Shape getShape() {
        Shape shape = null;
        if( image != null && shapeDirty ) {
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            this.shape = absoluteTx.createTransformedShape( rect );
            shape = this.shape;
        }
        return shape;
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape().contains( x, y );
    }

    protected Rectangle determineBounds() {
        Rectangle bounds = null;
        Shape shape = getShape();
        if( shape != null ) {
            bounds = shape.getBounds();
        }
        return bounds;
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            g.drawRenderedImage( image, absoluteTx );
        }
    }

    public void setPosition( int x, int y, double scale ) {
        AffineTransform tx = AffineTransform.getScaleInstance( scale, scale );
        setTransform( tx );
        setPosition( x, y );
    }

    public void setPositionCentered( int x, int y ) {
        if( image != null ) {
            setPosition( x - image.getWidth() / 2, y - image.getHeight() / 2 );
            updateAbsoluteTx();
        }
    }

    public void setPosition( int x, int y ) {
        if( location.x != x || location.y != y ) {
            location.setLocation( x, y );
            setBoundsDirty();
            shapeDirty = true;
            updateAbsoluteTx();
            repaint();
        }
    }

    public Point getPosition() {
        return location;
    }

    //    public void setPositionCentered( int x, int y, double scale ) {
    //        AffineTransform tx=AffineTransform.getTranslateInstance( x-image.getWidth( )/2,y-image.getHeight( )/2);
    //        tx.scale( scale, scale );
    //        setTransform( tx );
    //    }

    public void setTransform( AffineTransform transform ) {
        if( !transform.equals( this.relativeTx ) ) {
            this.relativeTx = transform;
            setBoundsDirty();
            shapeDirty = true;
            updateAbsoluteTx();
            repaint();
        }
    }

    /**
     * Any side effects produced on this relativeTx will not be automatically
     * observed by this class.  You must call setBoundsDirty(); repaint();
     * or simply setTransform().
     *
     * @return the AffineTransform associated with this PhetImageGraphic.
     */
    public AffineTransform getTransform() {
        return relativeTx;
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    private void updateAbsoluteTx() {
        absoluteTx.setToTranslation( location.x, location.y );
        absoluteTx.concatenate( relativeTx );
    }
}