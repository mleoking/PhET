/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.control.ToolIconNode.BoreholeDrillIconNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.GPSReceiverIconNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.GlacialBudgetMeterIconNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.IceThicknessToolIconNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.ThermometerIconNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.ToolIconListener;
import edu.colorado.phet.glaciers.control.ToolIconNode.TracerFlagIconNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 
 * ToolboxNode is the toolbox. It contains a collection of icons, 
 * positioned on a background, with a title tab in the upper left corner.
 * The origin of this node is at the upper-left corner of the tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolboxNode extends PNode {
    
    // spacing properties
    private static final int HORIZONTAL_ICON_SPACING = 15; // horizontal space between icons
    private static final int BACKGROUND_MARGIN = 5; // margin between the background and the icons
    private static final int TAB_MARGIN = 5; // margin between the tab and its title text
    
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
    
    private ArrayList _iconNodes; // array of IconNode
    
    /**
     * Constructor.
     */
    public ToolboxNode() {
        super();
        
        _iconNodes = new ArrayList();
        
        // create icons, under a common parent
        PNode iconsParentNode = new PNode();
        {
            _iconNodes.add( new ThermometerIconNode() );
            _iconNodes.add( new GlacialBudgetMeterIconNode() );
            _iconNodes.add( new TracerFlagIconNode() );
            _iconNodes.add( new IceThicknessToolIconNode() );
            _iconNodes.add( new BoreholeDrillIconNode() );
            if ( GlaciersApplication.isDeveloperControlsEnabled() ) {
                _iconNodes.add( new GPSReceiverIconNode() );
            }
            _iconNodes.add( new TrashCanNode() );
            layoutIcons( _iconNodes, iconsParentNode );
        }
        
        // create the background
        PPath backgroundNode = new PPath();
        {
            final double backgroundWidth = iconsParentNode.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_MARGIN );
            final double backgroundHeight = iconsParentNode.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_MARGIN );
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
        addChild( iconsParentNode );
        
        // origin at upper left corner of tab
        tabNode.setOffset( 0, 0 );
        backgroundNode.setOffset( tabNode.getFullBounds().getX(), tabNode.getFullBounds().getMaxY() - tabOverlap );
        iconsParentNode.setOffset( backgroundNode.getFullBounds().getX() + BACKGROUND_MARGIN, backgroundNode.getFullBounds().getY() + BACKGROUND_MARGIN );
        
        // only the tools are interactive
        this.setPickable( false );
        iconsParentNode.setPickable( false );
        backgroundNode.setPickable( false );
        backgroundNode.setChildrenPickable( false );
        tabNode.setPickable( false );
        tabNode.setChildrenPickable( false );
    }
    
    private static void layoutIcons( ArrayList nodes, PNode parentNode ) {
        
        // add all icons to parent, calculate max height
        Iterator i = nodes.iterator();
        while ( i.hasNext() ) {
            IconNode currentNode = (IconNode) i.next();
            parentNode.addChild( currentNode );
        }
        final double maxToolHeight = parentNode.getFullBoundsReference().getHeight();
        
        // arrange icons in the toolbox from left to right, vertically centered
        double x, y;
        PNode previousNode = null;
        Iterator j = nodes.iterator();
        while ( j.hasNext() ) {
            IconNode currentNode = (IconNode) j.next();
            if ( previousNode == null ) {
                x = 0;
                y = ( maxToolHeight - currentNode.getFullBoundsReference().getHeight() ) / 2;
            }
            else {
                x = previousNode.getFullBoundsReference().getMaxX() + HORIZONTAL_ICON_SPACING;
                y = ( maxToolHeight - currentNode.getFullBoundsReference().getHeight() ) / 2; 
            }
            currentNode.setOffset( x, y );
            previousNode = currentNode;
        }
    }
    
    public void addListener( ToolIconListener listener ) {
        Iterator i = _iconNodes.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ToolIconNode ) {
                ( (ToolIconNode) o ).addListener( listener );
            }
        }
    }

    public void removeListener( ToolIconListener listener ) {
        Iterator i = _iconNodes.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ToolIconNode ) {
                ( (ToolIconNode) o ).removeListener( listener );
            }
        }
    }
}
