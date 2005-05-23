/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.model.RampModel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

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
    private PhetShadowTextGraphic label;

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
                updateLabel();
            }
        };
        rampModel.addListener( listener );

        setCursorHand();
        label = new PhetShadowTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 18 ), "h=???", Color.black, 1, 1, Color.gray );
        addGraphic( label );
        label.setLocation( 10, -label.getHeight() - 4 );
        listener.zeroPointChanged();
        updateLabel();
    }

    private void updateLabel() {
        String str = new DecimalFormat( "0.0" ).format( rampModel.getZeroPointY() );
        label.setText( "h=0.0 @ y=" + str );
    }

    private void changeZeroPoint( TranslationEvent translationEvent ) {
        double zeroPointY = rampPanel.getRampGraphic().getScreenTransform().viewToModelY( translationEvent.getY() );
        rampModel.setZeroPointY( zeroPointY );
    }
}
