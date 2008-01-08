/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.colorado.phet.glaciers.model.GPS;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


public abstract class ToolIconNode extends IconNode {
    
    public static class ThermometerIconNode extends ToolIconNode {
        public ThermometerIconNode() {
            super( GlaciersImages.TOOLBOX_THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new Thermometer( position );
        }
    }
    
    public static class GlacialBudgetMeterIconNode extends ToolIconNode {
        public GlacialBudgetMeterIconNode() {
            super( GlaciersImages.TOOLBOX_GLACIAL_BUDGET_METER, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new GlacialBudgetMeter( position );
        }
    }
    
    public static class TracerFlagIconNode extends ToolIconNode {
        public TracerFlagIconNode() {
            super( GlaciersImages.TOOLBOX_TRACER_FLAG, GlaciersStrings.TOOLBOX_TRACER_FLAG );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new TracerFlag( position );
        }
    }
    
    public static class IceThicknessToolIconNode extends ToolIconNode {
        public IceThicknessToolIconNode() {
            super( GlaciersImages.TOOLBOX_ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new IceThicknessTool( position );
        }
    }
    
    public static class BoreholeDrillIconNode extends ToolIconNode {
        public BoreholeDrillIconNode() {
            super( GlaciersImages.TOOLBOX_BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new BoreholeDrill( position );
        }
    }
    
    public static class GPSIconNode extends ToolIconNode {
        public GPSIconNode() {
            super( GlaciersImages.TOOLBOX_GPS, GlaciersStrings.TOOLBOX_GPS );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new GPS( position );
        }
    }
    
    private AbstractTool _tool; // tool model element created during initial click and drag
    private ArrayList _listeners;
    
    public ToolIconNode( Image image, String name ) {
        super( image, name );

        _listeners = new ArrayList();

        addInputEventListener( new CursorHandler() );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                _tool = createTool( event.getCanvasPosition() );
                notifyAddTool( _tool );
            }
        } );
        
        addInputEventListener( new PDragEventHandler() {

            protected void drag( PInputEvent event ) {
                if ( _tool != null ) {
                    _tool.setPosition( event.getPosition() );
                }
            }

            protected void endDrag( PInputEvent event ) {
                _tool = null;
            }
        } );
    }
    
    protected abstract AbstractTool createTool( Point2D position );
    
    /**
     * Interface implemented by all listeners who are interested in events related tool icons.
     */
    public static interface ToolIconListener {
        public void addTool( AbstractTool tool );
    }
    
    public void addListener( ToolIconListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( ToolIconListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyAddTool( AbstractTool tool ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolIconListener) i.next() ).addTool( tool );
        }
    }
}
