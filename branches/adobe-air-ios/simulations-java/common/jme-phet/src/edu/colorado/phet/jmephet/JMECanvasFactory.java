// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Canvas;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class JMECanvasFactory {
    public static Canvas createCanvas( Frame parentFrame, Function1<Frame, PhetJMEApplication> applicationFactory ) {
        // do the following only for the first initialization, since we can only create one application
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
        final PhetJMEApplication app = applicationFactory.apply( parentFrame );

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

        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        return context.getCanvas();
    }
}
