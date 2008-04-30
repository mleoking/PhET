/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * ToolIconNode is the base class for all tool icons in the toolbox.
 * It handles the layout of the icon's image and text.
 * <p>
 * InteractiveToolNode adds interactivity to tool icons, resulting in
 * the creation of new tools.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractToolIconNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int VERTICAL_SPACING = 2; // vertical space between a tool's icon and label
    private static final Font LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param image image displayed on the icon
     * @param html HTML text, centered under image
     */
    public AbstractToolIconNode( Image image, String html ) {
        super();
        
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        HTMLNode labelNode = new HTMLNode( html );
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

    }
    
    //----------------------------------------------------------------------------
    // Subclass that adds interactivity
    //----------------------------------------------------------------------------
    
    /**
     * InteractiveToolIconNode adds interactivity to ToolIconNode.
     * When an interactive tool icon receives a mouse press, it asks a specified tool producer
     * to create a tool model element. As long as the mouse remains pressed, drag events 
     * are used to change the new tool's position.
     */
    protected static abstract class InteractiveToolIconNode extends AbstractToolIconNode {

        private IToolProducer _toolProducer;
        private ModelViewTransform _mvt;
        private Point2D _pModel; // reusable point for model-view transforms

        /**
         * Constructor.
         * 
         * @param image image displayed on the icon
         * @param html HTML text, centered under image
         * @param toolProducer object capable of creating tools
         * @param mvt model-view transform, used to convert mouse position to tool position
         */
        public InteractiveToolIconNode( Image image, String html, IToolProducer toolProducer, ModelViewTransform mvt ) {
            super( image, html );

            _toolProducer = toolProducer;
            _mvt = mvt;
            _pModel = new Point2D.Double();

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() {

                private AbstractTool _tool = null; // tool model element created when drag starts

                /* When the drag starts, create the new tool. */
                protected void startDrag( PInputEvent event ) {
                    _mvt.viewToModel( event.getPosition(), _pModel );
                    _tool = createTool( _pModel );
                    _tool.setDragging( false );
                    super.startDrag( event );
                }

                /* During the drag, set the position of the new tool. */
                protected void drag( PInputEvent event ) {
                    _mvt.viewToModel( event.getPosition(), _pModel );
                    _tool.setPosition( _pModel );
                }

                /* When the drag ends, release control of the tool and activate it. */
                protected void endDrag( PInputEvent event ) {
                    super.endDrag( event );
                    _tool.setDragging( true );
                    _tool = null;
                }
            } );
        }

        /*
         * Provides access to tool producer for subclasses.
         * The tool producer is responsible for creating the tool model element.
         */
        protected IToolProducer getToolProducer() {
            return _toolProducer;
        }

        /*
         * Creates the appropriate tool at the specified position.
         * 
         * @param position position in model coordinates
         */
        protected abstract AbstractTool createTool( Point2D position );
    }
}
