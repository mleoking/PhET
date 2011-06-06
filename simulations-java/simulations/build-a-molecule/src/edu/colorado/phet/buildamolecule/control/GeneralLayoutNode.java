//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A container that allows flexible layout generation.
 */
public class GeneralLayoutNode extends PNode {
    private final List<LayoutElement> elements = new ArrayList<LayoutElement>();

    /**
     * Takes up space that represents the bounds of all of the layout elements (with their padding)
     */
    private final PNode invisibleBackground = new PNode() {{
        setVisible( false );
    }};

    public GeneralLayoutNode() {
        addChild( invisibleBackground );
    }

    public void addChild( LayoutElement element ) {
        elements.add( element );
        super.addChild( element.node );
        updateLayout();
    }

    @Override public PNode removeChild( int index ) {
        PNode child = super.getChild( index );

        // if we have a reference, remove it
        for ( LayoutElement element : new ArrayList<LayoutElement>( elements ) ) {
            if ( element.node == child ) {
                elements.remove( element );
            }
        }
        return super.removeChild( index );
    }

    public void updateLayout() {
        LayoutProperties layoutProperties = new LayoutProperties( elements );
        for ( int i = 0; i < elements.size(); i++ ) {
            LayoutElement element = elements.get( i );
            element.method.layout( element, i, ( i > 0 ? elements.get( i - 1 ) : null ), layoutProperties );
        }

        // use the invisible background to take up all of our layout areas
        invisibleBackground.removeAllChildren();
        PBounds bounds = getLayoutBounds();
        if ( bounds != null ) {
            invisibleBackground.addChild( new PPath( bounds ) );
        }
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

        public PBounds getLayoutBounds() {
            PBounds bounds = node.getFullBounds();
            return new PBounds( bounds.getX() - paddingLeft,
                                bounds.getY() - paddingTop,
                                bounds.getWidth() + paddingLeft + paddingRight,
                                bounds.getHeight() + paddingTop + paddingBottom );
        }

        public void setLeft( double left ) {
            node.setOffset( left + paddingLeft, node.getYOffset() );
        }

        public void setTop( double top ) {
            node.setOffset( node.getXOffset(), top + paddingTop );
        }
    }
}
