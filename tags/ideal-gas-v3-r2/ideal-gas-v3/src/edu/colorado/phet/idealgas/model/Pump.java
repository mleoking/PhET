/**
 * Class: Pump
 * Package: edu.colorado.phet.idealgas.physics
 * Author: Another Guy
 * Date: Jun 25, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.GasSource;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;

import java.awt.geom.Point2D;

public class Pump extends SimpleObservable implements GasSource {


    // Coordinates of the intake port on the box
//    protected static final float s_intakePortX = 265 + IdealGasConfig.X_BASE_OFFSET;
//    protected static final float s_intakePortY = 480 + IdealGasConfig.Y_BASE_OFFSET;
    protected static final float s_intakePortX = 430 + IdealGasConfig.X_BASE_OFFSET;
    protected static final float s_intakePortY = 400 + IdealGasConfig.Y_BASE_OFFSET;
    // Offset for dithering the initial position of particles pumped into the box
    private static float s_intakePortOffsetY = 1;

    protected static final float PI_OVER_2 = (float)Math.PI / 2;
    protected static final float PI_OVER_4 = (float)Math.PI / 4;
    protected static final float MAX_V = -30;

    private IdealGasModel model;
    private Class currentGasSpecies = HeavySpecies.class;

    // The box to which the pump is attached
    private Box2D box;
    private Module module;

    public Pump( Module module, Box2D box ) {
        if( box == null ) {
            throw new RuntimeException( "box cannot be null" );
        }
        this.module = module;
        this.model = (IdealGasModel)module.getModel();
        this.box = box;
    }

    public void pump( int numMolecules ) {
        for( int i = 0; i < numMolecules; i++ ) {
            this.pumpGasMolecule();
        }
        return;
    }

    /**
     * Creates a gas molecule of the proper species
     */
    protected GasMolecule pumpGasMolecule() {

        // Add a new gas molecule to the system
        double moleculeEnergy = Math.max( IdealGasModel.DEFAULT_ENERGY, model.getAverageMoleculeEnergy() );
        GasMolecule newMolecule = createMolecule( this.currentGasSpecies,
                                                  moleculeEnergy );
        new PumpMoleculeCmd( model, newMolecule, module ).doIt();

        // Constrain the molecule to be inside the box
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule, model );
        newMolecule.addConstraint( constraintSpec );
        box.addContainedBody( newMolecule );     // added 9/14/04 RJL

        notifyObservers();
        return newMolecule;
    }

    public void setCurrentGasSpecies( Class currentGasSpecies ) {
        this.currentGasSpecies = currentGasSpecies;
    }

    public Class getCurrentGasSpecies() {
        return currentGasSpecies;
    }

    /**
     *
     */
    private GasMolecule createMolecule( Class species, double initialEnergy ) {

        s_intakePortOffsetY *= -1;

        // TODO: Make a gas factory or something. We only have a class to work with right now
        // and we can't call newInstance()
        GasMolecule newMolecule = null;

        // Compute the average energy of the gas in the box. It will be used to compute the
        // velocity of the new molecule. Create the new molecule with no velocity. We will
        // compute and assign it next
        if( species == LightSpecies.class ) {
            newMolecule = new LightSpecies( new Point2D.Double( s_intakePortX, s_intakePortY + s_intakePortOffsetY * 5 ),
                                            new Vector2D.Double( 0, 0 ),
                                            new Vector2D.Double( 0, 0 ) );
        }
        else if( species == HeavySpecies.class ) {
            newMolecule = new HeavySpecies( new Point2D.Double( s_intakePortX, s_intakePortY + s_intakePortOffsetY * 5 ),
                                            new Vector2D.Double( 0, 0 ),
                                            new Vector2D.Double( 0, 0 ) );
        }
        else {
            throw new RuntimeException( "No gas species set in application" );
        }

        double pe = model.getPotentialEnergy( newMolecule );
        //            double pe = model.getBodyEnergy( newMolecule );
        //        double pe = physicalSystem.getBodyEnergy( newMolecule );
        double vSq = 2 * ( initialEnergy - pe ) / newMolecule.getMass();
        if( vSq <= 0 ) {
            System.out.println( "vSq <= 0 in PumpMoleculeCmd.createMolecule" );
        }
        float v = vSq > 0 ? (float)Math.sqrt( vSq ) : 10;
        float theta = (float)Math.random() * PI_OVER_2 - PI_OVER_4;

//        theta += Math.PI / 2;

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