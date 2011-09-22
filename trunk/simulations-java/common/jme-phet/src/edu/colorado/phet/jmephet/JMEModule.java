// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

    private static Application app = null;
    private static JmeCanvasContext context;
    private static Canvas canvas;

    public JMEModule( Frame parentFrame, String name, IClock clock ) {
        super( name, clock );

        // do the following only for the first initialization, since we can only create one application
        if ( app == null ) {
            final AppSettings settings = new AppSettings( true );

            JMEUtils.initializeLibraries( settings );

            // antialiasing (use at most 4 anti-aliasing samples. more makes the UI look blurry)
            int maxSamples = JMEUtils.getMaximumAntialiasingSamples();
            settings.setSamples( Math.min( 4, maxSamples ) );

            // store settings within the properties
            JMEUtils.maxAllowedSamples = maxSamples;
            if ( JMEUtils.antiAliasingSamples.get() == null ) {
                JMEUtils.antiAliasingSamples.set( settings.getSamples() );
            }

            // limit the framerate
            settings.setFrameRate( JMEUtils.frameRate.get() );

            // TODO: better way than having each module know how to create its own canvas? (more of a global JME state?)
            app = createApplication( parentFrame );

            app.setPauseOnLostFocus( false );
            app.setSettings( settings );
            app.createCanvas();

            JMEUtils.frameRate.addObserver( new SimpleObserver() {
                public void update() {
                    AppSettings s = settings;
                    s.setFrameRate( JMEUtils.frameRate.get() );
                    app.setSettings( s );
                    app.restart();
                }
            } );

            JMEUtils.antiAliasingSamples.addObserver( new SimpleObserver() {
                public void update() {
                    AppSettings s = settings;
                    s.setSamples( JMEUtils.antiAliasingSamples.get() );
                    app.setSettings( s );
                    app.restart();
                }
            } );

            context = (JmeCanvasContext) app.getContext();
            canvas = context.getCanvas();

            addListener( new Listener() {
                public void activated() {
                    app.startCanvas();
                }

                public void deactivated() {
                }
            } );

            // listen to resize events on our canvas, so that we can update our layout
            getCanvas().addComponentListener( new ComponentAdapter() {
                @Override public void componentResized( ComponentEvent e ) {
                    ( (PhetJMEApplication) app ).onResize( getCanvas().getSize() );
                }
            } );
        }

        // hide most of the default things
        setClockControlPanel( null );
        setControlPanel( null );
        setLogoPanelVisible( false );

        final JPanel simulationPanel = new JPanel( new BorderLayout() ) {
            @Override public void setVisible( boolean aFlag ) {
            }
        };
        setSimulationPanel( simulationPanel );

        addListener( new Listener() {
            public void activated() {
                simulationPanel.add( canvas, BorderLayout.CENTER );
            }

            public void deactivated() {
                simulationPanel.remove( canvas );
            }
        } );
    }

    public abstract Application createApplication( Frame parentFrame );

    public Canvas getCanvas() {
        return canvas;
    }
}
