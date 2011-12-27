// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLResources.Symbols;
import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.beerslawlab.model.Shaker;
import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.SoluteForm;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModel implements Resettable {

    private static final double BEAKER_VOLUME = 1; // L
    public static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0, BEAKER_VOLUME, 0.5 ); // L
    public static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 10, 0.5 ); // moles
    public static final double MAX_EVAPORATION_RATE = 0.25; // L/sec
    public static final double MAX_INPUT_FLOW_RATE = 0.25; // L/sec
    public static final double MAX_OUTPUT_FLOW_RATE = MAX_INPUT_FLOW_RATE; // L/sec
    public static final double DROPPER_FLOW_RATE = 0.05; // L/sec
    public static final double SHAKER_MAX_DISPENSING_RATE = 0.1; // mol/sec

    // validate constants
    static {
        assert ( SOLUTION_VOLUME_RANGE.getMin() >= 0 );
        assert ( SOLUTION_VOLUME_RANGE.getMax() <= BEAKER_VOLUME );
        assert ( SOLUTE_AMOUNT_RANGE.getMin() >= 0 );
    }

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Property<Solute> solute; // the selected solute
    public final Solution solution;
    public final Property<SoluteForm> soluteForm = new Property<SoluteForm>( SoluteForm.SOLID );
    public final Shaker shaker;
    public final Dropper dropper;
    public final Property<Double> evaporationRate; // L/sec
    public final Beaker beaker;
    public final Faucet inputFaucet, outputFaucet;

    public ConcentrationModel( IClock clock ) {

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getWallTimeChange() / 1000d );
            }
        } );

        // Solutes, in rainbow (ROYBIV) order. Values are specified in the design document.
        this.solutes = new ArrayList<Solute>() {{
            final int precipitateParticlesPerMole = 200;
            add( new Solute( Strings.KOOL_AID, Symbols.KOOL_AID, 342.296, 5.96, 5.50, Color.RED, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COBALT_II_NITRATE, Symbols.COBALT_II_NITRATE, 182.942, 5.64, 5.0, Color.RED, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COBALT_CHLORIDE, Symbols.COBALT_CHLORIDE, 129.839, 4.33, 4.0, new Color( 0xFF6A6A ) /* rose pink */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_DICHROMATE, Symbols.POTASSIUM_DICHROMATE, 294.185, 0.51, 0.50, new Color( 0xFF7F00 ) /* orange */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_CHROMATE, Symbols.POTASSIUM_CHROMATE, 194.191, 3.35, 3.0, Color.YELLOW, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.NICKEL_II_CHLORIDE, Symbols.NICKEL_II_CHLORIDE, 129.599, 5.21, 5.0, new Color( 0x008000 ) /* green */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COPPER_SULFATE, Symbols.COPPER_SULFATE, 159.609, 1.38, 1.0, new Color( 0x1E90FF ) /* blue */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_PERMANGANATE, Symbols.POTASSIUM_PERMANGANATE, 158.034, 0.48, 0.4, new Color( 0x8B008B ) /* purple */, Color.BLACK, 5, precipitateParticlesPerMole ) );
        }};

        this.solute = new Property<Solute>( solutes.get( 0 ) );
        this.solution = new Solution( solute, SOLUTE_AMOUNT_RANGE.getDefault(), SOLUTION_VOLUME_RANGE.getDefault() );
        this.shaker = new Shaker( new ImmutableVector2D( 250, 20 ), new PBounds( 10, 10, 400, 200 ), solute, SHAKER_MAX_DISPENSING_RATE );
        this.dropper = new Dropper( new ImmutableVector2D( 375, 210 ), new PBounds( 230, 205, 250, 50 ), solute, DROPPER_FLOW_RATE );
        this.evaporationRate = new Property<Double>( 0d );
        this.beaker = new Beaker( new Point2D.Double( 400, 550 ), new PDimension( 600, 300 ), SOLUTION_VOLUME_RANGE.getMax() );
        this.inputFaucet = new Faucet( new Point2D.Double( 50, 30 ), 1000, MAX_INPUT_FLOW_RATE ); //TODO derive location and pipe length
        this.outputFaucet = new Faucet( new Point2D.Double( 723, 458 ), 20, MAX_OUTPUT_FLOW_RATE ); //TODO derive location and pipe length

        // Enable faucets based on amount of solution in the beaker.
        solution.volume.addObserver( new VoidFunction1<Double>() {
            public void apply( Double volume ) {
                inputFaucet.enabled.set( volume < SOLUTION_VOLUME_RANGE.getMax() );
                outputFaucet.enabled.set( volume > SOLUTION_VOLUME_RANGE.getMin() );
                dropper.enabled.set( volume < SOLUTION_VOLUME_RANGE.getMax() );
            }
        } );

        // Enable shaker and dropper based on amount of solute in the beaker
        solution.soluteAmount.addObserver( new VoidFunction1<Double>() {
            public void apply( Double soluteAmount ) {
                shaker.enabled.set( soluteAmount < SOLUTE_AMOUNT_RANGE.getMax() );
                dropper.enabled.set( soluteAmount < SOLUTE_AMOUNT_RANGE.getMax() );
            }
        } );
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    public void reset() {
        solution.reset();
        soluteForm.reset();
        shaker.reset();
        dropper.reset();
        evaporationRate.reset();
        inputFaucet.reset();
        outputFaucet.reset();
    }

    /*
     * Moves time forward by the specified amount.
     * @param deltaSeconds clock time change, in seconds.
     */
    private void stepInTime( double deltaSeconds ) {
        addSolventFromInputFaucet( deltaSeconds );
        drainSolutionFromOutputFaucet( deltaSeconds );
        addSoluteFromShaker( deltaSeconds );
        addStockSolutionFromDropper( deltaSeconds );
        evaporateSolvent( deltaSeconds );
    }

    // Add solvent from the input faucet
    private void addSolventFromInputFaucet( double deltaSeconds ) {
        addSolvent( inputFaucet.flowRate.get() * deltaSeconds );
    }

    // Drain solution from the output faucet
    private void drainSolutionFromOutputFaucet( double deltaSeconds ) {
        double drainVolume = outputFaucet.flowRate.get() * deltaSeconds;
        if ( drainVolume > 0 ) {
            double volumeDrained = removeSolute( solution.getConcentration() * drainVolume );
            removeSolvent( volumeDrained );
        }
    }

    // Add solute from the shaker
    private void addSoluteFromShaker( double deltaSeconds ) {
        addSolute( shaker.getDispensingRate() * deltaSeconds );
    }

    // Add stock solution from dropper
    private void addStockSolutionFromDropper( double deltaSeconds ) {
        double dropperVolume = dropper.getFlowRate() * deltaSeconds;
        if ( dropperVolume > 0 ) {
            double volumeAdded = addSolute( solution.solute.get().stockSolutionConcentration * dropperVolume );
            addSolvent( volumeAdded );
        }
    }

    // Evaporate solvent
    private void evaporateSolvent( double deltaSeconds ) {
        removeSolvent( evaporationRate.get() * deltaSeconds );
    }

    // Adds solvent to the solution. Returns the amount actually added.
    private double addSolvent( double deltaVolume ) {
        if ( deltaVolume > 0 ) {
            double volumeBefore = solution.volume.get();
            solution.volume.set( Math.min( SOLUTION_VOLUME_RANGE.getMax(), solution.volume.get() + deltaVolume ) );
            return solution.volume.get() - volumeBefore;
        }
        else {
            return 0;
        }
    }

    // Removes solvent from the solution. Returns the amount actually removed.
    private double removeSolvent( double deltaVolume ) {
        if ( deltaVolume > 0 ) {
            double volumeBefore = solution.volume.get();
            solution.volume.set( Math.max( SOLUTION_VOLUME_RANGE.getMin(), solution.volume.get() - deltaVolume ) );
            return volumeBefore - solution.volume.get();
        }
        else {
            return 0;
        }
    }

    // Adds solvent to the solution. Returns the amount actually added.
    private double addSolute( double deltaAmount ) {
        if ( deltaAmount > 0 ) {
            double amountBefore = solution.soluteAmount.get();
            solution.soluteAmount.set( Math.min( SOLUTE_AMOUNT_RANGE.getMax(), solution.soluteAmount.get() + deltaAmount ) );
            return solution.soluteAmount.get() - amountBefore;
        }
        else {
            return 0;
        }
    }

    // Removes solvent from the solution. Returns the amount actually removed.
    private double removeSolute( double deltaAmount ) {
        if ( deltaAmount > 0 ) {
            double amountBefore = solution.soluteAmount.get();
            solution.soluteAmount.set( Math.max( SOLUTE_AMOUNT_RANGE.getMin(), solution.soluteAmount.get() - deltaAmount ) );
            return amountBefore - solution.soluteAmount.get();
        }
        else {
            return 0;
        }
    }
}
