// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class MoleculeShapesModule extends Module {
    public MoleculeShapesModule( Frame parentFrame, String name ) {
        super( name, new ConstantDtClock( 30.0 ) );
        AppSettings settings = new AppSettings( true );

        // antialiasing
        settings.setSamples( 4 );

        // limit the framerate
        settings.setFrameRate( 60 );

        final MoleculeJMEApplication app = new MoleculeJMEApplication();

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
                app.onResize( canvas.getSize() );
            }
        } );

        // hide most of the default things
        setClockControlPanel( null );
        setControlPanel( null );
        setLogoPanelVisible( false );

        parentFrame.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );

        setSimulationPanel( new JPanel( new BorderLayout() ) {{
            setBackground( MoleculeShapesConstants.BACKGROUND_COLOR );

            add( canvas, BorderLayout.CENTER );

            // padding to hopefully maintain the default cursor
            add( Box.createHorizontalStrut( 5 ), BorderLayout.WEST );
            add( Box.createHorizontalStrut( 5 ), BorderLayout.EAST );
            add( Box.createVerticalStrut( 5 ), BorderLayout.NORTH );
            add( Box.createVerticalStrut( 5 ), BorderLayout.SOUTH );

            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }} );

    }
}
