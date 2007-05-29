package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.TimeSeriesGraphSetNode;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.RotationControlPanel;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:34:12 AM
 */

public abstract class AbstractRotationSimulationPanel extends BufferedPhetPCanvas {
    private AbstractRotationModule rotationModule;

    /*JComponents*/
    private RotationControlPanel rotationControlPanel;

    /* PNodes*/
    private RotationPlayAreaNode rotationPlayAreaNode;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PSwing rotationControlPanelNode;
    private GraphSuiteSet rotationGraphSet;

    private GraphSetModel graphSetModel;

    public AbstractRotationSimulationPanel( final AbstractRotationModule rotationModule ) {
        this.rotationModule = rotationModule;
        setBackground( new RotationLookAndFeel().getBackgroundColor() );
        rotationGraphSet = new RotationGraphSet( this, rotationModule.getRotationModel() );
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
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( graphSetModel, timeSeriesModel );

        rotationControlPanel = createControlPanel();
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
    }

    public GraphSuiteSet getRotationGraphSet() {
        return rotationGraphSet;
    }

    protected abstract RotationControlPanel createControlPanel();

    protected abstract RotationPlayAreaNode createPlayAreaNode();

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

    public AbstractRotationModule getAbstractRotationModule() {
        return rotationModule;
    }

    public RotationModel getRotationModel() {
        return rotationModule.getRotationModel();
    }
}
