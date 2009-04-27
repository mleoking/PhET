package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A layout node that can be "pinned" in place.  
 * The pin point corresponds to the bounds of a specified child node.
 * If no pin is specified, the pin point defaults to the layout's upper-left corner.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PinnedLayoutNode extends SwingLayoutNode {
    
    private PNode pinnedNode;
    private PBounds pinnedGlobalFullBounds;
    private final PropertyChangeListener pinnedNodePropertyChangeListener;
    
    /**
     * Uses a specific Swing layout manager.
     * @param layoutManager
     */
    public PinnedLayoutNode( LayoutManager layoutManager  ) {
        super( layoutManager );
        pinnedNodePropertyChangeListener = new PropertyChangeListener() {
            // When the pinned node's full bounds change, update the layout node's offset.
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateOffset();
                }
            }
        };
    }
    
    /**
     * Sets the node that will be pinned in place. 
     * The layout's offset will be dynamically adjusted so that the pinned nodes 
     * appears to remain stationary.
     * <p>
     * Note that it's important to call this *after* you've applied any transforms
     * (offset, scale, rotation,...) to your layout node.  The alternative would
     * be to override all methods related to transforms, and have them update the
     * pinned node's bounds.  But that seems like an unnecessary maintenance headache.
     * 
     * @param node
     * @throws IllegalStateException if the pin node is not a child of this node
     */
    public void setPinnedNode( PNode node ) {
        if ( node.getParent() != this ) {
            throw new IllegalStateException( "node must be a child" );
        }
        if ( pinnedNode != null ) {
            pinnedGlobalFullBounds = null;
            pinnedNode.removePropertyChangeListener( pinnedNodePropertyChangeListener );
        }
        pinnedNode = node;
        if ( pinnedNode != null ) {
            pinnedGlobalFullBounds = pinnedNode.getGlobalFullBounds();
            pinnedNode.addPropertyChangeListener( pinnedNodePropertyChangeListener ); // do this last, requesting bounds may fire a PropertyChangeEvent
        }
    }
    
    public PNode getPinnedNode() {
        return pinnedNode;
    }
    
    /**
     * Adjusts the bounds used to pin the layout.
     * Call this after applying transforms to the layout node.
     * @see setPinnedNode
     */
    public void adjustPinnedNode() {
        if ( pinnedNode != null ) {
            pinnedGlobalFullBounds = pinnedNode.getGlobalFullBounds();
        }
    }
    
    /*
     * Update the layout node's offset so that the pinned node appears to be stationary.
     */
    private void updateOffset() {
        if ( pinnedNode != null ) {
            // calculate how much the pinned node has moved in global coordinates
            PBounds currentGlobalFullBounds = pinnedNode.getGlobalFullBounds();
            double xOffset = pinnedGlobalFullBounds.getX() - currentGlobalFullBounds.getX();
            double yOffset = pinnedGlobalFullBounds.getY() - currentGlobalFullBounds.getY();
            // transform the pinned node movement to parent coordinates
            Point2D delta = localToParent( globalToLocal( new Point2D.Double( xOffset, yOffset ) ) );
            // adjust the layout node's offset, which is specified in parent coordinates
            super.setOffset( getXOffset() + delta.getX(), getYOffset() + delta.getY() );
        }
    }
    
    /* test */
    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 600, 400 );
        PhetPCanvas canvas = new PhetPCanvas( canvasSize );
        canvas.setPreferredSize( canvasSize );
        
        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );
        rootNode.addInputEventListener( new PBasicInputEventHandler() {
            // Shift+Drag up/down will scale the node up/down
            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                if ( event.isShiftDown() ) {
                    event.getPickedNode().scale( event.getCanvasDelta().height > 0 ? 0.98 : 1.02 );
                }
            }
        } );
        
        // nodes
        final PText valueNode = new PText( "0" ); // text will be set by valueSlider
        final PPath redCircle = new PPath( new Ellipse2D.Double( 0, 0, 25, 25 ) );
        redCircle.setPaint( Color.RED );
        
        // layout
        PinnedLayoutNode layoutNode = new PinnedLayoutNode( new FlowLayout() );
        rootNode.addChild( layoutNode );
        layoutNode.addChild( valueNode );
        layoutNode.addChild( redCircle );
        layoutNode.scale( 2.0 );
        layoutNode.setPinnedNode( redCircle );
        layoutNode.setOffset( 200, 150 );
        layoutNode.adjustPinnedNode();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
        final JSlider valueSlider = new JSlider( 0, 1000, 0 ); // controls dynamicNode
        valueSlider.setMajorTickSpacing( valueSlider.getMaximum() );
        valueSlider.setPaintTicks( true );
        valueSlider.setPaintLabels( true );
        valueSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                valueNode.setText( String.valueOf( valueSlider.getValue() ) );
            }
        } );
        controlPanel.add( valueSlider );
        
        JPanel appPanel = new JPanel( new BorderLayout() );
        appPanel.add( canvas, BorderLayout.CENTER );
        appPanel.add( controlPanel, BorderLayout.EAST );

        JFrame frame = new JFrame();
        frame.setContentPane( appPanel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
