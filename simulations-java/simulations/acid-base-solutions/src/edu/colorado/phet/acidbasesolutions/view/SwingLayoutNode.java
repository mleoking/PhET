package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Uses Swing layout facilities to position PNodes.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SwingLayoutNode extends PNode {
    
    private final JPanel container;
    //TODO: Since PNode doesn't correctly override equals or hashcode, it shouldn't be used as a key in a HashMap like this
    private final HashMap nodeComponentMap; // PNode -> NodeComponent
    private final PropertyChangeListener propertyChangeListener;

    /**
     * Uses a default FlowLayout.
     */
    public SwingLayoutNode() {
        this( new FlowLayout() );
    }
    
    /**
     * Uses a specific layout.
     * @param layoutManager
     */
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
    
    public Container getContainer() {
        return container;
    }
    
    /**
     * Sets the layout. Like Swing, if you call this after adding nodes,
     * the results can sometimes be a bit unpredictable.
     * 
     * @param layoutManager
     */
    public void setLayout( LayoutManager layoutManager ) {
        container.setLayout( layoutManager );
        updateLayout();
    }
    
    /**
     * Adds a child with some layout constraints.
     * Like Swing, bad things can happen if the type of the constraints
     * isn't compatible with the layout manager.
     * 
     * @param child
     * @param constraints
     */
    public void addChild( PNode child, Object constraints ) {
        super.addChild( child );
        addNodeComponent( child, constraints );
    }
    
    /**
     * Adds a child with no layout constraints.
     * Like Swing, bad things can happen the layout manager 
     * is expecting to receive constraints.
     * 
     * @param child
     */
    public void addChild( PNode child ) {
        addChild( child, null );
    }
    
    /**
     * Adds a child at the specified index.
     * Like Swing, bad things can happen if the type of the constraints
     * isn't compatible with the layout manager.
     * 
     * @param index
     * @param child
     * @param constraints
     */
    public void addChild( int index, PNode child, Object constraints ) {
        super.addChild( index, child  );
        addNodeComponent( child, constraints );
    }
    
    /**
     * Adds a child at the specified index with no layout constraints.
     * Like Swing, bad things can happen the layout manager 
     * is expecting to receive constraints.
     * 
     * @param index
     */
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
    
    /*
     * Adds a proxy component for a node.
     */
    private void addNodeComponent( PNode node, Object constraints ) {
        NodeComponent component = new NodeComponent( node );
        if ( constraints == null ) {
            container.add( component );
        }
        else {
            container.add( component, constraints );
        }
        container.setSize( container.getPreferredSize() );
        node.addPropertyChangeListener( propertyChangeListener );
        nodeComponentMap.put( node, component );
        updateLayout();
    }
    
    /*
     * Removes a proxy component for a node.
     */
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
        container.invalidate(); // necessary for layouts like BoxLayout that would otherwise use stale state 
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
        
        public Dimension getPreferredSize() {
            //TODO: round up dimensions instead of rounding down
            return new Dimension( (int) node.getFullBoundsReference().getWidth(), (int) node.getFullBoundsReference().getHeight() );
        }

        /**
         * Return the PNode size as the minimum dimension; required by layouts such as BoxLayout.
         * @return the minimum size for this component
         */
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public void setBounds( int x, int y, int width, int height ) {
            super.setBounds( x, y, width, height );
            node.setOffset( x, y );
        }
    }

    // test cases
    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 800, 600 );
        PhetPCanvas canvas = new PhetPCanvas( canvasSize );
        canvas.setPreferredSize( canvasSize );
        PNode rootNode = new PNode();
        canvas.addWorldChild( rootNode );
        
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap( 10 );
        borderLayout.setVgap( 5 );
        SwingLayoutNode borderLayoutNode = new SwingLayoutNode( borderLayout );
        borderLayoutNode.addChild( new PText( "West" ), BorderLayout.WEST );
        borderLayoutNode.addChild( new PText( "CENTER" ), BorderLayout.CENTER );
        borderLayoutNode.addChild( new PText( "South" ), BorderLayout.SOUTH );
        borderLayoutNode.addChild( new PText( "North" ), BorderLayout.NORTH );
        borderLayoutNode.addChild( new PText( "East" ), BorderLayout.EAST );
        borderLayoutNode.setOffset( 100, 100 );
        rootNode.addChild( borderLayoutNode );

        SwingLayoutNode flowLayoutNode = new SwingLayoutNode( new FlowLayout() );
        flowLayoutNode.addChild( new PText( "1+1" ) );
        flowLayoutNode.addChild( new PText( "2+2" ) );
        flowLayoutNode.setOffset( 200, 200 );
        rootNode.addChild( flowLayoutNode );

        SwingLayoutNode gridBagLayoutNode = new SwingLayoutNode( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
        gridBagLayoutNode.addChild( new PText( "FirstNode" ), gridBagConstraints );
        gridBagLayoutNode.addChild( new PText( "SecondNode" ), gridBagConstraints );
        gridBagConstraints.insets = new Insets( 50, 50, 50, 50 );
        gridBagLayoutNode.addChild( new PText( "ThirdNode" ), gridBagConstraints );
        gridBagLayoutNode.setOffset( 400, 250 );
        rootNode.addChild( gridBagLayoutNode );

        SwingLayoutNode horizontalLayoutNode = new SwingLayoutNode( new GridBagLayout() );
        horizontalLayoutNode.addChild( new PSwing( new JButton( "Zero" ) ) );
        horizontalLayoutNode.addChild( new PSwing( new JButton( "One" ) ) );
        horizontalLayoutNode.addChild( new PSwing( new JButton( "Two" ) ) );
        horizontalLayoutNode.addChild( new PSwing( new JLabel( "Three" ) ) );
        horizontalLayoutNode.addChild( new PSwing( new JSlider() ) );
        horizontalLayoutNode.addChild( new PSwing( new JTextField( "Four" ) ) );
        HTMLNode htmlNode = new HTMLNode( "<html>Five</html>", new PhetFont( 15, true ), Color.blue );
        htmlNode.scale( 3 );
        horizontalLayoutNode.addChild( htmlNode );
        horizontalLayoutNode.setOffset( 100, 400 );
        rootNode.addChild( horizontalLayoutNode );
      
        //TODO why do these paths overlap?
        SwingLayoutNode boxLayoutNode = new SwingLayoutNode();
        boxLayoutNode.setLayout( new BoxLayout( boxLayoutNode.getContainer(), BoxLayout.Y_AXIS ) );
        boxLayoutNode.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 50 ) ,new BasicStroke(2),Color.red) );
        boxLayoutNode.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 50 ) ,new BasicStroke(2),Color.blue) );
        boxLayoutNode.setOffset( 300, 300 );
        rootNode.addChild( boxLayoutNode );
        
        // 3x2 grid of values, shapes and labels (similar to a layout in acid-base-solutions)
        SwingLayoutNode gridNode = new SwingLayoutNode( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets( 10, 10, 10, 10 );
        /*---- column of values, right justified ---*/
        constraints.gridy = 0; // row
        constraints.gridx = 0; // column
        constraints.anchor = GridBagConstraints.EAST; //TODO why isn't this anchor respected when dynamicNode is updated?
        final PText dynamicNode = new PText("0"); // will be controlled by dynamicSlider
        gridNode.addChild( dynamicNode, constraints );
        constraints.gridy++;
        gridNode.addChild( new PText("0"), constraints );
        /*---- column of shapes, center justified ---*/
        constraints.gridy = 0; // row
        constraints.gridx++; // column
        constraints.anchor = GridBagConstraints.CENTER;
        PPath redCircle = new PPath( new Ellipse2D.Double( 0, 0, 25, 25 ));
        redCircle.setPaint( Color.RED );
        gridNode.addChild( redCircle, constraints );
        constraints.gridy++;
        PPath greenCircle = new PPath( new Ellipse2D.Double( 0, 0, 25, 25 ));
        greenCircle.setPaint( Color.GREEN );
        gridNode.addChild( greenCircle, constraints );
        /*---- column of labels, left justified ---*/
        constraints.gridy = 0; // row
        constraints.gridx++; // column
        constraints.anchor = GridBagConstraints.WEST;
        gridNode.addChild( new HTMLNode("<html>H<sub>2</sub>O</html>"), constraints );
        constraints.gridy++;
        gridNode.addChild( new HTMLNode("<html>H<sub>3</sub>O<sup>+</sup></html>"), constraints );
        gridNode.scale( 2.0 );
        gridNode.setOffset( 400, 50 );
        rootNode.addChild( gridNode );
        
        JPanel controlPanel = new JPanel();
        final JSlider dynamicSlider = new JSlider( 0, 1000000 ); // controls dynamicNode
        dynamicSlider.setMajorTickSpacing( 1000000 );
        dynamicSlider.setPaintTicks( true );
        dynamicSlider.setPaintLabels( true );
        dynamicSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dynamicNode.setText( String.valueOf( dynamicSlider.getValue() ) );
            }
        });
        controlPanel.add( dynamicSlider );
        
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
