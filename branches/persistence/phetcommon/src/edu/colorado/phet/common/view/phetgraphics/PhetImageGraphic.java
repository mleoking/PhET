/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
<<<<<<< PhetImageGraphic.java
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
=======
 * Branch : $Name$
<<<<<<< PhetImageGraphic.java
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
>>>>>>> 1.17
=======
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
>>>>>>> 1.17.2.2
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
 * @author ?
 * @version $Revision$
 */
public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    private boolean shapeDirty = true;
    private Shape shape;
    private String imageResourceName;

    /**
     * Provided for Java Beans conformance
     */
    public PhetImageGraphic() {
        // noop
    }

    protected PhetImageGraphic( Component component ) {
        this( component, null, 0, 0 );
    }

    public PhetImageGraphic( Component component, String imageResourceName ) {
        this( component, (BufferedImage)null );
        setImageResourceName( imageResourceName );
    }

    public PhetImageGraphic( Component component, BufferedImage image ) {
        this( component, image, 0, 0 );
    }

    public PhetImageGraphic( Component component, BufferedImage image, int x, int y ) {
        super( component );
        this.image = image;
        setLocation( x, y );
    }

    public Shape getShape() {
        if( shapeDirty ) {
            AffineTransform transform = getNetTransform();
            if( image == null ) {
                return null;
            }
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            this.shape = transform.createTransformedShape( rect );
            shapeDirty = false;
        }
        return shape;
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape() != null && getShape().contains( x, y );
    }

    protected Rectangle determineBounds() {
        return getShape() == null ? null : getShape().getBounds();
    }

    public void paint( Graphics2D g ) {
        if( isVisible() && image != null ) {

            // todo: doing location entirely separately from the transform
//            g.translate( getLocation().getX(), getLocation().getY() );

            g.drawRenderedImage( image, getNetTransform() );
        }
    }

    public void setBoundsDirty() {
        super.setBoundsDirty();
        shapeDirty = true;
    }

    public void setImage( BufferedImage image ) {
        if( this.image != image ) {
            this.image = image;
            setBoundsDirty();
            autorepaint();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    ///////////////////////////////////////////////////
    // Persistence support
    //
    public String getImageResourceName() {
        return imageResourceName;
    }

    public void setImageResourceName( String imageResourceName ) {
        this.imageResourceName = imageResourceName;
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Image resource not found: " + imageResourceName );
        }
        setImage( bufferedImage );
    }
}
