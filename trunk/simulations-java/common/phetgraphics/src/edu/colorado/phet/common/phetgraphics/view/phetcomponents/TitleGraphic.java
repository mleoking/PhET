/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14674 $
 * Date modified : $Date:2007-04-17 02:37:37 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.phetgraphics.view.phetcomponents;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 4:27:46 PM
 *
 */

public class TitleGraphic extends CompositePhetGraphic {
    private String title;
    private PhetGraphic target;
    private PhetTextGraphic textGraphic;

    public TitleGraphic( Component component, String title, PhetGraphic target ) {
        super( component );
        this.title = title;
        this.target = target;
        textGraphic = new PhetTextGraphic( component, new Font( "Lucida Sans", 0, 16 ), title, Color.black, 0, 0 );
        addGraphic( textGraphic );
        target.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                update();
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                setVisible( phetGraphic.isVisible() );
            }
        } );
        update();
    }

    private void update() {
        Rectangle targetBounds = target.getBounds();
        if( targetBounds != null ) {
            setLocation( targetBounds.x, targetBounds.y - getHeight() );
        }
    }

    public void setTitle( String title ) {
        textGraphic.setText( title );
        update();
    }
}