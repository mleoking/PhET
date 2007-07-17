package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:22:10 AM
 */
//Decorator pattern?
public class MinimizableControlGraph extends PNode {
    private String label;
    private boolean minimized = false;
    private PNode graphChild;
    private PNode stubChild;

    private ArrayList listeners = new ArrayList();
    private ControlGraph controlGraph;
    private PSwing closeButton;

    public MinimizableControlGraph( String label, ControlGraph controlGraph ) {
        this.label = label;

        graphChild = new PNode();
        stubChild = new PNode();

        this.controlGraph = controlGraph;
        graphChild.addChild( controlGraph );

        JButton minimizeButton = new JButton();
        try {
            minimizeButton.setIcon( new ImageIcon( MotionResources.loadBufferedImage( "minimizeButton.png" ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        minimizeButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        minimizeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( true );
            }
        } );
        closeButton = new PSwing( minimizeButton );
        closeButton.addInputEventListener( new CursorHandler() );

        graphChild.addChild( closeButton );

        addChild( graphChild );

        JButton maximizeButton = new JButton( "Maximize " + label + " graph" );
        maximizeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
        try {
            maximizeButton.setIcon( new ImageIcon( MotionResources.loadBufferedImage( "maximizeButton.png" ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        PSwing maxButton = new PSwing( maximizeButton );
        maxButton.addInputEventListener( new CursorHandler() );
        stubChild.addChild( maxButton );

        relayout();
    }

    public ControlGraph getControlGraph() {
        return controlGraph;
    }

    private void updateCloseButton() {
        int buttonInsetX = 3;
        int buttonInsetY = 3;

        controlGraph.getJFreeChartNode().updateChartRenderingInfo();
        closeButton.setOffset( controlGraph.getJFreeChartNode().getDataArea().getMaxX() - closeButton.getFullBounds().getWidth() - buttonInsetX + controlGraph.getJFreeChartNode().getOffset().getX(), controlGraph.getJFreeChartNode().getDataArea().getY() );
    }

    private void setMinimized( boolean b ) {
        if( this.minimized != b ) {
            this.minimized = b;
            if( minimized ) {
                removeChild( graphChild );
                addChild( stubChild );
            }
            else {
                removeChild( stubChild );
                addChild( graphChild );
            }
            relayout();
            notifyListeners();
        }

    }

    public String getLabel() {
        return label;
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setAvailableBounds( double width, double height ) {
        controlGraph.setBounds( 0, 0, width, height );
        relayout();
    }

    private void relayout() {
        updateCloseButton();
        stubChild.setOffset( controlGraph.getFullBounds().getMaxX() - stubChild.getFullBounds().getWidth(), 0 );
    }

    public double getFixedHeight() {
        return minimized ? stubChild.getFullBounds().getHeight() : 0.0;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void addValue( double time, double value ) {
        controlGraph.addValue( time, value );
    }

    public void addControlGraphListener( ControlGraph.Listener listener ) {
        controlGraph.addListener( listener );
    }

    public void addValue( int series, double time, double value ) {
        controlGraph.addValue( series, time, value );
    }

    public void clear() {
        controlGraph.clear();
    }

    public void setFlowLayout() {
        controlGraph.setFlowLayout();
    }

    public void setAlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
        controlGraph.setAlignedLayout( minimizableControlGraphs );
    }

    public void relayoutControlGraph() {
        controlGraph.relayout();
    }

    public void addControl( JComponent component ) {
        controlGraph.addControl( component );
    }

    public void forceUpdate() {
        controlGraph.getDynamicJFreeChartNode().forceUpdateAll();
    }

    public void addSeries( ControlGraphSeries controlGraphSeries ) {
        controlGraph.addSeries( controlGraphSeries );
    }

    public static interface Listener {
        void minimizeStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.minimizeStateChanged();
        }
    }

    public void addSeries( String title, Color color, String abbr, String units,ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, Stroke stroke ) {
        controlGraph.addSeries( title, color, abbr, units, simulationVariable, observableTimeSeries, stroke );
    }
}
