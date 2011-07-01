package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.*;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;

//Copied from http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/awt/TestCanvas.java
public class TestSwingCanvas {

    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static Application app;
    private static JFrame frame;

    private static void createFrame() {
        frame = new JFrame( "Test" );
        frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed( WindowEvent e ) {
                app.stop();
            }
        } );

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar( menuBar );

        JMenu menuFile = new JMenu( "File" );
        menuBar.add( menuFile );

        final JMenuItem itemRemoveCanvas = new JMenuItem( "Remove Canvas" );
        menuFile.add( itemRemoveCanvas );
        itemRemoveCanvas.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( itemRemoveCanvas.getText().equals( "Remove Canvas" ) ) {
                    frame.getContentPane().remove( canvas );

                    // force OS to repaint over canvas ..
                    // this is needed since AWT does not handle
                    // that when a heavy-weight component is removed.
                    frame.setVisible( false );
                    frame.setVisible( true );
                    frame.requestFocus();

                    itemRemoveCanvas.setText( "Add Canvas" );
                }
                else if ( itemRemoveCanvas.getText().equals( "Add Canvas" ) ) {
                    frame.getContentPane().add( canvas );
                    itemRemoveCanvas.setText( "Remove Canvas" );
                }
            }
        } );

        JMenuItem itemKillCanvas = new JMenuItem( "Stop/Start Canvas" );
        menuFile.add( itemKillCanvas );
        itemKillCanvas.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                frame.getContentPane().remove( canvas );
                app.stop( true );

                createCanvas();
                frame.getContentPane().add( canvas );
                frame.pack();
                startApp();
            }
        } );

        JMenuItem itemExit = new JMenuItem( "Exit" );
        menuFile.add( itemExit );
        itemExit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                frame.dispose();
                app.stop();
            }
        } );

        JMenu menuEdit = new JMenu( "Edit" );
        menuBar.add( menuEdit );
        JMenuItem itemDelete = new JMenuItem( "Delete" );
        menuEdit.add( itemDelete );

        JMenu menuView = new JMenu( "View" );
        menuBar.add( menuView );
        JMenuItem itemSetting = new JMenuItem( "Settings" );
        menuView.add( itemSetting );

        JMenu menuHelp = new JMenu( "Help" );
        menuBar.add( menuHelp );
    }

    public static void createCanvas() {
        AppSettings settings = new AppSettings( true );
        settings.setWidth( Math.max( 640, frame.getContentPane().getWidth() ) );
        settings.setHeight( Math.max( 480, frame.getContentPane().getHeight() ) );

        app = new ShowMolecule();

        app.setPauseOnLostFocus( false );
        app.setSettings( settings );
        app.createCanvas();

        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize( settings.getWidth(), settings.getHeight() );
    }

    public static void startApp() {
        app.startCanvas();
        app.enqueue( new Callable<Void>() {
            public Void call() {
                if ( app instanceof SimpleApplication ) {
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    simpleApp.getFlyByCamera().setDragToRotate( true );
                }
                return null;
            }
        } );

    }

    public static void main( String[] args ) {
        JmeFormatter formatter = new JmeFormatter();

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter( formatter );

        Logger.getLogger( "" ).removeHandler( Logger.getLogger( "" ).getHandlers()[0] );
        Logger.getLogger( "" ).addHandler( consoleHandler );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JPopupMenu.setDefaultLightWeightPopupEnabled( false );

                createFrame();
                createCanvas();

                frame.getContentPane().add( canvas );
                frame.pack();
                startApp();
                frame.setLocationRelativeTo( null );
                frame.setVisible( true );
            }
        } );
    }

}