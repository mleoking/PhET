/*  */
package edu.colorado.phet.forces1d.model;

/**
 * User: Sam Reid
 * Date: Jan 30, 2005
 * Time: 11:05:48 AM
 */
public abstract class BoundaryCondition {
    Force1DModel model;

    protected BoundaryCondition( Force1DModel model ) {
        this.model = model;
    }

    public abstract double apply();

    public abstract double getWallForce( double appliedForce, double frictionForce );

    public static interface Listener {
        void boundaryConditionOpen();

        void boundaryConditionWalls();
    }


    public static class Open extends BoundaryCondition {
        public Open( Force1DModel model ) {
            super( model );
        }

        public double apply() {
            model.setWallForce( 0.0 );
            return 0;
        }

        public double getWallForce( double appliedForce, double frictionForce ) {
            return 0.0;
        }
    }

    public static class Walls extends BoundaryCondition {
        private Block block;

        public Walls( Force1DModel model ) {
            super( model );
            this.block = model.getBlock();
        }

        public double apply() {
            if ( block.getPosition() > 10 ) {
                double mv = Math.abs( block.getMass() * block.getVelocity() );
                block.setPosition( 10 );
                block.setAcceleration( -20 );
                block.setVelocity( 0.0 );
//                System.out.println( "block = " + block );
                model.fireCollisionHappened( mv );
                return -20;
            }
            else if ( block.getPosition() < -10 ) {
                double mv = Math.abs( block.getMass() * block.getVelocity() );
                block.setPosition( -10 );
                block.setAcceleration( 20);
                block.setVelocity( 0.0 );
//                System.out.println( "block = " + block );
                model.fireCollisionHappened( mv );
                return 20;
            }
            else{
                return 0;
            }
        }

        public double getWallForce( double appliedForce, double frictionForce ) {
            boolean right = block.getPosition() >= 10.0 && appliedForce > 0;
            boolean left = block.getPosition() <= -10.0 && appliedForce < 0;
            if ( right || left ) {
                return -( appliedForce + frictionForce );
            }
            else {
                return 0.0;
            }
        }
    }
}
