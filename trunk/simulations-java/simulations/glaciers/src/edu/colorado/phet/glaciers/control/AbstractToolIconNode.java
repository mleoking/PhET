/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;


public abstract class AbstractToolIconNode extends PNode {
    
    public static class ThermometerIconNode extends AbstractToolIconNode {
        public ThermometerIconNode() {
            super( GlaciersImages.TOOLBOX_THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new Thermometer( position );
        }
    }
    
    public static class GlacialBudgetMeterIconNode extends AbstractToolIconNode {
        public GlacialBudgetMeterIconNode() {
            super( GlaciersImages.TOOLBOX_GLACIAL_BUDGET_METER, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new GlacialBudgetMeter( position );
        }
    }
    
    public static class TracerFlagIconNode extends AbstractToolIconNode {
        public TracerFlagIconNode() {
            super( GlaciersImages.TOOLBOX_TRACER_FLAG, GlaciersStrings.TOOLBOX_TRACER_FLAG );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new TracerFlag( position );
        }
    }
    
    public static class IceThicknessToolIconNode extends AbstractToolIconNode {
        public IceThicknessToolIconNode() {
            super( GlaciersImages.TOOLBOX_ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new IceThicknessTool( position );
        }
    }
    
    public static class BoreholeDrillIconNode extends AbstractToolIconNode {
        public BoreholeDrillIconNode() {
            super( GlaciersImages.TOOLBOX_BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return new BoreholeDrill( position );
        }
    }
    
    public static class TrashCanIconNode extends AbstractToolIconNode {
        public TrashCanIconNode() {
            super( GlaciersImages.TOOLBOX_TRASH_CAN, GlaciersStrings.TOOLBOX_TRASH_CAN, false /* isDraggable */ );
        }
        
        protected AbstractTool createTool( Point2D position ) {
            return null; //XXX
        }
    }
    
    private static final int VERTICAL_SPACING = 2; // vertical space between a tool's icon and label
    private static final Font LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    private AbstractTool _tool;
    private ArrayList _listeners;
    
    public AbstractToolIconNode( Image image, String name ) {
        this( image, name, true /* isDraggable */ );
    }
    
    public AbstractToolIconNode( Image image, String name, boolean isDraggable ) {
        super();
        
        _listeners = new ArrayList();
        
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        HTMLNode labelNode = new HTMLNode( name );
        labelNode.setFont( LABEL_FONT );
        labelNode.setHTMLColor( LABEL_COLOR );
        addChild( labelNode );
        
        if ( imageNode.getWidth() > labelNode.getWidth() ) {
            imageNode.setOffset( 0, 0 );
            labelNode.setOffset( imageNode.getX() + ( imageNode.getWidth() - labelNode.getWidth() ) / 2, imageNode.getY() + imageNode.getHeight() + VERTICAL_SPACING );
        }
        else {
            labelNode.setOffset( 0, imageNode.getY() + imageNode.getHeight() + VERTICAL_SPACING );
            imageNode.setOffset( labelNode.getX() + ( labelNode.getWidth() - imageNode.getWidth() ) / 2, 0 );
        }
        
        if ( isDraggable ) {
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
            });
        }
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
