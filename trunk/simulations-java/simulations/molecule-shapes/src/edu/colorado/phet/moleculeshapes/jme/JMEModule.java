// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import com.jme3.app.Application;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

/**
 * Support for creating a JME application, context and canvas
 * <p/>
 * TODO: support multiple modules in a single sim. right now this initialization looks to be more global, and would probably trounce other modules
 */
public abstract class JMEModule extends Module {

    private final Canvas canvas;

    public JMEModule( Frame parentFrame, String name, IClock clock ) {
        super( name, clock );

        final AppSettings settings = new AppSettings( true );

        JmeUtils.initializeLibraries( settings );

        // antialiasing (use at most 4 anti-aliasing samples. more makes the UI look blurry)
        int maxSamples = JmeUtils.getMaximumAntialiasingSamples();
        settings.setSamples( Math.min( 4, maxSamples ) );

        // store settings within the properties
        JmeUtils.maxAllowedSamples = maxSamples;
        JmeUtils.antiAliasingSamples.set( settings.getSamples() );

        // limit the framerate
        settings.setFrameRate( JmeUtils.frameRate.get() );

        final Application app = createApplication( parentFrame );

        app.setPauseOnLostFocus( false );
        app.setSettings( settings );
        app.createCanvas();

        JmeUtils.frameRate.addObserver( new SimpleObserver() {
            public void update() {
                AppSettings s = settings;
                s.setFrameRate( JmeUtils.frameRate.get() );
                app.setSettings( s );
                app.restart();
            }
        } );

        JmeUtils.antiAliasingSamples.addObserver( new SimpleObserver() {
            public void update() {
                AppSettings s = settings;
                s.setSamples( JmeUtils.antiAliasingSamples.get() );
                app.setSettings( s );
                app.restart();
            }
        } );

        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();

        addListener( new Listener() {
            public void activated() {
                app.startCanvas();
            }

            public void deactivated() {
            }
        } );

        // hide most of the default things
        setClockControlPanel( null );
        setControlPanel( null );
        setLogoPanelVisible( false );

        setSimulationPanel( new JPanel( new BorderLayout() ) {{
            add( canvas, BorderLayout.CENTER );
        }} );
    }

    public abstract Application createApplication( Frame parentFrame );

    public Canvas getCanvas() {
        return canvas;
    }
}
