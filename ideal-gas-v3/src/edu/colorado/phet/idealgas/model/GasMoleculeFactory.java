/**
 * Class: GasMoleculeFactory
 * Package: edu.colorado.phet.idealgas.model
 * Author: Another Guy
 * Date: Sep 23, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;


public class GasMoleculeFactory {

    protected static final float PI_OVER_2 = (float)Math.PI / 2;
    protected static final float PI_OVER_4 = (float)Math.PI / 4;
    protected static final float MAX_V = -30;
    protected static final float DEFAULT_ENERGY = 15000;

        private IdealGasModel model;
        protected Class speciesClass;
        private double initialEnergy;

//        public GasMolecule create( IdealGasModel model, double initialEnergy ) {
//            this.initialEnergy = ( initialEnergy == 0 ? DEFAULT_ENERGY : initialEnergy );
//            return create( model, currentGasSpecies );
//        }
//
//        public GasMolecule create( IdealGasModel model ) {
//            this.initialEnergy = DEFAULT_ENERGY;
//            return create( model, currentGasSpecies );
//        }

        public GasMolecule create( IdealGasModel model,
                                   Class speciesClass ) {
            this.model = model;
            this.speciesClass = speciesClass;
//            this.initialEnergy = DEFAULT_ENERGY;
            return pumpGasMolecule();
        }

        /**
         *
         */
        protected GasMolecule pumpGasMolecule() {

            GasMolecule newMolecule = null;

            // Compute the average energy of the gas in the box. It will be used to compute the
            // velocity of the new molecule
            if( speciesClass == LightSpecies.class ) {

                // Create the new molecule with no velocity. We will compute and assign it next
                newMolecule = new LightSpecies( new Point2D.Double(),
                                                new Vector2D.Double( 0, 0 ),
                                                new Vector2D.Double( 0, 0 ),
                                                5.0f );
            }
            else if( speciesClass == HeavySpecies.class ) {

                // Create the new molecule with no velocity. We will compute and assign it next
                newMolecule = new HeavySpecies( new Point2D.Double(),
                                                new Vector2D.Double( 0, 0 ),
                                                new Vector2D.Double( 0, 0 ),
                                                5.0 );
            }
            else {
                throw new RuntimeException( "No gas species set in application" );
            }

            double pe = model.getPotentialEnergy( newMolecule );
//            double pe = model.getBodyEnergy( newMolecule );
            //        double pe = physicalSystem.getBodyEnergy( newMolecule );
            double vSq = 2 * ( this.initialEnergy - pe ) / newMolecule.getMass();
            if( vSq <= 0 ) {
                System.out.println( "vSq <= 0 in PumpMoleculeCmd.pumpGasMolecule" );
            }
            float v = vSq > 0 ? (float)Math.sqrt( vSq ) : 10;
            float theta = (float)Math.random() * PI_OVER_2 - PI_OVER_4;

            // xV must be negative so that molecules move away from the intake port
            // Set the velocity twice, so the previous velocity is set to be
            // the same
            float xV = -(float)Math.abs( v * Math.cos( theta ) );
            float yV = v * (float)Math.sin( theta );
            newMolecule.setVelocity( xV, yV );
            newMolecule.setVelocity( xV, yV );

            return newMolecule;
        }
    }
