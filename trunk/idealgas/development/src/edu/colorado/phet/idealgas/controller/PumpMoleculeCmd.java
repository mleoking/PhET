/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 10:09:34 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.idealgas.physics.LightSpecies;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.controller.command.Command;

public class PumpMoleculeCmd implements Command {

    protected IdealGasApplication application;
    protected Class speciesClass;

    public PumpMoleculeCmd( IdealGasApplication application ) {
        this.application = application;
        this.speciesClass = application.getCurrentGasSpecies();
    }

    public Object doIt() {
        return pumpGasMolecule();
    }

    /**
     * Creates a gas molecule of the proper species
     */
    protected GasMolecule pumpGasMolecule() {
        return pumpGasMolecule( speciesClass,
                                (IdealGasSystem)application.getPhysicalSystem() );
    }

    /**
     *
     */
    protected GasMolecule pumpGasMolecule( Class species, IdealGasSystem physicalSystem ) {

        s_intakePortOffsetY *= -1;

        // TODO: Make a gas factory or something. We only have a class to work with right now
        // and we can't call newInstance()
        GasMolecule newMolecule = null;

        // Compute the average energy of the gas in the box. It will be used to compute the
        // velocity of the new molecule
//        float totalEnergy = physicalSystem.getTotalEnergy();
//        int numEnergeticBodies = HeavySpecies.getNumMolecules().intValue() +
//                LightSpecies.getNumMolecules().intValue();
//        float aveEnergy = numEnergeticBodies != 0 ? totalEnergy / numEnergeticBodies : 0;
        if( species == LightSpecies.class ) {

            // Create the new molecule with no velocity. We will compute and assign it next
            newMolecule = new LightSpecies(
                    new Vector2D( s_intakePortX, s_intakePortY + s_intakePortOffsetY * 5 ),
                    new Vector2D( 0, 0 ),
                    new Vector2D( 0, 0 ),
                    5.0f, 0.0f );
        }
        else if( species == HeavySpecies.class ) {

            // Create the new molecule with no velocity. We will compute and assign it next
            newMolecule = new HeavySpecies(
                    new Vector2D( s_intakePortX, s_intakePortY + s_intakePortOffsetY * 5 ),
                    new Vector2D( 0, 0 ),
                    new Vector2D( 0, 0 ),
                    5.0f, 0.0f );
        }
        else {
            throw new RuntimeException( "No gas species set in application" );
        }

        double pe = physicalSystem.getBodyEnergy( newMolecule );
        double vSq = 2 * ( DEFAULT_ENERGY - pe ) / newMolecule.getMass();
        if( vSq <= 0 ) {
            System.out.println( "vsvvsvs" );
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

        newMolecule.setPhysicalSystem( physicalSystem );
        application.addBody( newMolecule );

        return newMolecule;
    }

    //
    // Static fields and methods
    //
    protected static final float PI_OVER_2 = (float)Math.PI / 2;
    protected static final float PI_OVER_4 = (float)Math.PI / 4;
    protected static final float MAX_V = -30;

    protected static final float DEFAULT_ENERGY = 10000;
    // Coordinates of the intake port on the box
    protected static final float s_intakePortX = 430 + IdealGasConfig.X_BASE_OFFSET;
    protected static final float s_intakePortY = 400 + IdealGasConfig.Y_BASE_OFFSET;
    // Offset for dithering the initial position of particles pumped into the box
    private static float s_intakePortOffsetY = 1;
}
