// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;

public class MoleculeShapesModule extends Module {
    public MoleculeShapesModule( Frame parentFrame, String name ) {
        super( name, new ConstantDtClock( 30.0 ) );
        AppSettings settings = new AppSettings( true );

        initializeLibraries( settings );

        // antialiasing (use at most 4 anti-aliasing samples. more makes the UI look blurry)
        settings.setSamples( Math.min( 4, getMaximumAntialiasingSamples() ) );

        // limit the framerate
        settings.setFrameRate( 60 );

        final MoleculeJMEApplication app = new MoleculeJMEApplication();

        app.setPauseOnLostFocus( false );
        app.setSettings( settings );
        app.createCanvas();

        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        final Canvas canvas = context.getCanvas();

        addListener( new Listener() {
            public void activated() {
                app.startCanvas();
            }

            public void deactivated() {
            }
        } );
        canvas.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                app.onResize( canvas.getSize() );
            }
        } );

        // hide most of the default things
        setClockControlPanel( null );
        setControlPanel( null );
        setLogoPanelVisible( false );

        setSimulationPanel( new JPanel( new BorderLayout() ) {{
            setBackground( MoleculeShapesConstants.BACKGROUND_COLOR );
            add( canvas, BorderLayout.CENTER );
        }} );
    }

    public static void initializeLibraries( AppSettings settings ) {

        // if we aren't running fron JNLP (and haven't initialized before)
        if ( !JmeSystem.isLowPermissions() ) {
            try {
                JmeNatives.extractNativeLibs( JmeSystem.getPlatform(), settings );
            }
            catch ( IOException e ) {
                throw new RuntimeException( "JME3 failure", e );
            }

            // mark as initialized
            JmeSystem.setLowPermissions( true );
        }
    }

    public static int getMaximumAntialiasingSamples() {
        int result = 0;
        try {
            Pbuffer pb = new Pbuffer( 10, 10, new PixelFormat( 32, 0, 24, 8, 0 ), null );
            pb.makeCurrent();
            boolean supported = GLContext.getCapabilities().GL_ARB_multisample;
            if ( supported ) {
                result = GL11.glGetInteger( GL30.GL_MAX_SAMPLES );
            }
            pb.destroy();
        }
        catch ( LWJGLException e ) {
            //e.printStackTrace();
        }
        return result;
    }

}
