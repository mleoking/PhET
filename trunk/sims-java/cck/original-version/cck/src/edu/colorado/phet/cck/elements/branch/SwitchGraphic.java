/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.components.Switch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 27, 2003
 * Time: 3:57:43 AM
 * Copyright (c) Oct 27, 2003 by Sam Reid
 */
public class SwitchGraphic extends ImageBranchGraphic {
    private Switch myswitch;
    private boolean open;
    private AffineTransform handleTransform;
//    private AffineTransform closedTransform;
    private BufferedImage handle;
    private Shape handleShape;
    private Point2D pivot;
    static final double OPEN_ANGLE = Math.PI / 4.0;
    static final double CLOSED_ANGLE = 0;
    private double angle = OPEN_ANGLE;
    private double baseAngle;

    public SwitchGraphic( final Circuit circuit, ModelViewTransform2D transform,
                          Switch branch, Color color, Stroke stroke, CCK2Module module,
                          BufferedImage baseImage, final BufferedImage handle, Stroke highlightStroke, Color highlightColor ) {
        super( circuit, transform, branch, color, stroke, module, baseImage, highlightStroke, highlightColor );
        this.myswitch = branch;
        this.handle = handle;
        myswitch.addSwitchListener( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
        LeverGraphic lg = new LeverGraphic();
        super.addGraphic( lg, 10 );
        if( branch.isOpen() ) {
            this.angle = OPEN_ANGLE;
        }
        else {
            this.angle = CLOSED_ANGLE;
        }

        update();
    }

    class LeverGraphic implements InteractiveGraphic {
        private Point dragStart;

        public boolean canHandleMousePress( MouseEvent event ) {
            return handleShape.contains( event.getPoint() );
        }

        public void mousePressed( MouseEvent event ) {
            dragStart = event.getPoint();
        }

        public void mouseDragged( MouseEvent event ) {
            Point p = event.getPoint();
            double angle = Math.atan2( p.y - pivot.getY(), p.x - pivot.getX() ) + Math.PI - baseAngle;
            while( angle > Math.PI * 2 ) {
                angle -= Math.PI * 2;
            }
            while( angle < 0 ) {
                angle += Math.PI * 2;
            }
            if( angle > Math.PI + Math.PI / 4 ) {
                angle = 0;
            }
            else if( angle > 2.18 ) {
                angle = 2.18;
            }
//            angle=Math.max(0,angle);
//            angle=Math.min(2.18,angle);
//            System.out.println("angle = " + angle);

            //.1 is closed
            setAngle( angle );
        }

        public void mouseMoved( MouseEvent e ) {
        }

        public void mouseReleased( MouseEvent event ) {
        }

        public void mouseClicked( MouseEvent e ) {
        }

        public void mouseEntered( MouseEvent event ) {
            event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
        }

        public void mouseExited( MouseEvent event ) {
            event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }

        public void paint( Graphics2D g ) {
            g.drawRenderedImage( handle, handleTransform );
            g.setColor( Color.blue );
//            g.fill(handleShape);
//            g.drawString("open="+open,100,100);
        }

        public boolean contains( int x, int y ) {
            return handleShape.contains( x, y );
        }

    }

    private void changed() {
        open = myswitch.isOpen();
    }

    public void setOpen( boolean open ) {
        if( open ) {
            setAngle( .1 );
        }
        else {
            setAngle( 0 );
        }
    }

    private void setAngle( double angle ) {
        this.angle = angle;
        if( angle <= .1 ) {
            myswitch.setOpen( false );
        }
        else {
            myswitch.setOpen( true );
        }
        update();
    }

    public void update() {
        if( handle == null ) {
            return;
        }
        super.update();
        baseAngle = super.getAngle();
        handleTransform = new AffineTransform( super.getImageTransform() );
//        handleTransform.translate(0, 0);
        handleTransform.rotate( angle, handle.getWidth(), handle.getHeight() / 2.0 );
        Rectangle2D.Double orig = new Rectangle2D.Double( 0, 0, 40, 47 );
        handleShape = handleTransform.createTransformedShape( orig );
        Point pivotOrig = new Point( handle.getWidth(), handle.getHeight() / 2 );
        pivot = handleTransform.transform( pivotOrig, null );
    }
}
