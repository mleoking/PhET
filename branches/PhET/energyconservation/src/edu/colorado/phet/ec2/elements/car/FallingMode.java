/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.car;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.elements.spline.CurveGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 27, 2003
 * Time: 12:19:31 AM
 * Copyright (c) Jul 27, 2003 by Sam Reid
 */
public class FallingMode implements CarMode {
    StateTransition st;
//    private DragHandler dh;
    private ModelElement bounds;
    Mover mover = new Mover();
    Speeder speeder = new Speeder();

    public FallingMode( ModelElement bounds ) {
        this.bounds = bounds;
    }

    public void stepInTime( Car c, double dt ) {
        if( st == null ) {
            return;
        }

//                double e1=c.getKineticEnergy()+c.getPotentialEnergy();
        c.applyNewton( dt );
        bounds.stepInTime( dt );
//        double e2=c.getKineticEnergy()+c.getPotentialEnergy();
//        friction+=(e2-e1);

        c.updateObservers();
        st.testAndApply( c, dt );
    }

    public void initialize( Car car, double dt ) {
    }

    public void mousePressed( CarGraphic cg, MouseEvent event ) {
        if( SwingUtilities.isLeftMouseButton( event ) ) {
            mover.mousePressed( cg, event );
        }
        else {
            speeder.mousePressed( cg, event );
        }
    }

    public void mouseDragged( CarGraphic cg, MouseEvent event ) {
        if( SwingUtilities.isLeftMouseButton( event ) ) {
            mover.mouseDragged( cg, event );
        }
        else {
            speeder.mouseDragged( cg, event );
        }
    }

    public void mouseReleased( CarGraphic cg, MouseEvent event ) {
        if( SwingUtilities.isLeftMouseButton( event ) ) {
            mover.mouseReleased( cg, event );
        }
        else {
            speeder.mouseReleased( cg, event );
        }
    }

    public void setStateTransition( StateTransition stateTransition ) {
        this.st = stateTransition;
    }

    /**
     * Classes.
     */
    class Speeder {
        DragHandler dh;
        double scale = 10;
        DecimalFormat df = new DecimalFormat( "#0.0#" );

        public void mousePressed( CarGraphic cg, MouseEvent event ) {
            dh = new DragHandler( event.getPoint(), cg.getCarViewLocation() );
            cg.getCar().setGrabbed( true );
            cg.getCar().setFriction( 0 );
            EC2Module.setMessage( "Setting Initial Velocity." );
        }

        public void mouseReleased( CarGraphic cg, MouseEvent event ) {
            Point vector = cg.getInitialVelocityVector();
            if( vector == null ) {
                return;
            }
            dh = null;
            cg.getCar().setGrabbed( false );
            Point pt = cg.getCarViewLocation();
            double vx = vector.x - pt.x;
            double vy = vector.y - pt.y;
            vx /= scale;
            vy /= -scale;
            cg.getCar().setVelocity( vx, vy );
            cg.setInitialVelocityVector( null );
        }

        public void mouseDragged( CarGraphic cg, MouseEvent event ) {
            Point velEndPoint = dh.getNewLocation( event.getPoint() );
//            Point velEndPoint = new Point(velEndPoint.x, velEndPoint.y);

            cg.setInitialVelocityVector( velEndPoint );
            PhetVector carloc = cg.getCar().getPosition();
            double angle = Math.atan2( velEndPoint.y - carloc.getY(), velEndPoint.x - carloc.getX() );
            Car c = cg.getCar();
            Point viewLoc = cg.getCarViewLocation();
            c.setAngle( angle + Math.PI );
            c.updateObservers();
            PhetVector velocityVector = new PhetVector( velEndPoint.x - viewLoc.x, velEndPoint.y - viewLoc.y );
            double speed = velocityVector.getMagnitude() / scale;
            double mph = speed * 2.23693629205;
//            Point initvel=new Point(velocityVector.getX(),velocityVector.getY());
            EC2Module.setMessage( "Initial Speed=" + df.format( speed ) + " m/s   ( " + df.format( mph ) + " mph )", 1000 );
        }
    }

    class Mover {
        DragHandler dh;
        private double lastHeight = 0;

        public void mouseReleased( CarGraphic cg, MouseEvent event ) {
            dh = null;
            Car c = cg.getCar();
            c.setGrabbed( false );
            c.setVelocity( 0, 0 );
        }

        public void mousePressed( CarGraphic cg, MouseEvent event ) {
            dh = new DragHandler( event.getPoint(), new Point() );//Create with view coords.
            Car c = cg.getCar();
            c.setGrabbed( true );
            c.setFriction( 0 );
            c.setVelocity( 0, 0 );
        }

        public void mouseDragged( CarGraphic cg, MouseEvent event ) {
            if( dh == null ) {
                mousePressed( cg, event );
            }
            ModelViewTransform2d transform = cg.getTransform();
            Car c = cg.getCar();
            Point rel = dh.getNewLocation( event.getPoint() );
            //New view coordinates.
//                Point rel = dragger.getDifferentialLocation(event.getPoint());
            Point2D.Double modelPt = CurveGraphic.transformLocalViewToModel( transform, rel.x, rel.y );//transform.viewToModel(rel.x,rel.y);
//        Point2D.Double modelPoint = transform.viewToModel(novel.x, novel.y);
            c.setPosition( c.getX() + modelPt.x, c.getY() + modelPt.y );
//        module.getApparatusPanel().repaint();
            dh = new DragHandler( event.getPoint(), new Point() );

            double height = c.getHeightAboveGround();
            if( height > lastHeight ) {
                EC2Module.setMessage( "Adding energy from external source.", 1000 );
            }
            else if( height < lastHeight ) {
                EC2Module.setMessage( "Releasing energy to external source.", 1000 );
            }
            else {
            }
            lastHeight = height;
        }
    }
}
