/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;


public abstract class AbstractToolIconNode extends PNode {
    
    private static final int VERTICAL_SPACING = 2; // vertical space between a tool's icon and label
    private static final Font LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    private AbstractTool _dragTool;
    
    public AbstractToolIconNode( Image image, String name ) {
        this( image, name, true /* isDraggable */ );
    }
    
    public AbstractToolIconNode( Image image, String name, boolean isDraggable ) {
        super();
        
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
            addInputEventListener( new PDragEventHandler() {
                protected void drag( PInputEvent event ) {
                    if ( _dragTool != null ) {
                        _dragTool.setPosition( event.getPosition() );
                    }
                }
                protected void endDrag( PInputEvent event ) {
                    _dragTool = null;
                }
            });
        }
    }
    
    /**
     * Sets the tool model element that will be dragged when this node receives drag events.
     * 
     * @param dragTool
     */
    public void setDragTool( AbstractTool dragTool ) {
        _dragTool = dragTool;
    }
    
    public static class ThermometerIconNode extends AbstractToolIconNode {
        public ThermometerIconNode() {
            super( GlaciersImages.TOOLBOX_THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER );
        }
    }
    
    public static class GlacialBudgetMeterIconNode extends AbstractToolIconNode {
        public GlacialBudgetMeterIconNode() {
            super( GlaciersImages.TOOLBOX_GLACIAL_BUDGET_METER, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER );
        }
    }
    
    public static class TracerFlagIconNode extends AbstractToolIconNode {
        public TracerFlagIconNode() {
            super( GlaciersImages.TOOLBOX_TRACER_FLAG, GlaciersStrings.TOOLBOX_TRACER_FLAG );
        }
    }
    
    public static class IceThicknessToolIconNode extends AbstractToolIconNode {
        public IceThicknessToolIconNode() {
            super( GlaciersImages.TOOLBOX_ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL );
        }
    }
    
    public static class BoreholeDrillIconNode extends AbstractToolIconNode {
        public BoreholeDrillIconNode() {
            super( GlaciersImages.TOOLBOX_BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL );
        }
    }
    
    public static class TrashCanIconNode extends AbstractToolIconNode {
        public TrashCanIconNode() {
            super( GlaciersImages.TOOLBOX_TRASH_CAN, GlaciersStrings.TOOLBOX_TRASH_CAN, false );
        }
    }
}
