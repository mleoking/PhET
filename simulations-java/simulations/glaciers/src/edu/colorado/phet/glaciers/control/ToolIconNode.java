/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.GPSReceiverNode;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * ToolIconNode is the base class for all tool icons in the toolbox.
 * When a tool icon receives a mouse press, a corresponding tool is created in the model.
 * As long as the mouse remains pressed, drag events are used to change the tool's position.
 * <p>
 * This class contains an inner subclass for each type of tool in the toolbox.
 * Each subclass knows about its image and text label, and what type of tool to create.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ToolIconNode extends IconNode {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IToolProducer _toolProducer;
    private ModelViewTransform _mvt;
    private AbstractTool _tool; // tool model element created during initial click and drag
    private Point2D _pModel; // reusable point for model-view transforms
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param image image displayed on the icon
     * @param html HTML text, centered under image
     * @param toolProducer object capable of creating tools
     * @param mvt model-view transform, used to convert mouse position to tool position
     */
    public ToolIconNode( Image image, String html, IToolProducer toolProducer, ModelViewTransform mvt ) {
        super( image, html );

        _toolProducer = toolProducer;
        _mvt = mvt;
        _pModel = new Point2D.Double();

        addInputEventListener( new CursorHandler() );

        addInputEventListener( new PDragEventHandler() {

            /* When the drag starts, create the new tool. */
            protected void startDrag( PInputEvent event ) {
                _mvt.viewToModel( event.getPosition(), _pModel );
                _tool = createTool( _pModel );
                super.startDrag( event );
            }
            
            /* During the drag, set the position of the new tool. */ 
            protected void drag( PInputEvent event ) {

                if ( _tool != null ) {
                    _mvt.viewToModel( event.getPosition(), _pModel );
                    _tool.setPosition( _pModel );
                }
            }

            /* When the drag ends, we release control of the tool. */
            protected void endDrag( PInputEvent event ) {
                _tool = null;
                super.endDrag( event );
            }
        } );
    }
    
    /*
     * Provides access to tool producer for subclasses.
     */
    protected IToolProducer getToolProducer() {
        return _toolProducer;
    }
    
    /*
     * Creates the appropriate tool at the specified position.
     * This method is implemented by each subclass.
     * 
     * @param position position in model coordinates
     */
    protected abstract AbstractTool createTool( Point2D position );
    
    //----------------------------------------------------------------------------
    // Subclasses for each tool type
    //----------------------------------------------------------------------------
    
    /**
     * ThermometerIconNode
     */
    public static class ThermometerIconNode extends ToolIconNode {
        
        public ThermometerIconNode( IToolProducer toolProducer, ModelViewTransform mvt ) {
            super( GlaciersImages.THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER, toolProducer, mvt );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addThermometer( position );
        }
    }
    
    /**
     * GlacialBudgetMeterIconNode
     */
    public static class GlacialBudgetMeterIconNode extends ToolIconNode {
        
        public GlacialBudgetMeterIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
            super( GlaciersImages.GLACIAL_BUDGET_METER, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER, toolProducer, mvt );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addGlacialBudgetMeter( position );
        }
    }
    
    /**
     * TracerFlagIconNode
     */
    public static class TracerFlagIconNode extends ToolIconNode {
        
        public TracerFlagIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
            super( GlaciersImages.TRACER_FLAG, GlaciersStrings.TOOLBOX_TRACER_FLAG, toolProducer, mvt );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addTracerFlag( position );
        }
    }
    
    /**
     * IceThicknessToolIconNode
     */
    public static class IceThicknessToolIconNode extends ToolIconNode {
        
        public IceThicknessToolIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
            super( GlaciersImages.ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL, toolProducer, mvt );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addIceThicknessTool( position );
        }
    }
    
    /**
     * BoreholeDrillIconNode
     */
    public static class BoreholeDrillIconNode extends ToolIconNode {
        
        public BoreholeDrillIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
            super( GlaciersImages.BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL, toolProducer, mvt );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().addBoreholeDrill( position );
        }
    }
    
    /**
     * GPSReceiverIconNode
     */
    public static class GPSReceiverIconNode extends ToolIconNode {
        
        public GPSReceiverIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
            super( GPSReceiverNode.createImage(), GlaciersStrings.TOOLBOX_GPS_RECEIVER, toolProducer, mvt );
        }
        
        public AbstractTool createTool( Point2D position ) {
            return getToolProducer().createGPSReceiver( position );
        }
    }
}
