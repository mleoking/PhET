/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.car;

import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.physics1d.Particle1d;
import edu.colorado.phet.ec2.DefaultLandingEvent;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.common.util.CursorHandler;
import edu.colorado.phet.ec2.elements.spline.Segment;
import edu.colorado.phet.ec2.elements.spline.SegmentPath;
import edu.colorado.phet.ec2.elements.spline.Spline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 27, 2003
 * Time: 12:21:05 AM
 * Copyright (c) Jul 27, 2003 by Sam Reid
 */
public class SplineMode implements CarMode {
    Particle1d particle;
    private Spline spline;
    StateTransition transition;
    private double totalEnergyOnInit;
    public static boolean healEnergy = true;
    private boolean outputEnergy = false;
    private boolean carExited = false;
    long exitTime;
    private int numTestLocations = 100;
    Spline lastCollidedSpline;
    private static boolean ALLOW_CORRECTION = true;

    public static boolean getAllowCorrection() {
        return ALLOW_CORRECTION;
    }

    public boolean isTop() {
        return top;
    }

    private boolean top;

    public void setStateTransition( StateTransition transition ) {
        this.transition = transition;
    }

    public SplineMode() {
    }

    public void setSpline( Spline spline ) {
        this.spline = spline;
    }

    public Spline getSpline() {
        return spline;
    }

    private void applyToCar( Car c ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        applyToCar( c, segmentPath );
    }

    private void applyToCar( Car c, SegmentPath segmentPath ) {
        Point2D.Double particle2DPosition = segmentPath.getPosition( particle.getPosition() );
        if( particle2DPosition != null ) {

            offsetFromTrack( c, particle2DPosition );

            c.setPosition( particle2DPosition.x, particle2DPosition.y );
            applyVelocity( c );
        }
    }

    private void offsetFromTrack( Car car, Point2D.Double candidatePoint ) {
        Segment seg = getCurrentSegment();
        if( seg != null ) {
            PhetVector dir = seg.getDirectionVector();
            PhetVector normal = dir.getNormalVector();
            normal.scale( car.getBoundsHeight() / 2 );
//            Point dx = module.getTransform().modelToViewDifferential(normal.getX(), normal.getY());

            if( top ) {
                candidatePoint.x += normal.getX();
                candidatePoint.y += normal.getY();
            }
            else {
                candidatePoint.x -= normal.getX();
                candidatePoint.y -= normal.getY();
            }
        }
    }

    private void applyVelocity( Car c ) {
        Segment seg = spline.getSegmentPath().getSegment( particle.getPosition() );
        PhetVector pv = PhetVector.parseAngleAndMagnitude( seg.getAngle(), particle.getVelocity() );
        c.setVelocity( pv.getX(), pv.getY() );
    }

    public void stepInTime( Car c, double dt ) {
        //need the angle of the part of track the car is on right now.
        SegmentPath segmentPath = spline.getSegmentPath();
        Segment seg = segmentPath.getSegment( particle.getPosition() );
        if( seg == null ) {
//            Toolkit.getDefaultToolkit().beep();
            carExited = true;
            exitTime = System.currentTimeMillis();
//            particle.setPosition(segmentPath.getLength() / 2);
        }
        else {
            double energyInit = c.getMechanicalEnergy();
            if( Double.isNaN( energyInit ) ) {
                new RuntimeException( "Mechanical Energy NaN" ).printStackTrace();
            }

            double gSinTheta = Math.sin( seg.getAngle() ) * c.getGravity();
            double proposedVelocity = particle.getVelocity() + gSinTheta * dt;
            double proposedPosition = particle.getPosition() + proposedVelocity * dt;
            particle.setPosition( proposedPosition );
            particle.setVelocity( proposedVelocity );
            applyToCar( c );
            //Can't heal energy for zero gravity.
            if( healEnergy && c.getGravity() != 0 && ALLOW_CORRECTION ) {
                correctEnergyWithHighestError( c, energyInit );
                //correctEnergyWithHeight(c, energyInit);
            }
            else if( !ALLOW_CORRECTION ) {
                //just account for it in terms of lost energy.
                double energyFinal = c.getMechanicalEnergy();
                double deltaEnergy = energyFinal - energyInit;
                c.addFriction( -deltaEnergy );
            }
            segmentPath = spline.getSegmentPath();
            seg = segmentPath.getSegment( particle.getPosition() );
            if( seg == null ) {
                carExited = true;
                exitTime = System.currentTimeMillis();
            }
            c.updateObservers();
        }
        boolean changed = transition.testAndApply( c, dt );
    }

    private void correctEnergyWithHighestError( Car c, double energyInit ) {
        double ke = c.getKineticEnergy();
        double pe = c.getPotentialEnergy();
        if( ke > 1 )//arbitrary joules threshold.
        {
            correctEnergyWithSpeed( c, energyInit );
        }
        else {
            correctEnergyWithHeight( c, energyInit );
        }
    }

    private void correctEnergyWithSpeed( Car c, double energyInit ) {
        double energyFinal = c.getMechanicalEnergy();
        double deltaEnergy = energyFinal - energyInit;
        double pe = c.getPotentialEnergy();
        double keReq = energyInit - pe;

//        if (outputEnergy)
//            O.d("InitEnergy=" + energyInit + ", final KE=" + ke + ", so Final PE should be: " + peReq);
//        double reqHeightAboveFloor = -(energyInit - ke) / (c.getMass() * c.getGravity());
        boolean positive = particle.getVelocity() >= 0;
        double reqSpeed = Math.sqrt( 2.0 * keReq / c.getMass() );
//        double speed = particle.getVelocity();

        if( !positive ) {
            reqSpeed *= -1;
        }
        if( Double.isNaN( reqSpeed ) ) {
            RuntimeException re = new RuntimeException( "Required Speed was NaN, energyInit=" + energyInit + ", energyFinal=" + energyFinal + ", deltaEnergy=" + deltaEnergy
                                                        + "pe=" + pe + ", kereq=" + keReq + ", pos=" + positive + ", requiredSpeed=" + reqSpeed );
            re.printStackTrace();
            return;
        }
        particle.setVelocity( reqSpeed );
//                O.d("Speed="+speed+", reqSpeed="+reqSpeed);

//                O.d("The height for that pe would be " + reqHeightAboveFloor);
//                System.out.println("(reqHeightAboveFloor*c.getMass()*c.getGravity()) = " + (reqHeightAboveFloor * c.getMass() * c.getGravity()));
//        c.setHeightAboveGround(reqHeightAboveFloor);
        applyToCar( c );
        double energyAfterPatch = c.getMechanicalEnergy();
        double deAfterPatch = energyAfterPatch - energyInit;
        if( outputEnergy ) {
            System.out.println( "de=" + deltaEnergy + ", deAfterPatch=" + deAfterPatch );
        }
//        O.d("time="+System.currentTimeMillis()+", deAfterPatch="+deAfterPatch);

    }

    private void correctEnergyWithHeight( Car c, double energyInit ) {
//        System.out.println("Correcting energy with height, c=" + c);
        double energyFinal = c.getMechanicalEnergy();
        double deltaEnergy = energyFinal - energyInit;
        //move up and down to get it right.
//            double y=c.getY();
        double ke = c.getKineticEnergy();
        double peReq = energyInit - ke;
        if( outputEnergy ) {
            System.out.println( "InitEnergy=" + energyInit + ", final KE=" + ke + ", so Final PE should be: " + peReq );
        }
        double reqHeightAboveFloor = -( energyInit - ke ) / ( c.getMass() * c.getGravity() );

//                O.d("The height for that pe would be " + reqHeightAboveFloor);
//                System.out.println("(reqHeightAboveFloor*c.getMass()*c.getGravity()) = " + (reqHeightAboveFloor * c.getMass() * c.getGravity()));
        if( Double.isNaN( reqHeightAboveFloor ) ) {
            new RuntimeException( "Required height above floor is NaN, energyInit=" + energyInit + ", energyFinal=" + energyFinal + ", ke=" + ke +
                                  "peReq=" + peReq + ", reqheight=" + reqHeightAboveFloor ).printStackTrace();
            c.setHeightAboveGround( c.getHeightAboveGround() );
        }
        else {

            c.setHeightAboveGround( reqHeightAboveFloor );

            double energyAfterPatch = c.getMechanicalEnergy();
            double deAfterPatch = energyAfterPatch - energyInit;
            if( outputEnergy ) {
                System.out.println( "de=" + deltaEnergy + ", deAfterPatch=" + deAfterPatch );
            }
        }
    }

    public long getTimeSinceExit() {
        return System.currentTimeMillis() - this.exitTime;
    }

    public Spline getLastCollidedSpline() {
        return lastCollidedSpline;
    }

    public void initialize( Car car, double dt ) {
        lastCollidedSpline = spline;
        double carEnergyBeforeCrash = car.getMechanicalEnergy();
        particle = new Particle1d();
        PhetVector carPositionInit = car.getPosition();
        Point2D.Double carPositionInitPt = new Point2D.Double( carPositionInit.getX(), carPositionInit.getY() );
        double x = DefaultLandingEvent.getSplineLocation( spline.getSegmentPath(), carPositionInitPt, numTestLocations );
        particle.setPosition( x );
        double speed = car.getVelocityVector().getMagnitude();

        totalEnergyOnInit = car.getMechanicalEnergy();
        PhetVector carVelocity2d = car.getVelocityVector();
        Segment seg = spline.getSegmentPath().getSegment( x );

        PhetVector pathDir = seg.getDirectionVector();
        determinePathSide( pathDir, carPositionInit, seg );

        double velocityParallel = pathDir.dot( carVelocity2d );
        speed = Math.abs( velocityParallel );

        PhetVector vectorBeforeLanding = car.getVelocityVector();
        double angleBeforeLanding = vectorBeforeLanding.getAngle();
        double ang = seg.getAngle();
        double dtheta = Math.abs( angleBeforeLanding - ang );
//        O.d("ang="+ang+", angBeforeLanding="+angleBeforeLanding+", dtheta="+dtheta+", speed="+speed);

        if( dtheta < Math.PI / 2 ) {//|| dtheta > Math.PI * 3 / 2) {
            particle.setVelocity( speed );
        }
        else {
            particle.setVelocity( -speed );
        }

        applyToCar( car );

        //totalEnergyOnInit = car.getMechanicalEnergy();used to be here.

        double carEnergyAfterCrash = car.getMechanicalEnergy();
        double de = carEnergyAfterCrash - carEnergyBeforeCrash;
        car.addFriction( -de );

        carExited = false;
    }

    Mover mover = new Mover();
    SpeedSetter speeder = new SpeedSetter();

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

    class SpeedSetter {
        DifferentialDragHandler dh;
        DecimalFormat df = new DecimalFormat( "#0.0#" );
        double scale = 10;
        private double modelSpeed = 0;
        private boolean positive;

        public void mousePressed( CarGraphic cg, MouseEvent event ) {
            dh = new DifferentialDragHandler( event.getPoint() );
            cg.getCar().setGrabbed( true );
            cg.getCar().setFriction( 0 );
            EC2Module.setMessage( "Setting Initial Velocity." );

            healEnergy = false;
            cursorHandler.setCursor( Cursor.CROSSHAIR_CURSOR, event.getComponent() );
            modelSpeed = 0;
        }

        public void mouseDragged( CarGraphic cg, MouseEvent event ) {
            Point dx = dh.getDifferentialLocation( event.getPoint() );

            PhetVector pathDir = getCurrentSegment().getDirectionVector();
            pathDir = new PhetVector( pathDir.getX(), -pathDir.getY() );
            PhetVector dxVector = new PhetVector( dx.x, dx.y );
            double dot = pathDir.dot( dxVector );
            if( dot > 0 ) {
                positive = true;
            }
            else {
                positive = false;
            }
            PhetVector dotVector = pathDir.getScaledInstance( dot );
            Point viewVelocity = new Point( (int)dotVector.getX(), (int)dotVector.getY() );
            cg.setInitialVelocityVector( new Point( cg.getX() + viewVelocity.x, cg.getY() + viewVelocity.y ) );
//            Point viewVelocity=new Point();
//            Point2D.Double modelVelocity=cg.getTransform().viewToModel(viewVelocity.x,viewVelocity.y);
            modelSpeed = viewVelocity.distance( 0, 0 ) / scale;
            if( !positive ) {
                modelSpeed = -modelSpeed;
            }

//            EC2Module.setMessage("Setting speed=" + df.format(modelSpeed) + " ");

            double mph = modelSpeed * 2.23693629205;
//            Point initvel=new Point(velocityVector.getX(),velocityVector.getY());
            EC2Module.setMessage( "Initial Speed=" + df.format( modelSpeed ) + " m/s   ( " + df.format( mph ) + " mph )", 1000 );
//            dh = new DifferentialDragHandler(event.getPoint());
        }

        public void mouseReleased( CarGraphic cg, MouseEvent event ) {
            dragger = null;
            particle.setVelocity( 0 );
            cg.setInitialVelocityVector( null );
            cg.getCar().setGrabbed( false );
            healEnergy = true;

            particle.setVelocity( modelSpeed );
            applyToCar( cg.getCar() );
            cursorHandler.setDefaultCursor( event.getComponent() );
        }
    }

    class Mover {
        Point moveStartPoint = null;
        private double releaseThreshold = 80;

        public void mousePressed( CarGraphic cg, MouseEvent event ) {
            moveStartPoint = event.getPoint();
            dragger = new DifferentialDragHandler( event.getPoint() );
            cg.getCar().setGrabbed( true );
            healEnergy = false;
            cursorHandler.setCursor( Cursor.HAND_CURSOR, event.getComponent() );
        }

        public void mouseDragged( CarGraphic cg, MouseEvent event ) {
            double distFromStart = event.getPoint().distance( moveStartPoint );
            if( distFromStart > 50 ) {
                PhetVector position = cg.getCar().getPosition();
                Point viewCoord = cg.getTransform().modelToView( position.getX(), position.getY() );
                if( viewCoord.distance( event.getPoint() ) > 100 ) {
                    mouseReleased( cg, event );
                    cg.getCar().setMode( cg.getCar().MODE_FALL );
                    event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                    Point2D.Double modelLocation = cg.getTransform().viewToModel( event.getPoint() );
                    cg.getCar().setPosition( modelLocation.x, modelLocation.y );
                    return;
                }
            }
            Car c = cg.getCar();

//            ModelViewTransform2d transform = cg.getTransform();
//            Point dx = dragger.getDifferentialLocation(event.getPoint());
            SegmentPath path = spline.getSegmentPath();

            Point2D.Double modelLocation = cg.getTransform().viewToModel( event.getPoint() );
            double scalar = DefaultLandingEvent.getSplineLocation( path, modelLocation, 40 );
            particle.setPosition( scalar );
            applyToCar( c );
            dragger = new DifferentialDragHandler( event.getPoint() );
        }

        public void mouseReleased( CarGraphic cg, MouseEvent event ) {
            dragger = null;
            particle.setVelocity( 0 );
            cg.getCar().setGrabbed( false );
            cursorHandler.setDefaultCursor( event.getComponent() );
            applyToCar( cg.getCar() );
            healEnergy = true;
        }
    }

    private void determinePathSide( PhetVector pathDir, PhetVector carPositionInit, Segment seg ) {
        PhetVector normal = pathDir.getNormalVector();
        //carPositionInit is the car position.
        PhetVector startPoint = new PhetVector( seg.getStartPoint().getX(), seg.getStartPoint().getY() );
        PhetVector carVector = carPositionInit.getSubtractedInstance( startPoint );
        double dot = normal.dot( carVector );
        if( dot > 0 ) {
            top = true;
        }
        else {
            top = false;
        }
    }

    DifferentialDragHandler dragger;
    CursorHandler cursorHandler = new CursorHandler();

    public boolean carExited() {
        return carExited;
    }

    public Segment getCurrentSegment() {
        return spline.getSegmentPath().getSegment( particle.getPosition() );
    }

    public double getVelocity() {
        return particle.getVelocity();
    }

    public static void setCorrectEnergy( boolean correct ) {
        ALLOW_CORRECTION = correct;
    }

}
