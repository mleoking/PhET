/**
 * Class: MeasuringTape
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 3, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class PressureMeasurementTool extends MouseInputAdapter {

    boolean init;
    int crosshairRadius = 15;
    Point2D.Double startPoint = new Point2D.Double();
    Point2D.Double endPoint = new Point2D.Double();
    private edu.colorado.phet.common.view.ApparatusPanel panel;
    private double y;
    private JTextField yTF = new JTextField( 5 );
    private MeasuringGraphic measuringGraphic = new MeasuringGraphic();
    private ModelViewTransform2D tx;
    private boolean armed;
    private int xOffset;
    private int yOffset;
    private boolean dragging;

    public PressureMeasurementTool( edu.colorado.phet.common.view.ApparatusPanel panel ) {

        this.panel = panel;

    }

    // implements java.awt.event.MouseListener
    public void mousePressed( MouseEvent e ) {

        Rectangle box = new Rectangle( measuringGraphic.getLocation().x,
                                       measuringGraphic.getLocation().y,
                                       measuringGraphic.width,
                                       measuringGraphic.height );
        if( box.contains( e.getPoint() ) ) {
            dragging = true;
//        tx = panel.getTx();
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
//        endPoint = tx.viewToModel( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
            double dx = endPoint.getX() - startPoint.getX();
            double dy = endPoint.getY() - startPoint.getY();
            y = endPoint.getY();
        }
    }

    public void setArmed( boolean armed ) {
        this.armed = armed;
        if( armed ) {
            panel.addMouseListener( this );
            panel.addMouseMotionListener( this );
            panel.addGraphic( measuringGraphic, 9 );
        }
        else {
            panel.removeMouseListener( this );
            panel.removeMouseMotionListener( this );
            panel.removeGraphic( measuringGraphic );
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
        }
    }
}
