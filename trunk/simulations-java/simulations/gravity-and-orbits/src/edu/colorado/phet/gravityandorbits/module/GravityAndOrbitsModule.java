/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;
    private Property<Boolean> forcesProperty = new Property<Boolean>( false );
    private Property<Boolean> tracesProperty = new Property<Boolean>( false );
    private Property<Boolean> velocityProperty = new Property<Boolean>( false );
    private Property<Boolean> showMassesProperty = new Property<Boolean>( false );
    private Property<Boolean> moonProperty = new Property<Boolean>( false );
    private Property<Boolean> toScaleProperty = new Property<Boolean>( false );

    private final GravityAndOrbitsMode mySunAndPlanet = new GravityAndOrbitsMode( "My Sun & Planet" );
    private final GravityAndOrbitsMode sunEarthAndMoon = new GravityAndOrbitsMode( "Sun, Earth & Moon" );
    private final GravityAndOrbitsMode myPlanetAndSpaceStation = new GravityAndOrbitsMode( "My Planet & Space Station" );
    private final GravityAndOrbitsMode earthAndSpaceStation = new GravityAndOrbitsMode( "Earth & Space Station" );
    private final ArrayList<GravityAndOrbitsMode> modes = new ArrayList<GravityAndOrbitsMode>() {{
        add( mySunAndPlanet );
        add( sunEarthAndMoon );
        add( myPlanetAndSpaceStation );
        add( earthAndSpaceStation );
    }};
    private Property<GravityAndOrbitsMode> modeProperty = new Property<GravityAndOrbitsMode>( modes.get( 0 ) );

    public ArrayList<GravityAndOrbitsMode> getModes() {
        return new ArrayList<GravityAndOrbitsMode>( modes );
    }

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GravityAndOrbitsModule( JFrame parentFrame, String[] commandLineArgs ) {
        super( GravityAndOrbitsStrings.TITLE_EXAMPLE_MODULE
               + ": " + Arrays.asList( commandLineArgs )//For simsharing
                ,
               new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.CLOCK_DT ) );

        // Model
        GravityAndOrbitsClock clock = (GravityAndOrbitsClock) getClock();
        model = new GravityAndOrbitsModel( clock, moonProperty );

        // Canvas
        canvas = new GravityAndOrbitsCanvas( parentFrame, model, this );
        setSimulationPanel( canvas );

        setClockControlPanel( null );//clock panel appears in the canvas

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    public GravityAndOrbitsModel getGravityAndOrbitsModel() {
        return model;
    }

    public GravityAndOrbitsCanvas getCanvas() {
        return canvas;
    }
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // reset the clock
        GravityAndOrbitsClock clock = model.getClock();
        clock.resetSimulationTime();
    }

    public Property<Boolean> getForcesProperty() {
        return forcesProperty;
    }

    public Property<Boolean> getTracesProperty() {
        return tracesProperty;
    }

    public Property<Boolean> getVelocityProperty() {
        return velocityProperty;
    }

    public Property<Boolean> getShowMassesProperty() {
        return showMassesProperty;
    }

    public Property<Boolean> getMoonProperty() {
        return moonProperty;
    }

    public Property<Boolean> getToScaleProperty() {
        return toScaleProperty;
    }

    public void resetAll() {
        forcesProperty.reset();
        tracesProperty.reset();
        velocityProperty.reset();
        showMassesProperty.reset();
        moonProperty.reset();
        toScaleProperty.reset();
        model.resetAll();
    }

    public Property<GravityAndOrbitsMode> getModeProperty() {
        return modeProperty;
    }
}