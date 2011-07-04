// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.collision.Box2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig.Calibration;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.ISolubleSaltsModelContainer;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule.ResetListener;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ISugarAndSaltModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SODIUM_CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SUCROSE;
import static java.lang.Math.sqrt;

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

    private final SolubleSaltsModel solubleSaltsModel;
    public final Property<Integer> evaporationRate = new Property<Integer>( 0 );

    //TODO: Eventually we will want to let the fluid volume go to zero, but to fix bugs for interviews, we limit it now
    public final static int MIN_FLUID_VOLUME = 60 * 2;//2.0 E-23 L

    private DoubleProperty numSaltIons = new DoubleProperty( 0.0 );
    private DoubleProperty numSugarMolecules = new DoubleProperty( 0.0 );
    private ISolubleSaltsModelContainer container;

    private static final double framesPerSecond = 30;

    //Soluble salts model only works in well in its predefined coordinate frame, so map its model coordinates to our model coordinates
    public final ModelViewTransform solubleSaltsTransform;

    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    private final Property<Integer> stepsOfAddingSalt = new Property<Integer>( 0 );
    private final Property<Integer> stepsOfAddingSugar = new Property<Integer>( 0 );
    private Box2D myWaterBox = new Box2D();

    public MicroModel() {
        //SolubleSalts clock runs much faster than wall time
        super( new ConstantDtClock( framesPerSecond ),

               //The volume of the micro beaker should be 2E-23L
               //In the macro tab, the dimension is new BeakerDimension( x = -0.1, width = 0.2, height = 0.1, depth = 0.1 )
               //So if it is to have the same shape is as the previous tab then we use width*height*depth = 2E-23 and width = 2*height = 2*depth
               //Solving for width, we have: 2E-23 = width * width/2 * depth / 2 => 8E-23 = width^3.  Therefore width = sqrt(8E-23)
               new BeakerDimension( sqrt( 8E-23 ) ),

               //Flow rate must be slowed since the beaker is so small.  TODO: compute this factor analytically so that it will match the first tab perfectly?  Factor out numbers?
               0.0005 * 1E-31,

               //Values sampled at runtime using a debugger
               5.195833333333284E-13, 1.1824999999999597E-12 );
        container = new ISolubleSaltsModelContainer() {
            public Calibration getCalibration() {
                return new Calibration( 1.7342E-25, 5E-23, 1E-23, 0.5E-23 );
            }

            public void addResetListener( ResetListener resetListener ) {
            }

            public double getMinimumFluidVolume() {
                return MicroModel.MIN_FLUID_VOLUME;
            }
        };

        //Create a soluble salts model, but use our own coordinates for the vessel fluid, so it will match up with the shape of the water in our model
        solubleSaltsModel = new SolubleSaltsModel( clock, container ) {
            @Override protected Vessel newVessel() {
                return new Vessel( vesselWidth, vesselDepth, vesselWallThickness, vesselLoc, this ) {
                    @Override public Box2D getWater() {
                        return myWaterBox;
                    }
                };
            }
        };

        //When the clock ticks, update the soluble salt model, but run it much faster since it runs at a different rate than the sugar and salt solutions model
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                solubleSaltsModel.update( new ClockEvent( clock ) {
                    @Override public double getSimulationTimeChange() {
                        return clockEvent.getSimulationTimeChange() * 20;
                    }
                } );
            }
        } );

        //When the user selects a different solute, update the dispenser type
        dispenserType.addObserver( new SimpleObserver() {
            public void update() {
                if ( dispenserType.get() == DispenserType.SALT ) {
                    solubleSaltsModel.setCurrentSalt( new SodiumChloride() );
                }
                else {
                    solubleSaltsModel.setCurrentSalt( new SugarCrystal() );
                }
                updateShakerAllowed();
            }
        } );

        //Create the mapping from soluble salt model coordinates to our coordinates
        solubleSaltsTransform = createRectangleInvertedYMapping( getSolubleSaltsModel().getVessel().getShape(), beaker.getWallShape().getBounds2D() );

        //Update concentration when fluid volume changes
        waterVolume.addObserver( new SimpleObserver() {
            public void update() {
                updateConcentrations();

                //Update the bounds of the water
                final Rectangle2D bounds = solubleSaltsTransform.viewToModel( beaker.getWaterShape( 0, waterVolume.get() ) ).getBounds2D();
                myWaterBox.setBounds( bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight() );
            }
        } );

        //Update ion counts and concentration when ions leave/enter
        solubleSaltsModel.addIonListener( new IonListener() {
            public void ionAdded( IonEvent event ) {
                ionCountChanged();
            }

            public void ionRemoved( IonEvent event ) {
                ionCountChanged();
            }
        } );

        solubleSaltsModel.getClock().addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                double delta = ( clockEvent.getSimulationTimeChange() * evaporationRate.get() ) / 50;
                solubleSaltsModel.getVessel().setWaterLevel( Math.max( MIN_FLUID_VOLUME, solubleSaltsModel.getVessel().getWaterLevel() - delta ), false );
            }
        } );
    }

    //When a macro salt would be shaken out of the shaker, instead add a micro salt crystal
    @Override public void addMacroSalt( MacroSalt salt ) {
        shakeMicroCrystal( stepsOfAddingSalt, salt.position.get(), new SodiumChloride() );
    }

    //When a macro sugar would be shaken out of the shaker, instead add a micro sugar crystal
    @Override public void addMacroSugar( MacroSugar sugar ) {
        shakeMicroCrystal( stepsOfAddingSugar, sugar.position.get(), new SugarCrystal() );
    }

    private void shakeMicroCrystal( Property<Integer> stepsTowardAdding, ImmutableVector2D position, Salt crystal ) {
        //Don't call super here, instead create a micro crystal
        stepsTowardAdding.set( stepsTowardAdding.get() + 1 );
        if ( stepsTowardAdding.get() % 10 == 0 ) {
            //Set the location of the shaker in the soluble salts model
            getSolubleSaltsModel().getShaker().setPosition( solubleSaltsTransform.viewToModel( position ).toPoint2D() );

            //Create a microscopic crystal at the shaker's new location
            getSolubleSaltsModel().getShaker().setCurrentSalt( crystal );
            getSolubleSaltsModel().getShaker().shakeCrystal();
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

    public SolubleSaltsModel getSolubleSaltsModel() {
        return solubleSaltsModel;
    }

    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return numSugarMolecules.greaterThan( 0.0 );
    }

    public void removeSalt() {
        super.removeSalt();
        for ( Object modelElement : new ArrayList<Ion>() {{
            addAll( solubleSaltsModel.getIonsOfType( Sodium.class ) );
            addAll( solubleSaltsModel.getIonsOfType( Chlorine.class ) );
        }} ) {
            solubleSaltsModel.removeModelElement( (ModelElement) modelElement );
        }
    }

    public void removeSugar() {
        super.removeSugar();
        for ( Ion ion : new ArrayList<Ion>() {{
            addAll( solubleSaltsModel.getIonsOfType( PositiveSugarIon.class ) );
            addAll( solubleSaltsModel.getIonsOfType( NegativeSugarIon.class ) );
        }} ) {
            solubleSaltsModel.removeModelElement( ion );
        }
    }

    public int getNumFreeSugarMolecules() {
        return solubleSaltsModel.getNumFreeIonsOfType( PositiveSugarIon.class ) + solubleSaltsModel.getNumFreeIonsOfType( NegativeSugarIon.class );
    }

    public int getNumFreeSaltMolecules() {
        // assumes # Na == # Cl
        return solubleSaltsModel.getNumFreeIonsOfType( Sodium.class );
    }

    public int getNumTotalSugarMolecules() {
        return solubleSaltsModel.getIonsOfType( PositiveSugarIon.class ).size() + solubleSaltsModel.getIonsOfType( NegativeSugarIon.class ).size();
    }

    public int getNumTotalSaltMolecules() {
        // assumes # Na == # Cl
        return solubleSaltsModel.getIonsOfType( Sodium.class ).size();
    }

    //Change whether the shaker can emit more solutes.  limit the amount of solute you can add - lets try 60 particles of salt (so 60 Na+ and 60 Cl- ions) and 10 particles of sugar
    public void updateShakerAllowed() {
        solubleSaltsModel.getShaker().setEnabledBasedOnMax( dispenserType.get() == DispenserType.SALT ?
                                                            getNumTotalSaltMolecules() < 60 :
                                                            getNumTotalSugarMolecules() < 10 );
    }

    //Update concentrations and whether the shaker can emit more solutes
    private void ionCountChanged() {
        updateShakerAllowed();
        updateConcentrations();
    }

    private void updateConcentrations() {
        //according to VesselGraphic, the way to get the volume in liters is by multiplying the water height by the volumeCalibrationFactor:
        double volumeInLiters = solubleSaltsModel.getVessel().getWaterLevel() * container.getCalibration().volumeCalibrationFactor;

        final double molesSugarPerLiter = volumeInLiters == 0 ? 0 : getNumFreeSugarMolecules() / 6.022E23 / volumeInLiters;

        //Set sugar concentration in SI (moles per m^3), convert to SI
        sugarConcentration.set( molesSugarPerLiter * 1000 );

        final double molesSaltPerLiter = volumeInLiters == 0 ? 0 : getNumFreeSaltMolecules() / 6.022E23 / volumeInLiters;
        saltConcentration.set( molesSaltPerLiter * 1000 );

        numSaltIons.set( solubleSaltsModel.getNumIonsOfType( Sodium.class ) + solubleSaltsModel.getNumIonsOfType( Chlorine.class ) + 0.0 );
        numSugarMolecules.set( solubleSaltsModel.getNumIonsOfType( PositiveSugarIon.class ) + solubleSaltsModel.getNumIonsOfType( NegativeSugarIon.class ) + 0.0 );
    }

    public void addIonListener( IonGraphicManager ionGraphicManager ) {
        solubleSaltsModel.addIonListener( ionGraphicManager );
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