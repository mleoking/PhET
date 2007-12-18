/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


public class ToolboxControlPanel extends PNode {

    private ArrayList _listeners;
    
    public ToolboxControlPanel() {
        super();
        
        _listeners = new ArrayList();
        
        ToolboxNode toolboxNode = new ToolboxNode();
        addChild( toolboxNode );
        
        toolboxNode.getThermometer().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyAddThermometer( event.getCanvasPosition() );
            }
        } );
        
        toolboxNode.getGlacialBudgetMeter().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyAddGlacialBudgetMeter( event.getCanvasPosition() );
            }
        } );

        toolboxNode.getTracerFlag().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyAddTraceFlag( event.getCanvasPosition() );
            }
        } );

        toolboxNode.getIceThicknessTool().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyAddIceThicknessTool( event.getCanvasPosition() );
            }
        } );

        toolboxNode.getBoreholeDrill().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyAddBoreholeDrill( event.getCanvasPosition() );
            }
        } );
    }
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public static interface ToolboxControlPanelListener {
        public void addThermometer( Point2D atCanvasPosition );
        public void addGlacialBudgetMeter( Point2D atCanvasPosition );
        public void addTracerFlag( Point2D atCanvasPosition );
        public void addIceThicknessTool( Point2D atCanvasPosition );
        public void addBoreholeDrill( Point2D atCanvasPosition );
    }
    
    public static class ToolboxControlPanelAdapter implements ToolboxControlPanelListener {
        public void addThermometer( Point2D atCanvasPosition ) {};
        public void addGlacialBudgetMeter( Point2D atCanvasPosition ) {};
        public void addTracerFlag( Point2D atCanvasPosition ) {};
        public void addIceThicknessTool( Point2D atCanvasPosition ) {};
        public void addBoreholeDrill( Point2D atCanvasPosition ) {};
    }
    
    public void addListener( ToolboxControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( ToolboxControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyAddThermometer( Point2D atCanvasPosition ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxControlPanelListener) i.next() ).addThermometer( atCanvasPosition );
        }
    }
    
    private void notifyAddGlacialBudgetMeter( Point2D atCanvasPosition ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxControlPanelListener) i.next() ).addGlacialBudgetMeter( atCanvasPosition );
        }
    }
    
    private void notifyAddTraceFlag( Point2D atCanvasPosition ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxControlPanelListener) i.next() ).addTracerFlag( atCanvasPosition );
        }
    }
    
    private void notifyAddIceThicknessTool( Point2D atCanvasPosition ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxControlPanelListener) i.next() ).addIceThicknessTool( atCanvasPosition );
        }
    }
    
    private void notifyAddBoreholeDrill( Point2D atCanvasPosition ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxControlPanelListener) i.next() ).addBoreholeDrill( atCanvasPosition );
        }
    }
}
