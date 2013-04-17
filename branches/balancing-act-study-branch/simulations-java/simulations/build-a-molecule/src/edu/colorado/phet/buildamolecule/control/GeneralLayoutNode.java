//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

//REVIEW this looks like it might be generally useful. Consider migration to phetcommon?

/**
 * A container that allows flexible layout generation.
 * <p/>
 * Based on layout methods that can be composed together.
 */
public class GeneralLayoutNode extends PNode {
    private static final boolean DEBUG = false;

    private boolean activelyLayingOut = false; // used to prevent children bounds changes during layout trigger another layout

    private final List<LayoutElement> elements = new ArrayList<LayoutElement>();

    private boolean updateOnChildBounds;
    private PropertyChangeListener childBoundsListener = new PropertyChangeListener() {
        public void propertyChange( final PropertyChangeEvent event ) {
            final String propertyName = event.getPropertyName();
            if ( propertyName.equals( PNode.PROPERTY_VISIBLE ) || propertyName.equals( PNode.PROPERTY_FULL_BOUNDS ) ||
                 propertyName.equals( PNode.PROPERTY_BOUNDS ) || propertyName.equals( PNode.PROPERTY_TRANSFORM ) ) {
                updateLayout();
            }
        }
    };

    /**
     * Takes up space that represents the bounds of all of the layout elements (with their padding)
     */
    private final PNode invisibleBackground = new PNode() {{
        setVisible( false );
    }};

    public GeneralLayoutNode() {
        this( true ); // default to changing layout
    }

    public GeneralLayoutNode( boolean updateOnChildBounds ) {
        this.updateOnChildBounds = updateOnChildBounds;
        addChild( invisibleBackground );
    }

    public LayoutProperties getLayoutProperties() {
        return new LayoutProperties( elements );
    }

    /*---------------------------------------------------------------------------*
    * adding children
    * adds a node at an optional index with a specific layout method (and optional padding)
    *----------------------------------------------------------------------------*/

    public void addChild( PNode node, LayoutMethod method ) {
        addChild( new LayoutElement( node, method ) );
    }

    public void addChild( PNode node, LayoutMethod method, double paddingTop, double paddingLeft, double paddingBottom, double paddingRight ) {
        addChild( new LayoutElement( node, method, paddingTop, paddingLeft, paddingBottom, paddingRight ) );
    }

    public void addChild( final int index, PNode node, LayoutMethod method ) {
        addChild( index, new LayoutElement( node, method ) );
    }

    public void addChild( final int index, PNode node, LayoutMethod method, double paddingTop, double paddingLeft, double paddingBottom, double paddingRight ) {
        addChild( index, new LayoutElement( node, method, paddingTop, paddingLeft, paddingBottom, paddingRight ) );
    }

    public void addChild( LayoutElement element ) {
        addChild( elements.size(), element );
    }

    public void addChild( final int index, LayoutElement element ) {
        elements.add( index, element );
        super.addChild( index + 1, element.node ); // index + 1 since we take into account invisibleBackground
        if ( updateOnChildBounds ) {
            element.node.addPropertyChangeListener( childBoundsListener );
        }
        updateLayout();
    }

    // handle removal of children properly, however it is done
    @Override public PNode removeChild( int index ) {
        PNode child = super.getChild( index );

        // if we have a reference, remove it
        for ( LayoutElement element : new ArrayList<LayoutElement>( elements ) ) {
            if ( element.node == child ) {
                elements.remove( element );
                element.node.removePropertyChangeListener( childBoundsListener );
            }
        }
        return super.removeChild( index );
    }

    /**
     * Fully updates the layout
     */
    public void updateLayout() {
        if ( activelyLayingOut ) {
            // don't start another layout while one is going on!
            return;
        }
        activelyLayingOut = true;
        LayoutProperties layoutProperties = getLayoutProperties();
        for ( int i = 0; i < elements.size(); i++ ) {
            LayoutElement element = elements.get( i );
            element.method.layout( element, i, ( i > 0 ? elements.get( i - 1 ) : null ), layoutProperties );
        }

        // use the invisible background to take up all of our layout areas
        invisibleBackground.removeAllChildren();
        PBounds bounds = getLayoutBounds();
        if ( bounds != null ) {
            invisibleBackground.addChild( new PPath( bounds ) {{
                setStroke( null );
            }} );
        }

        if ( DEBUG ) {
            invisibleBackground.setVisible( true );

            for ( LayoutElement element : elements ) {
                invisibleBackground.addChild( new PhetPPath( new PBounds( element.node.getFullBounds() ) ) {{
                    setStrokePaint( Color.BLUE );
                    setPaint( new Color( 255, 0, 0, 100 ) );
                }} );
            }
        }
        activelyLayingOut = false;
    }

    public PBounds getLayoutBounds() {
        PBounds bounds = null;
        for ( LayoutElement element : elements ) {
            if ( bounds == null ) {
                bounds = element.getLayoutBounds();
            }
            else {
                bounds.add( element.getLayoutBounds() );
            }
        }
        return bounds;
    }

    public static interface LayoutMethod {
        void layout( LayoutElement element, int index, LayoutElement previousElement, LayoutProperties layoutProperties );
    }

    public static class CompositeLayoutMethod implements LayoutMethod {
        private final LayoutMethod[] methods;

        public CompositeLayoutMethod( LayoutMethod... methods ) {
            this.methods = methods;
        }

        public void layout( LayoutElement element, int index, LayoutElement previousElement, LayoutProperties layoutProperties ) {
            for ( LayoutMethod method : methods ) {
                method.layout( element, index, previousElement, layoutProperties );
            }
        }
    }

    /**
     * Lay out elements vertically
     */
    public static class VerticalLayoutMethod implements LayoutMethod {
        public void layout( LayoutElement element, int index, LayoutElement previousElement, LayoutProperties layoutProperties ) {
            element.setTop( previousElement == null ? 0 : previousElement.getLayoutBounds().getMaxY() );
        }
    }

    /**
     * Horizontally align an element
     */
    public static class HorizontalAlignMethod implements LayoutMethod {
        private final Align align;

        public HorizontalAlignMethod( Align align ) {
            this.align = align;
        }

        public void layout( LayoutElement element, int index, LayoutElement previousElement, LayoutProperties layoutProperties ) {
            switch( align ) {
                case Left:
                    element.setLeft( 0 );
                    break;
                case Centered:
                    element.setLeft( ( layoutProperties.maxWidth - element.getLayoutBounds().getWidth() ) / 2 );
                    break;
                case Right:
                    element.setLeft( layoutProperties.maxWidth - element.getLayoutBounds().getWidth() );
                    break;
            }
        }

        public static enum Align {
            Left,
            Centered,
            Right
        }
    }

    public static class LayoutProperties {
        public final double maxWidth;
        public final double maxHeight;

        public LayoutProperties( double maxWidth, double maxHeight ) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
        }

        public LayoutProperties( List<LayoutElement> elements ) {
            double largestWidth = 0;
            double largestHeight = 0;

            for ( LayoutElement element : elements ) {
                largestWidth = Math.max( largestWidth, element.getLayoutBounds().getWidth() );
                largestHeight = Math.max( largestHeight, element.getLayoutBounds().getHeight() );
            }

            maxWidth = largestWidth;
            maxHeight = largestHeight;
        }
    }

    public static class LayoutElement {
        public final PNode node;
        public final LayoutMethod method;

        public final double paddingTop;
        public final double paddingLeft;
        public final double paddingBottom;
        public final double paddingRight;

        public LayoutElement( PNode node, LayoutMethod method ) {
            this( node, method, 0, 0, 0, 0 );
        }

        public LayoutElement( PNode node, LayoutMethod method, double paddingTop, double paddingLeft, double paddingBottom, double paddingRight ) {
            this.node = node;
            this.method = method;

            this.paddingTop = paddingTop;
            this.paddingLeft = paddingLeft;
            this.paddingBottom = paddingBottom;
            this.paddingRight = paddingRight;
        }

        public PBounds getFullBounds() {
            return node.getFullBounds();
        }

        public PBounds getLayoutBounds() {
            PBounds bounds = getFullBounds();
            return new PBounds( bounds.getX() - paddingLeft,
                                bounds.getY() - paddingTop,
                                bounds.getWidth() + paddingLeft + paddingRight,
                                bounds.getHeight() + paddingTop + paddingBottom );
        }

        public void setLeft( double left ) {
            // how far does the node stick out to the left of its x=0 line?
            double overflow = node.getXOffset() - getFullBounds().getX();

            // x offset such that the layout bounds will have a left of "left"
            double newXOffset = left + paddingLeft + overflow;
            if ( node.getXOffset() != newXOffset ) {
                node.setOffset( newXOffset, node.getYOffset() );
            }
        }

        public void setTop( double top ) {
            // how far does the node stick out to the top of its y=0 line?
            double overflow = node.getYOffset() - getFullBounds().getY();

            // y offset such that the layout bounds will have a top of "top"
            double newYOffset = top + paddingTop + overflow;
            if ( node.getYOffset() != newYOffset ) {
                node.setOffset( node.getXOffset(), newYOffset );
            }
        }
    }
}
