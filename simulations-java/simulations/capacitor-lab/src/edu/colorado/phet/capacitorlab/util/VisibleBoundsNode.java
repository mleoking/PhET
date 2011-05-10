// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * In Piccolo, full bounds is computed using all children, regardless of their visibility.
 * This node can be used to prune invisible nodes and their children from the full bounds computation.
 * If this node is invisible, its full bounds will be empty.
 * If this node is visible, then only its visible children will be included in its full bounds computation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VisibleBoundsNode extends PNode {

    private final PropertyChangeListener visiblityListener;

    public VisibleBoundsNode() {
        this( null );
    }

    public VisibleBoundsNode( PNode child ) {
        this.visiblityListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                // when visibility of this node or its children changes, recompute bounds.
                invalidateFullBounds();
            }
        };
        addPropertyChangeListener( visiblityListener );
        if ( child != null ) {
            addChild( child );
        }
    }

    @Override public void addChild( PNode child ) {
        super.addChild( child );
        child.addPropertyChangeListener( PROPERTY_VISIBLE, visiblityListener );
    }

    @Override public PNode removeChild( PNode child ) {
        PNode node = super.removeChild( child );
        child.removePropertyChangeListener( visiblityListener );
        return node;
    }

    @Override public void removeChildren( final Collection children ) {
        removeVisiblityListener( new ArrayList<PNode>( children ) );
        super.removeChildren( children );
    }

    @Override public void removeAllChildren() {
        removeVisiblityListener( new ArrayList<PNode>( getChildrenReference() ) );
        super.removeAllChildren();
    }

    private void removeVisiblityListener( ArrayList<PNode> children ) {
        for ( PNode child : children ) {
            child.removePropertyChangeListener( visiblityListener );
        }
    }

    /**
     * Compute and returns the union of the full bounds of all VISIBLE children of
     * this node. If the dstBounds parameter is not null then it will be used to
     * return the results instead of creating a new PBounds.
     *
     * @param dstBounds if not null the new bounds will be stored here
     * @return union of children bounds
     */
    @Override public PBounds getUnionOfChildrenBounds( PBounds dstBounds ) {

        // initialize the result
        PBounds resultBounds;
        if ( dstBounds == null ) {
            resultBounds = new PBounds();
        }
        else {
            resultBounds = dstBounds;
            resultBounds.resetToZero();
        }

        // if this node is visible...
        if ( getVisible() ) {
            // add full bounds of all visible children
            List children = getChildrenReference();
            final int count = children.size();
            for ( int i = 0; i < count; i++ ) {
                final PNode child = (PNode) children.get( i );
                if ( child.getVisible() ) {
                    resultBounds.add( child.getFullBoundsReference() );
                }
            }
        }

        return resultBounds;
    }

    public static void main( String[] args ) {

        // a few children, identical rectangles, different colors
        Shape shape = new Rectangle2D.Double( 0, 0, 100, 100 );
        final PPath child1 = new PhetPPath( shape, Color.RED );
        final PPath child2 = new PhetPPath( shape, Color.GREEN );
        final PPath child3 = new PhetPPath( shape, Color.BLUE );

        // parent of the rectangles
        final VisibleBoundsNode parentNode = new VisibleBoundsNode() {{
            addChild( child1 );
            addChild( child2 );
            addChild( child3 );
            // layout children in a row
            for ( int i = 1; i < getChildrenCount(); i++ ) {
                getChild( i ).setOffset( getChild( i - 1 ).getFullBoundsReference().getMaxX(), getChild( i - 1 ).getYOffset() );
            }
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() ); // this makes children individually draggable
        }};

        // draws the full bounds of parentNode
        final PPath boundsNode = new PPath( parentNode.getFullBoundsReference() ) {{
            setPaint( null );
            setStrokePaint( Color.RED );
            setStroke( new BasicStroke( 1f ) );
        }};

        // when the parent node's full bounds change, update the bounds path
        parentNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                boundsNode.setPathTo( parentNode.getFullBoundsReference() );
            }
        } );

        // check boxes, for visibility of parent and each child
        final JCheckBox checkBox0 = new JCheckBox( "parent", parentNode.getVisible() );
        checkBox0.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                parentNode.setVisible( checkBox0.isSelected() );
            }
        } );
        final JCheckBox checkBox1 = new JCheckBox( "red", child1.getVisible() );
        checkBox1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                child1.setVisible( checkBox1.isSelected() );
            }
        } );
        final JCheckBox checkBox2 = new JCheckBox( "green", child2.getVisible() );
        checkBox2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                child2.setVisible( checkBox2.isSelected() );
            }
        } );
        final JCheckBox checkBox3 = new JCheckBox( "blue", child3.getVisible() );
        checkBox3.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                child3.setVisible( checkBox3.isSelected() );
            }
        } );
        final GridPanel controlPanel = new GridPanel() {{
            setBorder( new TitledBorder( "Visibility" ) );
            setGridX( 0 );
            setAnchor( Anchor.WEST );
            add( checkBox0 );
            add( checkBox1 );
            add( checkBox2 );
            add( checkBox3 );
        }};
        final PSwing pswingControlPanel = new PSwing( controlPanel );

        // instructions
        final HTMLNode instructions = new HTMLNode( "<html>Drag the rectangles.<br>Red outline shows bounds of parent.</html>" ) {{
            setFont( new PhetFont( 24 ) );
        }};

        // canvas
        final Dimension canvasSize = new Dimension( 1024, 768 );
        final PhetPCanvas canvas = new PhetPCanvas( canvasSize ) {{
            setPreferredSize( canvasSize );
            addWorldChild( parentNode );
            addWorldChild( boundsNode );
            addWorldChild( pswingControlPanel );
            addWorldChild( instructions );
            // layout
            instructions.setOffset( 300, 50 );
            pswingControlPanel.setOffset( 100, 100 );
            parentNode.setOffset( 300, 300 );
        }};

        // frame
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
