/**
 * Class: PowerMeter
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 22, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.event.MouseEvent;
import java.awt.*;

public class PowerMeter extends MouseInputAdapter implements Graphic {
//public class PowerMeter extends MouseInputAdapter {

    boolean init;
    int crosshairRadius = 15;
    Point2D.Double startPoint = new Point2D.Double();
    Point2D.Double endPoint = new Point2D.Double();
    private double y;
    private JTextField yTF = new JTextField( 5 );
    private MeasuringGraphic measuringGraphic = new MeasuringGraphic();
    private int xOffset;
    private int yOffset;
    private boolean dragging;
    private MeasuringGraphic graphic;

    public PowerMeter() {
        this.graphic = new MeasuringGraphic();
    }

    public void paint( Graphics2D graphics2D ) {
        graphic.paint( graphics2D );
    }

    // implements java.awt.event.MouseListener
    public void mousePressed( MouseEvent e ) {

        Rectangle box = new Rectangle( measuringGraphic.getLocation().x,
                                       measuringGraphic.getLocation().y,
                                       measuringGraphic.width,
                                       measuringGraphic.height );
        if( box.contains( e.getPoint() ) ) {
            dragging = true;
            startPoint = new Point2D.Double( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
            endPoint = new Point2D.Double( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
            xOffset = (int)( startPoint.x - measuringGraphic.getLocation().x );
            yOffset = (int)( startPoint.y - measuringGraphic.getLocation().y );
        }
    }

    // implements java.awt.event.MouseListener
    public void mouseReleased( MouseEvent e ) {
        dragging = false;
        super.mouseReleased( e );
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseMoved( MouseEvent e ) {
        super.mouseMoved( e );
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseDragged( MouseEvent e ) {
        if( dragging ) {
            endPoint = new Point2D.Double( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
            double dx = endPoint.getX() - startPoint.getX();
            double dy = endPoint.getY() - startPoint.getY();
            y = endPoint.getY();
        }
    }

    //
    // Inner classes
    //
    private class MeasuringGraphic implements Graphic {
        private int readoutWidth = 60;
        private int readoutHeight = 50;
        private int leading = 20;
        Point location = new Point();
        public int width = crosshairRadius * 2 + readoutWidth + 30;;
        public int height = readoutHeight + 20;

        private Point getLocation() {
            return location;
        }

        public void paint( Graphics2D g2 ) {
            // Get the transform attached to the Graphics2D so we can get at its attributes
            AffineTransform orgTx = g2.getTransform();
            // Set up the transform that will be applied to the thermometer images
            AffineTransform imageTx = new AffineTransform();
            // Translate the transform to the model origin
            imageTx.translate( orgTx.getTranslateX() / orgTx.getScaleX(), orgTx.getTranslateY() / orgTx.getScaleY() );
            // Concatenate the inverse of the model-to-Swing coords transform
            try {
                imageTx.concatenate( g2.getTransform().createInverse() );
            }
            catch( NoninvertibleTransformException e ) {
                throw new RuntimeException( e );
            }
            imageTx.scale( orgTx.getScaleX() , orgTx.getScaleY() );
            imageTx.translate( orgTx.getTranslateX(), orgTx.getTranslateY() );
            g2.setTransform( imageTx );
            GraphicsUtil.setAntiAliasingOn( g2 );

            // Translate the transform so the bottom of the image is now at the model origin
//            imageTx.translate( 0, -image.getHeight() );
//            // Move the image to it's location in model coordinates
//            imageTx.translate( location.getX() * orgTx.getScaleX() - image.getWidth() / 2,
//                               location.getY() * orgTx.getScaleY() );


            location.setLocation( (int)endPoint.getX() - xOffset, (int)endPoint.getY() - yOffset );
            Point upperLeft = new Point( location.x, location.y );
            Point readoutLocation = new Point( upperLeft.x + crosshairRadius * 2 + 10,
                                               upperLeft.y + crosshairRadius - readoutHeight / 2 );

            g2.setColor( Color.white );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ));
            g2.fillRoundRect(upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                              width, height,
                              5, 5);

            g2.setColor( Color.white );
//            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.DST_OUT, 1f ));
            g2.fillOval( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );

            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC, 1f ));
            g2.setColor( Color.BLACK );
            g2.setStroke( new BasicStroke( 2f ));
            g2.drawRoundRect( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                              crosshairRadius * 2 + readoutWidth + 30,
                              readoutHeight + 20,
                              5, 5);

            g2.setStroke( new BasicStroke( 3f ) );
            g2.drawOval( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
            g2.setStroke( new BasicStroke( 1f ) );
            g2.drawLine( upperLeft.x + crosshairRadius, upperLeft.y, upperLeft.x + crosshairRadius, upperLeft.y + crosshairRadius * 2 );
            g2.drawLine( upperLeft.x, upperLeft.y + crosshairRadius, upperLeft.x + crosshairRadius * 2, upperLeft.y + crosshairRadius );
            g2.drawRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );
            g2.setColor( Color.WHITE );
            g2.fillRect( readoutLocation.x + 1, readoutLocation.y + 1, readoutWidth - 1, readoutHeight - 1 );
            String pressureStr = "p = " + endPoint.x;
            g2.setColor( Color.black );
            g2.drawString( pressureStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 );
            String heightStr = "h = " + y;
            g2.setColor( Color.black );
            g2.drawString( heightStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading );

            g2.setTransform( orgTx );
        }
    }

    public void stepInTime( double v ) {
    }
}
