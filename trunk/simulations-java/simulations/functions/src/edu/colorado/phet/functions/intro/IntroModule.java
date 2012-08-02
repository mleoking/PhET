package edu.colorado.phet.functions.intro;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.functions.buildafunction2.BuildAFunctionPrototype2Canvas;

import static edu.colorado.phet.functions.FunctionModule.radioButton;

/**
 * @author Sam Reid
 */
public class IntroModule extends PiccoloModule {
    public IntroModule() {
        super( "Intro", new ConstantDtClock() );

        setSimulationPanel( new IntroCanvas() {{
            setBackground( BuildAFunctionPrototype2Canvas.BACKGROUND_COLOR );
            addScreenChild( new HBox( radioButton( "Shapes", false ), radioButton( "Text", true ), radioButton( "Numbers", false ) ) {{
                setOffset( INSET, INSET );
            }} );
            addScreenChild( new ResetAllButtonNode( new Resettable() {
                public void reset() {
                }
            }, this, new PhetFont( 18, true ), Color.black, Color.orange ) {{
                setOffset( 1024 - this.getFullWidth() - INSET - INSET - INSET, 768 - this.getFullHeight() - INSET - 40 - 40 - 10 - INSET );
            }} );
        }} );
        setClockControlPanel( null );
    }
}