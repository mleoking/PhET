// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLResources.Symbols;
import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.ConcentrationMeter;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.beerslawlab.model.Evaporator;
import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.beerslawlab.model.Precipitate;
import edu.colorado.phet.beerslawlab.model.Shaker;
import edu.colorado.phet.beerslawlab.model.ShakerParticles;
import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.Solute.SoluteForm;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
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
    //TODO consider setting max SOLUTE_AMOUNT dynamically, based on the amount required to saturate 1L of the current solute
    private static final DoubleRange SOLUTE_AMOUNT = new DoubleRange( 0, 6, 0 ); // moles
    private static final double DEFAULT_SOLUTE_AMOUNT = 0; // moles
    private static final double MAX_EVAPORATION_RATE = 0.25; // L/sec
    private static final double MAX_INPUT_FLOW_RATE = 0.25; // L/sec
    private static final double MAX_OUTPUT_FLOW_RATE = MAX_INPUT_FLOW_RATE; // L/sec
    private static final double DROPPER_FLOW_RATE = 0.05; // L/sec
    private static final double SHAKER_MAX_DISPENSING_RATE = 0.1; // mol/sec

    // validate constants
    static {
        assert ( SOLUTION_VOLUME_RANGE.getMin() >= 0 );
        assert ( SOLUTION_VOLUME_RANGE.getMax() <= BEAKER_VOLUME );
    }

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Property<Solute> solute; // the selected solute
    public final Solution solution;
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

        // Solutes, in rainbow (ROYBIV) order. Values are specified in the design document.
        this.solutes = new ArrayList<Solute>() {{
            final int precipitateParticlesPerMole = 200;
            add( new Solute( Strings.KOOL_AID, Symbols.KOOL_AID, 342.296, 5.96, 5.50, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COBALT_II_NITRATE, Symbols.COBALT_II_NITRATE, 182.942, 5.64, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COBALT_CHLORIDE, Symbols.COBALT_CHLORIDE, 129.839, 4.33, 4.0, new ColorRange( new Color( 255, 242, 242 ), new Color( 0xFF6A6A ) /* rose pink */ ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_DICHROMATE, Symbols.POTASSIUM_DICHROMATE, 294.185, 0.51, 0.50, new ColorRange( new Color( 255, 232, 210 ), new Color( 0xFF7F00 ) /* orange */ ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_CHROMATE, Symbols.POTASSIUM_CHROMATE, 194.191, 3.35, 3.0, new ColorRange( new Color( 255, 255, 199 ), Color.YELLOW ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.NICKEL_II_CHLORIDE, Symbols.NICKEL_II_CHLORIDE, 129.599, 5.21, 5.0, new ColorRange( new Color( 234, 244, 234 ), new Color( 0x008000 ) /* green */ ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COPPER_SULFATE, Symbols.COPPER_SULFATE, 159.609, 1.38, 1.0, new ColorRange( new Color( 222, 238, 255 ), new Color( 0x1E90FF ) /* blue */ ), 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_PERMANGANATE, Symbols.POTASSIUM_PERMANGANATE, 158.034, 0.48, 0.4, new ColorRange( new Color( 255, 0, 255 ), new Color( 0x8B008B ) /* purple */ ), Color.BLACK, 5, precipitateParticlesPerMole ) );
        }};

        this.solute = new Property<Solute>( solutes.get( 0 ) );
        this.solution = new Solution( solute, DEFAULT_SOLUTE_AMOUNT, SOLUTION_VOLUME_RANGE.getDefault() );
        this.shaker = new Shaker( new ImmutableVector2D( 340, 170 ), 0.75 * Math.PI, new PBounds( 225, 50, 400, 160 ), solute, SHAKER_MAX_DISPENSING_RATE );
        this.shakerParticles = new ShakerParticles( shaker, solution );
        this.dropper = new Dropper( new ImmutableVector2D( 375, 210 ), new PBounds( 230, 205, 400, 30 ), solute, DROPPER_FLOW_RATE );
        this.evaporator = new Evaporator( MAX_EVAPORATION_RATE, solution );
        this.beaker = new Beaker( new ImmutableVector2D( 400, 550 ), new PDimension( 600, 300 ), SOLUTION_VOLUME_RANGE.getMax() );
        this.precipitate = new Precipitate( solution, beaker );
        this.solventFaucet = new Faucet( new ImmutableVector2D( 150, 190 ), 1000, MAX_INPUT_FLOW_RATE );
        this.drainFaucet = new Faucet( new ImmutableVector2D( 825, 618 ), 20, MAX_OUTPUT_FLOW_RATE );
        //  meter drag bounds chosen so that meter and probe can't go behind control panel */
        this.concentrationMeter = new ConcentrationMeter( new ImmutableVector2D( 785, 270 ), new PBounds( 10, 150, 825, 530 ),
                                                          new ImmutableVector2D( 580, 330 ), new PBounds( 30, 150, 935, 605 ) );

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
    }

    // Add solvent from the input faucet
    private void addSolventFromInputFaucet( double deltaSeconds ) {
        addSolvent( solventFaucet.flowRate.get() * deltaSeconds );
    }

    // Drain solution from the output faucet
    private void drainSolutionFromOutputFaucet( double deltaSeconds ) {
        double drainVolume = drainFaucet.flowRate.get() * deltaSeconds;
        if ( drainVolume > 0 ) {
            double concentration = solution.getConcentration(); // get concentration before changing volume
            double volumeRemoved = removeSolvent( drainVolume );
            removeSolute( concentration * volumeRemoved );
        }
    }

    // Propagate solid solute that came out of the shaker
    private void propagateShakerParticles( double deltaSeconds ) {
        shakerParticles.stepInTime( deltaSeconds );
    }

    // Add stock solution from dropper
    private void addStockSolutionFromDropper( double deltaSeconds ) {
        double dropperVolume = dropper.getFlowRate() * deltaSeconds;
        if ( dropperVolume > 0 ) {
            double volumeAdded = addSolvent( dropperVolume );
            //TODO if we don't add the full amount of solute here, then we've added too much solvent.
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
