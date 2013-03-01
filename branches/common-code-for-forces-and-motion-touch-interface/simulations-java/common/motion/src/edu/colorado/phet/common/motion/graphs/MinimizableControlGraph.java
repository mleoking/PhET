// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

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

    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private ControlGraph controlGraph;
    private PSwing closeButton;
    private final int buttonInsetX = 3;

    public MinimizableControlGraph( String label, ControlGraph controlGraph ) {
        this( label, controlGraph, false );
    }

    public MinimizableControlGraph( String label, ControlGraph controlGraph, boolean minimized ) {
        this.label = label;

        graphChild = new PNode();
        stubChild = new PNode();

        this.controlGraph = controlGraph;
        graphChild.addChild( controlGraph );

        JButton minimizeButton = new JButton();
        try {
            minimizeButton.setIcon( new ImageIcon( MotionResources.loadBufferedImage( "minimizeButton.png" ) ) );
        }
        catch ( IOException e ) {
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

        JButton maximizeButton = new JButton( label );
        maximizeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
        try {
            maximizeButton.setIcon( new ImageIcon( MotionResources.loadBufferedImage( "maximizeButton.png" ) ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        PSwing maxButton = new PSwing( maximizeButton );
        maxButton.addInputEventListener( new CursorHandler() );
        stubChild.addChild( maxButton );

        relayout();
        setMinimized( minimized );
    }

    public ControlGraph getControlGraph() {
        return controlGraph;
    }

    private void updateCloseButton() {
        controlGraph.getJFreeChartNode().updateChartRenderingInfo();
        closeButton.setOffset( getButtonMaxX() - closeButton.getFullBounds().getWidth(), controlGraph.getJFreeChartNode().getDataArea().getY() );
    }

    private double getButtonMaxX() {
        return controlGraph.getJFreeChartNode().getDataArea().getMaxX() - buttonInsetX + controlGraph.getJFreeChartNode().getOffset().getX();
    }

    public void setMinimized( boolean b ) {
        if ( this.minimized != b ) {
            this.minimized = b;
            if ( minimized ) {
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
        stubChild.setOffset( getButtonMaxX() - stubChild.getFullBounds().getWidth(), controlGraph.getJFreeChartNode().getDataArea().getY() );
    }

    public double getFixedHeight() {
        return minimized ? stubChild.getFullBounds().getHeight() : 0.0;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void addControlGraphListener( ControlGraph.Listener listener ) {
        controlGraph.addListener( listener );
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

    public void forceUpdate() {
        controlGraph.getDynamicJFreeChartNode().forceUpdateAll();
    }

    public void resetRange() {
        controlGraph.resetRange();
    }

    public static interface Listener {
        void minimizeStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for ( Listener listener : listeners ) {
            listener.minimizeStateChanged();
        }
    }

    public void addSeries( ControlGraphSeries series ) {
        getControlGraph().addSeries( series );
    }

    public void addControl( JComponent jComponent ) {
        getControlGraph().addControl( jComponent );
    }

    public PSwing getCloseButton() {
        return closeButton;
    }
}
