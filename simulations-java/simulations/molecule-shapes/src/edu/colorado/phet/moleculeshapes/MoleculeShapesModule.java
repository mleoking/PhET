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

        setControlPanel( new JPanel() {{
            JComponent parent = this;
            setLayout( new BoxLayout( parent, BoxLayout.Y_AXIS ) );
            add( new JButton( "(Test) Add Atom" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        app.enqueue( new Callable<Object>() {
                            public Object call() throws Exception {
                                app.testAddAtom( false );
                                return null;
                            }
                        } );
                    }
                } );
            }} );
            add( new JButton( "(Test) Add Lone Pair" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        app.enqueue( new Callable<Object>() {
                            public Object call() throws Exception {
                                app.testAddAtom( true );
                                return null;
                            }
                        } );
                    }
                } );
            }} );
            add( new JButton( "(Test) Remove Random" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        app.enqueue( new Callable<Object>() {
                            public Object call() throws Exception {
                                app.testRemoveAtom();
                                return null;
                            }
                        } );
                    }
                } );
            }} );

            class VSEPRButton extends JButton {
                VSEPRButton( String nickname, final int X, final int E ) {
                    super( nickname + " AX" + X + "E" + E );
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            app.setState( X, E );
                        }
                    } );
                }
            }

            add( new VSEPRButton( "Linear", 2, 3 ) );
            add( new VSEPRButton( "Trigonal pyramidal", 3, 1 ) );
            add( new VSEPRButton( "T-shaped", 3, 2 ) );
            add( new VSEPRButton( "Seesaw", 4, 1 ) );
            add( new VSEPRButton( "Square Planar", 4, 2 ) );
            add( new VSEPRButton( "Square Pyramidal", 5, 1 ) );
            add( new VSEPRButton( "Pentagonal pyramidal", 6, 1 ) );
            add( new ResetAllButton( parent ) );
        }} );

        setSimulationPanel( new JPanel( new BorderLayout() ) {{
            add( canvas, BorderLayout.CENTER );
            setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }} );

    }
}
