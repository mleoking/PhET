/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


public abstract class ToolIconNode extends IconNode {
    
    public static class ThermometerIconNode extends ToolIconNode {
        public ThermometerIconNode( IToolProducer toolProducer ) {
            super( GlaciersImages.TOOLBOX_THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER, toolProducer );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addThermometer( position );
        }
    }
    
    public static class GlacialBudgetMeterIconNode extends ToolIconNode {
        public GlacialBudgetMeterIconNode( IToolProducer toolProducer  ) {
            super( GlaciersImages.TOOLBOX_GLACIAL_BUDGET_METER, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER, toolProducer );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addGlacialBudgetMeter( position );
        }
    }
    
    public static class TracerFlagIconNode extends ToolIconNode {
        public TracerFlagIconNode( IToolProducer toolProducer  ) {
            super( GlaciersImages.TOOLBOX_TRACER_FLAG, GlaciersStrings.TOOLBOX_TRACER_FLAG, toolProducer );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addTracerFlag( position );
        }
    }
    
    public static class IceThicknessToolIconNode extends ToolIconNode {
        public IceThicknessToolIconNode( IToolProducer toolProducer  ) {
            super( GlaciersImages.TOOLBOX_ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL, toolProducer );
        }
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addIceThicknessTool( position );
        }
    }
    
    public static class BoreholeDrillIconNode extends ToolIconNode {
        public BoreholeDrillIconNode( IToolProducer toolProducer  ) {
            super( GlaciersImages.TOOLBOX_BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL, toolProducer );
        }
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addBoreholeDrill( position );
        }
    }
    
    public static class GPSReceiverIconNode extends ToolIconNode {
        public GPSReceiverIconNode( IToolProducer toolProducer  ) {
            super( GlaciersImages.TOOLBOX_GPS_RECEIVER, GlaciersStrings.TOOLBOX_GPS_RECEIVER, toolProducer );
        }
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().createGPSReceiver( position );
        }
    }
    
    private IToolProducer _toolProducer;
    private AbstractTool _tool; // tool model element created during initial click and drag
    
    public ToolIconNode( Image image, String name, IToolProducer toolProducer ) {
        super( image, name );

        _toolProducer = toolProducer;

        addInputEventListener( new CursorHandler() );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                _tool = createTool( event.getCanvasPosition() );
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
    
    protected IToolProducer getToolProducer() {
        return _toolProducer;
    }
    
    protected abstract AbstractTool createTool( Point2D position );
}
