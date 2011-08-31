// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.AnyChangeAdapter;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays a 3D view for molecules that are "real" versions of the currently visible VSEPR model
 */
public class RealMoleculePanelNode extends PNode {

    private PNode child = null;
    private final MoleculeModel molecule;
    private final double SIZE = MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH;
    private PhetPPath background;

    public RealMoleculePanelNode( MoleculeModel molecule ) {
        this.molecule = molecule;
        // padding, and make sure we have the width
        background = new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE, SIZE ), new Color( 0.1f, 0.1f, 0.1f, 1f ) );
        addChild( background );

        updateChild();

        molecule.addListener( new AnyChangeAdapter() {
            @Override public void onGroupChange( PairGroup group ) {
                updateChild();
            }
        } );
    }

    public PBounds getOverlayBounds() {
        // TODO: fix
        return background.getGlobalFullBounds();
    }

    private void updateChild() {
        if ( child != null ) {
            removeChild( child );
        }

        // get the list of real molecules that correspond to our VSEPR model
        List<RealMolecule> molecules = RealMolecule.getMatchingMolecules( molecule );

        // blank node
        // TODO i18n
//        child = new PText( "(none)" ){{
//            setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
//        }};
//
//        // center the text
//        child.setOffset( ( SIZE - child.getWidth() ) / 2, ( SIZE - child.getHeight() ) / 2 );
//
//        addChild( child );

        repaint();
    }

    private static <A, B> List<B> map( List<A> list, Function1<A, B> map ) {
        // TODO: move to somewhere more convenient
        List<B> result = new ArrayList<B>();
        for ( A element : list ) {
            result.add( map.apply( element ) );
        }
        return result;
    }

    private PText labelText( String str ) {
        return new PText( str ) {{
            setFont( new PhetFont( 14 ) );
            setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        }};
    }
}
