// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Canvas for the "Three Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsCanvas extends MPCanvas {

    public ThreeAtomsCanvas( Frame parentFrame, Resettable... resettable ) {
        super();
        addChild( new PText( "Under Construction" ) {{
            setFont( new PhetFont( 38 ) );
            setOffset( 0, 300 );
        }} );
    }
}
