// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.geom.Line2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.util.SmartDoubleFormat;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Tick mark is a horizontal line with a label to the left of the line.
 * The label can be switched between showing a value and qualitative text.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TickMarkNode extends PComposite {

    private final PNode valueNode, qualityNode;

    public TickMarkNode( double value, String units, String qualityLabel, final PhetFont labelFont, double tickLength, SmartDoubleFormat format ) {

        // nodes
        PNode tickNode = new PPath( new Line2D.Double( -tickLength, 0, 0, 0 ) );
        String valueString = MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, format.format( value ), units );
        valueNode = new PText( valueString ) {{
            setFont( labelFont );
        }};
        qualityNode = new PText( qualityLabel ) {{
            setFont( labelFont );
        }};

        // rendering order
        addChild( tickNode );
        addChild( valueNode );
        addChild( qualityNode );

        // layout
        valueNode.setOffset( tickNode.getFullBoundsReference().getMinX() - valueNode.getFullBoundsReference().getWidth() - 3,
                             -( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        qualityNode.setOffset( tickNode.getFullBoundsReference().getMinX() - qualityNode.getFullBoundsReference().getWidth() - 3,
                               -( qualityNode.getFullBoundsReference().getHeight() / 2 ) );

        // default state
        setValueVisible( true );
    }

    public void setValueVisible( boolean visible ) {
        valueNode.setVisible( visible );
        qualityNode.setVisible( !visible );
    }
}
