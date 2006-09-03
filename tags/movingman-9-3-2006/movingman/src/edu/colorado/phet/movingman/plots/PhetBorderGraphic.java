/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 18, 2005
 * Time: 4:31:51 PM
 * Copyright (c) Apr 18, 2005 by Sam Reid
 */

public class PhetBorderGraphic extends CompositePhetGraphic {
    private PhetGraphic target;
    private Stroke stroke;
    private Color color;
    private PhetShapeGraphic phetShapeGraphic;

    public PhetBorderGraphic( PhetGraphic target, Color color, Stroke stroke ) {
        super( target.getComponent() );
        this.target = target;
        this.stroke = stroke;
        this.color = color;
        target.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                update();
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                update();
            }
        } );
        phetShapeGraphic = new PhetShapeGraphic( getComponent(), null, stroke, color );
        addGraphic( phetShapeGraphic );
        update();
    }

    private void update() {
        setVisible( target.isVisible() );
        if( isVisible() ) {
            Rectangle rect = target.getBounds();
            phetShapeGraphic.setShape( rect );
        }
    }

}
