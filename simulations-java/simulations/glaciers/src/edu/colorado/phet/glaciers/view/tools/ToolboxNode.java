/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 
 * ToolboxNode is the toolbox. It contains a collection of icons, 
 * positioned on a background, with a title tab in the upper left corner.
 * The origin of this node is at the upper-left corner of the tab.
 * <p>
 * When an icon is clicked in the toolbox, the icon asked a specified "tool producer"
 * to create a tool (model and view). The tool icon then handles the initial
 * dragging of from the toolbox to the world.  See ToolIconNode.  
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolboxNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
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
    private static final Font TAB_LABEL_FONT = new PhetFont( 12 );
    private static final Color TAB_LABEL_COLOR = Color.BLACK;
    private static final Color TAB_COLOR = BACKGROUND_COLOR;
    private static final Color TAB_STROKE_COLOR = BACKGROUND_STROKE_COLOR;
    private static final Stroke TAB_STROKE = BACKGROUND_STROKE;
    private static final double TAB_CORNER_RADIUS = BACKGROUND_CORNER_RADIUS;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _toolIconNodes; // array of ToolIconNode, in the toolbox
    private final TrashCanIconNode _trashCanIconNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param toolProducer
     * @param mvt
     */
    public ToolboxNode( IToolProducer toolProducer, ModelViewTransform mvt ) {
        super();
        
        _toolIconNodes = new ArrayList();
        
        // create icons, under a common parent
        PNode iconsParentNode = new PNode();
        {
            // interactive tools
            _toolIconNodes.add( new ThermometerIconNode( toolProducer, mvt ) );
            _toolIconNodes.add( new GlacialBudgetMeterIconNode( toolProducer, mvt ) );
            _toolIconNodes.add( new TracerFlagIconNode( toolProducer, mvt ) );
            _toolIconNodes.add( new IceThicknessToolIconNode( toolProducer, mvt ) );
            _toolIconNodes.add( new BoreholeDrillIconNode( toolProducer, mvt ) );
            _toolIconNodes.add( new GPSReceiverIconNode( toolProducer, mvt ) );
            
            // trash can is special
            _trashCanIconNode = new TrashCanIconNode( toolProducer );
            _toolIconNodes.add( _trashCanIconNode );
            
            layoutIcons( _toolIconNodes, iconsParentNode );
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
    
    /*
     * Sets the positions of the icons.
     */
    private static void layoutIcons( ArrayList nodes, PNode parentNode ) {
        
        // add all icons to parent, calculate max height
        Iterator i = nodes.iterator();
        while ( i.hasNext() ) {
            AbstractToolIconNode currentNode = (AbstractToolIconNode) i.next();
            parentNode.addChild( currentNode );
        }
        final double maxToolHeight = parentNode.getFullBoundsReference().getHeight();
        
        // arrange icons in the toolbox from left to right, vertically centered
        double x, y;
        PNode previousNode = null;
        Iterator j = nodes.iterator();
        while ( j.hasNext() ) {
            AbstractToolIconNode currentNode = (AbstractToolIconNode) j.next();
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
    
    public TrashCanIconNode getTrashCan() {
        return _trashCanIconNode;
    }
}
