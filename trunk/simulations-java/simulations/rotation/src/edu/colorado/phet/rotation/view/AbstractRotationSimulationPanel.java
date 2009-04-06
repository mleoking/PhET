package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:34:12 AM
 */

public abstract class AbstractRotationSimulationPanel extends BufferedPhetPCanvas {//using the buffer removes some glitches without causing significant performance impact
    private AbstractRotationModule rotationModule;

    private RotationPlayAreaNode rotationPlayAreaNode;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PSwing rotationControlPanelNode;
    private GraphSuiteSet rotationGraphSet;

    private GraphSetModel graphSetModel;
    private AngleUnitModel angleUnitModel = new AngleUnitModel( false );
    private JComponent controlPanel;
    private long paintTime = 0;
    private PClip playAreaClip;

    public static final Color PLAY_AREA_BACKGROUND_COLOR = new Color( 144, 240, 168 );

    public AbstractRotationSimulationPanel( final AbstractRotationModule rotationModule, final JFrame phetFrame ) {
        this.rotationModule = rotationModule;
        setBackground( new RotationLookAndFeel().getBackgroundColor() );
        rotationGraphSet = createRotationGraphSet();
        graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        rotationPlayAreaNode = createPlayAreaNode();
        final TimeSeriesModel timeSeriesModel = rotationModule.getRotationModel().getTimeSeriesModel();
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                if ( timeSeriesModel.numPlaybackStates() == 0 ) {
                    rotationModule.getRotationModel().clear();
                    rotationGraphSet.clear();
                }
            }
        } );
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( graphSetModel, timeSeriesModel, RotationClock.DEFAULT_CLOCK_DT / 2.0, RotationClock.DEFAULT_CLOCK_DT * 2 );

        controlPanel = createControlPanel( getRulerNode(), phetFrame );
        rotationControlPanelNode = new PSwing( controlPanel );

        addScreenChild( rotationControlPanelNode );

        playAreaClip = new PClip();
        playAreaClip.setPaint( PLAY_AREA_BACKGROUND_COLOR );
        playAreaClip.addChild( rotationPlayAreaNode );
        addScreenChild( playAreaClip );
        addScreenChild( timeSeriesGraphSetNode );

        relayout();
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                relayout();
                timeSeriesGraphSetNode.forceRepaintGraphs();
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );
        addKeyListener( new PDebugKeyHandler() );
        addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F5 && e.isAltDown() ) {
                    relayout();
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F ) {
                    setFlowLayout();
                }
            }
        } );
        addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_A ) {
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
        //must happen after units have changed in the graph, or layout will be wrong after graph changes
        angleUnitModel.addListener( new AngleUnitModel.Listener() {
            public void changed() {
                timeSeriesGraphSetNode.forceRelayout();
            }
        } );

        //hack
        addGraphicsRepaintRectangleWorkaround();
    }

    private void addGraphicsRepaintRectangleWorkaround() {
        rotationModule.getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                AbstractRotationSimulationPanel.this.superPaintImmediately();
            }
        } );
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( rotationModule.getClock().isPaused() ) {
                    AbstractRotationSimulationPanel.this.superPaintImmediately();
                }
            }
        } ).start();
    }

    boolean okToPaint = false;

    public void superPaintImmediately() {
        okToPaint = true;
        super.paintImmediately( 0, 0, getWidth(), getHeight() );
        okToPaint = false;
    }

    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
    }

    public void paintImmediately() {
        if ( okToPaint ) {
            super.paintImmediately();
        }
    }

    public void paintImmediately( int x, int y, int w, int h ) {
        if ( okToPaint ) {
            super.paintImmediately( x, y, w, h );
        }
    }

    public void paintImmediately( Rectangle r ) {
        if ( okToPaint ) {
            super.paintImmediately( r );
        }
    }


    public void resetAll() {
        rotationGraphSet.resetAll();
        graphSetModel.setGraphSuite( rotationGraphSet.getGraphSuite( 0 ) );
        rotationPlayAreaNode.resetAll();
        if ( controlPanel instanceof Resettable ) {
            ( (Resettable) controlPanel ).reset();
        }
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new RotationGraphSet( this, rotationModule.getRotationModel(), getAngleUnitModel() );
    }

    public GraphSuiteSet getRotationGraphSet() {
        return rotationGraphSet;
    }

    protected abstract JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame );

    protected abstract RotationPlayAreaNode createPlayAreaNode();

    private void setAlignedLayout() {
        timeSeriesGraphSetNode.setAlignedLayout();
    }

    private void setFlowLayout() {
        timeSeriesGraphSetNode.setFlowLayout();
    }

    private void relayout() {
        new RotationLayout( this, rotationPlayAreaNode, rotationControlPanelNode,
                            timeSeriesGraphSetNode, rotationPlayAreaNode.getPlatformNode(), playAreaClip ).layout();
    }

    public GraphSuite getGraphSuite( int i ) {
        return rotationGraphSet.getGraphSuite( i );
    }

    public GraphSetModel getGraphSetModel() {
        return graphSetModel;
    }

    public AbstractRotationModule getAbstractRotationModule() {
        return rotationModule;
    }

    public RotationModel getRotationModel() {
        return rotationModule.getRotationModel();
    }

    public RulerNode getRulerNode() {
        return rotationPlayAreaNode.getRulerNode();
    }

    public void startApplication() {
        timeSeriesGraphSetNode.forceRelayout();
        timeSeriesGraphSetNode.forceRepaintGraphs();
        paintImmediately( 0, 0, getWidth(), getHeight() );//workaround for gray rect
    }

    public void setGraphsBufferedImmediateSeries() {
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().getDynamicJFreeChartNode().setBufferedImmediateSeries();
        }
    }

    public void setGraphsBufferedSeries() {
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().getDynamicJFreeChartNode().setBufferedSeries();
        }
    }

    public void setGraphsPiccoloSeries() {
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().getDynamicJFreeChartNode().setPiccoloSeries();
        }
    }

    public RotationPlayAreaNode getRotationPlayAreaNode() {
        return rotationPlayAreaNode;
    }

    public AngleUnitModel getAngleUnitModel() {
        return angleUnitModel;
    }

    public double getLastPaintTime() {
        return paintTime;
    }

    public VectorViewModel getVectorViewModel() {
        return rotationModule.getVectorViewModel();
    }
}
