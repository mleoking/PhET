package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class IonIndicatorNode extends PNode {
    private static final Font ATOM_ION_FONT = new PhetFont( 28, true );

    public IonIndicatorNode( final Atom atom, final BooleanProperty showLabels ) {
        addChild( new PText(BuildAnAtomStrings.POSITIVE_ION) {{       //dummy text is never shown, just used for initial layout size
            setFont( ATOM_ION_FONT );
            setTextPaint( Color.blue );
            final SimpleObserver observer = new SimpleObserver() {
                public void update() {
                    setVisible( showLabels.getValue() &&
                                atom.getCharge() != 0 &&
                                atom.getNumProtons() > 0 );//don't show the ion indicator when only electrons are present
                    setText( atom.getCharge() > 0 ? BuildAnAtomStrings.POSITIVE_ION : BuildAnAtomStrings.NEGATIVE_ION );
                    setTextPaint( atom.getCharge() > 0 ? Color.red : Color.blue );
                }
            };
            atom.addObserver( observer );
            showLabels.addObserver( observer );
        }} );
    }
}
