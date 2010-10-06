package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 * @author Sam Reid
 */
public class SymbolIndicatorNode extends PNode {
    private final Atom atom;

    public SymbolIndicatorNode( final Atom atom, double width, double height ) {
        this.atom = atom;
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), Color.white, new BasicStroke( 1 ), Color.black ) );
        addChild( new PText( atom.getSymbol() ) {{
            atom.addObserver( new SimpleObserver() {
                public void update() {
                    setText( atom.getSymbol() );
                }
            } );
        }} );
    }
}
