// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * May 21, 2008 at 10:53:52 AM
 */
public class EatingAndExercisePText extends PText {
    public EatingAndExercisePText() {
        setFont( new PhetFont() );
    }

    public EatingAndExercisePText( String aText ) {
        super( aText );
        setFont( new PhetFont() );
    }
}
