/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.QuickTimer;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 9:23:32 PM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class SRRWavelengthSlider extends GraphicLayerSet {
    private SpectrumSliderKnob spectrumSliderKnob;
    private Function.LinearFunction linearFunction;

    public SRRWavelengthSlider( Component component ) {
        super( component );
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
        addGraphic( new PhetImageGraphic( component, image ) );

        spectrumSliderKnob = new SpectrumSliderKnob( component, new Dimension( 20, 20 ), 0 );
        addGraphic( spectrumSliderKnob );
        spectrumSliderKnob.setLocation( 0, image.getHeight() );
        spectrumSliderKnob.setCursorHand();
        spectrumSliderKnob.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                int dx = translationEvent.getDx();
                int newX = spectrumSliderKnob.getX() + dx;
                newX = (int)MathUtil.clamp( 0, newX, image.getWidth() );
                spectrumSliderKnob.setLocation( newX, image.getHeight() );
                dragPointChanged();
            }
        } );

        PhetTextGraphic phetTextGraphic = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), "Wavelength", Color.blue );
        addGraphic( phetTextGraphic );
        phetTextGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        phetTextGraphic.setLocation( 0, -phetTextGraphic.getHeight() - 5 );
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
        spectrumSliderKnob.setLocation( image.getWidth() / 2, image.getHeight() );
        dragPointChanged();
    }

    private void dragPointChanged() {
        double wavelength = getWavelength();
//        System.out.println( "x = " + x + ", wav=" + wavelength );
        spectrumSliderKnob.setColor( new VisibleColor( wavelength ) );
        ChangeEvent e = new ChangeEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );

            changeListener.stateChanged( e );
        }
    }

    public double getWavelength() {
        int x = spectrumSliderKnob.getX();
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
