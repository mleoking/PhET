// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.Color;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This Piccolo node shows a textual label to indicate whether the atom is
 * stable or unstable.
 *
 * So that it is easier to position, the center of this node is at offset
 * (0,0), rather than the more common upper left edge.
 *
 * @author Sam Reid
 */
public class StabilityIndicator extends PNode {

    public StabilityIndicator( final IDynamicAtom atom, final BooleanProperty showLabels ) {
        final PText stabilityText = new PText( BuildAnAtomStrings.UNSTABLE ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.black );
        }};
        addChild( stabilityText );
        final AtomListener updateText = new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                setVisible( showLabels.getValue() && atom.getMassNumber() > 0 );
                if (atom.isStable()){
                    stabilityText.setText( BuildAnAtomStrings.STABLE );
                }else{
                    stabilityText.setText( BuildAnAtomStrings.UNSTABLE );
                }
                // Adjust the offset so that the center of this node is at (0,0).
                stabilityText.setOffset(
                        -stabilityText.getFullBoundsReference().width / 2,
                        -stabilityText.getFullBoundsReference().height / 2 );
            }
        };
        atom.addAtomListener( updateText );
        showLabels.addObserver( new SimpleObserver(){
            public void update(){
                updateText.configurationChanged();
            }
        } );
        updateText.configurationChanged(); // Initial update.
    }
}
