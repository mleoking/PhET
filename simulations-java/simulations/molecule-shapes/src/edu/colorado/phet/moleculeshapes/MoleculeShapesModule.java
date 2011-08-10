// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.moleculeshapes.view.BaseJMEApplication;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class MoleculeShapesModule extends Module {
    public MoleculeShapesModule( String name ) {
        super( name, new ConstantDtClock( 30.0 ) );
        AppSettings settings = new AppSettings( true );

        // antialiasing
        settings.setSamples( 4 );

        // limit the framerate
        settings.setFrameRate( 60 );

        final MoleculeJMEApplication app = new MoleculeJMEApplication();

        //Improve default camera angle and mouse behavior
        app.enqueue( new Callable<Void>() {
            public Void call() {
                BaseJMEApplication simpleApp = (BaseJMEApplication) app;
                //simpleApp.getFlyByCamera().setDragToRotate( true );
                return null;
            }
        } );

        app.setPauseOnLostFocus( false );
        app.setSettings( settings );
        app.createCanvas();

        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        final Canvas canvas = context.getCanvas();
        canvas.setSize( settings.getWidth(), settings.getHeight() );
        addListener( new Listener() {
            public void activated() {
                app.startCanvas();
            }

            public void deactivated() {
            }
        } );
        canvas.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                app.onResize(canvas.getSize());
            }
        } );

        setClockControlPanel( null );

        setControlPanel( null );
        setLogoPanelVisible( false );

        setSimulationPanel( new JPanel( new BorderLayout() ) {{
            add( canvas, BorderLayout.CENTER );
            setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }} );

    }
}
