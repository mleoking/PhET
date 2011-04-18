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
 * This Piccolo node shows a textual label to indicate whether the atom is stable or unstable.
 *
 * @author Sam Reid
 */
public class StabilityIndicator extends PNode {

    public StabilityIndicator( final IDynamicAtom atom, final BooleanProperty showLabels ) {
        final PText child = new PText( BuildAnAtomStrings.UNSTABLE ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.black );
        }};
        addChild( child );
        final AtomListener updateVisibility = new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                setVisible( showLabels.getValue() && atom.getMassNumber() > 0 );
                if (atom.isStable()){
                    child.setText( BuildAnAtomStrings.STABLE );
                }else{
                    child.setText( BuildAnAtomStrings.UNSTABLE );
                }
            }
        };
        atom.addAtomListener( updateVisibility );
        showLabels.addObserver( new SimpleObserver(){
            public void update(){
                updateVisibility.configurationChanged();
            }
        } );
        updateVisibility.configurationChanged(); // Initial update.
    }
}
