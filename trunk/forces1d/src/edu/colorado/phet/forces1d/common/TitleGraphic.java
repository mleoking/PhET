/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 4:27:46 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
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
            }
        } );
        update();
    }

    private void update() {
        Rectangle targetBounds = target.getBounds();
        setLocation( targetBounds.x, targetBounds.y - getHeight() );
    }

    public void setTitle( String title ) {
        textGraphic.setText( title );
        update();
    }
}
