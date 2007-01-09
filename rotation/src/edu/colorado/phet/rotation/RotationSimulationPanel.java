package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSuite;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.graphs.TimeSeriesModel;
import edu.colorado.phet.rotation.util.BufferedPhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:34:12 AM
 * Copyright (c) Dec 27, 2006 by Sam Reid
 */

public class RotationSimulationPanel extends BufferedPhetPCanvas {
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

    public RotationSimulationPanel( RotationModule rotationModule ) {
        this.rotationModule = rotationModule;
        setBackground( new RotationLookAndFeel().getBackgroundColor() );
        rotationGraphSet = new RotationGraphSet( this, rotationModule.getRotationModel() );
        graphSetModel = new GraphSetModel( rotationGraphSet.getGraphSuite( 0 ) );

        rotationPlayArea = new RotationPlayArea();
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( this, graphSetModel, new TimeSeriesModel() );

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
    }

    private void relayout() {
        rotationPlayArea.setOffset( 0, 0 );
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double maxX = Math.max( rotationPlayArea.getFullBounds().getMaxX(), rotationControlPanelNode.getFullBounds().getMaxX() );
        timeSeriesGraphSetNode.setBounds( maxX, 0, getWidth() - maxX, getHeight() );
    }

    public GraphSuite getGraphSuite( int i ) {
        return rotationGraphSet.getGraphSuite( i );
    }

    public GraphSetModel getGraphSetModel() {
        return graphSetModel;
    }
}
