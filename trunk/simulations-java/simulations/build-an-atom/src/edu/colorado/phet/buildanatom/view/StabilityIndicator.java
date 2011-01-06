package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

//DOC
/**
 * @author Sam Reid
 */
public class StabilityIndicator extends PNode {

    public StabilityIndicator( final SimpleAtom atom, final BooleanProperty showLabels ) {
        final PText child = new PText( BuildAnAtomStrings.UNSTABLE ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.black );
        }};
        addChild( child );
        SimpleObserver updateVisibility = new SimpleObserver() {
            public void update() {
                setVisible( showLabels.getValue() && atom.getAtomicMassNumber() > 0 );
                if (atom.isStable()){
                    child.setText( BuildAnAtomStrings.STABLE );
                }else{
                    child.setText( BuildAnAtomStrings.UNSTABLE );
                }
            }
        };
        atom.addObserver( updateVisibility );
        showLabels.addObserver( updateVisibility );
        updateVisibility.update();
    }
}
