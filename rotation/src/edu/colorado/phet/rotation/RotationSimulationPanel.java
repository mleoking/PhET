package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSuite;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.timeseries.TimeSeriesModel;
import edu.colorado.phet.rotation.util.BufferedPhetPCanvas;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:34:12 AM
 * Copyright (c) Dec 27, 2006 by Sam Reid
 */

public class RotationSimulationPanel extends BufferedPhetPCanvas {
    //public class RotationSimulationPanel extends PhetPCanvas {
    /*MVC Model components*/
    private RotationModule rotationModule;

    /*JComponents*/
    private RotationControlPanel rotationControlPanel;

    /* PNodes*/
    private RotationPlayArea rotationPlayArea;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PSwing rotationControlPanelNode;
    private RotationGraphSet rotationGraphSet;

    private GraphSetModel graphSetModel;

    public RotationSimulationPanel( final RotationModule rotationModule ) {
        this.rotationModule = rotationModule;
        setBackground( new RotationLookAndFeel().getBackgroundColor() );
        rotationGraphSet = new RotationGraphSet( this, rotationModule.getRotationModel() );
        graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        rotationPlayArea = new RotationPlayArea( rotationModule );
        TimeSeriesModel timeSeriesModel = new TimeSeriesModel();
        timeSeriesModel.addListener( new TimeSeriesModel.Listener() {
            public void stateChanged() {
            }

            public void clear() {
                rotationModule.getRotationModel().clear();
                rotationGraphSet.clear();
            }
        } );
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( this, graphSetModel, timeSeriesModel );

        rotationControlPanel = new RotationControlPanel( rotationGraphSet, graphSetModel, rotationModule.getVectorViewModel() );
        rotationControlPanelNode = new PSwing( this, rotationControlPanel );

        addScreenChild( rotationPlayArea );
        addScreenChild( rotationControlPanelNode );
        addScreenChild( timeSeriesGraphSetNode );

        relayout();
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F1 && e.isAltDown() ) {
                    PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F2 && e.isAltDown() ) {
                    relayout();
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F ) {
                    setFlowLayout();
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_A ) {
                    setAlignedLayout();
                }
            }
        } );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        setAlignedLayout();
    }

    private void setAlignedLayout() {
        timeSeriesGraphSetNode.setAlignedLayout();
    }

    private void setFlowLayout() {
        timeSeriesGraphSetNode.setFlowLayout();
    }

    private void relayout() {
        rotationPlayArea.setOffset( 0, 0 );
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double maxX = Math.max( rotationPlayArea.getFullBounds().getMaxX(), rotationControlPanelNode.getFullBounds().getMaxX() );
        Rectangle2D bounds = new Rectangle2D.Double( maxX, 0, getWidth() - maxX, getHeight() );
        System.out.println( "RSP::bounds = " + bounds );
        timeSeriesGraphSetNode.setBounds( bounds );
    }

    public GraphSuite getGraphSuite( int i ) {
        return rotationGraphSet.getGraphSuite( i );
    }

    public GraphSetModel getGraphSetModel() {
        return graphSetModel;
    }
}
