/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.QuickTimer;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 9:23:32 PM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class SRRWavelengthSlider extends PNode {
    private SpectrumSliderKnob spectrumSliderKnob;
    private Function.LinearFunction linearFunction;

    public SRRWavelengthSlider( Component component ) {
        super();
        double minWavelength = VisibleColor.MIN_WAVELENGTH;
        double maxWavelength = VisibleColor.MAX_WAVELENGTH;

        QuickTimer quickTimer = new QuickTimer();
        final BufferedImage image = new BufferedImage( 200, 10, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        linearFunction = new Function.LinearFunction( 0, image.getWidth(), minWavelength, maxWavelength );
        for( int i = 0; i < image.getWidth(); i++ ) {
            double wavelength = linearFunction.evaluate( i );
            VisibleColor visibleColor = new VisibleColor( wavelength );
            g2.setColor( visibleColor );
            g2.fillRect( i, 0, 1, image.getHeight() );
        }
        System.out.println( "image construction time= " + quickTimer );
        addChild( new PImage( image ) );


        spectrumSliderKnob = new SpectrumSliderKnob( component, new Dimension( 20, 20 ), 0 );
        //todo piccolo
        addChild( spectrumSliderKnob );
        spectrumSliderKnob.setOffset( 0, image.getHeight() );
        spectrumSliderKnob.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        //todo piccolo
        spectrumSliderKnob.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                Point2D pt = event.getPositionRelativeTo( SRRWavelengthSlider.this );
                double newX = (int)MathUtil.clamp( 0, pt.getX(), image.getWidth() );
                spectrumSliderKnob.setOffset( new Point2D.Double( newX, image.getHeight() ) );
//                double dx = event.getDeltaRelativeTo( SRRWavelengthSlider.this ).getWidth();
//                System.out.println( "dx = " + dx );
//                double newX = spectrumSliderKnob.getFullBounds().getX() + dx;

//                System.out.println( "newX = " + newX );
//                spectrumSliderKnob.setOffset( new Point2D.Double( newX, image.getHeight() ) );
                dragPointChanged();
            }
        } );
//        spectrumSliderKnob.addTranslationListener( new TranslationListener() {
//            public void translationOccurred( TranslationEvent translationEvent ) {
//                int dx = translationEvent.getDx();
//                int newX = spectrumSliderKnob.getX() + dx;
//                newX = (int)MathUtil.clamp( 0, newX, image.getWidth() );
//                spectrumSliderKnob.setLocation( newX, image.getHeight() );
//                dragPointChanged();
//            }
//        } );

        PText phetTextGraphic = new PText( "Wavelength" );
        addChild( phetTextGraphic );
        //todo piccolo
//        phetTextGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        phetTextGraphic.setOffset( 0, -phetTextGraphic.getHeight() - 5 );
//        spectrumSliderKnob.addMouseInputListener( new MouseInputAdapter() {
//            boolean dragging = false;
//            Point startPoint = null;
//            int startX = 0;
//
//            // implements java.awt.event.MouseMotionListener
//            public void mouseDragged( MouseEvent e ) {
//                if( !dragging ) {
//                    startDrag( e );
//                }
//                else {
//                    Point curPt = e.getPoint();
//                    int dx = curPt.x - startX-startPoint.x;
////                    int x = spectrumSliderKnob.getX();
//                    int newX = startX + dx;
//                    newX = (int)MathUtil.clamp( 0, newX, image.getWidth() );
//                    System.out.println( "newX = " + newX );
//                    spectrumSliderKnob.setLocation( newX, image.getHeight() );
//                    dragPointChanged();
////                    startPoint = e.getPoint();
//                }
//            }
//
//            // implements java.awt.event.MouseListener
//            public void mouseExited( MouseEvent e ) {
//                endDrag();
//            }
//
//            // implements java.awt.event.MouseMotionListener
//            public void mouseMoved( MouseEvent e ) {
//                endDrag();
//            }
//
//            // implements java.awt.event.MouseListener
//            public void mousePressed( MouseEvent e ) {
//                startDrag( e );
//            }
//
//            // implements java.awt.event.MouseListener
//            public void mouseReleased( MouseEvent e ) {
//                endDrag();
//            }
//
//            private void endDrag() {
//                dragging = false;
//                startPoint = null;
//            }
//
//            private void startDrag( MouseEvent event ) {
//                dragging = true;
//                startPoint = event.getPoint();
//                startX = spectrumSliderKnob.getX();
//            }
//        } );
        spectrumSliderKnob.setOffset( image.getWidth() / 2, image.getHeight() );
        dragPointChanged();
    }

    private void dragPointChanged() {
        double wavelength = getWavelength();
//        System.out.println( "x = " + x + ", wav=" + wavelength );
        spectrumSliderKnob.setPaint( new VisibleColor( wavelength ) );
        ChangeEvent e = new ChangeEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );
            changeListener.stateChanged( e );
        }
    }

    public double getWavelength() {
//        double x = spectrumSliderKnob.getX();
        double x = spectrumSliderKnob.getOffset().getX();
        double wavelength = linearFunction.evaluate( x );
        return wavelength;
    }

    private ArrayList listeners = new ArrayList();

    public void addChangeListener( ChangeListener changeListener ) {
        listeners.add( changeListener );
    }

    public void removeChangeListener( ChangeListener changeListener ) {
        listeners.remove( changeListener );
    }
}
