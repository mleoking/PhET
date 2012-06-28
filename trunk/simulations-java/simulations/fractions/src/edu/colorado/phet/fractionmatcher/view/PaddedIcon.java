// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import fj.F;
import fj.data.List;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.view.FNode._fullHeight;
import static edu.colorado.phet.fractions.view.FNode._fullWidth;
import static fj.Ord.doubleOrd;

// REVIEW - Could this be replaced by PadBoundsNode from piccolo-phet?

/**
 * Adds padding around an icon to make it a standardized size.
 *
 * @author Sam Reid
 */
public class PaddedIcon extends PNode {
    public PaddedIcon( final double maxIconWidth, final double maxIconHeight, final PNode icon ) {
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, maxIconWidth, maxIconHeight ) ) {{
            setVisible( false );
            setPickable( false );
            setChildrenPickable( false );
        }} );
        addChild( new ZeroOffsetNode( icon ) {{
            setOffset( maxIconWidth / 2 - icon.getFullBounds().getWidth() / 2, maxIconHeight / 2 - icon.getFullBounds().getHeight() / 2 );
        }} );
    }

    public PaddedIcon( final Dimension2D maxSize, final PNode icon ) {
        this( maxSize.getWidth(), maxSize.getHeight(), icon );
    }

    //Take a list of icons and pad so they each have the same dimensions (of the max)
    public static List<PNode> normalize( final List<PNode> icons ) {
        final Dimension2DDouble maxSize = getMaxSize( icons );
        return icons.map( new F<PNode, PNode>() {
            @Override public PNode f( final PNode node ) {
                return new PaddedIcon( maxSize.width, maxSize.height, node );
            }
        } );
    }

    public static Dimension2DDouble getMaxSize( final List<PNode> icons ) {
        return new Dimension2DDouble( icons.map( _fullWidth ).maximum( doubleOrd ), icons.map( _fullHeight ).maximum( doubleOrd ) );
    }
}