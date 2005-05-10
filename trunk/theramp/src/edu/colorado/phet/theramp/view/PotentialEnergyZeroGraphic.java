/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.model.RampModel;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:59:25 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */

public class PotentialEnergyZeroGraphic extends CompositePhetGraphic {
    private RampModel rampModel;
    private RampPanel rampPanel;
    private PhetShapeGraphic phetShapeGraphic;

    public PotentialEnergyZeroGraphic( RampPanel component, final RampModel rampModel ) {
        super( component );
        this.rampPanel = component;
        this.rampModel = rampModel;
        phetShapeGraphic = new PhetShapeGraphic( component, new Line2D.Double( 0, 0, 1000, 0 ),
                                                 new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{20, 20}, 0 ), Color.black );
        addGraphic( phetShapeGraphic );
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                changeZeroPoint( translationEvent );
            }
        } );
        RampModel.Listener listener = new RampModel.Listener() {
            public void appliedForceChanged() {
            }

            public void zeroPointChanged() {
                setLocation( 0, rampPanel.getRampGraphic().getScreenTransform().modelToViewY( rampModel.getZeroPointY() ) );
            }
        };
        rampModel.addListener( listener );
        listener.zeroPointChanged();
        setCursorHand();
    }

    private void changeZeroPoint( TranslationEvent translationEvent ) {
        double zeroPointY = rampPanel.getRampGraphic().getScreenTransform().viewToModelY( translationEvent.getY() );
        rampModel.setZeroPointY( zeroPointY );
    }
}
