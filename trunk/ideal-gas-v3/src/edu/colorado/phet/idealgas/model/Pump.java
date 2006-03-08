/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.GasSource;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Pump
 * <p/>
 * A source of gas molecules that has an EnergyStrategy. The EnergyStrategy determines
 * what the kinetic energy of new gas molecules produced by the pump will be.
 * <p/>
 * EnergyStrategy is an inner interface, with a couple of implementations, also inner
 * to the Pump class.
 */
public class Pump extends SimpleObservable implements GasSource {


    // Coordinates of the intake port on the box
    public static final float s_intakePortX = 430 + IdealGasConfig.X_BASE_OFFSET;
    public static final float s_intakePortY = 405 + IdealGasConfig.Y_BASE_OFFSET;
    // Offset for dithering the initial position of particles pumped into the box
    private static float s_intakePortOffsetY = 1;

    protected static final float PI_OVER_2 = (float)Math.PI / 2;
    protected static final float PI_OVER_4 = (float)Math.PI / 4;
    protected static final float MAX_V = -30;

    private IdealGasModel model;
    private PhetGraphicsModule module;
    private Class currentGasSpecies = HeavySpecies.class;
    private PumpingEnergyStrategy pumpingEnergyStrategy;

    // The box to which the pump is attached
    private Box2D box;

    // The minimum and maximum angles for the velocity of particles produced by the pump
    private double minTheta = Math.PI * 3 / 4;;
    private double maxTheta = Math.PI * 5 / 4;

    /**
     * @param module
     * @param box
     * @param pumpingEnergyStrategy
     */
    public Pump( PhetGraphicsModule module, Box2D box, PumpingEnergyStrategy pumpingEnergyStrategy ) {
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
            GasMolecule molecule = this.pumpGasMolecule( species );
            for( int j = 0; j < listeners.size(); j++ ) {
                Listener listener = (Listener)listeners.get( j );
                listener.moleculeAdded( molecule );
            }
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
            for( int j = 0; j < listeners.size(); j++ ) {
                Listener listener = (Listener)listeners.get( j );
                listener.moleculeAdded( molecule );
            }
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

        notifyObservers();
        return newMolecule;
    }

    /**
     * Sets the default species of gas that will be produced by the pump
     *
     * @param currentGasSpecies
     */
    public void setCurrentGasSpecies( Class currentGasSpecies ) {
        this.currentGasSpecies = currentGasSpecies;
    }

    public Class getCurrentGasSpecies() {
        return currentGasSpecies;
    }

    /**
     * Set the strategy that will be used to set the energy of particles produced by the pump
     *
     * @param pumpingEnergyStrategy
     */
    public void setPumpingEnergyStrategy( PumpingEnergyStrategy pumpingEnergyStrategy ) {
        this.pumpingEnergyStrategy = pumpingEnergyStrategy;
    }

    /**
     * Sets the minimum and maximum angles for the velocity of particles produced by the pump
     *
     * @param minTheta
     * @param maxTheta
     */
    public void setDispersionAngle( double minTheta, double maxTheta ) {
        this.minTheta = minTheta;
        this.maxTheta = maxTheta;
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

        double vSq = 2 * ( initialEnergy ) / newMolecule.getMass();
        if( vSq <= 0 ) {
            System.out.println( "vSq <= 0 in PumpMoleculeCmd.createMolecule" );
        }
        float v = vSq > 0 ? (float)Math.sqrt( vSq ) : 10;
        double theta = Math.random() * ( maxTheta - minTheta ) + minTheta;

        float xV = v * (float)Math.cos( theta );
        float yV = v * (float)Math.sin( theta );
        // Set the velocity twice, so the previous velocity is set to be the same
        newMolecule.setVelocity( xV, yV );
        newMolecule.setVelocity( xV, yV );

        return newMolecule;
    }

    //-------------------------------------------------------------------------------------
    // Event and Listener definitions
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

        void moleculeAdded( GasMolecule molecule );
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

    /**
     * Defines an interface for strategies that determine the energy at which molecules should be introduced
     * into the system
     */
    public interface PumpingEnergyStrategy {
        double getMoleculeEnergy();
    }

    /**
     * A strategy that pumps in molecules at the average kinetic energy of the molecules already in the system
     */
    public static class ConstantEnergyStrategy implements PumpingEnergyStrategy {
        private IdealGasModel model;

        public ConstantEnergyStrategy( IdealGasModel model ) {
            this.model = model;
        }

        public double getMoleculeEnergy() {
            double energy = 0;
            if( model.getNumMolecules() > 0 ) {
                energy = model.getAverageGasEnergy();
            }
            else {
                energy = IdealGasModel.DEFAULT_ENERGY;
            }
            return energy;
        }
    }

    /**
     * A strategy that pumps in molecules at a specified energy
     */
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