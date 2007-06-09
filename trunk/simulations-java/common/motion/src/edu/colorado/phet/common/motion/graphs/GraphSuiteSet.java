package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.MotionResources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:42:02 AM
 */
public class GraphSuiteSet {
    protected ArrayList suites = new ArrayList();
    protected CursorModel cursorModel;
    private MotionControlGraph.Listener zoomListener = new MotionControlGraph.Listener() {
        public void horizontalZoomChanged( MotionControlGraph source ) {
            setDomainUpperBound( source.getMaxDataX() );
        }
    };

    public GraphSuiteSet( CursorModel cursorModel ) {
        this.cursorModel = cursorModel;
    }

    public static BufferedImage loadRedArrow() {
        return loadArrow( "red-arrow.png" );
    }

    public static BufferedImage loadGreenArrow() {
        return loadArrow( "green-arrow.png" );
    }

    public static BufferedImage loadBlueArrow() {
        return loadArrow( "blue-arrow.png" );
    }

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
        for( int i = 0; i < graphs.length; i++ ) {
            graphs[i].getControlGraph().setDomainUpperBound( bound );
        }
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

    public void addGraphSuite( MinimizableControlGraph[] graphs ) {
        addGraphSuite( new GraphSuite( graphs ) );
    }

    public void addGraphSuite( GraphSuite graphSuite ) {
        suites.add( graphSuite );
        updateListeners();
    }

    private void updateListeners() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            MinimizableControlGraph graph = graphs[i];
            if( graph.getControlGraph() instanceof MotionControlGraph ) {
                MotionControlGraph motionControlGraph = (MotionControlGraph)graph.getControlGraph();
                if( !motionControlGraph.hasListener( zoomListener ) ) {
                    motionControlGraph.addListener( zoomListener );
                }
            }
        }
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
