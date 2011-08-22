// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Creates a C12H22O11 label to be shown on top of each sucrose molecule (even if there are many in a crystal), used in CompoundListNode
 *
 * @author Sam Reid
 */
public class SucroseLabel extends Option.Some<Function1<Sucrose, PNode>> {

    public static final PhetFont FONT = new PhetFont( 19, true );

    //Create the function used to label each sucrose molecule
    //All sucrose molecules have the same label, so ignore the argument to the function1
    public SucroseLabel() {
        super( new Function1<Sucrose, PNode>() {
            public PNode apply( Sucrose sucrose ) {
                return new PImage( new HTMLNode( "C<sub>12</sub>H<sub>22</sub>O<sub>11</sub>" ) {{ setFont( FONT ); }}.toImage() );
            }
        } );
    }
}