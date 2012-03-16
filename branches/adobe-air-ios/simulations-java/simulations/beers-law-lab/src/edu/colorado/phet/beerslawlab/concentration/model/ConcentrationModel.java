// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.concentration.model.Solute.CobaltChloride;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.CobaltIINitrate;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.CopperSulfate;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.DrinkMix;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.NickelIIChloride;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.PotassiumChromate;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.PotassiumDichromate;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.PotassiumPermanganate;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.SoluteForm;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModel implements Resettable {

    // ranges and rates
    private static final double BEAKER_VOLUME = 1; // L
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0, BEAKER_VOLUME, 0.5 ); // L
    private static final DoubleRange SOLUTE_AMOUNT = new DoubleRange( 0, 6, 0 ); // moles
    private static final double DEFAULT_SOLUTE_AMOUNT = 0; // moles
    private static final double MAX_EVAPORATION_RATE = 0.25; // L/sec
    private static final double MAX_INPUT_FLOW_RATE = 0.25; // L/sec
    private static final double MAX_OUTPUT_FLOW_RATE = MAX_INPUT_FLOW_RATE; // L/sec
    private static final double DROPPER_FLOW_RATE = 0.05; // L/sec
    private static final double SHAKER_MAX_DISPENSING_RATE = 0.2; // mol/sec

    // validate constants
    static {
        assert ( SOLUTION_VOLUME_RANGE.getMin() >= 0 );
        assert ( SOLUTION_VOLUME_RANGE.getMax() <= BEAKER_VOLUME );
    }

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Property<Solute> solute; // the selected solute
    public final ConcentrationSolution solution;
    public final Property<SoluteForm> soluteForm = new Property<SoluteForm>( SoluteForm.SOLID );
    public final Shaker shaker;
    public final ShakerParticles shakerParticles;
    public final Dropper dropper;
    public final Evaporator evaporator;
    public final Beaker beaker;
    public final Precipitate precipitate;
    public final Faucet solventFaucet, drainFaucet;
    public final ConcentrationMeter concentrationMeter;

    public ConcentrationModel( IClock clock ) {

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getWallTimeChange() / 1000d );
            }
        } );

        // Solutes, in rainbow (ROYGBIV) order.
        this.solutes = new ArrayList<Solute>() {{
            add( new DrinkMix() );
            add( new CobaltIINitrate() );
            add( new CobaltChloride() );
            add( new PotassiumDichromate() );
            add( new PotassiumChromate() );
            add( new NickelIIChloride() );
            add( new CopperSulfate() );
            add( new PotassiumPermanganate() );
        }};

        this.solute = new Property<Solute>( solutes.get( 0 ) );
        this.solution = new ConcentrationSolution( solute, DEFAULT_SOLUTE_AMOUNT, SOLUTION_VOLUME_RANGE.getDefault() );
        this.beaker = new Beaker( new ImmutableVector2D( 400, 550 ), new PDimension( 600, 300 ), SOLUTION_VOLUME_RANGE.getMax() );
        this.precipitate = new Precipitate( solution, beaker );
        this.shaker = new Shaker( new ImmutableVector2D( 340, 170 ), 0.75 * Math.PI, new PBounds( 225, 50, 400, 160 ), solute, SHAKER_MAX_DISPENSING_RATE );
        this.shakerParticles = new ShakerParticles( shaker, solution, beaker );
        this.dropper = new Dropper( new ImmutableVector2D( 375, 210 ), new PBounds( 230, 205, 400, 30 ), solute, DROPPER_FLOW_RATE );
        this.evaporator = new Evaporator( MAX_EVAPORATION_RATE, solution );
        this.solventFaucet = new Faucet( new ImmutableVector2D( 150, 190 ), 1000, MAX_INPUT_FLOW_RATE );
        this.drainFaucet = new Faucet( new ImmutableVector2D( 825, 618 ), 20, MAX_OUTPUT_FLOW_RATE );
        //  meter drag bounds chosen so that meter and probe can't go behind control panel */
        this.concentrationMeter = new ConcentrationMeter( new ImmutableVector2D( 785, 210 ), new PBounds( 10, 150, 825, 530 ),
                                                          new ImmutableVector2D( 775, 390 ), new PBounds( 30, 150, 935, 530 ) );

        // Things to do when the solute is changed.
        solute.addObserver( new SimpleObserver() {
            public void update() {
                solution.soluteAmount.set( 0d );
            }
        } );

        // Show the correct dispenser for the solute form
        soluteForm.addObserver( new VoidFunction1<SoluteForm>() {
            public void apply( SoluteForm form ) {
                shaker.visible.set( form == SoluteForm.SOLID );
                dropper.visible.set( form == SoluteForm.STOCK_SOLUTION );
            }
        } );

        // Enable faucets and dropper based on amount of solution in the beaker.
        solution.volume.addObserver( new VoidFunction1<Double>() {
            public void apply( Double volume ) {
                solventFaucet.enabled.set( volume < SOLUTION_VOLUME_RANGE.getMax() );
                drainFaucet.enabled.set( volume > SOLUTION_VOLUME_RANGE.getMin() );
                dropper.enabled.set( !dropper.empty.get() && ( volume < SOLUTION_VOLUME_RANGE.getMax() ) );
            }
        } );

        // Empty shaker and dropper when max solute is reached.
        solution.soluteAmount.addObserver( new VoidFunction1<Double>() {
            public void apply( Double soluteAmount ) {
                final boolean containsMaxSolute = ( soluteAmount >= SOLUTE_AMOUNT.getMax() );
                shaker.empty.set( containsMaxSolute );
                dropper.empty.set( containsMaxSolute );
                dropper.enabled.set( !dropper.empty.get() && !containsMaxSolute && solution.volume.get() < SOLUTION_VOLUME_RANGE.getMax() );
            }
        } );
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    public void reset() {
        solute.reset();
        solution.reset();
        soluteForm.reset();
        shaker.reset();
        dropper.reset();
        evaporator.reset();
        solventFaucet.reset();
        drainFaucet.reset();
        concentrationMeter.reset();
    }

    /*
     * Moves time forward by the specified amount.
     * @param deltaSeconds clock time change, in seconds.
     */
    private void stepInTime( double deltaSeconds ) {
        addSolventFromInputFaucet( deltaSeconds );
        drainSolutionFromOutputFaucet( deltaSeconds );
        addStockSolutionFromDropper( deltaSeconds );
        evaporateSolvent( deltaSeconds );
        propagateShakerParticles( deltaSeconds );
        createShakerParticles();
    }

    // Add solvent from the input faucet
    private void addSolventFromInputFaucet( double deltaSeconds ) {
        addSolvent( solventFaucet.flowRate.get() * deltaSeconds );
    }

    // Drain solution from the output faucet
    private void drainSolutionFromOutputFaucet( double deltaSeconds ) {
        double drainVolume = drainFaucet.flowRate.get() * deltaSeconds;
        if ( drainVolume > 0 ) {
            double concentration = solution.concentration.get(); // get concentration before changing volume
            double volumeRemoved = removeSolvent( drainVolume );
            removeSolute( concentration * volumeRemoved );
        }
    }

    // Propagate solid solute that came out of the shaker
    private void propagateShakerParticles( double deltaSeconds ) {
        shakerParticles.stepInTime( deltaSeconds );
    }

    // Create new solute particles when the shaker is shaken.
    private void createShakerParticles() {
        shaker.stepInTime();
    }

    // Add stock solution from dropper
    private void addStockSolutionFromDropper( double deltaSeconds ) {
        double dropperVolume = dropper.flowRate.get() * deltaSeconds;
        if ( dropperVolume > 0 ) {
            double volumeAdded = addSolvent( dropperVolume );
            addSolute( solution.solute.get().stockSolutionConcentration * volumeAdded );
        }
    }

    // Evaporate solvent
    private void evaporateSolvent( double deltaSeconds ) {
        removeSolvent( evaporator.evaporationRate.get() * deltaSeconds );
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
            solution.soluteAmount.set( Math.min( SOLUTE_AMOUNT.getMax(), solution.soluteAmount.get() + deltaAmount ) );
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
            solution.soluteAmount.set( Math.max( SOLUTE_AMOUNT.getMin(), solution.soluteAmount.get() - deltaAmount ) );
            return amountBefore - solution.soluteAmount.get();
        }
        else {
            return 0;
        }
    }
}
