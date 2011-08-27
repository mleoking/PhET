// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SaltIon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.sugarandsaltsolutions.water.view.SucroseLabel.FONT;

/**
 * Creates a label to be shown on top of each salt ion (such as Cl- or Na+), used in CompoundListNode
 *
 * @author Sam Reid
 */
public class SaltIonLabel extends Option.Some<Function1<SaltIon, PNode>> {
    public SaltIonLabel() {
        super( new Function1<SaltIon, PNode>() {
            public PNode apply( SaltIon ion ) {

                //Convert to image for improved performance
                return new PImage( new HTMLNode( ion.getName() ) {{ setFont( FONT ); }}.toImage() );
            }
        } );
    }
}