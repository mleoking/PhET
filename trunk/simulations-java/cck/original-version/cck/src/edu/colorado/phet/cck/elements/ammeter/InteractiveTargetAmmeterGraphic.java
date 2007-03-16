/** Sam Reid*/
package edu.colorado.phet.cck.elements.ammeter;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 19, 2004
 * Time: 12:13:37 AM
 * Copyright (c) May 19, 2004 by Sam Reid
 */
public class InteractiveTargetAmmeterGraphic extends DefaultInteractiveGraphic {
    public InteractiveTargetAmmeterGraphic( final TargetAmmeterGraphic graphic ) {
        super( graphic );
        addCursorHandBehavior();
        addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Point2D.Double modelDX = graphic.getTransform().viewToModelDifferential( (int)dx, (int)dy );
                graphic.translate( modelDX.x, modelDX.y );
            }
        } );
    }

}
