/* Copyright 2004, Sam Reid */
package edu.umd.cs.piccolo.examples;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.tests.piccolo.TestPiccolo2;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
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
 * Date: Jul 14, 2005
 * Time: 1:15:03 PM
 * Copyright (c) Jul 14, 2005 by Sam Reid
 */

public class PSwingExample extends PFrame {
    public PSwingExample() {
        setTitle( "Test PSwing" );

        //        PDebug.debugPrintFrameRate = true;
//        PDebug.debugRegionManagement = true;
//        PDebug.debugFullBounds = true;

//        RepaintManager.setCurrentManager( new ZBasicRepaintManager() );
        PSwingCanvas pCanvas = new PSwingCanvas();
//        pCanvas.add( ZSwing.getSwingWrapper() );

        final PText pText = new PText( "Hello PhET\nTesting" );
        pCanvas.getLayer().addChild( pText );
        pText.addInputEventListener( new TestPiccolo2.DragBehavior() );
        JFrame frame = this;
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



//        testCheckBox();

        // A Slider
        JSlider slider2 = new JSlider();
        PSwing pSlider2 = new PSwing( pCanvas, slider2 );
        pSlider2.translate( 200, 200 );
        PNode root = new PNode();
        root.addChild( pSlider2 );
        root.scale( 1.5 );
        root.rotate( Math.PI / 4 );
        root.translate( 300, 200 );
        pCanvas.getLayer().addChild( root );


        // Revalidate and repaint
        pCanvas.revalidate();
        pCanvas.repaint();
    }
}
