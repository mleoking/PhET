// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * C12H22O11 label to be shown on top of each sucrose molecule (even if there are many in a crystal), used in CompoundListNode
 *
 * @author Sam Reid
 */
public class SucroseLabel extends Option.Some<Function0<PNode>> {
    public SucroseLabel() {
        super( new Function0<PNode>() {
            public PNode apply() {
                return new PImage( new HTMLNode( "C<sub>12</sub>H<sub>22</sub>O<sub>11</sub>" ) {{ setFont( new PhetFont( 19, true ) ); }}.toImage() );
            }
        } );
    }
}