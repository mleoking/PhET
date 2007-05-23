package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:42:02 AM
 */
public class GraphSuiteSet {
    protected ArrayList suites = new ArrayList();
    protected CursorModel cursorModel;

    public GraphSuiteSet( CursorModel cursorModel ) {
        this.cursorModel = cursorModel;
    }

    protected Image loadArrow( String s ) {
        try {
            return MotionResources.loadBufferedImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    protected ControlGraph toControlGraph( PhetPCanvas pSwingCanvas, String label, String title,
                                           double min, double max, Color color,
                                           SimulationVariable simulationVariable, PNode thumb, boolean editable, final CursorModel cursorModel,
                                           final MotionModel rotationModel ) {
        final MotionControlGraph motionControlGraph = new MotionControlGraph( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb, rotationModel, editable, cursorModel );
        motionControlGraph.addListener( new MotionControlGraph.Listener() {
            public void horizontalZoomChanged() {
                MinimizableControlGraph[] graphs = getAllGraphs();
                for( int i = 0; i < graphs.length; i++ ) {
                    graphs[i].getControlGraph().setDomainUpperBound( motionControlGraph.getMaxDataX() );
                }
            }
        } );
        return motionControlGraph;
    }

    public MinimizableControlGraph[] getAllGraphs() {
        ArrayList graphs = new ArrayList();
        for( int i = 0; i < suites.size(); i++ ) {
            GraphSuite graphSuite = (GraphSuite)suites.get( i );
            for( int k = 0; k < graphSuite.getGraphComponentCount(); k++ ) {
                if( !graphs.contains( graphSuite.getGraphComponent( k ) ) ) {
                    graphs.add( graphSuite.getGraphComponent( k ) );
                }
            }
        }
        return (MinimizableControlGraph[])graphs.toArray( new MinimizableControlGraph[0] );
    }

    public GraphSuite getGraphSuite( int i ) {
        return (GraphSuite)suites.get( i );
    }


    public void addGraphSuite( GraphSuite graphSuite ) {
        suites.add( graphSuite );
    }

    public void clear() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            graphs[i].clear();
        }
    }

    public int getNumGraphSuites() {
        return suites.size();
    }
}
