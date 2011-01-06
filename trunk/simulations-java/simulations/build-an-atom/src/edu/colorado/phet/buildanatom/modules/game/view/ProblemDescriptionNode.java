package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

//DOC
/**
 * @author Sam Reid
 */
public class ProblemDescriptionNode extends PText {
    public ProblemDescriptionNode( String text ) {
        super( text );
        setFont( new PhetFont( 34, true ) );
    }
    public void centerAbove( PNode guessingNode){
        setOffset( guessingNode.getFullBounds().getCenterX()-getFullBounds().getWidth()/2,guessingNode.getFullBounds().getMinY()-60);
    }
}
