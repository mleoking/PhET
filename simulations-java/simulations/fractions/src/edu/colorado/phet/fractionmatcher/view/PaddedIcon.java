package edu.colorado.phet.fractionmatcher.view;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.view.FNode;
import edu.umd.cs.piccolo.PNode;

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

    //Take a list of icons and pad so they each have the same dimensions (of the max)
    public static List<PNode> normalize( final List<PNode> icons ) {
        final double maxWidth = icons.map( FNode._fullWidth ).maximum( Ord.doubleOrd );
        final double maxHeight = icons.map( FNode._fullHeight ).maximum( Ord.doubleOrd );
        return icons.map( new F<PNode, PNode>() {
            @Override public PNode f( final PNode node ) {
                return new PaddedIcon( maxWidth, maxHeight, node );
            }
        } );
    }
}