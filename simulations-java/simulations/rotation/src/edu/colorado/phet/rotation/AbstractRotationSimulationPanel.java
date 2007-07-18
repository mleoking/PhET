package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.*;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:34:12 AM
 */

public abstract class AbstractRotationSimulationPanel extends BufferedPhetPCanvas {
    private AbstractRotationModule rotationModule;

    /* PNodes*/
    private RotationPlayAreaNode rotationPlayAreaNode;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PSwing rotationControlPanelNode;
    private GraphSuiteSet rotationGraphSet;

    private GraphSetModel graphSetModel;

    public AbstractRotationSimulationPanel( final AbstractRotationModule rotationModule, JFrame phetFrame ) {
        this.rotationModule = rotationModule;
        setBackground( new RotationLookAndFeel().getBackgroundColor() );
        rotationGraphSet = createRotationGraphSet();
        graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        rotationPlayAreaNode = createPlayAreaNode();
        final TimeSeriesModel timeSeriesModel = rotationModule.getRotationModel().getTimeSeriesModel();
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                if( timeSeriesModel.numPlaybackStates() == 0 ) {
                    rotationModule.getRotationModel().clear();
                    rotationGraphSet.clear();
                }
            }
        } );
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( graphSetModel, timeSeriesModel, AbstractRotationModule.DEFAULT_CLOCK_DT / 4.0, AbstractRotationModule.DEFAULT_CLOCK_DT );

        rotationControlPanelNode = new PSwing( createControlPanel( getRulerNode(), phetFrame ) );

        addScreenChild( rotationControlPanelNode );
        addScreenChild( timeSeriesGraphSetNode );
        addScreenChild( rotationPlayAreaNode );

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
        //todo: should be after clock finished tick, not in line with other tick handlers
        rotationModule.getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if( synchronousPaint ) {
                    paintImmediately( 0, 0, getWidth(), getHeight() );
                }
            }
        } );
    }

    private boolean synchronousPaint = true;

    public boolean isSynchronousPaint() {
        return synchronousPaint;
    }

    public void setSynchronousPaint( boolean synchronousPaint ) {
        this.synchronousPaint = synchronousPaint;
    }

    public void resetAll() {
        rotationGraphSet.resetAll();
        graphSetModel.setGraphSuite( rotationGraphSet.getGraphSuite( 0 ) );
        rotationPlayAreaNode.resetAll();
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new RotationGraphSet( this, rotationModule.getRotationModel() );
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
                            timeSeriesGraphSetNode, rotationPlayAreaNode.getPlatformNode(), rotationPlayAreaNode.getOriginNode() ).layout();
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
    }

    public void setGraphsBufferedImmediateSeries() {
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().getDynamicJFreeChartNode().setBufferedImmediateSeries();
        }
    }

    public void setGraphsBufferedSeries() {
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().getDynamicJFreeChartNode().setBufferedSeries();
        }
    }

    public void setGraphsPiccoloSeries() {
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().getDynamicJFreeChartNode().setPiccoloSeries();
        }
    }

    public RotationPlayAreaNode getRotationPlayAreaNode() {
        return rotationPlayAreaNode;
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        if( rotationModule != null && rotationModule.getClock() != null && rotationModule.getClock().isRunning() && synchronousPaint ) {
            //wait for synchronous paint
        }
        else {
            super.repaint( tm, x, y, width, height );
        }
    }

}
