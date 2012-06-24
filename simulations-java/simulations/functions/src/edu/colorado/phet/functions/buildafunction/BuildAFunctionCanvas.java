package edu.colorado.phet.functions.buildafunction;

import java.awt.Color;

/**
 * @author Sam Reid
 */
public class BuildAFunctionCanvas extends AbstractFunctionCanvas {
    public BuildAFunctionCanvas() {
        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );
        addChild( new DraggableToken<Integer>( 2 ) );
        addChild( new DraggableToken<String>( "x" ) );

        addChild( new FunctionBoxWithText( "*2" ) {{
            setOffset( 400, 100 );
        }} );
        addChild( new FunctionBoxWithText( "+1" ) {{
            setOffset( 400, 275 );
        }} );

        addChild( new FunctionBox() {{
            setOffset( 400, 275 + 175 );
        }} );
    }
}