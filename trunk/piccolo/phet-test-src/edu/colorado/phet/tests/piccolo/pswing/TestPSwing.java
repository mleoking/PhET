/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.pswing;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.tests.piccolo.TestPiccolo2;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.P3DRect;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2005
 * Time: 12:15:55 PM
 * Copyright (c) Jul 11, 2005 by Sam Reid
 */

public class TestPSwing {
    public static void main( String[] args ) {

//        PDebug.debugPrintFrameRate = true;
//        PDebug.debugRegionManagement = true;
//        PDebug.debugFullBounds = true;

//        RepaintManager.setCurrentManager( new ZBasicRepaintManager() );
        PSwingCanvas pCanvas = new PSwingCanvas();
//        pCanvas.add( ZSwing.getSwingWrapper() );

        final PText pText = new PText( "Hello PhET\nTesting" );
        pCanvas.getLayer().addChild( pText );
        pText.addInputEventListener( new TestPiccolo2.DragBehavior() );
        JFrame frame = new JFrame( "Test Piccolo" );
//        pCanvas.getCamera().translateView( 50, 50 );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 600, 800 );
        frame.setVisible( true );

        PText text2 = new PText( "Text2" );
        text2.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        pCanvas.getLayer().addChild( text2 );
        text2.translate( 100, 100 );
        text2.addInputEventListener( new PZoomEventHandler() );

        pCanvas.removeInputEventListener( pCanvas.getPanEventHandler() );

        P3DRect child = new P3DRect( 0, 0, 30, 30 );
        child.setRaised( true );
        child.setPaint( Color.green );
        pText.addChild( child );

        JButton jButton = new JButton( "MyButton!" );
        jButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "TestZSwing.actionPerformed!!!!!!!!!!!!!!*********************" );
            }
        } );
//        jButton.setLocation( -5, -5 );
        final PSwing pSwing = new PSwing( pCanvas, jButton );
        pCanvas.getLayer().addChild( pSwing );
        pSwing.repaint();

        JSpinner jSpinner = new JSpinner();
        jSpinner.setPreferredSize( new Dimension( 100, jSpinner.getPreferredSize().height ) );
        PSwing pSpinner = new PSwing( pCanvas, jSpinner );
        pCanvas.getLayer().addChild( pSpinner );
        pSpinner.translate( 0, 150 );

        JCheckBox jcb = new JCheckBox( "CheckBox", true );
        jcb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "TestZSwing.JCheckBox.actionPerformed" );
            }
        } );
        jcb.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "TestPSwing.JChekbox.stateChanged@" + System.currentTimeMillis() );
            }
        } );
        PSwing pCheckBox = new PSwing( pCanvas, jcb );
        pCanvas.getLayer().addChild( pCheckBox );
        pCheckBox.translate( 100, 0 );


        // Growable JTextArea
        JTextArea textArea = new JTextArea( "This is a growable TextArea.\nTry it out!" );
        textArea.setBorder( new LineBorder( Color.blue, 3 ) );
        PSwing swing = new PSwing( pCanvas, textArea );
        swing.translate( 150, 150 );
        pCanvas.getLayer().addChild( swing );


        // A Slider
        JSlider slider = new JSlider();
        PSwing pSlider = new PSwing( pCanvas, slider );
        pSlider.translate( 200, 200 );
        pCanvas.getLayer().addChild( pSlider );

        // A Scrollable JTree
        JTree tree = new JTree();
        tree.setEditable( true );
        JScrollPane p = new JScrollPane( tree );
        p.setPreferredSize( new Dimension( 150, 150 ) );
        PSwing pTree = new PSwing( pCanvas, p );
        pCanvas.getLayer().addChild( pTree );
        pTree.translate( 0, 250 );

        // A JColorChooser - also demonstrates JTabbedPane
        JColorChooser chooser = new JColorChooser();
        PSwing pChooser = new PSwing( pCanvas, chooser );
        pCanvas.getLayer().addChild( pChooser );
        pChooser.translate( 100, 300 );

        JPanel myPanel = new JPanel();
        myPanel.setBorder( BorderFactory.createTitledBorder( "Titled Border" ) );
        myPanel.add( new JCheckBox( "CheckBox" ) );
        PSwing panelSwing = new PSwing( pCanvas, myPanel );
        pCanvas.getLayer().addChild( panelSwing );
        panelSwing.translate( 400, 50 );


        // Revalidate and repaint
        pCanvas.revalidate();
        pCanvas.repaint();

//        testCheckBox();
    }

    private static void testCheckBox() {
        JFrame frame = new JFrame( "Test Check Box" );
        JPanel myPanel = new JPanel();
        myPanel.setBorder( BorderFactory.createTitledBorder( "Titled Border" ) );
        myPanel.add( new JCheckBox( "CheckBox" ) );
        frame.setContentPane( myPanel );

        frame.setSize( 400, 400 );
        frame.show();
    }

}
