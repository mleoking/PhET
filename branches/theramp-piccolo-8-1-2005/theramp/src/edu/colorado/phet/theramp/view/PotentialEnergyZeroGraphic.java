/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.theramp.model.RampModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:59:25 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */

public class PotentialEnergyZeroGraphic extends PNode {
    private RampModel rampModel;
    private RampWorld rampWorld;
    private RampPanel rampPanel;
    private PPath phetShapeGraphic;
    private PText label;

    public PotentialEnergyZeroGraphic( RampPanel component, final RampModel rampModel, final RampWorld rampWorld ) {
        super();
        this.rampPanel = component;
        this.rampModel = rampModel;
        this.rampWorld = rampWorld;
//        phetShapeGraphic = new PhetShapeGraphic( component, new Line2D.Double( 0, 0, 1000, 0 ),new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{20, 20}, 0 ), Color.black );
        phetShapeGraphic = new PPath( new Line2D.Double( 0, 0, 1000, 0 ), new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{20, 20}, 0 ) );
        phetShapeGraphic.setPaint( Color.black );
        addChild( phetShapeGraphic );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                changeZeroPoint( event );
            }
        } );
        //todo piccolo
//        addTranslationListener( new TranslationListener() {
//            public void translationOccurred( TranslationEvent translationEvent ) {
//                changeZeroPoint( translationEvent );
//            }
//        } );
        RampModel.Listener listener = new RampModel.Listener() {
            public void appliedForceChanged() {
            }

            public void zeroPointChanged() {
                setOffset( 0, rampWorld.getRampGraphic().getScreenTransform().modelToViewY( rampModel.getZeroPointY() ) );
                updateLabel();
            }

            public void stepFinished() {
            }
        };
        rampModel.addListener( listener );

        //setCursorHand();
        label = new PText( "h=???" );
        addChild( label );
//        label.setLocation( 10, -label.getHeight() - 4 );
//        label.setLocation( 10, 0);//-label.getHeight() - 4 );
//        label.setLocation( 10, (int)( label.getHeight()*.075 ) );//-label.getHeight() - 4 );
        label.setOffset( 10, label.getHeight() - 15 );//-label.getHeight() - 4 );
//        listener.zeroPointChanged();
        updateLabel();
    }

    private void updateLabel() {
        String str = new DecimalFormat( "0.0" ).format( rampModel.getZeroPointY() );
//        label.setText( "h=0.0 @ y=" + str );
        label.setText( "y=0.0" );
    }

    private void changeZeroPoint( PInputEvent pEvent ) {
        Point pt = rampWorld.convertToWorld( pEvent.getPosition() );
        double zeroPointY = rampPanel.getRampGraphic().getScreenTransform().viewToModelY( pt.y );
        rampModel.setZeroPointY( zeroPointY );
    }
}
