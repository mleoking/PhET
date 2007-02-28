package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ImagePortion implements InteractiveGraphic {
    private AffineTransform imageTransform;
    private BufferedImage image;
    private Shape imageShape;
    private DefaultBranchInteractionHandler interactionHandler;
    private BufferedImage flameImage;
    private Branch b;
    Rectangle imageRectagle;
    private AffineTransform fireTransform;

    public ImagePortion( BufferedImage image, DefaultBranchInteractionHandler interactionHandler, BufferedImage flame, Branch b ) {
        this.image = image;
        this.interactionHandler = interactionHandler;
        this.flameImage = flame;
        this.b = b;
        this.imageRectagle = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
    }

    public BufferedImage getFlameImage() {
        return flameImage;
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return imageShape.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        interactionHandler.mousePressed( event );
    }

    public void mouseDragged( MouseEvent event ) {
        interactionHandler.mouseDragged( event );
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent event ) {
        interactionHandler.mouseReleased( event );
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent event ) {
        interactionHandler.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        interactionHandler.mouseExited( event );
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( image, imageTransform );
        if( b.isOnFire() ) {
//            AffineTransform at=new AffineTransform(imageTransform);
//            AffineTransform trans=AffineTransform.getTranslateInstance(0,-75);
//            at.preConcatenate(trans);
            g.drawRenderedImage( flameImage, fireTransform );
        }
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
        update();
    }

    public Shape getImageShape() {
        return imageShape;
    }

    public void update() {
        Rectangle imageRect = imageRectagle;//getImageRectangle();
        this.imageShape = ( imageTransform.createTransformedShape( imageRect ) );
    }

    public Shape getExpandedShape( int expansionWidth ) {
        Rectangle2D.Double r = new Rectangle2D.Double( imageRectagle.x - expansionWidth / 2, imageRectagle.y - expansionWidth / 2,
                                                       imageRectagle.getWidth() + expansionWidth, imageRectagle.getHeight() + expansionWidth );
        return imageTransform.createTransformedShape( r );
    }

    public void setImageTransform( AffineTransform imageTransform, AffineTransform fireTransform ) {
        this.imageTransform = imageTransform;
        this.fireTransform = fireTransform;
        update();
    }

    public AffineTransform getImageTranform() {
        return imageTransform;
    }

    public BufferedImage getImage() {
        return image;
    }


    public int getImageWidth() {
        return image.getWidth();
    }

    public boolean contains( int x, int y ) {
        return imageShape.contains( x, y );
    }

}
