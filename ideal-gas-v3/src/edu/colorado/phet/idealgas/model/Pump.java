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
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

public class Pump extends SimpleObservable implements GasSource {


    // Coordinates of the intake port on the box
    public static final float s_intakePortX = 430 + IdealGasConfig.X_BASE_OFFSET;
    public static final float s_intakePortY = 400 + IdealGasConfig.Y_BASE_OFFSET;
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
    private PumpingEnergyStrategy pumpingEnergyStrategy;

    /**
     * @param module
     * @param box
     * @param pumpingEnergyStrategy
     */
    public Pump( Module module, Box2D box, PumpingEnergyStrategy pumpingEnergyStrategy ) {
        this.pumpingEnergyStrategy = pumpingEnergyStrategy;
        if( box == null ) {
            throw new RuntimeException( "box cannot be null" );
        }
        this.module = module;
        this.model = (IdealGasModel)module.getModel();
        this.box = box;
    }

    /**
     * Creates a specified number of gas moleculese of the pump's current
     * species
     *
     * @param numMolecules
     */
    public void pump( int numMolecules ) {
        pump( numMolecules, currentGasSpecies );
    }

    /**
     * Creates a specified number of gas molecules of a specified species
     *
     * @param numMolecules
     * @param species
     */
    public void pump( int numMolecules, Class species ) {
        for( int i = 0; i < numMolecules; i++ ) {
            Object obj = this.pumpGasMolecule( species );
        }
        MoleculeEvent event = new MoleculeEvent( this, species, numMolecules );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.moleculesAdded( event );
        }
        return;
    }

    /**
     * Creates a specified number of gas molecules of a specified species, and places
     * them all at a specified location
     *
     * @param numMolecules
     * @param species
     * @param location
     */
    public void pump( int numMolecules, Class species, Point2D location ) {
        for( int i = 0; i < numMolecules; i++ ) {
            GasMolecule molecule = this.pumpGasMolecule( species );
            molecule.setPosition( location );
        }
        MoleculeEvent event = new MoleculeEvent( this, species, numMolecules );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.moleculesAdded( event );
        }
        return;
    }

    /**
     * Creates a gas molecule of the pump's current species
     */
    protected GasMolecule pumpGasMolecule() {
        return pumpGasMolecule( this.currentGasSpecies );
    }

    /**
     * Creates a gas molecule of a specified species
     *
     * @param species
     * @return
     */
    protected GasMolecule pumpGasMolecule( Class species ) {
        // Give the new molecule the same energy as the average of all molecules in the system.
        // If there aren't any other molecules in the system, use the default energy
        double moleculeEnergy = pumpingEnergyStrategy.getMoleculeEnergy();
        GasMolecule newMolecule = createMolecule( species, moleculeEnergy );
        new PumpMoleculeCmd( model, newMolecule, module ).doIt();

        // Constrain the molecule to be inside the box
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule, model );
        newMolecule.addConstraint( constraintSpec );
        box.addContainedBody( newMolecule );

        notifyObservers();
        return newMolecule;
    }

    public void setCurrentGasSpecies( Class currentGasSpecies ) {
        this.currentGasSpecies = currentGasSpecies;
    }

    public Class getCurrentGasSpecies() {
        return currentGasSpecies;
    }

    public void setPumpingEnergyStrategy( PumpingEnergyStrategy pumpingEnergyStrategy ) {
        this.pumpingEnergyStrategy = pumpingEnergyStrategy;
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
        double vSq = 2 * ( initialEnergy ) / newMolecule.getMass();
//        double vSq = 2 * ( initialEnergy - pe ) / newMolecule.getMass();
        if( vSq <= 0 ) {
            System.out.println( "vSq <= 0 in PumpMoleculeCmd.createMolecule" );
        }
        float v = vSq > 0 ? (float)Math.sqrt( vSq ) : 10;

        // Calibrate nm/sec to pixels/clock-tick
//        double factor = 10 / .22;  // 10px = .22nm
//        v *= factor;

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

    //-------------------------------------------------------------------------------------
    // Inner classes
    //-------------------------------------------------------------------------------------
    private ArrayList listeners = new ArrayList();

    public void addListener( Pump.Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Pump.Listener listener ) {
        listeners.remove( listener );
    }

    public interface Listener extends EventListener {
        void moleculesAdded( MoleculeEvent event );
    }

    public class MoleculeEvent extends EventObject {
        private Class species;
        private int numMolecules;

        public MoleculeEvent( Object source, Class species, int numMolecules ) {
            super( source );
            this.species = species;
            this.numMolecules = numMolecules;
        }

        public Class getSpecies() {
            return species;
        }

        public int getNumMolecules() {
            return numMolecules;
        }
    }

    //------------------------------------------------------------------------------
    // Pumping energy strategies
    //------------------------------------------------------------------------------

    public interface PumpingEnergyStrategy {
        double getMoleculeEnergy();
    }

    public static class ConstantEnergyStrategy implements PumpingEnergyStrategy {
        private IdealGasModel model;

        public ConstantEnergyStrategy( IdealGasModel model ) {
            this.model = model;
        }

        public double getMoleculeEnergy() {
            double energy = 0;
            if( model.getNumMolecules() > 0 ) {
                energy = model.getAverageEnergy();
            }
            else {
                energy = IdealGasModel.DEFAULT_ENERGY;
            }
            return energy;
        }
    }

    public static class FixedEnergyStrategy implements PumpingEnergyStrategy {
        private double fixedEnergy = IdealGasModel.DEFAULT_ENERGY;

        public FixedEnergyStrategy() {
        }

        public FixedEnergyStrategy( double fixedEnergy ) {
            this.fixedEnergy = fixedEnergy;
        }

        public double getMoleculeEnergy() {
            return fixedEnergy;
        }
    }
}