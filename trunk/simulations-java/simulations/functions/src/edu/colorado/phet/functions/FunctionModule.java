// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions;

import java.awt.Color;

import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FunctionModule extends PiccoloModule {

    //    private static final Color CREAM = new Color( 247, 243, 183 );
    private static final Color CREAM = new Color( 255, 252, 209 );//a little brighter
    public static final int INSET = 10;

    public FunctionModule( final String name ) {
        super( name, new ConstantDtClock() );
        setSimulationPanel( new PhetPCanvas() {{
            setBackground( CREAM );
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

    private PNode radioButton( final String shapes, boolean selected ) {
        return new PSwing( new JRadioButton( shapes, selected ) {{
            setFont( new PhetFont( 18, true ) );
            setOpaque( false );
        }} );
    }
}