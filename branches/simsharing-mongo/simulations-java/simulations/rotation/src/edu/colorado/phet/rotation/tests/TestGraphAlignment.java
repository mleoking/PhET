// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;

public class TestGraphAlignment {
    private JFrame suiteSelectionFrame;
    private JFrame plotFrame;
    private GraphSetNode graphSetNode;
    private PhetPCanvas phetPCanvas;

    private RotationModel rotationModel;
    private Timer timer;

    public TestGraphAlignment() {
        new PhetLookAndFeel().initLookAndFeel();
        suiteSelectionFrame = new JFrame();
        suiteSelectionFrame.setSize( 600, 600 );
        suiteSelectionFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( new Color( 200, 240, 200 ) );

        rotationModel = new RotationModel( new ConstantDtClock( 30, 1 ) );

        GraphSuiteSet rotationGraphSet = new RotationGraphSet( phetPCanvas, rotationModel, new AngleUnitModel( true ) );
        GraphSetModel graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        suiteSelectionFrame.getContentPane().add( graphSelectionControl );

        plotFrame = new JFrame();
        graphSetNode = new GraphSetNode( graphSetModel );

        phetPCanvas.addScreenChild( graphSetNode );
        plotFrame.setContentPane( phetPCanvas );
        plotFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        plotFrame.setSize( 800, 600 );
        plotFrame.setLocation( suiteSelectionFrame.getX() + suiteSelectionFrame.getWidth(), suiteSelectionFrame.getY() );

        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );
        phetPCanvas.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F1 ) {
                    graphSetNode.setFlowLayout();
                }
                if ( e.getKeyCode() == KeyEvent.VK_F2 ) {
                    graphSetNode.setAlignedLayout();
                }
            }
        } );
        HelpBalloon child = new HelpBalloon( phetPCanvas, "Press F1 and F2 to toggle between layouts" );
        child.setEnabled( true );
        child.setBalloonFillPaint( Color.white );
        phetPCanvas.getLayer().addChild( child );
        graphSetNode.setFlowLayout();
    }

    private void step() {
        rotationModel.stepInTime( 1.0 );
    }

    private void relayout() {
        graphSetNode.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestGraphAlignment().start();
    }

    private void start() {
        suiteSelectionFrame.setVisible( true );
        plotFrame.setVisible( true );
        timer.start();
        relayout();
        phetPCanvas.requestFocus();
    }
}
