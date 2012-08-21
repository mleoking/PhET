package edu.colorado.phet.functions.intro;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionPrototype2Canvas;
import edu.colorado.phet.functions.buildafunction.ValueContext;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.intro.view.NavigationBar;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends AbstractFunctionsCanvas implements ValueContext, SceneContext {

    private Scene scene;
    private final ResetAllButtonNode resetAllButtonNode;

    public IntroCanvas() {

        setBackground( BuildAFunctionPrototype2Canvas.BACKGROUND_COLOR );
        addChild( new NavigationBar() {{
            setOffset( INSET, INSET );
        }} );
        resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
            }
        }, this, new PhetFont( 18, true ), Color.black, Color.orange ) {{
            setOffset( 1024 - this.getFullWidth() - INSET - INSET - INSET, 768 - this.getFullHeight() - INSET - 40 - 40 - 10 - INSET );
        }};
        addScreenChild( resetAllButtonNode );

        scene = new Scene1( this );
        addChild( scene );
    }

    public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        scene.mouseDragged( valueNode, delta );
    }

    public void mouseReleased( final ValueNode valueNode ) {
        scene.mouseReleased( valueNode );
    }

    public void showNextButton() {
        final HTMLImageButtonNode nextButton = new HTMLImageButtonNode( "Next" ) {{
            setFont( resetAllButtonNode.getFont() );
            setBackground( Color.green );
            setOffset( resetAllButtonNode.getFullBounds().getX(), resetAllButtonNode.getFullBounds().getMinY() - getFullBounds().getHeight() - 10 );
        }};
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                IntroCanvas.this.removeChild( scene );
                scene = new Scene2( IntroCanvas.this );
                IntroCanvas.this.addChild( scene );
                IntroCanvas.this.removeChild( nextButton );
            }
        } );
        addChild( nextButton );
    }

}