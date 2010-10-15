package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class IonIndicatorNode extends PNode {
    private static final Font ATOM_ION_FONT = new PhetFont( 28, true );

    public IonIndicatorNode( final Atom atom ) {
        addChild( new PText( "Ion" ) {{       //todo: i18n
            setFont( ATOM_ION_FONT );
            setTextPaint( Color.blue );
            atom.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( atom.getCharge() != 0 &&
                                atom.getNumElectrons()!=atom.getNumParticles() );//don't show the ion indicator when only electrons are present
                    setText( atom.getCharge()>0?"+ Ion":"- Ion" );//todo: i18n
                    setTextPaint( atom.getCharge()>0?Color.red:Color.blue );
                }
            } );
        }} );
    }
}
