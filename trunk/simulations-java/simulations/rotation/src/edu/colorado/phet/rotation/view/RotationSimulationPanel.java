package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.RotationControlPanel;
import edu.colorado.phet.rotation.RotationModule;
import edu.colorado.phet.rotation.TimeSeriesGraphSetNode;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSuite;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.util.BufferedPhetPCanvas;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:34:12 AM
 */

public class RotationSimulationPanel extends BufferedPhetPCanvas {
    //public class RotationSimulationPanel extends PhetPCanvas {
    /*MVC Model components*/
    private RotationModule rotationModule;

    /*JComponents*/
    private RotationControlPanel rotationControlPanel;

    /* PNodes*/
    private RotationPlayAreaNode rotationPlayAreaNode;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PSwing rotationControlPanelNode;
    private RotationGraphSet rotationGraphSet;

    private GraphSetModel graphSetModel;

    public RotationSimulationPanel( final RotationModule rotationModule ) {
        this.rotationModule = rotationModule;
        setBackground( new RotationLookAndFeel().getBackgroundColor() );
        rotationGraphSet = new RotationGraphSet( this, rotationModule.getRotationModel() );
        graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        rotationPlayAreaNode = new RotationPlayAreaNode( rotationModule.getRotationModel() );

//        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( rotationModule.getRotationModel().getTimeSeries(), 1000.0 );
        final TimeSeriesModel timeSeriesModel = rotationModule.getRotationModel().getTimeSeriesModel();
        timeSeriesModel.addListener( new TimeSeriesModel.Listener() {
            public void stateChanged() {
                if( timeSeriesModel.numPlaybackStates() == 0 ) {
                    rotationModule.getRotationModel().clear();
                    rotationGraphSet.clear();
                }
            }
        } );
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( graphSetModel, timeSeriesModel );

        rotationControlPanel = new RotationControlPanel( rotationGraphSet, graphSetModel, rotationModule.getVectorViewModel() );
        rotationControlPanelNode = new PSwing( rotationControlPanel );

        addScreenChild( rotationPlayAreaNode );
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
                    PDebug.debugBounds = !PDebug.debugBounds;
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F3 && e.isAltDown() ) {
                    PDebug.debugFullBounds = !PDebug.debugFullBounds;
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F5 && e.isAltDown() ) {
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

//        setZoomEventHandler( new PZoomEventHandler() );
    }

    private void setAlignedLayout() {
        timeSeriesGraphSetNode.setAlignedLayout();
    }

    private void setFlowLayout() {
        timeSeriesGraphSetNode.setFlowLayout();
    }

    private void relayout() {
        rotationPlayAreaNode.setOffset( 0, 0 );
        rotationPlayAreaNode.setScale( 1.0 );
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double maxX = Math.max( rotationPlayAreaNode.getFullBounds().getMaxX(), rotationControlPanelNode.getFullBounds().getMaxX() );

        double width = rotationControlPanelNode.getFullBounds().getWidth();
        double sx = width / rotationPlayAreaNode.getPlatformNode().getFullBounds().getWidth();

        double height = getHeight() - rotationControlPanelNode.getFullBounds().getHeight();
        double sy = height / rotationPlayAreaNode.getPlatformNode().getFullBounds().getHeight();
        double scale = Math.min( sx, sy );
        System.out.println( "sx = " + sx + ", sy=" + sy + ", scale=" + scale );
        if( scale > 0 ) {
            rotationPlayAreaNode.scale( scale );
        }

        Rectangle2D bounds = new Rectangle2D.Double( maxX, 0, getWidth() - maxX, getHeight() );
//        System.out.println( "RSP::bounds = " + bounds );
        timeSeriesGraphSetNode.setBounds( bounds );
    }

    public GraphSuite getGraphSuite( int i ) {
        return rotationGraphSet.getGraphSuite( i );
    }

    public GraphSetModel getGraphSetModel() {
        return graphSetModel;
    }
}
