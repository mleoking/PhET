package edu.colorado.phet.acidbasesolutions.view;

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
import edu.colorado.phet.common.piccolophet.nodes.SwingLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;


public class PinnedLayoutNode extends SwingLayoutNode {
    
    private final PropertyChangeListener pinNodePropertyChangeListener;
    private PNode pinNode;
    private PBounds previousPinNodeBounds;
    
    public PinnedLayoutNode( LayoutManager layoutManager  ) {
        super( layoutManager );
        pinNodePropertyChangeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                System.out.println( "pinNodePropertyChangeListener.propertyChanged " + event.getPropertyName() );//XXX
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateOffset();
                }
            }
        };
    }
    
    //TODO test if there are dependencies on the order in which setPinNode is called
    /**
     * 
     * @param node
     * @throws IllegalStateException if the pin node is not a child of this node
     */
    public void setPinNode( PNode node ) {
        if ( node.getParent() != this ) {
            throw new IllegalStateException( "node must be a child" );
        }
        if ( pinNode != null ) {
            pinNode.removePropertyChangeListener( pinNodePropertyChangeListener );
        }
        pinNode = node;
        previousPinNodeBounds = pinNode.getGlobalFullBounds();
        System.out.println( "setPinNode previousPinNodeBounds=" + previousPinNodeBounds );//XXX
        pinNode.addPropertyChangeListener( pinNodePropertyChangeListener );  // do this last because requesting bounds may fire a PropertyChangeEvent
    }
    
    public PNode getPinNode() {
        return pinNode;
    }
    
    private void updateOffset() {
        if ( pinNode != null ) {
            System.out.println( "previousPinNodeBounds=" + previousPinNodeBounds.toString() );
            PBounds currentPinNodeBounds = pinNode.getGlobalFullBounds();
            System.out.println( "currentPinNodeBounds=" + currentPinNodeBounds.toString() );
            PBounds thisBounds = this.getGlobalBounds(); // not full bounds!
            double xOffset = thisBounds.getX() + previousPinNodeBounds.getX() - currentPinNodeBounds.getX();
            double yOffset = thisBounds.getY() + previousPinNodeBounds.getY() - currentPinNodeBounds.getY();
            Point2D globalOffset = new Point2D.Double( xOffset, yOffset );
            Point2D localOffset = globalToLocal( globalOffset );
            Point2D parentOffset = localToParent( localOffset );
            setOffset( getXOffset() + parentOffset.getX(), getYOffset() + parentOffset.getY() );
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
        layoutNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                System.out.println( "PinnedLayoutNode.propertyChanged " + event.getPropertyName() );
                System.out.println( "redCircle.globalFullBounds=" + event.getPropertyName() );
            }
        } );
        layoutNode.addChild( valueNode );
        layoutNode.addChild( redCircle );
        layoutNode.scale( 2.0 );
        rootNode.addChild( layoutNode );
        layoutNode.setOffset( 200, 150 );
        layoutNode.setPinNode( redCircle );
        
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
