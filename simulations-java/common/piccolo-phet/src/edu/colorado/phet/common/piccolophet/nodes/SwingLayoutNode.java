package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class uses a swing layout manager to position PNodes.
 * TODO: This implementation doesn't handle PNode resizes
 */
public class SwingLayoutNode extends PNode {
    private JPanel container;

    public SwingLayoutNode( LayoutManager layoutManager ) {
        this.container = new JPanel( layoutManager ) {
            public boolean isVisible() {
                return true;//required for layouts to work
            }
        };
    }

    public void addChild( PNode node, Object constraints ) {
        NodeComponent component = new NodeComponent( node );
        container.add( component, constraints );
        container.setSize( container.getPreferredSize() );
        container.doLayout();
        super.addChild( node );
    }

    public void addChild( PNode child ) {
        addChild( child, null );
    }

    /**
     * The NodeComponent is an adapter for the Swing layout; it's layout is used to set the location of the PNode
     */
    private static class NodeComponent extends JComponent {
        private PNode node;

        public NodeComponent( PNode node ) {
            this.node = node;
        }

        public boolean isVisible() {
            return true;//required for layouts to work
        }

        //TODO: allow reshaping of piccolo nodes, or centering within bounding box
        public Dimension getPreferredSize() {
            return new Dimension( (int) node.getFullBounds().getWidth(), (int) node.getFullBounds().getHeight() );
        }

        public void setBounds( int x, int y, int width, int height ) {
            super.setBounds( x, y, width, height );
            node.setOffset( x, y );
        }
    }

    //Support for types like BoxLayout that need a reference to the container
    private static final LayoutType BOX_Y = new LayoutType() {
        public LayoutManager getLayout( JPanel container ) {
            return new BoxLayout( container, BoxLayout.Y_AXIS );
        }
    };

    static interface LayoutType {
        LayoutManager getLayout( JPanel container );
    }

    public SwingLayoutNode( LayoutType layoutType ) {
        this.container = new JPanel() {
            public boolean isVisible() {
                return super.isVisible();
            }
        };
        container.setLayout( layoutType.getLayout( container ) );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
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


        frame.setContentPane( contentPane );
        frame.setVisible( true );
    }
}
