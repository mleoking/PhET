/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 
 * ToolboxNode is the toolbox. It contains a collection of tools, 
 * positioned on a background, with a title tab in the upper left corner.
 * The origin of this node is at the upper-left corner of the tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolboxNode extends PNode {
    
    // spacing properties
    private static final int VERTICAL_LABEL_SPACING = 2; // vertical space between a tool's icon and label
    private static final int HORIZONTAL_TOOL_SPACING = 15; // horizontal space between tools
    private static final int BACKGROUND_MARGIN = 5; // margin between the background and the tools
    private static final int TAB_MARGIN = 5; // margin between the tab and its title text
    
    // tool properties
    private static final Font TOOL_LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color TOOL_LABEL_COLOR = Color.BLACK;
    
    // background properties
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY; // toolbox background
    private static final Color BACKGROUND_STROKE_COLOR = new Color( 82, 126, 90 ); // green
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 2f );
    private static final double BACKGROUND_CORNER_RADIUS = 10;
    
    // tab properties
    private static final Font TAB_LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color TAB_LABEL_COLOR = Color.BLACK;
    private static final Color TAB_COLOR = BACKGROUND_COLOR;
    private static final Color TAB_STROKE_COLOR = BACKGROUND_STROKE_COLOR;
    private static final Stroke TAB_STROKE = BACKGROUND_STROKE;
    private static final double TAB_CORNER_RADIUS = BACKGROUND_CORNER_RADIUS;
    
    /**
     * ToolNode is a tool in the toolbox.
     */
    private class ToolNode extends PNode {
        
        public ToolNode( Image image, String name ) {
            this( image, name, true /* isDraggable */ );
        }
        
        public ToolNode( Image image, String name, boolean isDraggable ) {
            super();
            
            PImage imageNode = new PImage( image );
            addChild( imageNode );
            
            HTMLNode labelNode = new HTMLNode( name );
            labelNode.setFont( TOOL_LABEL_FONT );
            labelNode.setHTMLColor( TOOL_LABEL_COLOR );
            addChild( labelNode );
            
            if ( imageNode.getWidth() > labelNode.getWidth() ) {
                imageNode.setOffset( 0, 0 );
                labelNode.setOffset( imageNode.getX() + ( imageNode.getWidth() - labelNode.getWidth() ) / 2, imageNode.getY() + imageNode.getHeight() + VERTICAL_LABEL_SPACING );
            }
            else {
                labelNode.setOffset( 0, imageNode.getY() + imageNode.getHeight() + VERTICAL_LABEL_SPACING );
                imageNode.setOffset( labelNode.getX() + ( labelNode.getWidth() - imageNode.getWidth() ) / 2, 0 );
            }
            
            if ( isDraggable ) {
                addInputEventListener( new CursorHandler() );
            }
        }
    }
    
    // tools in the toolbox
    private ToolNode _thermometer;
    private ToolNode _glacialBudgetMeter;
    private ToolNode _tracerFlag;
    private ToolNode _iceThicknessTool;
    private ToolNode _boreholeDrill;
    private ToolNode _trashCan;
    
    /**
     * Constructor.
     */
    public ToolboxNode() {
        super();
        
        // create tools, under a common parent
        PNode toolsParent = new PNode();
        {
            _thermometer = new ToolNode( GlaciersImages.TOOLBOX_THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER );
            _glacialBudgetMeter = new ToolNode( GlaciersImages.TOOLBOX_GLACIAL_BUDGET_METER, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER );
            _tracerFlag = new ToolNode( GlaciersImages.TOOLBOX_TRACER_FLAG, GlaciersStrings.TOOLBOX_GLACIAL_BUDGET_METER );
            _iceThicknessTool = new ToolNode( GlaciersImages.TOOLBOX_ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL );
            _boreholeDrill = new ToolNode( GlaciersImages.TOOLBOX_BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL );
            _trashCan = new ToolNode( GlaciersImages.TOOLBOX_TRASH_CAN, GlaciersStrings.TOOLBOX_TRASH_CAN, false );
            
            toolsParent.addChild( _thermometer );
            toolsParent.addChild( _glacialBudgetMeter );
            toolsParent.addChild( _tracerFlag );
            toolsParent.addChild( _iceThicknessTool );
            toolsParent.addChild( _boreholeDrill );
            toolsParent.addChild( _trashCan );
            final double maxToolHeight = toolsParent.getFullBoundsReference().getHeight();
            
            // arrange tools in the toolbox from left to right, vertically centered
            double x = 0;
            double y = ( maxToolHeight - _thermometer.getFullBoundsReference().getHeight() ) / 2;
            _thermometer.setOffset( x, y );
            
            x = _thermometer.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - _glacialBudgetMeter.getFullBoundsReference().getHeight() ) / 2;
            _glacialBudgetMeter.setOffset( x, y );
            
            x = _glacialBudgetMeter.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - _tracerFlag.getFullBoundsReference().getHeight() ) / 2;
            _tracerFlag.setOffset( x, y );
            
            x = _tracerFlag.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - _iceThicknessTool.getFullBoundsReference().getHeight() ) / 2;
            _iceThicknessTool.setOffset( x, y );
            
            x = _iceThicknessTool.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - _boreholeDrill.getFullBoundsReference().getHeight() ) / 2;
            _boreholeDrill.setOffset( x, y );
            
            x = _boreholeDrill.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - _trashCan.getFullBoundsReference().getHeight() ) / 2;
            _trashCan.setOffset( x, y );
        }
        
        // create the background
        PPath backgroundNode = new PPath();
        {
            final double backgroundWidth = toolsParent.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_MARGIN );
            final double backgroundHeight = toolsParent.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_MARGIN );
            RoundRectangle2D r = new RoundRectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight, BACKGROUND_CORNER_RADIUS, BACKGROUND_CORNER_RADIUS );
            backgroundNode.setPathTo( r );
            backgroundNode.setPaint( BACKGROUND_COLOR );
            backgroundNode.setStroke( BACKGROUND_STROKE );
            backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        }
        
        // create the title tab
        PComposite tabNode = new PComposite();
        final double tabOverlap = 100;
        {
            PText titleNode = new PText( GlaciersStrings.TITLE_TOOLBOX );
            titleNode.setFont( TAB_LABEL_FONT );
            titleNode.setTextPaint( TAB_LABEL_COLOR );
            
            final double tabWidth = titleNode.getFullBoundsReference().getWidth() + ( 2 * TAB_MARGIN );
            final double tabHeight = titleNode.getFullBoundsReference().getHeight() + ( 2 * TAB_MARGIN ) + tabOverlap;
            RoundRectangle2D r = new RoundRectangle2D.Double( 0, 0, tabWidth, tabHeight, TAB_CORNER_RADIUS, TAB_CORNER_RADIUS );
            PPath pathNode = new PPath( r );
            pathNode.setPaint( TAB_COLOR );
            pathNode.setStroke( TAB_STROKE );
            pathNode.setStrokePaint( TAB_STROKE_COLOR );
            
            tabNode.addChild( pathNode );
            tabNode.addChild( titleNode );
            
            pathNode.setOffset( 0, 0 );
            titleNode.setOffset( TAB_MARGIN, TAB_MARGIN );
        }
       
        addChild( tabNode );
        addChild( backgroundNode );
        addChild( toolsParent );
        
        // origin at upper left corner of tab
        tabNode.setOffset( 0, 0 );
        backgroundNode.setOffset( tabNode.getFullBounds().getX(), tabNode.getFullBounds().getMaxY() - tabOverlap );
        toolsParent.setOffset( backgroundNode.getFullBounds().getX() + BACKGROUND_MARGIN, backgroundNode.getFullBounds().getY() + BACKGROUND_MARGIN );
        
        // only the tools are interactive
        this.setPickable( false );
        toolsParent.setPickable( false );
        backgroundNode.setPickable( false );
        backgroundNode.setChildrenPickable( false );
        tabNode.setPickable( false );
        tabNode.setChildrenPickable( false );
    }
    
    public PNode getThermometer() {
        return _thermometer;
    }
    
    public PNode getGlacialBudgetMeter() {
        return _glacialBudgetMeter;
    }

    public PNode getTracerFlag() {
        return _tracerFlag;
    }
    
    public PNode getIceThicknessTool() {
        return _iceThicknessTool;
    }
    
    public PNode getBoreholeDrill() {
        return _boreholeDrill;
    }
    
    public PNode getTrashCan() {
        return _trashCan;
    }
}
