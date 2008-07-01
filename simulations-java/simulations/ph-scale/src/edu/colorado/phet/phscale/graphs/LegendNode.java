
package edu.colorado.phet.phscale.graphs;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.view.H2ONode;
import edu.colorado.phet.phscale.view.H3ONode;
import edu.colorado.phet.phscale.view.OHNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LegendNode is the molecules legend that appears below the bar graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LegendNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font LEGEND_FONT = new PhetFont( 18 );
    private static final double LEGEND_ITEM_Y_SPACING = 5;
    private static final double LEGEND_X_SPACING = 25;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LegendNode() {
        super();
        LegendItemNode h3oLegendItemNode = new LegendItemNode( new H3ONode(), PHScaleStrings.LABEL_H3O );
        LegendItemNode ohLegendItemNode = new LegendItemNode( new OHNode(), PHScaleStrings.LABEL_OH );
        LegendItemNode h2oLegendItemNode = new LegendItemNode( new H2ONode(), PHScaleStrings.LABEL_H2O );
        addChild( h3oLegendItemNode );
        addChild( ohLegendItemNode );
        addChild( h2oLegendItemNode );
        h3oLegendItemNode.setOffset( 0, 0 );
        PBounds h3ob = h3oLegendItemNode.getFullBoundsReference();
        ohLegendItemNode.setOffset( h3ob.getMaxX() + LEGEND_X_SPACING, h3ob.getY() );
        PBounds ohb = ohLegendItemNode.getFullBoundsReference();
        h2oLegendItemNode.setOffset( ohb.getMaxX() + LEGEND_X_SPACING, ohb.getY() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /*
     * A legend item consists of an icon with a label centered beneath it.
     */
    private static class LegendItemNode extends PComposite {

        public LegendItemNode( PNode iconNode, String label ) {
            super();
            setPickable( false );
            setChildrenPickable( false );

            iconNode.scale( 0.4 );//XXX
            addChild( iconNode );

            HTMLNode htmlNode = new HTMLNode( label );
            htmlNode.setFont( LEGEND_FONT );
            addChild( htmlNode );

            PBounds ib = iconNode.getFullBoundsReference();
            PBounds hb = htmlNode.getFullBoundsReference();
            if ( ib.getWidth() > hb.getWidth() ) {
                iconNode.setOffset( 0, 0 );
                htmlNode.setOffset( ( ib.getWidth() - hb.getWidth() ) / 2, ib.getHeight() + LEGEND_ITEM_Y_SPACING );
            }
            else {
                iconNode.setOffset( ( hb.getWidth() - ib.getWidth() ) / 2, 0 );
                htmlNode.setOffset( 0, ib.getHeight() + LEGEND_ITEM_Y_SPACING );
            }
        }
    }
}
