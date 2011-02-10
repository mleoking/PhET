// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * @author Sam Reid
 */
public class ControlPanelNode extends PNode {
    public static final PhetFont labelFont = new PhetFont( 16 );

    public ControlPanelNode( final PNode content ) {
        final double inset = 9;

        final PhetPPath background = new PhetPPath( Color.lightGray, new BasicStroke( 2 ), Color.darkGray ) {{
            final PropertyChangeListener updateSize = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final PBounds b = content.getFullBounds();
                    setPathTo( new RoundRectangle2D.Double( 0, 0, b.getWidth() + inset * 2, b.getHeight() + inset * 2, 20, 20 ) );
                }
            };
            content.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateSize );
            updateSize.propertyChange( null );
        }};
        content.setOffset( inset, inset );
        addChild( background );
        addChild( content );
    }
}