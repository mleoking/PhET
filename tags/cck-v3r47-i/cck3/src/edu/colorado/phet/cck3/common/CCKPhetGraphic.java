package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 29, 2004
 * Time: 3:31:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CCKPhetGraphic extends PhetGraphic {
    public CCKPhetGraphic( Component component ) {
        super( component );
        setVisible( false );
    }
}
