
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * A layout node that can be "pinned" in place.
 * The node is pinned to the global full bounds of a specified child node.
 * The layout node's offset is dynamically adjusted so that the child node
 * appears to remain stationary.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PinnedLayoutNode extends SwingLayoutNode {

    private PNode pinnedNode;
    private PBounds pinnedGlobalFullBounds;
    private final PropertyChangeListener pinnedNodePropertyChangeListener;

    /**
     * Uses a default FlowLayout.
     */
    public PinnedLayoutNode() {
        this( new FlowLayout() );
    }

    /**
     * Uses a specific Swing layout manager.
     * @param layoutManager
     */
    public PinnedLayoutNode( LayoutManager layoutManager ) {
        this( new JPanel( layoutManager ) );
    }

    /**
     * Constructs a PinnedLayoutNode that lays out its children as though they
     * were children of the provided Container. Whatever LayoutManager is being used
     * by the container will be used when laying out nodes.
     *
     * @param container Container in which child nodes will effectively be laid out
     */
    public PinnedLayoutNode( Container container ) {
        super( container );
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
     * The layout's offset will be dynamically adjusted so that the pinned node
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
        updateOffset();
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

        final ArrayList<PText> valueNodes = new ArrayList<PText>(); // array of PText
        final double xOffset = 200;
        double yOffset = 25;
        final double ySpacing = 75;

        // vertical green line at x=200
        {
            PPath pathNode = new PhetPPath( new Line2D.Double( 0, 0, 0, 600 ), null, new BasicStroke( 2f ), Color.GREEN );
            pathNode.setOffset( xOffset, 0 );
            rootNode.addChild( pathNode );
        }

        // BoxLayout
        {
            JPanel panel = new JPanel();
            BoxLayout layout = new BoxLayout( panel, BoxLayout.X_AXIS  );
            panel.setLayout( layout );
            final PinnedLayoutNode layoutNode = new PinnedLayoutNode( panel );
            rootNode.addChild( layoutNode );
            layoutNode.scale( 1.5 );
            layoutNode.setOffset( xOffset, yOffset );
            yOffset += ySpacing;
            // value
            final PText valueNode = new PText( "0" );
            valueNodes.add( valueNode );
            layoutNode.addChild( valueNode );
            // red circle
            final PPath pathNode = new PhetPPath( new Ellipse2D.Double( 0, 0, 25, 25 ), Color.RED );
            layoutNode.addChild( pathNode );
            // label
            final PText labelNode = new PText( "BoxLayout" );
            layoutNode.addChild( labelNode );
            // pin
            layoutNode.setPinnedNode( pathNode );
        }

        // BorderLayout
        {
            final PinnedLayoutNode layoutNode = new PinnedLayoutNode( new BorderLayout() );
            rootNode.addChild( layoutNode );
            layoutNode.scale( 1.5 );
            layoutNode.setOffset( xOffset, yOffset );
            yOffset += ySpacing;
            // value
            final PText valueNode = new PText( "0" );
            valueNodes.add( valueNode );
            layoutNode.addChild( valueNode, BorderLayout.WEST );
            // red circle
            final PPath pathNode = new PhetPPath( new Ellipse2D.Double( 0, 0, 25, 25 ), Color.RED );
            layoutNode.addChild( pathNode, BorderLayout.CENTER );
            // label
            final PText labelNode = new PText( "BorderLayout" );
            layoutNode.addChild( labelNode, BorderLayout.EAST );
            // pin
            layoutNode.setPinnedNode( pathNode );
        }

        // FlowLayout
        {
            final PinnedLayoutNode layoutNode = new PinnedLayoutNode( new FlowLayout( FlowLayout.CENTER, 0, 0 ) );
            rootNode.addChild( layoutNode );
            layoutNode.scale( 1.5 );
            layoutNode.setOffset( xOffset, yOffset );
            yOffset += ySpacing;
            // value
            final PText valueNode = new PText( "0" );
            valueNodes.add( valueNode );
            layoutNode.addChild( valueNode );
            // red circle
            final PPath pathNode = new PhetPPath( new Ellipse2D.Double( 0, 0, 25, 25 ), Color.RED );
            layoutNode.addChild( pathNode );
            // label
            final PText labelNode = new PText( "FlowLayout" );
            layoutNode.addChild( labelNode );
            // pin
            layoutNode.setPinnedNode( pathNode );
        }

        // GridBagLayout
        {
            final PinnedLayoutNode layoutNode = new PinnedLayoutNode( new GridBagLayout() );
            rootNode.addChild( layoutNode );
            layoutNode.scale( 1.5 );
            layoutNode.setOffset( xOffset, yOffset );
            yOffset += ySpacing;
            // constraints
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = GridBagConstraints.RELATIVE;
            constraints.gridy = 0;
            // value
            final PText valueNode = new PText( "0" );
            valueNodes.add( valueNode );
            layoutNode.addChild( valueNode, constraints );
            // red circle
            final PPath pathNode = new PhetPPath( new Ellipse2D.Double( 0, 0, 25, 25 ), Color.RED );
            layoutNode.addChild( pathNode, constraints );
            // label
            final PText labelNode = new PText( "GridBagLayout" );
            layoutNode.addChild( labelNode, constraints );
            // pin
            layoutNode.setPinnedNode( pathNode );
        }

        // GridLayout
        {
            final PinnedLayoutNode layoutNode = new PinnedLayoutNode( new GridLayout( 0, 3 ) );
            rootNode.addChild( layoutNode );
            layoutNode.scale( 1.5 );
            layoutNode.setOffset( xOffset, yOffset );
            yOffset += ySpacing;
            // value
            final PText valueNode = new PText( "0" );
            valueNodes.add( valueNode );
            layoutNode.addChild( valueNode );
            // red circle
            final PPath pathNode = new PhetPPath( new Ellipse2D.Double( 0, 0, 25, 25 ), Color.RED );
            layoutNode.addChild( pathNode );
            // label
            final PText labelNode = new PText( "GridLayout" );
            layoutNode.addChild( labelNode );
            // pin
            layoutNode.setPinnedNode( pathNode );
        }

        //TODO add test cases for GroupLayout, SpringLayout

        // control panel with slider
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
        final JSlider valueSlider = new JSlider( 0, 1000, 0 ); // controls dynamicNode
        valueSlider.setMajorTickSpacing( valueSlider.getMaximum() );
        valueSlider.setPaintTicks( true );
        valueSlider.setPaintLabels( true );
        valueSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                for ( PText textNode : valueNodes ) {
                    textNode.setText( String.valueOf( valueSlider.getValue() ) );
                }
            }
        } );
        controlPanel.add( valueSlider );

        // layout like a sim
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
