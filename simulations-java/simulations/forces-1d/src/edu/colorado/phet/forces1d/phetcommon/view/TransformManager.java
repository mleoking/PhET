/*  */
package edu.colorado.phet.forces1d.phetcommon.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 27, 2005
 * Time: 10:50:16 AM
 */

public class TransformManager {
    private JComponent component;
    private AffineTransform graphicTx = new AffineTransform();
    private AffineTransform mouseTx = new AffineTransform();
    private boolean referenceSizeSet;

    // Bounds of the panel when scale is 1:1
    private Rectangle referenceBounds;
    // Size of the canvas that PhetGraphics on this panel draw to
    private Dimension canvasSize = new Dimension();
    private double scale = 1.0;
    private static final boolean DEBUG_OUTPUT_ENABLED = false;
    private Point2D viewPointOrigin = new Point2D.Double();

    public TransformManager( JComponent component ) {
        this.component = component;
    }

    public void setReferenceSize() {
        referenceSizeSet = true;
        referenceBounds = component.getBounds();
        //this should probably be new Rectangle(0,0,dim);
    }

    public Rectangle getReferenceBounds() {
        return referenceBounds;
    }

    public AffineTransform getGraphicTx() {
        return graphicTx;
    }

    public Rectangle transform( Rectangle r ) {

        if ( graphicTx != null ) {
            Rectangle r2 = graphicTx.createTransformedShape( r ).getBounds();//TODO I thought PhetGraphics should return their exact screen bounds on a call to phetGraphic.getBounds(), which are the x,y,width,height in this method.
            //TODO maybe if we just change the transform on the GraphicLayerSet in this object, this would be automatic, and cleaner.
            return r2;
        }
        else {
            return r;
        }
    }

    public double getScale() {
        return scale;
    }

    public Dimension getCanvasSize() {
        return canvasSize;
//        return new Dimension(canvasSize);
    }

    public boolean isReferenceSizeSet() {
        return referenceSizeSet;
    }

    public void setScale( double scale ) {
        graphicTx = AffineTransform.getScaleInstance( scale, scale );
        graphicTx.translate( viewPointOrigin.getX(), viewPointOrigin.getY() );
        this.scale = scale;
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "ApparatusPanel2.setScale: scale=" + scale );
        }
        try {
            mouseTx = graphicTx.createInverse();
        }
        catch( NoninvertibleTransformException e1 ) {
            e1.printStackTrace();
        }
    }

    public AffineTransform getMouseTx() {
        return mouseTx;
    }

    /**
     * Determines the size of the current drawing area.
     *
     * @return true if the canvas size has changed, false if not
     */
    public boolean determineCanvasSize() {
        double refAspectRatio = referenceBounds.getHeight() / referenceBounds.getWidth();
        double currAspectRatio = ( (double) component.getHeight() ) / component.getWidth();
        double widthFactor = 1;
        double heightFactor = 1;
        if ( currAspectRatio < refAspectRatio ) {
            widthFactor = refAspectRatio / currAspectRatio;
        }
        else {
            heightFactor = currAspectRatio / refAspectRatio;
        }
        Dimension oldSize = new Dimension( canvasSize );
        canvasSize.setSize( referenceBounds.getWidth() * widthFactor, referenceBounds.getHeight() * heightFactor );
        if ( oldSize.width != canvasSize.width || oldSize.height != canvasSize.height ) {
            return true;
        }
        return false;
    }

    public void setReferenceSize( int width, int height ) {
        referenceSizeSet = true;
        referenceBounds = new Rectangle( width, height );
        if ( component.getWidth() > 0 && component.getHeight() > 0 ) {
            double asX = ( (double) width ) / component.getWidth();
            double asY = ( (double) height ) / component.getHeight();
            double aspectRatio = Math.min( 1.0 / asX, 1.0 / asY );
            setScale( aspectRatio );
            component.paintImmediately( 0, 0, component.getWidth(), component.getHeight() );
        }
//        System.out.println( "referenceBounds = " + referenceBounds );
    }

    public Point2D getViewPortOrigin() {
        return viewPointOrigin;
    }

    public void setViewPortOrigin( double x, double y ) {
        this.viewPointOrigin.setLocation( x, y );
        setScale( getScale() );
    }
}
