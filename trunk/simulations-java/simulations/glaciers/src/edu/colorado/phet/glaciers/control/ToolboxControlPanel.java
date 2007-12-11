/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


public class ToolboxControlPanel extends PNode {

    private ArrayList _listenerList;
    
    public ToolboxControlPanel() {
        super();
        
        _listenerList = new ArrayList();
        
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
     * Interface implemented by all listeners who are interested in toolbox events.
     */
    public static interface ToolboxListener {
        public void addThermometer( Point2D atCanvasPosition );
        public void addGlacialBudgetMeter( Point2D atCanvasPosition );
        public void addTracerFlag( Point2D atCanvasPosition );
        public void addIceThicknessTool( Point2D atCanvasPosition );
        public void addBoreholeDrill( Point2D atCanvasPosition );
    }
    
    /**
     * Default implementation of ToolboxListener.
     */
    public static class ToolboxAdapter implements ToolboxListener {
        public void addThermometer( Point2D atCanvasPosition ) {};
        public void addGlacialBudgetMeter( Point2D atCanvasPosition ) {};
        public void addTracerFlag( Point2D atCanvasPosition ) {};
        public void addIceThicknessTool( Point2D atCanvasPosition ) {};
        public void addBoreholeDrill( Point2D atCanvasPosition ) {};
    }
    
    /**
     * Adds a ToolboxListener.
     * @param listener
     */
    public void addListener( ToolboxListener listener ) {
        _listenerList.add( listener );
    }
    
    /**
     * Removes a ToolboxListener.
     * @param listener
     */
    public void removeListener( ToolboxListener listener ) {
        _listenerList.remove( listener );
    }
    
    private void notifyAddThermometer( Point2D atCanvasPosition ) {
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxListener) i.next() ).addThermometer( atCanvasPosition );
        }
    }
    
    private void notifyAddGlacialBudgetMeter( Point2D atCanvasPosition ) {
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxListener) i.next() ).addGlacialBudgetMeter( atCanvasPosition );
        }
    }
    
    private void notifyAddTraceFlag( Point2D atCanvasPosition ) {
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxListener) i.next() ).addTracerFlag( atCanvasPosition );
        }
    }
    
    private void notifyAddIceThicknessTool( Point2D atCanvasPosition ) {
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxListener) i.next() ).addIceThicknessTool( atCanvasPosition );
        }
    }
    
    private void notifyAddBoreholeDrill( Point2D atCanvasPosition ) {
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (ToolboxListener) i.next() ).addBoreholeDrill( atCanvasPosition );
        }
    }
}
