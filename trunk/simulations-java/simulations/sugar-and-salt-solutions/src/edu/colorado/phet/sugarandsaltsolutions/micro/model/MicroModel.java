// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ISugarAndSaltModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SugarLattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.NitrateCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateShaker;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SODIUM_CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SUCROSE;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;

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

    //List of all sugar particles
    public final ItemList<SugarMolecule> sugarList = new ItemList<SugarMolecule>();

    //List of all free spherical particles, used to keep track of which particles to move about randomly
    public final ItemList<Particle> freeParticles = new ItemList<Particle>();

    //Lists of lattices
    public final ItemList<SodiumChlorideCrystal> saltCrystals = new ItemList<SodiumChlorideCrystal>();
    public final ItemList<SodiumNitrateCrystal> sodiumNitrateCrystals = new ItemList<SodiumNitrateCrystal>();
    public final ItemList<CalciumChlorideCrystal> calciumChlorideCrystals = new ItemList<CalciumChlorideCrystal>();
    public final ItemList<SugarCrystal> sugarCrystals = new ItemList<SugarCrystal>();
    public final ItemList<Crystal> nitrates = new ItemList<Crystal>();

    //The factor by which to scale particle sizes, so they look a bit smaller in the graphics
    public static final double sizeScale = 0.35;

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
        dispensers.add( new SodiumNitrateShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, "Sodium<br>Nitrate", distanceScale, dispenserType, DispenserType.SODIUM_NITRATE ) );
        dispensers.add( new CalciumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, "Calcium<br>Chloride", distanceScale, dispenserType, DispenserType.CALCIUM_CHLORIDE ) );
    }

    //When a macro salt would be shaken out of the shaker, instead add a micro salt crystal
    public void addSaltCrystal( SodiumChlorideCrystal sodiumChlorideCrystal ) {
        //Add the components of the lattice to the model so the graphics will be created
        for ( LatticeConstituent latticeConstituent : sodiumChlorideCrystal ) {

            //TODO: split up sodium and chloride ions into separate lists?  Or generalize the list
            sphericalParticles.add( (SphericalParticle) latticeConstituent.particle );
        }
        saltCrystals.add( sodiumChlorideCrystal );
    }

    public void addSodiumNitrateCrystal( SodiumNitrateCrystal crystal ) {
        addCrystalComponents( crystal );
        sodiumNitrateCrystals.add( crystal );
    }

    //Add the components of the lattice to the model so the graphics will be created
    private void addCrystalComponents( Crystal crystal ) {
        for ( LatticeConstituent latticeConstituent : crystal ) {
            sphericalParticles.add( (SphericalParticle) latticeConstituent.particle );
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
            final SugarCrystal crystal = new SugarCrystal( sugar.position.get(), (SugarLattice) new SugarLattice().grow( 3 ), sizeScale );

            //Add the components of the lattice to the model so the graphics will be created
            for ( LatticeConstituent latticeConstituent : crystal ) {

                //TODO: split up sodium and chloride ions into separate lists?  Or generalize the list
                sugarList.add( (SugarMolecule) latticeConstituent.particle );
            }
            sugarCrystals.add( crystal );
        }
    }

    //When the simulation clock ticks, move the particles
    @Override protected void updateModel( double dt ) {
        super.updateModel( dt );
        updateParticles( dt, freeParticles );
        updateParticles( dt, sugarList );
        updateParticles( dt, nitrates );

        updateDissolvableCrystals( dt, saltCrystals );
        updateDissolvableCrystals( dt, sodiumNitrateCrystals );
        updateDissolvableCrystals( dt, calciumChlorideCrystals );
        updateDissolvableCrystals( dt, sugarCrystals );
    }

    //Update the crystals by moving them about and possibly dissolving them
    private void updateDissolvableCrystals( double dt, ItemList<? extends Crystal> crystals ) {
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

            //Determine whether it is time for the lattice to dissolve
            if ( lattice.isUnderwater() ) {
                final double timeUnderwater = time - lattice.getUnderWaterTime();
                if ( timeUnderwater > 1 ) {
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
            for ( LatticeConstituent constituent : crystal ) {
                constituent.particle.velocity.set( velocity.getRotatedInstance( Math.random() * Math.PI * 2 ) );
                SphericalParticle particle = (SphericalParticle) constituent.particle;
                if ( particle.color.equals( Color.red ) ) {
                    freeParticles.add( particle );
                }
            }

            //add new compounds for the nitrates so they will remain together
            for ( final Nitrate nitrate : sodiumNitrateCrystal.getNitrates() ) {
                nitrates.add( new NitrateCrystal( nitrate.nitrogen.position.get(), sizeScale ) {{
                    latticeConstituents.add( new LatticeConstituent( nitrate.nitrogen, new ImmutableVector2D() ) );
                    latticeConstituents.add( new LatticeConstituent( nitrate.o1, nitrate.o1Position ) );
                    latticeConstituents.add( new LatticeConstituent( nitrate.o2, nitrate.o2Position ) );
                    latticeConstituents.add( new LatticeConstituent( nitrate.o3, nitrate.o3Position ) );
                    velocity.set( crystal.velocity.get().getRotatedInstance( Math.random() * Math.PI * 2 ) );
                }} );
            }

            crystals.getItems().remove( crystal );
        }

        //Splits up all constituents in a crystal lattice
        else {
            for ( LatticeConstituent constituent : crystal ) {
                constituent.particle.velocity.set( crystal.velocity.get().getRotatedInstance( Math.random() * Math.PI * 2 ) );
                freeParticles.add( constituent.particle );
            }

            crystals.getItems().remove( crystal );
        }
    }

    //Determine whether the object is underwater--when it touches the water it should slow down
    private boolean isAnyPartUnderwater( Particle particle ) {
        return particle.getShape().intersects( solution.shape.get().getBounds2D() );
    }

    //Determine whether the object is completely underwater--when it goes completely underwater it should dissolve soon
    private boolean isCompletelyUnderwater( Particle particle ) {
        return solution.shape.get().getBounds2D().contains( particle.getShape().getBounds2D() );
    }

    private final Random random = new Random();

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
        sugarConcentration.reset();
        saltConcentration.reset();
        showConcentrationValues.reset();
        dispenserType.reset();
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
        return SODIUM_CHLORIDE;
    }

    /**
     * @inheritDoc
     */
    @Override protected String getSugarDispenserName() {
        return SUCROSE;
    }
}