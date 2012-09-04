// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.controlpanel;

import fj.data.List;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static fj.Ord.doubleOrd;

/**
 * Adds padding around a node to make it a standardized size.
 * Different than piccolo-phet's PadBoundsNode because that neither centers the node
 * nor allows different sizes in the x and y directions.
 *
 * @author Sam Reid
 */
public class PaddedNode extends PNode {

    public PaddedNode( final double paddedWidth, final double paddedHeight, final PNode node ) {
        assert node.getFullBoundsReference().getWidth() <= paddedWidth && node.getFullBoundsReference().getHeight() <= paddedHeight;
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, paddedWidth, paddedHeight ) ) {{
            setVisible( false );
            setPickable( false );
            setChildrenPickable( false );
            setStroke( null );
        }} );
        addChild( new ZeroOffsetNode( node ) {{
            setOffset( paddedWidth / 2 - node.getFullBounds().getWidth() / 2, paddedHeight / 2 - node.getFullBounds().getHeight() / 2 );
        }} );
    }

    public PaddedNode( final Dimension2D paddedSize, final PNode node ) {
        this( paddedSize.getWidth(), paddedSize.getHeight(), node );
    }

    // test
    public static void main( String[] args ) {

        PText text = new PText( "hello" );

        final double desiredWidth = text.getFullBoundsReference().getWidth() + 10;
        final double desiredHeight = text.getFullBoundsReference().getHeight() + 10;
        PaddedNode node = new PaddedNode( desiredWidth, desiredHeight, text );

        // Verify that PaddedNode gave us the size we asked for.
        System.out.println( "width: desired=" + desiredWidth + ", actual=" + node.getFullBoundsReference().getWidth() );
        System.out.println( "height: desired=" + desiredHeight + ", actual=" + node.getFullBoundsReference().getHeight() );
        assert ( desiredWidth == node.getFullBoundsReference().getWidth() );
        assert ( desiredHeight == node.getFullBoundsReference().getHeight() );
    }
}