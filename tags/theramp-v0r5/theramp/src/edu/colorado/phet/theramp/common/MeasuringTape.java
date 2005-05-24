/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 20, 2005
 * Time: 10:42:18 PM
 * Copyright (c) May 20, 2005 by Sam Reid
 */

public class MeasuringTape extends GraphicLayerSet {
    private ModelViewTransform2D modelViewTransform2D;
    private Point2D.Double modelSrc;
    private Point2D.Double modelDst;
    public BodyGraphic bodyGraphic;
    public TapeGraphic tapeGraphic;
    public EndGraphic endGraphic;
    public ReadoutGraphic readoutGraphic;

    public MeasuringTape( Component component, ModelViewTransform2D modelViewTransform2D, Point2D.Double modelSrc ) {
        super( component );
        this.modelViewTransform2D = modelViewTransform2D;
        this.modelSrc = modelSrc;
        this.modelDst = new Point2D.Double( modelSrc.x + modelViewTransform2D.viewToModelDifferentialX( 100 ), modelSrc.y );

        bodyGraphic = new BodyGraphic();
        tapeGraphic = new TapeGraphic();
        endGraphic = new EndGraphic();
        readoutGraphic = new ReadoutGraphic( "m" );


        addGraphic( tapeGraphic );
        addGraphic( bodyGraphic );

        addGraphic( endGraphic );
        addGraphic( readoutGraphic );

        update();
    }

    class BodyGraphic extends CompositePhetGraphic {
        public BodyGraphic() {
            super( MeasuringTape.this.getComponent() );
            PhetImageGraphic imageGraphic = new PhetImageGraphic( getComponent(), "images/tape.gif" );
            setRegistrationPoint( imageGraphic.getWidth(), imageGraphic.getHeight() );
            addGraphic( imageGraphic );

            addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    MeasuringTape.this.translateAll( translationEvent.getDx(), translationEvent.getDy() );
                }
            } );
            CrossHairGraphic crossHairGraphic = new CrossHairGraphic( getComponent(), 10 );
            addGraphic( crossHairGraphic );
            crossHairGraphic.setLocation( imageGraphic.getWidth(), imageGraphic.getHeight() );
            setCursorHand();
        }
    }

    private void translateAll( int dx, int dy ) {
        Point2D modelTx = modelViewTransform2D.viewToModelDifferential( dx, dy );
        modelSrc.x += modelTx.getX();
        modelSrc.y += modelTx.getY();
        modelDst.x += modelTx.getX();
        modelDst.y += modelTx.getY();
        update();
    }

    private void update() {
        Point viewSrc = modelViewTransform2D.modelToView( modelSrc );
        Point viewDst = modelViewTransform2D.modelToView( modelDst );
        Vector2D.Double viewVector = new Vector2D.Double( viewSrc, viewDst );
        bodyGraphic.setTransform( AffineTransform.getRotateInstance( viewVector.getAngle() ) );
        Line2D.Double line = new Line2D.Double( viewSrc, viewDst );
        tapeGraphic.setLine( line );
        bodyGraphic.setLocation( viewSrc );
        endGraphic.setLocation( viewDst );

        double modelDistance = new Vector2D.Double( modelSrc, modelDst ).getMagnitude();
        readoutGraphic.setDistance( modelDistance );
        readoutGraphic.setLocation( viewSrc.x, (int)( viewSrc.y + readoutGraphic.getHeight() * 1.2 ) );
    }

    class TapeGraphic extends CompositePhetGraphic {
        public PhetShapeGraphic phetShapeGraphic;

        public TapeGraphic() {
            super( MeasuringTape.this.getComponent() );
            phetShapeGraphic = new PhetShapeGraphic( getComponent(), null, new BasicStroke( 2 ), Color.black );
            addGraphic( phetShapeGraphic );

        }

        public void setLine( Line2D.Double line ) {
            phetShapeGraphic.setShape( line );
        }
    }

    class EndGraphic extends CompositePhetGraphic {

        public EndGraphic() {
            super( MeasuringTape.this.getComponent() );
            Ellipse2D.Double shape = new Ellipse2D.Double( 0, 0, 15, 15 );

            PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( getComponent(), shape, Color.black );
            addGraphic( phetShapeGraphic );
            setRegistrationPoint( phetShapeGraphic.getWidth() / 2, phetShapeGraphic.getHeight() / 2 );
            addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    MeasuringTape.this.translateEndPoint( translationEvent.getDx(), translationEvent.getDy() );
                }
            } );
            setCursorHand();

            CrossHairGraphic crossHairGraphic = new CrossHairGraphic( getComponent(), 10 );
            addGraphic( crossHairGraphic );

            crossHairGraphic.setLocation( phetShapeGraphic.getWidth() / 2, phetShapeGraphic.getHeight() / 2 );
        }
    }

    private void translateEndPoint( int dx, int dy ) {
        Point2D modelDX = modelViewTransform2D.viewToModelDifferential( dx, dy );
        modelDst.x += modelDX.getX();
        modelDst.y += modelDX.getY();
        update();
    }

    class ReadoutGraphic extends CompositePhetGraphic {
        DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        private String units;
        public PhetShadowTextGraphic phetShadowTextGraphic;

        public ReadoutGraphic( String units ) {
            super( MeasuringTape.this.getComponent() );
            this.units = units;
            phetShadowTextGraphic = new PhetShadowTextGraphic( getComponent(), new Font( "Lucida Sans", Font.BOLD, 14 ), "", Color.black, 1, 1, Color.gray );
            addGraphic( phetShadowTextGraphic );
        }

        public void setDistance( double modelDistance ) {
            String text = decimalFormat.format( modelDistance ) + " " + units;
            phetShadowTextGraphic.setText( text );
        }
    }

    static class CrossHairGraphic extends CompositePhetGraphic {
        private int length;

        public CrossHairGraphic( Component component, int length ) {
            super( component );
            this.length = length;
            PhetShapeGraphic verticalStroke = new PhetShapeGraphic( component, new Line2D.Double( length / 2, 0, length / 2, length ), new BasicStroke( 2 ), Color.red );
            PhetShapeGraphic horizStroke = new PhetShapeGraphic( component, new Line2D.Double( 0, length / 2, length, length / 2 ), new BasicStroke( 2 ), Color.red );
            addGraphic( verticalStroke );
            addGraphic( horizStroke );
            setRegistrationPoint( length / 2, length / 2 );
            setIgnoreMouse( true );
        }
    }
}
