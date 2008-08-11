package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.eatingandexercise.view.LabelNode;

/**
 * Created by: Sam
 * Aug 11, 2008 at 1:14:23 PM
 */
public class WarningMessage extends LabelNode {
    public WarningMessage( String text ) {
        super( text );
        setFont( new PhetFont( 30, true ) );
    }
}
