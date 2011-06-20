// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig.Calibration;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel.ChangeEvent;
import edu.colorado.phet.solublesalts.model.Vessel.ChangeListener;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.ISolubleSaltsModelContainer;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule.ResetListener;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ISugarAndSaltModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;

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

    public MicroModel() {
        //SolubleSalts clock runs much faster than wall time
        super( new ConstantDtClock( (int) ( 1000 / framesPerSecond ), 1 / framesPerSecond * 10 ) );
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
        solubleSaltsModel = new SolubleSaltsModel( clock, container );
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                solubleSaltsModel.update( clockEvent );
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

        //Update concentration when fluid volume changes
        solubleSaltsModel.getVessel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateConcentrations();
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

    @Override public void addMacroSalt( MacroSalt salt ) {
        super.addMacroSalt( salt );
        //The shaker has to be reset between shakes, then shake it up and down to produce a crystal
        getSolubleSaltsModel().getShaker().reset();
        getSolubleSaltsModel().getShaker().shake( 10 );
        getSolubleSaltsModel().getShaker().shake( -10 );
    }

    public void reset() {
        super.reset();
        sugarConcentration.reset();
        saltConcentration.reset();
        showConcentrationValues.reset();
        dispenserType.reset();
        showConcentrationBarChart.reset();
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
}