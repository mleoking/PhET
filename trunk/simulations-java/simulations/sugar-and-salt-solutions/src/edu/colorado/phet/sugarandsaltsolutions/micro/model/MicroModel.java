// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.IfElse;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ISugarAndSaltModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseLattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseMolecule;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.metersCubedToLiters;
import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.numberToMoles;

/**
 * Model for the micro tab, which uses code from soluble salts sim.
 *
 * @author Sam Reid
 */
public class MicroModel extends SugarAndSaltSolutionModel implements ISugarAndSaltModel {
    //Model for the concentration in SI (moles/m^3)
    //Shadows parent values of sugarConcentration and saltConcentration
    public final DoubleProperty sugarConcentration = new DoubleProperty( 0.0 );
    public final DoubleProperty saltConcentration = new DoubleProperty( 0.0 );

    private DoubleProperty numSaltIons = new DoubleProperty( 0.0 );
    private DoubleProperty numSugarMolecules = new DoubleProperty( 0.0 );

    private static final double framesPerSecond = 30;

    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    private final Property<Integer> stepsOfAddingSugar = new Property<Integer>( 0 );

    //List of all spherical particles
    public final ItemList<SphericalParticle> sphericalParticles = new ItemList<SphericalParticle>();

    //List of all free spherical particles, used to keep track of which particles to move about randomly
    public final ItemList<Particle> freeParticles = new ItemList<Particle>();

    //Lists of compounds
    public final ItemList<SodiumChlorideCrystal> saltCrystals = new ItemList<SodiumChlorideCrystal>();
    public final ItemList<SodiumNitrateCrystal> sodiumNitrateCrystals = new ItemList<SodiumNitrateCrystal>();
    public final ItemList<CalciumChlorideCrystal> calciumChlorideCrystals = new ItemList<CalciumChlorideCrystal>();
    public final ItemList<SucroseCrystal> sugarCrystals = new ItemList<SucroseCrystal>();

    //Randomness for random walks
    private final Random random = new Random();

    //The factor by which to scale particle sizes, so they look a bit smaller in the graphics
    public static final double sizeScale = 0.35;
    public final BooleanProperty showChargeColor = new BooleanProperty( false );

    //settable property that indicates whether the clock is running or paused
    public final Property<Boolean> clockPausedProperty = new Property<Boolean>( false );

    //The index of the kit selected by the user
    public final Property<Integer> selectedKit = new Property<Integer>( 0 ) {{

        //When the user switches kits, clear the solutes
        addObserver( new SimpleObserver() {
            public void update() {
                clearSolutes();
            }
        } );
    }};

    //Observable property that gives the concentration in mol/L for specific dissolved components (such as Na+ or sucrose)
    public class IonConcentration extends CompositeDoubleProperty {
        public IonConcentration( final Class type ) {
            super( new Function0<Double>() {
                public Double apply() {
                    return Units.numberToMoles( freeParticles.count( type ) ) / Units.metersCubedToLiters( waterVolume.get() );
                }
            }, waterVolume );
            VoidFunction1<Particle> listener = new VoidFunction1<Particle>() {
                public void apply( Particle particle ) {
                    notifyIfChanged();
                }
            };
            freeParticles.addItemAddedListener( listener );
            freeParticles.addItemRemovedListener( listener );
        }
    }

    public class IonColor extends IfElse<Color> {
        public IonColor( SphericalParticle particle ) {
            super( showChargeColor, particle.chargeColor, particle.color );
        }
    }

    public final ObservableProperty<Color> sodiumColor = new IonColor( new SodiumIonParticle() );
    public final ObservableProperty<Color> chlorideColor = new IonColor( new ChlorideIonParticle() );

    //Free Na+ disassociated from NaCl
    public final ObservableProperty<Double> sodiumConcentration = new IonConcentration( SodiumIonParticle.class );

    //Free Na+ disassociated from NaCl
    public final ObservableProperty<Double> chlorideConcentration = new IonConcentration( ChlorideIonParticle.class );

    public MicroModel() {
        //SolubleSalts clock runs much faster than wall time
        super( new ConstantDtClock( framesPerSecond ),

               //The volume of the micro beaker should be 2E-23L
               //In the macro tab, the dimension is BeakerDimension( width = 0.2, height = 0.1, depth = 0.1 ), each unit in meters
               //So if it is to have the same shape is as the previous tab then we use
               // width*height*depth = 2E-23
               // and
               // width = 2*height = 2*depth
               //Solving for width, we have:
               // 2E-23 = width * width/2 * width/2
               // =>
               // 8E-23 = width^3.  Therefore
               // width = cuberoot(8E-23)
               new BeakerDimension( Math.pow( 8E-23
                                              //convert L to meters cubed
                                              * 0.001, 1 / 3.0 ) ),

               //Flow rate must be slowed since the beaker is so small.  TODO: compute this factor analytically so that it will match the first tab perfectly?  Factor out numbers?
               0.0005 * 2E-23 / 2,

               //Values sampled at runtime using a debugger using this line in SugarAndSaltSolutionModel.update: System.out.println( "solution.shape.get().getBounds2D().getMaxY() = " + solution.shape.get().getBounds2D().getMaxY() );
               2.5440282964793075E-10, 5.75234062238494E-10,

               //Ratio of length scales in meters
               1.0 / Math.pow( 8E-23 * 0.001, 1 / 3.0 ) / 0.2 );

        //Add models for the various dispensers: sugar, salt, etc.
        dispensers.add( new SodiumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSaltAllowed, getSaltShakerName(), distanceScale, dispenserType, SALT ) );
        dispensers.add( new SugarDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, getSugarDispenserName(), distanceScale, dispenserType, SUGAR ) );
        dispensers.add( new SodiumNitrateShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, SODIUM_NITRATE_NEW_LINE, distanceScale, dispenserType, DispenserType.SODIUM_NITRATE ) );
        dispensers.add( new CalciumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, CALCIUM_CHLORIDE_NEW_LINE, distanceScale, dispenserType, DispenserType.CALCIUM_CHLORIDE ) );

        //When the pause button is pressed, pause the clock
        clockPausedProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean paused ) {
                clock.setPaused( paused );
            }
        } );

        //When the clock pauses or unpauses, update the property
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockPaused( ClockEvent clockEvent ) {
                clockPausedProperty.set( true );
            }

            @Override public void clockStarted( ClockEvent clockEvent ) {
                clockPausedProperty.set( false );
            }
        } );
    }

    //When a macro salt would be shaken out of the shaker, instead add a micro salt crystal
    public void addSaltCrystal( SodiumChlorideCrystal sodiumChlorideCrystal ) {
        //Add the components of the lattice to the model so the graphics will be created
        for ( Constituent constituent : sodiumChlorideCrystal ) {
            //TODO: separate list for NaCl crystals so no cast required here?
            sphericalParticles.add( (SphericalParticle) constituent.particle );
        }
        saltCrystals.add( sodiumChlorideCrystal );
    }

    public void addSodiumNitrateCrystal( SodiumNitrateCrystal crystal ) {
        addCrystalComponents( crystal );
        sodiumNitrateCrystals.add( crystal );
    }

    //Add the components of the lattice to the model so the graphics will be created
    private void addCrystalComponents( Crystal crystal ) {
        for ( Constituent constituent : crystal ) {
            sphericalParticles.add( (SphericalParticle) constituent.particle );
        }
    }

    public void addCalciumChlorideCrystal( CalciumChlorideCrystal calciumChlorideCrystal ) {
        addCrystalComponents( calciumChlorideCrystal );
        calciumChlorideCrystals.add( calciumChlorideCrystal );
    }

    //When a macro sugar would be shaken out of the shaker, instead add a micro sugar crystal
    //TODO: Duplicated code with addMacroSalt
    @Override public void addMacroSugar( MacroSugar sugar ) {

        //Only add a crystal every N steps, otherwise there are too many
        stepsOfAddingSugar.set( stepsOfAddingSugar.get() + 1 );
        if ( stepsOfAddingSugar.get() % 30 == 0 ) {
            //TODO: Make getPosition abstract so the particle can query the lattice?

            //Create a random crystal
            //TODO: get rid of cast here
            final SucroseCrystal crystal = new SucroseCrystal( sugar.position.get(), (SucroseLattice) new SucroseLattice().grow( 3 ) );
            addCrystalComponents( crystal );
            sugarCrystals.add( crystal );
        }
    }

    //When the simulation clock ticks, move the particles
    @Override protected void updateModel( double dt ) {
        super.updateModel( dt );
        updateParticles( dt, freeParticles );

        updateDissolvableCrystals( dt, saltCrystals,
                                   //Determine whether the concentration is low enough to allow it to dissolve
                                   getMolesPerLiterSodiumInNaCl() < 6.14 );
        updateDissolvableCrystals( dt, sodiumNitrateCrystals );
        updateDissolvableCrystals( dt, calciumChlorideCrystals );
        updateDissolvableCrystals( dt, sugarCrystals );
//        System.out.println( "numSodiumsForNaCl = " + numSodiumsForNaCl );
    }

    //Compute the concentration of sodium that could contribute to making NaCl for purposes of determining saturation
    private double getMolesPerLiterSodiumInNaCl() {
        //Count the number of sodiums
        double molesSodium = numberToMoles( freeParticles.count( SodiumIonParticle.class ) );
        double litersWater = metersCubedToLiters( waterVolume.get() );
        return molesSodium / litersWater;
    }

    //Update the crystals by moving them about and possibly dissolving them
    private void updateDissolvableCrystals( double dt, ItemList<? extends Crystal> crystals ) {
        //No saturation
        updateDissolvableCrystals( dt, crystals, true );
    }

    //Update the crystals by moving them about and possibly dissolving them
    private void updateDissolvableCrystals( double dt, ItemList<? extends Crystal> crystals, boolean belowSaturationPoint ) {
        //Keep track of which lattices should dissolve in this time step
        ArrayList<Crystal> toDissolve = new ArrayList<Crystal>();
        for ( Crystal lattice : crystals ) {
            //Accelerate the particle due to gravity and perform an euler integration step
            //This number was obtained by guessing and checking to find a value that looked good for accelerating the particles out of the shaker
            double mass = 1E-10;

            //Cache the value to improve performance by 30% when number of particles is large
            final boolean anyPartUnderwater = isAnyPartUnderwater( lattice );

            //If any part touched the water, the lattice should slow down and move at a constant speed
            if ( anyPartUnderwater ) {
                lattice.velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 ) );
            }

            //If completely underwater, lattice should prepare to dissolve
            if ( isCompletelyUnderwater( lattice ) && !lattice.isUnderwater() ) {
                lattice.setUnderwater( time );
            }
            lattice.stepInTime( getExternalForce( anyPartUnderwater ).times( mass ), dt );

            //Collide with the bottom of the beaker
            double minY = lattice.getShape().getBounds2D().getMinY();
            if ( minY < 0 ) {
                lattice.translate( 0, -minY );
            }

            //Determine whether it is time for the lattice to dissolve
            if ( lattice.isUnderwater() ) {
                final double timeUnderwater = time - lattice.getUnderWaterTime();
                if ( timeUnderwater > 1 && belowSaturationPoint ) {
                    toDissolve.add( lattice );
                }
            }
        }

        //Handle dissolving the lattices
        for ( Crystal crystal : toDissolve ) {
            dissolve( crystals, crystal );
        }
    }

    //Get the external force acting on the particle, gravity if the particle is in free fall or zero otherwise (e.g., in solution)
    private ImmutableVector2D getExternalForce( final boolean anyPartUnderwater ) {
        return new ImmutableVector2D( 0, anyPartUnderwater ? 0 : -9.8 );
    }

    //Dissolve the lattice
    private void dissolve( ItemList<? extends Crystal> crystals, final Crystal crystal ) {
        //Todo: Move this differing behavior to callbacks on the crystal
        if ( crystal instanceof SodiumNitrateCrystal ) {
            SodiumNitrateCrystal sodiumNitrateCrystal = (SodiumNitrateCrystal) crystal;
            ImmutableVector2D velocity = crystal.velocity.get();

            //Set the sodium ions free
            for ( Constituent constituent : crystal ) {
                constituent.particle.velocity.set( velocity.getRotatedInstance( Math.random() * Math.PI * 2 ) );
                SphericalParticle particle = (SphericalParticle) constituent.particle;
                if ( particle instanceof SodiumIonParticle ) {
                    freeParticles.add( particle );
                }
            }

            //add new compounds for the nitrates so they will remain together
            for ( final Nitrate nitrate : sodiumNitrateCrystal.getNitrates() ) {
                freeParticles.add( new Compound( nitrate.relativePosition.plus( sodiumNitrateCrystal.position.get() ) ) {{
                    constituents.addAll( nitrate.constituents );
                    velocity.set( crystal.velocity.get().getRotatedInstance( Math.random() * Math.PI * 2 ) );
                }} );
            }
        }

        //Break apart a sucrose crystal
        else if ( crystal instanceof SucroseCrystal ) {
            SucroseCrystal sucroseCrystal = (SucroseCrystal) crystal;
            for ( final SucroseMolecule sucroseMolecule : sucroseCrystal.sucroseMolecules ) {
                freeParticles.add( new Compound( sucroseCrystal.position.get() ) {{
                    constituents.addAll( sucroseMolecule.constituents );
                    velocity.set( crystal.velocity.get().getRotatedInstance( Math.random() * Math.PI * 2 ) );
                }} );
            }
        }

        //Splits up all constituents in a crystal lattice
        else {
            for ( Constituent constituent : crystal ) {
                constituent.particle.velocity.set( crystal.velocity.get().getRotatedInstance( Math.random() * Math.PI * 2 ) );
                freeParticles.add( constituent.particle );
            }
        }
        crystals.getItems().remove( crystal );
    }

    //Determine whether the object is underwater--when it touches the water it should slow down
    private boolean isAnyPartUnderwater( Particle particle ) {
        return particle.getShape().intersects( solution.shape.get().getBounds2D() );
    }

    //Determine whether the object is completely underwater--when it goes completely underwater it should dissolve soon
    private boolean isCompletelyUnderwater( Particle particle ) {
        return solution.shape.get().getBounds2D().contains( particle.getShape().getBounds2D() );
    }

    //When the simulation clock ticks, move the particles
    private void updateParticles( double dt, ItemList<? extends Particle> particles ) {
        for ( Particle particle : particles ) {
            //Accelerate the particle due to gravity and perform an euler integration step
            //This number was obtained by guessing and checking to find a value that looked good for accelerating the particles out of the shaker
            double mass = 1E-10;
            ImmutableVector2D initialPosition = particle.position.get();
            particle.stepInTime( getExternalForce( isAnyPartUnderwater( particle ) ).times( mass ).times( mass ), dt );

            //Random Walk, implementation taken from edu.colorado.phet.solublesalts.model.RandomWalk
            double theta = random.nextDouble() * Math.toRadians( 30.0 ) * MathUtil.nextRandomSign();
            particle.velocity.set( particle.velocity.get().getRotatedInstance( theta ) );

            //Prevent the particles from leaving the solution
            if ( !solution.shape.get().contains( particle.getShape().getBounds2D() ) ) {
                ImmutableVector2D delta = particle.position.get().minus( initialPosition );
                particle.position.set( initialPosition );

                //If the particle hit the wall, point its velocity in the opposite direction so it will move away from the wall
                particle.velocity.set( ImmutableVector2D.parseAngleAndMagnitude( particle.velocity.get().getMagnitude(), delta.getAngle() + Math.PI ) );
            }
        }
    }

    public void reset() {
        super.reset();

        //Clear out solutes, particles, concentration values
        clearSolutes();

        //Reset model for user settings
        showConcentrationValues.reset();
        dispenserType.reset();
        showChargeColor.reset();
        selectedKit.reset();
    }

    private void clearSolutes() {
        //Clear concentration values
        sugarConcentration.reset();
        saltConcentration.reset();

        //Clear particle lists
        sphericalParticles.clear();
        freeParticles.clear();
        saltCrystals.clear();
        sodiumNitrateCrystals.clear();
        calciumChlorideCrystals.clear();
        sugarCrystals.clear();
    }

    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return numSaltIons.greaterThan( 0.0 );
    }

    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return numSugarMolecules.greaterThan( 0.0 );
    }

    public void removeSalt() {
        super.removeSalt();
        //TODO: remove
    }

    public void removeSugar() {
        super.removeSugar();
        //TODO: remove
    }

    public int getNumFreeSugarMolecules() {
        //TODO
        return 0;
    }

    public int getNumFreeSaltMolecules() {
        // assumes # Na == # Cl
        //TODO
        return 0;
    }

    public int getNumTotalSugarMolecules() {
        //TODO
        return 0;
    }

    public int getNumTotalSaltMolecules() {
        // assumes # Na == # Cl
        //TODO
        return 0;
    }

    //Change whether the shaker can emit more solutes.  limit the amount of solute you can add - lets try 60 particles of salt (so 60 Na+ and 60 Cl- ions) and 10 particles of sugar
    public void updateShakerAllowed() {
        //TODO
    }

    //Update concentrations and whether the shaker can emit more solutes
    private void ionCountChanged() {
        updateShakerAllowed();
        updateConcentrations();
    }

    private void updateConcentrations() {
//        //according to VesselGraphic, the way to get the volume in liters is by multiplying the water height by the volumeCalibrationFactor:
//        double volumeInLiters = solubleSaltsModel.getVessel().getWaterLevel() * container.getCalibration().volumeCalibrationFactor;
//
//        final double molesSugarPerLiter = volumeInLiters == 0 ? 0 : getNumFreeSugarMolecules() / 6.022E23 / volumeInLiters;
//
//        //Set sugar concentration in SI (moles per m^3), convert to SI
//        sugarConcentration.set( molesSugarPerLiter * 1000 );
//
//        final double molesSaltPerLiter = volumeInLiters == 0 ? 0 : getNumFreeSaltMolecules() / 6.022E23 / volumeInLiters;
//        saltConcentration.set( molesSaltPerLiter * 1000 );
//
//        numSaltIons.set( solubleSaltsModel.getNumIonsOfType( Sodium.class ) + solubleSaltsModel.getNumIonsOfType( Chlorine.class ) + 0.0 );
//        numSugarMolecules.set( solubleSaltsModel.getNumIonsOfType( PositiveSugarIon.class ) + solubleSaltsModel.getNumIonsOfType( NegativeSugarIon.class ) + 0.0 );
        //TODO:
    }

    /**
     * @inheritDoc
     */
    @Override protected String getSaltShakerName() {
        return SODIUM_CHLORIDE_NEW_LINE;
    }

    /**
     * @inheritDoc
     */
    @Override protected String getSugarDispenserName() {
        return SUCROSE;
    }

    //Called when water flows out of the output faucet, so that we can move update the particles accordingly
    @Override protected void waterDrained( double outVolume, double initialSaltConcentration, double initialSugarConcentration ) {
        super.waterDrained( outVolume, initialSaltConcentration, initialSugarConcentration );
        updateParticlesDueToWaterLevelDropped( outVolume );
    }

    //Iterate over particles that take random walks so they don't move above the top of the water
    private void updateParticlesDueToWaterLevelDropped( double changeInWaterHeight ) {
        waterLevelDropped( freeParticles, changeInWaterHeight );
        waterLevelDropped( sugarCrystals, changeInWaterHeight );
    }

    //When water level decreases, move the particles down with the water level.
    //Beaker base is at y=0.  Move particles proportionately to how close they are to the top.
    private void waterLevelDropped( ItemList<? extends Particle> particles, double volumeDropped ) {
        double changeInWaterHeight = beaker.getHeightForVolume( volumeDropped ) - beaker.getHeightForVolume( 0 );
        for ( Particle particle : particles ) {
            double yLocationInBeaker = particle.position.get().getY();
            double fractionToTop = yLocationInBeaker / beaker.getHeightForVolume( waterVolume.get() );
            particle.position.set( particle.position.get().minus( 0, changeInWaterHeight * fractionToTop ) );
        }
    }

    //When water evaporates, move the particles so they move down with the water level
    @Override protected void waterEvaporated( double evaporatedWater ) {
        super.waterEvaporated( evaporatedWater );
        updateParticlesDueToWaterLevelDropped( evaporatedWater );
    }
}