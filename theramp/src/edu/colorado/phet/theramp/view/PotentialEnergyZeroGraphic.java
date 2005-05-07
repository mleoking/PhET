/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:59:25 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */

public class PotentialEnergyZeroGraphic extends CompositePhetGraphic {
    public PotentialEnergyZeroGraphic( Component component ) {
        super( component );
        PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( component, new Rectangle( 0, 0, 1000, 1 ),
                                                                  new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{20, 20}, 0 ), Color.black );
        addGraphic( phetShapeGraphic );
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        } );
        setCursorHand();
    }
}
