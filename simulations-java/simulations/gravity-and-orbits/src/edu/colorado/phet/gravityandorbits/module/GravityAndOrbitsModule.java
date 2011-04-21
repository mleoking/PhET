// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.module;

import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * The GravityAndOrbitsModule has a set of "modes", one mode for each configuration of bodies (eg, Sun + Planet).
 * Each mode has its own model, canvas, clock, etc, which are used in place of this Module's data.
 * The module contains information that is shared across all modes, such as whether certain features are shown (such as
 * showing the gravitational force).
 *
 * @author Sam Reid
 * @author Jon Olson
 * @author Chris Malley
 * @author John Blanco
 * @see edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel
 */
public class GravityAndOrbitsModule extends PiccoloModule {
    public static final double G = 6.67428E-11;

    //Properties that are common to all "modes" should live here.
    public final Property<Boolean> showGravityForceProperty = new Property<Boolean>( false );
    public final Property<Boolean> showPathProperty = new Property<Boolean>( false );
    public final Property<Boolean> showGridProperty = new Property<Boolean>( false );
    public final Property<Boolean> showVelocityProperty = new Property<Boolean>( false );
    public final Property<Boolean> showMassProperty = new Property<Boolean>( false );
    public final Property<Boolean> clockPausedProperty = new Property<Boolean>( true );
    public final Property<Double> timeSpeedScaleProperty = new Property<Double>( ( 0.1 + 2 ) / 4 );//one quarter of the way up between 1/10 and 2 scale factors
    public final Property<Boolean> measuringTapeVisibleProperty = new Property<Boolean>( false );
    public final Property<Boolean> gravityEnabledProperty = new Property<Boolean>( true );
    public final Property<Boolean> stepping = new Property<Boolean>( false );
    public final Property<Boolean> rewinding = new Property<Boolean>( false );

    public final Property<GravityAndOrbitsMode> modeProperty;
    public final Property<Boolean> whiteBackgroundProperty;
    public final boolean showMeasuringTape;
    private final ArrayList<GravityAndOrbitsMode> modes;
    public final boolean showMassCheckBox;

    public GravityAndOrbitsModule( final PhetFrame phetFrame, Property<Boolean> whiteBackgroundProperty, final String name, boolean showMeasuringTape, Function1<ModeListParameterList, ArrayList<GravityAndOrbitsMode>> createModes, int initialModeIndex, boolean showMassCheckBox ) {
        super( name, new ConstantDtClock( 30, 1 ) );//TODO: I don't think this clock is used since each mode has its own clock; perhaps this just runs the active tab?
        this.showMassCheckBox = showMassCheckBox;
        modes = createModes.apply( new ModeListParameterList( clockPausedProperty, gravityEnabledProperty, stepping, rewinding, timeSpeedScaleProperty ) );
        modeProperty = new Property<GravityAndOrbitsMode>( modes.get( initialModeIndex ) );
        this.whiteBackgroundProperty = whiteBackgroundProperty;
        this.showMeasuringTape = showMeasuringTape;
        getModulePanel().setLogoPanel( null );
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.init( this );
        }

        setSimulationPanel( getMode().getCanvas() );

        // Switch the entire canvas on mode switches
        modeProperty.addObserver( new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setSimulationPanel( getMode().getCanvas() );
                    }
                } );
                updateActiveModule();
            }
        } );
        setClockControlPanel( null );//clock panel appears in the canvas

        reset();
    }

    public int getModeIndex() {
        return modes.indexOf( getMode() );
    }

    private GravityAndOrbitsMode getMode() {
        return modeProperty.getValue();
    }

    public ArrayList<GravityAndOrbitsMode> getModes() {
        return new ArrayList<GravityAndOrbitsMode>( modes );
    }

    private void updateActiveModule() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.active.setValue( mode == getMode() );
        }
    }

    public void reset() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.reset();
        }
        showGravityForceProperty.reset();
        showPathProperty.reset();
        showGridProperty.reset();
        showVelocityProperty.reset();
        showMassProperty.reset();
        clockPausedProperty.reset();
        timeSpeedScaleProperty.reset();
        measuringTapeVisibleProperty.reset();
        gravityEnabledProperty.reset();
        stepping.reset();
        rewinding.reset();
        modeProperty.reset();
    }

    public void setTeacherMode( boolean b ) {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.getModel().teacherMode = b;
        }
    }

    public void addModelSteppedListener( SimpleObserver simpleObserver ) {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.getModel().addModelSteppedListener( simpleObserver );
        }
    }

    public void setModeIndex( int selectedMode ) {
        modeProperty.setValue( modes.get( selectedMode ) );
    }
}