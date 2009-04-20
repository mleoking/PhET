package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class uses a swing layout manager to position PNodes.
 * TODO: This implementation doesn't handle PNode resizes
 */
public class SwingLayoutNode extends PNode {
    
    private final JPanel container;
    private final HashMap nodeComponentMap; // PNode -> NodeComponent
    private final PropertyChangeListener propertyChangeListener;

    public SwingLayoutNode() {
        this( new FlowLayout() );
    }
    
    public SwingLayoutNode( LayoutManager layoutManager ) {
        this.container = new JPanel( layoutManager );
        this.nodeComponentMap = new HashMap();
        this.propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( isLayoutProperty( event.getPropertyName() ) ) {
                    updateLayout();
                }
            }
        };
    }
    
    public void setLayout( LayoutManager layoutManager ) {
        container.setLayout( layoutManager );
        updateLayout();
    }
    
    public void addChild( PNode child, Object constraints ) {
        super.addChild( child );
        addNodeComponent( child, constraints );
    }
    
    public void addChild( PNode child ) {
        addChild( child, null );
    }
    
    public void addChild( int index, PNode child, Object constraints ) {
        super.addChild( index, child  );
        addNodeComponent( child, constraints );
    }
    
    public void addChild( int index, PNode node ) {
        super.addChild( index, node  );
    }
    
    public PNode removeChild( PNode child ) {
        PNode node = super.removeChild( child );
        removeNodeComponent( node );
        return node;
    }
    
    public PNode removeChild( int index ) {
        PNode node = super.removeChild( index );
        removeNodeComponent( node );
        return node;
    }
    
    private void addNodeComponent( PNode node, Object constraints ) {
        NodeComponent component = new NodeComponent( node );
        container.add( component, constraints );
        container.setSize( container.getPreferredSize() );
        node.addPropertyChangeListener( propertyChangeListener );
        nodeComponentMap.put( node, component );
        updateLayout();
    }
    
    private void removeNodeComponent( PNode node ) {
        if ( node != null ) {
            NodeComponent component = (NodeComponent) nodeComponentMap.get( node );
            if ( component != null ) {
                container.remove( component );
                node.removePropertyChangeListener( propertyChangeListener );
                nodeComponentMap.remove( node );
                updateLayout();
            }
        }
    }
    
    /*
     * True if p is a PNode property that is related to layout.
     */
    private boolean isLayoutProperty( String p ) {
        return ( p.equals( PNode.PROPERTY_VISIBLE ) || p.equals( PNode.PROPERTY_FULL_BOUNDS ) );
    }
    
    private void updateLayout() {
        container.doLayout();
    }

    /*
     * JComponent that acts as a proxy for a PNode.
     * Supplies a Swing layout manager with the PNode's layout info.
     */
    private static class NodeComponent extends JComponent {

        private final PNode node;

        public NodeComponent( PNode node ) {
            this.node = node;
        }
        
        public PNode getNode() {
            return node;
        }

        public Dimension getPreferredSize() {
            return new Dimension( (int) node.getFullBoundsReference().getWidth(), (int) node.getFullBoundsReference().getHeight() );
        }

        public void setBounds( int x, int y, int width, int height ) {
            super.setBounds( x, y, width, height );
            node.setOffset( x, y );
        }
    }

    public static void main( String[] args ) {
        
        PhetPCanvas contentPane = new PhetPCanvas();
        SwingLayoutNode borderLayoutNode = new SwingLayoutNode( new BorderLayout() );
        borderLayoutNode.addChild( new PText( "West" ), BorderLayout.WEST );
        borderLayoutNode.addChild( new PText( "CENTER" ), BorderLayout.CENTER );
        borderLayoutNode.addChild( new PText( "South" ), BorderLayout.SOUTH );
        borderLayoutNode.addChild( new PText( "North" ), BorderLayout.NORTH );
        borderLayoutNode.addChild( new PText( "East" ), BorderLayout.EAST );
        borderLayoutNode.setOffset( 100, 100 );
        contentPane.addScreenChild( borderLayoutNode );

        SwingLayoutNode flowLayoutNode = new SwingLayoutNode( new FlowLayout() );
        flowLayoutNode.addChild( new PText( "First Node" ) );
        flowLayoutNode.addChild( new PText( "Second Node" ) );
        flowLayoutNode.setOffset( 200, 200 );
        contentPane.addScreenChild( flowLayoutNode );

        SwingLayoutNode gridBagConstraintNode = new SwingLayoutNode( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
        gridBagConstraintNode.addChild( new PText( "First Node" ), gridBagConstraints );
        gridBagConstraintNode.addChild( new PText( "Second Node" ), gridBagConstraints );
        gridBagConstraints.insets = new Insets( 50, 50, 50, 50 );
        gridBagConstraintNode.addChild( new PText( "Third Node" ), gridBagConstraints );
        gridBagConstraintNode.setOffset( 400, 400 );
        contentPane.addScreenChild( gridBagConstraintNode );

        SwingLayoutNode verticalLayout = new SwingLayoutNode( new GridBagLayout() );
        verticalLayout.addChild( new PSwing( new JButton( "Midnight" ) ) );
        verticalLayout.addChild( new PSwing( new JButton( "One O'Clock" ) ) );
        verticalLayout.addChild( new PSwing( new JButton( "Two" ) ) );
        verticalLayout.addChild( new PSwing( new JLabel( "Three" ) ) );
        verticalLayout.addChild( new PSwing( new JSlider() ) );
        verticalLayout.addChild( new PSwing( new JTextField( "Four" ) ) );
        ShadowHTMLNode child = new ShadowHTMLNode( "<html> Five </html>", Color.blue );
        child.setFont( new PhetFont( 15, true ) );
        verticalLayout.addChild( child );
        verticalLayout.setOffset( 100, 400 );
        contentPane.addScreenChild( verticalLayout );

        JFrame frame = new JFrame();
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
