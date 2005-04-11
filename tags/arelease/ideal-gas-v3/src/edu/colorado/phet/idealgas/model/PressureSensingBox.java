/*
 * Class: PressureSensingBox
 * Package: edu.colorado.phet.physicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 29, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.controller.AddModelElementCmd;
import edu.colorado.phet.idealgas.util.ScalarDataRecorder;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 */
public class PressureSensingBox extends Box2D {

    private ScalarDataRecorder pressureRecorder;
    private PressureSlice pressureSlice;

    /**
     * Constructor
     */
    public PressureSensingBox( Point2D corner1, Point2D corner2, IdealGasModel model, AbstractClock clock ) {
        //    public PressureSensingBox( Vector2D corner1, Vector2D corner2 ) {
        super( corner1, corner2, model );
        pressureRecorder = new ScalarDataRecorder( clock );
        pressureSlice = new PressureSlice( this, model, clock );
        model.addPrepCmd( new AddModelElementCmd( model, pressureSlice ) );
        //        PhysicalSystem.instance().addPrepCmd( new AddModelElementCmd( pressureSlice ) );
        pressureSlice.setY( ( corner1.getY() + corner2.getY() ) / 2 );
    }

    //    public void setPhysicalSystem( PhysicalSystem physicalSystem ) {
    //        super.setPhysicalSystem( physicalSystem );
    //    }

    /**
     *
     */
    public void clear() {
        pressureRecorder.clear();
    }

    /**
     *
     */
    public double getPressure() {

        //        float perimeter = (( this.getMaxY() - this.getMinY() )
        //                            + ( this.getMaxX() - this.getMinX() )) * 2 ;
        //        float pressure = pressureRecorder.getDataAverage();
        ////        float pressure = pressureRecorder.getDataTotal();
        //        float pressureLength = (( this.getMaxX() - this.getMinX() ) * 2);
        ////        return pressure / pressureLength;
        //      return pressure / perimeter;
        return pressureSlice.getPressure();
    }

    /**
     * TODO: rewrite this to use methods in BOX2D that find closest wall
     *
     * @param particle
     */
    public Wall collideWithParticle( SphericalBody particle ) {

        throw new RuntimeException( "to be implemented" );
//        double vxPrev = particle.getVelocityPrev().getX();
//        double vyPrev = particle.getVelocityPrev().getY();
//
//        // Handle the change in momentum of the particle
//        Wall collidingWall = super.collideWithParticle( particle );
//
//        // Record the force of the collision
//        double vxNew = particle.getVelocity().getX();
//        double vyNew = particle.getVelocity().getY();
//        double particleMomentum = 0;
//
//        // If velocity changed directions, then we know that there was a momentum
//        // change
//        if( vxPrev * vxNew < 0 ) {
//            particleMomentum += 2 * Math.abs( vxNew ) * particle.getMass();
//        }
//        if( vyPrev * vyNew < 0 ) {
//            particleMomentum += 2 * Math.abs( vyNew ) * particle.getMass();
//        }
//
//        this.pressureRecorder.addDataRecordEntry( particleMomentum );
//        return collidingWall;
    }
}
