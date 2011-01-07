// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.MotionResources;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:42:02 AM
 */
public class GraphSuiteSet {
    protected ArrayList suites = new ArrayList();
    private MotionControlGraph.Listener zoomListener = new MotionControlGraph.Listener() {
        public void horizontalZoomChanged( MotionControlGraph source ) {
            setDomainUpperBound( source.getMaxDataX() );
        }
    };

    public static BufferedImage loadArrow( String s ) {
        try {
            return MotionResources.loadBufferedImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public void setDomainUpperBound( double bound ) {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().setDomainUpperBound( bound );
        }
    }

    public MinimizableControlGraph[] getAllGraphs() {
        ArrayList graphs = new ArrayList();
        for ( int i = 0; i < suites.size(); i++ ) {
            GraphSuite graphSuite = (GraphSuite) suites.get( i );
            for ( int k = 0; k < graphSuite.getGraphComponentCount(); k++ ) {
                if ( !graphs.contains( graphSuite.getGraphComponent( k ) ) ) {
                    graphs.add( graphSuite.getGraphComponent( k ) );
                }
            }
        }
        return (MinimizableControlGraph[]) graphs.toArray( new MinimizableControlGraph[0] );
    }

    public GraphSuite getGraphSuite( int i ) {
        return (GraphSuite) suites.get( i );
    }

    public void addGraphSuite( MinimizableControlGraph[] graphs ) {
        addGraphSuite( new GraphSuite( graphs ) );
    }

    public void addGraphSuite( GraphSuite graphSuite ) {
        suites.add( graphSuite );
        updateListeners();
    }

    private void updateListeners() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            MinimizableControlGraph graph = graphs[i];
            if ( graph.getControlGraph() instanceof MotionControlGraph ) {
                MotionControlGraph motionControlGraph = (MotionControlGraph) graph.getControlGraph();
                if ( !motionControlGraph.hasListener( zoomListener ) ) {
                    motionControlGraph.addListener( zoomListener );
                }
            }
        }
    }

    public void clear() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].clear();
        }
        forceUpdateAll();
    }

    public int getNumGraphSuites() {
        return suites.size();
    }

    public void forceUpdateAll() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].forceUpdate();
        }
    }

    public void resetAll() {
        resetRange();
        maximizeAll();
        clear();
    }

    private void resetRange() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].resetRange();
        }
    }

    private void maximizeAll() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            graphs[i].setMinimized( false );
        }
    }
}
