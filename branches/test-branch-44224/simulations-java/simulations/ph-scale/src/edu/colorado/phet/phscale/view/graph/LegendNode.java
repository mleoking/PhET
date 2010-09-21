/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.graph;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.view.H2OMoleculeNode;
import edu.colorado.phet.phscale.view.H3OMoleculeNode;
import edu.colorado.phet.phscale.view.OHMoleculeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LegendNode is the legend for molecules that appears below the bar graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LegendNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = new PhetFont( 18 );
    private static final double ICON_X_SPACING = 25;
    private static final double Y_SPACING = 0;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LegendNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // icons
        PNode h3oMoleculeNode = new H3OMoleculeNode();
        PNode ohMoleculeNode = new OHMoleculeNode();
        PNode h2oMoleculeNode = new H2OMoleculeNode();
        
        // labels
        HTMLNode h3oLabelNode = new HTMLNode( PHScaleStrings.LABEL_H3O );
        h3oLabelNode.setFont( FONT );
        HTMLNode ohLabelNode = new HTMLNode( PHScaleStrings.LABEL_OH );
        ohLabelNode.setFont( FONT );
        HTMLNode h2oLabelNode = new HTMLNode( PHScaleStrings.LABEL_H2O );
        h2oLabelNode.setFont( FONT );
        
        addChild( h3oMoleculeNode );
        addChild( ohMoleculeNode );
        addChild( h2oMoleculeNode );
        addChild( h3oLabelNode );
        addChild( ohLabelNode );
        addChild( h2oLabelNode );
        
        // vertically align centers of the icons
        h3oMoleculeNode.setOffset( 0, 0 );
        PBounds h3ob = h3oMoleculeNode.getFullBoundsReference();
        PBounds ohb = ohMoleculeNode.getFullBoundsReference();
        PBounds h2ob = h2oMoleculeNode.getFullBoundsReference();
        ohMoleculeNode.setOffset( h3ob.getMaxX() + ICON_X_SPACING, h3ob.getCenterY() - ohb.getHeight() / 2 );
        ohb = ohMoleculeNode.getFullBoundsReference();
        h2oMoleculeNode.setOffset( ohb.getMaxX() + ICON_X_SPACING, h3ob.getCenterY() - h2ob.getHeight() / 2 );
        h2ob = h2oMoleculeNode.getFullBoundsReference();
        
        // center labels below the icons
        final double y = Math.max( h3ob.getMaxY(), Math.max( ohb.getMaxY(), h2ob.getMaxY() ) ) + Y_SPACING;
        h3oLabelNode.setOffset( h3ob.getCenterX() - h3oLabelNode.getFullBoundsReference().getWidth() / 2, y );
        ohLabelNode.setOffset( ohb.getCenterX() - ohLabelNode.getFullBoundsReference().getWidth() / 2, y );
        h2oLabelNode.setOffset( h2ob.getCenterX() - h2oLabelNode.getFullBoundsReference().getWidth() / 2, y + 7 ); // +7 is a hack to align baselines of HTML nodes
    }
}
