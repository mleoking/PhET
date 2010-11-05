/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsState;
import edu.colorado.phet.gravityandorbits.simsharing.SimSharingStudentClient;
import edu.colorado.phet.gravityandorbits.simsharing.SimSharingTeacherClient;
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

        if ( Arrays.asList( commandLineArgs ).contains( "-teacher" ) ) {
            model.teacherMode = true;
            try {
                new SimSharingTeacherClient( this, parentFrame ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        else if ( Arrays.asList( commandLineArgs ).contains( "-student" ) ) {
            try {
                new SimSharingStudentClient( this, parentFrame ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
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

    public GravityAndOrbitsState getState() {
        return new GravityAndOrbitsState( model.getSun().toBodyState(), model.getPlanet().toBodyState(), model.getMoon().toBodyState() );
    }

    public void setState( GravityAndOrbitsState gravityAndOrbitsState ) {
        model.getSun().updateBodyStateFromModel( gravityAndOrbitsState.sunState );
        model.getPlanet().updateBodyStateFromModel( gravityAndOrbitsState.planetState );
        model.getMoon().updateBodyStateFromModel( gravityAndOrbitsState.moonState );
    }
}
