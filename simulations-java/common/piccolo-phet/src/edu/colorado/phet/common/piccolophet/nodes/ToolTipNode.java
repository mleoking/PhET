// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ToolTipNode is a Swing-style "tool tip" that can be associated with a node.
 * When the mouse is placed over the associated node, the tool tip appears after a brief delay.
 * Pressing the mouse or moving the mouse off the associated node hides the tool tip.
 * <p/>
 * Several tool tip location strategies are provided, and you can provide your own.
 * The default strategy centers the tool tip above the mouse cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolTipNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final int DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance().getInitialDelay();
    private static final Font DEFAULT_FONT = new PhetFont( 12 );
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Paint DEFAULT_STROKE_PAINT = new Color( 0, 0, 0, 200 );
    private static final Paint DEFAULT_BACKGROUND_PAINT = new Color( 255, 255, 204 ); // light yellow
    private static final Paint DEFAULT_BACKGROUND_SHADOW_PAINT = new Color( 0, 0, 0, 100 ); // transparent black
    private static final double DEFAULT_MARGIN = 5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final PNode _associatedNode;
    private boolean _enabled;
    private Timer _showToolTipTimer;
    private IToolTipLocationStrategy _locationStrategy;
    private PNode toolTipTextNode;
    private PPath _backgroundNode;
    private PPath _backgroundShadowNode;
    private double _margin;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor that provides a good default behavior.
     *
     * @param toolTipText    HTML or plain-text format
     * @param associatedNode the node that the tool tip describes
     */
    public ToolTipNode( final String toolTipText, final PNode associatedNode ) {
        this( toolTipText, associatedNode, DEFAULT_INITIAL_DELAY );
    }

    /**
     * Constructor that provides a good default behavior and also specifies a default initial delay.
     *
     * @param toolTipText    HTML or plain-text format
     * @param associatedNode the node that the tool tip describes
     * @param initialDelay   the delay of time in milliseconds before the tool tip appears
     */
    public ToolTipNode( final String toolTipText, final PNode associatedNode, final int initialDelay ) {
        this( toolTipText, associatedNode, initialDelay,
              DEFAULT_FONT, DEFAULT_TEXT_COLOR,
              DEFAULT_STROKE, DEFAULT_STROKE_PAINT,
              DEFAULT_BACKGROUND_PAINT, DEFAULT_BACKGROUND_SHADOW_PAINT,
              DEFAULT_MARGIN );
    }

    /*
    * Fully-parameterized constructor.
    * Subclass and use this constructor if you want something other than the default behavior.
    *
    * @param toolTipText
    * @param associatedNode
    * @param startTime
    * @param font
    * @param textColor
    * @param backgroundStroke
    * @param backgroundStrokePaint
    * @param backgroundPaint
    * @param backgroundShadowPaint
    * @param margin
    */
    protected ToolTipNode( final String toolTipText, final PNode associatedNode, final int initialDelay,
                           Font font, Color textColor,
                           Stroke backgroundStroke, Paint backgroundStrokePaint,
                           Paint backgroundPaint, Paint backgroundShadowPaint,
                           double margin ) {
        super();

        setVisible( false );
        setPickable( false );
        setChildrenPickable( false );

        _margin = margin;
        _associatedNode = associatedNode;
        _enabled = true;

        if ( BasicHTML.isHTMLString( toolTipText ) ) {
            HTMLNode htmlNode = new HTMLNode( toolTipText );
            htmlNode.setFont( font );
            htmlNode.setHTMLColor( textColor );
            toolTipTextNode = htmlNode;
        }
        else {
            PText ptextNode = new PText( toolTipText );
            ptextNode.setFont( font );
            ptextNode.setTextPaint( textColor );
            toolTipTextNode = ptextNode;
        }

        _backgroundNode = new PPath();
        _backgroundNode.setStroke( backgroundStroke );
        _backgroundNode.setStrokePaint( backgroundStrokePaint );
        _backgroundNode.setPaint( backgroundPaint );

        _backgroundShadowNode = new PPath();
        _backgroundShadowNode.setStroke( null );
        _backgroundShadowNode.setPaint( backgroundShadowPaint );
        _backgroundShadowNode.setOffset( 2, 2 );

        updateBackgroundNodeShapes();

        addChild( _backgroundShadowNode );
        addChild( _backgroundNode );
        addChild( toolTipTextNode );
        toolTipTextNode.setOffset( margin, margin );

        _locationStrategy = new CenteredAboveMouseCursor();

        associatedNode.addInputEventListener( new PBasicInputEventHandler() {

            public void mouseEntered( final PInputEvent mouseEvent ) {
                if ( ToolTipNode.this.isEnabled() ) {

                    ActionListener onListener = new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            _locationStrategy.setToolTipLocation( ToolTipNode.this, _associatedNode, mouseEvent );
                            ToolTipNode.this.setVisible( true );
                        }
                    };

                    _showToolTipTimer = new Timer( initialDelay, onListener );
                    _showToolTipTimer.setInitialDelay( initialDelay );
                    _showToolTipTimer.setRepeats( false );
                    _showToolTipTimer.start();
                }
            }

            public void mousePressed( PInputEvent event ) {
                hideToolTip();
            }

            public void mouseExited( PInputEvent event ) {
                hideToolTip();
            }

            private void hideToolTip() {
                ToolTipNode.this.setVisible( false );
                if ( _showToolTipTimer != null ) {
                    _showToolTipTimer.stop();
                    _showToolTipTimer = null;
                }
            }
        } );
    }

    private void updateBackgroundNodeShapes() {
        PBounds b = toolTipTextNode.getFullBoundsReference();
        final double w = b.getWidth() + ( 2 * _margin );
        final double h = b.getHeight() + ( 2 * _margin );
        _backgroundNode.setPathTo( new Rectangle2D.Double( 0, 0, w, h ) );
        _backgroundShadowNode.setPathTo( new Rectangle2D.Double( 0, 0, w, h ) );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    /**
     * Enables or disables the tool tip.
     * When disable, the tool tip will never become visible.
     *
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }

    /**
     * Is the tool tip enabled?
     *
     * @return
     */
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Set the font for the text in this ToolTipNode.
     *
     * @param font the desired font
     */
    public void setFont( Font font ) {
        if ( toolTipTextNode instanceof HTMLNode ) {
            ( (HTMLNode) toolTipTextNode ).setFont( font );
        }
        else if ( toolTipTextNode instanceof PText ) {
            ( (PText) toolTipTextNode ).setFont( font );
        }
        else {
            throw new RuntimeException( "Illegal value for tooltipTextNode: " + toolTipTextNode );
        }
        updateBackgroundNodeShapes();
    }

    /**
     * Sets the strategy for placing the tool tip (setting its location).
     *
     * @param locationStrategy
     */
    public void setLocationStrategy( IToolTipLocationStrategy locationStrategy ) {
        _locationStrategy = locationStrategy;
    }

    //----------------------------------------------------------------------------
    // Strategies for setting the tool tip's location
    //----------------------------------------------------------------------------

    /**
     * Interface implemented by all strategies that set a tool tip's location.
     */
    public interface IToolTipLocationStrategy {
        /**
         * Sets the tool tip's location.
         *
         * @param toolTipNode    the tool tip node
         * @param associatedNode the node that the tool tip describes
         * @param event          the mouseEntered event that made the tool tip visible
         */
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event );
    }

    /**
     * Tool tip is centered about the mouse cursor.
     */
    public static class CenteredAboveMouseCursor implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            Point2D pGlobal = event.getPosition();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( pGlobal );
            double xOffset = pLocal.getX() - ( toolTipNode.getFullBoundsReference().getWidth() / 2 );
            double yOffset = pLocal.getY() - toolTipNode.getFullBoundsReference().getHeight();
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    /**
     * Tool tip is left-aligned about the mouse cursor.
     * Useful when the associated node is at the left edge of the play area.
     */
    public static class LeftAlignedAboveMouseCursor implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            Point2D pGlobal = event.getPosition();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( pGlobal );
            double xOffset = pLocal.getX();
            double yOffset = pLocal.getY() - toolTipNode.getFullBoundsReference().getHeight();
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    /**
     * Tool tip is right-aligned about the mouse cursor.
     * Useful when the associated node is at the right edge of the play area.
     */
    public static class RightAlignedAboveMouseCursor implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            Point2D pGlobal = event.getPosition();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( pGlobal );
            double xOffset = pLocal.getX() - toolTipNode.getFullBoundsReference().getWidth();
            double yOffset = pLocal.getY() - toolTipNode.getFullBoundsReference().getHeight();
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    /**
     * Tool tip is centered below its associated node.
     */
    public static class CenteredBelowAssociatedNode implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            PBounds bGlobal = associatedNode.getGlobalFullBounds();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( new Point2D.Double( bGlobal.getX(), bGlobal.getMaxY() ) );
            double xOffset = pLocal.getX() + ( associatedNode.getFullBoundsReference().getWidth() - toolTipNode.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = pLocal.getY() + 5;
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    /**
     * Tool tip is left-aligned below its associated node.
     * Useful when the associated node is at the left edge of the play area.
     */
    public static class LeftAlignedBelowAssociatedNode implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            PBounds bGlobal = associatedNode.getGlobalFullBounds();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( new Point2D.Double( bGlobal.getX(), bGlobal.getMaxY() ) );
            double xOffset = pLocal.getX();
            double yOffset = pLocal.getY() + 5;
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    /**
     * Tool tip is right-aligned below its associated node.
     * Useful when the associated node is at the right edge of the play area.
     */
    public static class RightAlignedBelowAssociatedNode implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            PBounds bGlobal = associatedNode.getGlobalFullBounds();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( new Point2D.Double( bGlobal.getX(), bGlobal.getMaxY() ) );
            double xOffset = pLocal.getX() + ( associatedNode.getFullBoundsReference().getWidth() - toolTipNode.getFullBoundsReference().getWidth() );
            double yOffset = pLocal.getY() + 5;
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    //----------------------------------------------------------------------------
    // Testing
    //----------------------------------------------------------------------------

    public static void main( String[] args ) {

        // Add test nodes and their tool tips to these lists
        ArrayList<PNode> testNodes = new ArrayList<PNode>();
        ArrayList<ToolTipNode> toolTips = new ArrayList<ToolTipNode>();

        // Instructions
        PText instructionsNode = new PText( "Place mouse over a square to see its tool tip." );
        instructionsNode.setFont( new PhetFont( 14 ) );

        // Cyan Square
        PPath cyanNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        cyanNode.setPaint( Color.CYAN );
        ToolTipNode cyanToolTipNode = new ToolTipNode( "left-aligned above mouse cursor", cyanNode );
        cyanToolTipNode.setLocationStrategy( new LeftAlignedAboveMouseCursor() );
        testNodes.add( cyanNode );
        toolTips.add( cyanToolTipNode );

        // Blue Square
        PPath blueNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        blueNode.setPaint( Color.BLUE );
        ToolTipNode blueToolTipNode = new ToolTipNode( "centered above mouse cursor", blueNode );
        blueToolTipNode.setFont( new PhetFont( 24, true ) );
        testNodes.add( blueNode );
        toolTips.add( blueToolTipNode );

        // Yellow Square
        PPath yellowNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        yellowNode.setPaint( Color.YELLOW );
        ToolTipNode yellowToolTipNode = new ToolTipNode( "right-aligned above mouse cursor", yellowNode );
        yellowToolTipNode.setLocationStrategy( new RightAlignedAboveMouseCursor() );
        testNodes.add( yellowNode );
        toolTips.add( yellowToolTipNode );

        // Red Square
        PPath redNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        redNode.setPaint( Color.RED );
        ToolTipNode redToolTipNode = new ToolTipNode( "centered below node", redNode );
        redToolTipNode.setLocationStrategy( new CenteredBelowAssociatedNode() );
        testNodes.add( redNode );
        toolTips.add( redToolTipNode );

        // Green Square
        PPath greenNode = new PPath( new Rectangle( 0, 0, 100, 50 ) );
        greenNode.setPaint( Color.GREEN );
        ToolTipNode greenToolTipNode = new ToolTipNode( "<html><center><b>centered</b><br>below<br>node<br>(HTML)</center></html>", greenNode );
        greenToolTipNode.setLocationStrategy( new CenteredBelowAssociatedNode() );
        testNodes.add( greenNode );
        toolTips.add( greenToolTipNode );

        // Gray Square
        PPath grayNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        grayNode.setPaint( Color.GRAY );
        ToolTipNode grayToolTipNode = new ToolTipNode( "left-aligned below node", grayNode );
        grayToolTipNode.setLocationStrategy( new LeftAlignedBelowAssociatedNode() );
        testNodes.add( grayNode );
        toolTips.add( grayToolTipNode );

        // Black Square
        PPath blackNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        blackNode.setPaint( Color.BLACK );
        ToolTipNode blackToolTipNode = new ToolTipNode( "right-aligned below node", blackNode );
        blackToolTipNode.setLocationStrategy( new RightAlignedBelowAssociatedNode() );
        testNodes.add( blackNode );
        toolTips.add( blackToolTipNode );

        // Orange Square
        PPath orangeNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        orangeNode.setPaint( Color.ORANGE );
        final int initialDelay = 2000;
        ToolTipNode orangeToolTipNode = new ToolTipNode( initialDelay + " ms initial delay", orangeNode, initialDelay );
        orangeToolTipNode.setLocationStrategy( new CenteredBelowAssociatedNode() );
        testNodes.add( orangeNode );
        toolTips.add( orangeToolTipNode );

        // Add tests above here ---------------

        // Add nodes to scenegraph, set their positions to create 1 row of test nodes
        PCanvas canvas = new PCanvas();
        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );
        PNode previousNode = null;
        rootNode.addChild( instructionsNode );
        final int margin = 50;
        final int spacing = 50;
        instructionsNode.setOffset( margin, margin );
        // one row of nodes
        for ( int i = 0; i < testNodes.size(); i++ ) {
            PNode currentNode = testNodes.get( i );
            currentNode.addInputEventListener( new CursorHandler() ); // hand cursor
            rootNode.addChild( currentNode );
            if ( previousNode == null ) {
                currentNode.setOffset( margin, instructionsNode.getFullBoundsReference().getMaxY() + spacing );
            }
            else {
                currentNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + spacing, previousNode.getFullBoundsReference().getY() );
            }
            previousNode = currentNode;
        }

        // Add tool tips to scenegraph after test nodes, so they'll be on top
        for ( int i = 0; i < toolTips.size(); i++ ) {
            rootNode.addChild( toolTips.get( i ) );
        }

        // Frame
        final int frameWidth = (int) ( rootNode.getFullBoundsReference().getWidth() + margin );
        final int frameHeight = (int) ( rootNode.getFullBoundsReference().getHeight() + 150 );
        JFrame frame = new JFrame( "ToolTipNode test" );
        frame.setContentPane( canvas );
        frame.setSize( frameWidth, frameHeight );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
