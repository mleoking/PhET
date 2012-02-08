// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A control panel node with a specific title that is moved over the top border
 * TODO: match other constructors?
 * TODO: title fonts?
 */
public class TitledControlPanelNode extends ControlPanelNode {

    public static final int DEFAULT_INSET = 9;

    protected PhetPPath titleBackground;

    public TitledControlPanelNode( final PNode content, final String title, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor ) {
        this( content, title, backgroundColor, borderStroke, borderColor, DEFAULT_INSET );
    }

    public TitledControlPanelNode( final PNode content, final String title, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor, final int inset ) {
        this( content, new TitleNode( title ), backgroundColor, borderStroke, borderColor, inset );
    }

    public TitledControlPanelNode( final PNode content, final PNode titleNode, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor ) {
        this( content, titleNode, backgroundColor, borderStroke, borderColor, DEFAULT_INSET );
    }

    public TitledControlPanelNode( final PNode content, final PNode titleNode, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor, final int inset ) {
        super( content, backgroundColor, borderStroke, borderColor, inset );

        final ControlPanelNode controlPanelNode = this;

        // title
        background.addChild( 0, new PNode() {{
            // background to block out border
            titleBackground = new PhetPPath( padBoundsHorizontally( titleNode.getFullBounds(), 10 ), backgroundColor );
            addChild( titleBackground );
            addChild( titleNode );
            setOffset( ( controlPanelNode.getFullBounds().getWidth() - titleNode.getFullBounds().getWidth() ) / 2,
                       -titleNode.getFullBounds().getHeight() / 2 );
        }} );
    }

    public static class TitleNode extends PText {
        public TitleNode( String title ) {
            super( title );

            setFont( MoleculeShapesConstants.CONTROL_PANEL_TITLE_FONT );

            // TODO: better way of doing this?
            MoleculeShapesColor.CONTROL_PANEL_TITLE.getProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setTextPaint( MoleculeShapesColor.CONTROL_PANEL_TITLE.get() );
                }
            } );
        }
    }

    private static PBounds padBoundsHorizontally( PBounds bounds, double amount ) {
        return new PBounds( bounds.x - amount, bounds.y, bounds.width + 2 * amount, bounds.height );
    }
}
