/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view.monitors;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.*;
import edu.colorado.phet.lasers.LasersConfig;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

/**
 * PowerMeter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PowerMeterGraphic extends GraphicLayerSet {

    public PowerMeterGraphic( Component component, LaserModel model, final PartialMirror rightMirror ) throws HeadlessException {
        super( component );

        PhetImageGraphic bezelImageGraphic = new PhetImageGraphic( component, LasersResources.getImage( LasersConfig.POWER_METER_IMAGE ) );
        addGraphic( bezelImageGraphic );

        int leftInset = 70;
        int topInset = 30;
        int middleInset = 25;

        Dimension meterWindowSize = new Dimension( 200, 30 );

        final Meter cavityMeter = new InsideMeter( component,
                                                   meterWindowSize,
                                                   Meter.HORIZONTAL );
        cavityMeter.setLocation( leftInset, topInset );
        addGraphic( cavityMeter );

        final Meter outsideMeter = new OutsideMeter( component,
                                                     meterWindowSize,
                                                     Meter.HORIZONTAL );
        outsideMeter.setLocation( leftInset,
                                  (int) ( cavityMeter.getLocation().getY() + meterWindowSize.getHeight() + middleInset ) );
        addGraphic( outsideMeter );


        model.addLaserListener( new LaserModel.ChangeListenerAdapter() {
            public void lasingPopulationChanged( LaserModel.ChangeEvent event ) {
                super.lasingPopulationChanged( event );
                cavityMeter.update( event.getLasingPopulation() );
                outsideMeter.update( event.getLasingPopulation() * ( 1 - rightMirror.getReflectivity() ) );
            }
        } );

        setBoundsDirty();
        repaint();
    }

    /**
     *
     */
    private abstract static class Meter extends CompositePhetGraphic {
        final static Object HORIZONTAL = new Object();
        final static Object VERTICAL = new Object();

        Object orientation;
        private Insets insets;
        Rectangle2D background;
        Paint backgroundColor = Color.black;
        PhetShapeGraphic[] segments;
        Paint belowLasingPaint = Color.white;
        Paint lasingPaint = Color.green;
        Paint aboveLasingPaint = Color.red;
        int segmentWidth = 3;
        int interSegmentSpace = 1;
        double dangerThreshold = LasersConfig.KABOOM_THRESHOLD * 0.75;
        double scale;

        public Meter( Component component, Dimension size, Object orientation ) {
            super( component );

            boolean validOrientation = false;
            if ( orientation == VERTICAL ) {
                validOrientation = true;
                insets = new Insets( 0, 5, 0, 5 );
            }
            if ( orientation == HORIZONTAL ) {
                validOrientation = true;
                insets = new Insets( 5, 0, 5, 0 );
            }
            if ( validOrientation ) {
                this.orientation = orientation;
            }
            else {
                throw new IllegalArgumentException();
            }

            // The background
            background = new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() );
            PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component, background, backgroundColor );
            addGraphic( backgroundGraphic );

            // Determine the scale
            scale = LasersConfig.KABOOM_THRESHOLD / background.getWidth();

            // Create the segments
            int numSegments = (int) ( background.getWidth() / ( segmentWidth + interSegmentSpace ) );
            segments = new PhetShapeGraphic[numSegments];
            for ( int i = 0; i < segments.length; i++ ) {
                RoundRectangle2D rr = new RoundRectangle2D.Double( background.getX() + i * ( segmentWidth + interSegmentSpace ),
                                                                   background.getY() + insets.top,
                                                                   segmentWidth,
                                                                   background.getHeight() - insets.top - insets.bottom,
                                                                   3, 3 );
                PhetShapeGraphic psg = new PhetShapeGraphic( component, rr, backgroundColor );
                segments[i] = psg;
                addGraphic( psg );
            }

            update( 0 );
            setBoundsDirty();
            repaint();
        }

        public void update( double value ) {
            for ( int i = 0; i < segments.length; i++ ) {
                PhetShapeGraphic segment = segments[i];
                double segmentValue = i * ( segmentWidth + interSegmentSpace ) * scale;
                boolean isSegmentLit = value > segmentValue;
                segment.setVisible( isSegmentLit );
            }
            setBoundsDirty();
            repaint();
        }
    }

    private static class OutsideMeter extends Meter {
        public OutsideMeter( Component component, Dimension size, Object orientation ) {
            super( component, size, orientation );

            // Color the segments
            for ( int i = 0; i < segments.length; i++ ) {
                segments[i].setPaint( Color.white );
            }
        }
    }

    private static class InsideMeter extends Meter {
        public InsideMeter( Component component, Dimension size, Object orientation ) {
            super( component, size, orientation );

            // Color the segments
            for ( int i = 0; i < segments.length; i++ ) {
                double segmentValue = i * ( segmentWidth + interSegmentSpace ) * scale;
                Paint paint = null;
                if ( segmentValue < LasersConfig.LASING_THRESHOLD ) {
                    paint = belowLasingPaint;
                }
                else if ( segmentValue >= LasersConfig.LASING_THRESHOLD
                          && segmentValue < dangerThreshold ) {
                    paint = lasingPaint;
                }
                else if ( segmentValue >= dangerThreshold ) {
                    paint = aboveLasingPaint;
                }
                else {
                    paint = backgroundColor;
                }
                segments[i].setPaint( paint );
            }

            // Add threshold lines to the bezel
            int lasingThresholdLocX = (int) ( LasersConfig.LASING_THRESHOLD / scale );
            Rectangle2D.Double rect = new Rectangle2D.Double( 0, background.getHeight(), 2, 15 );
            PhetShapeGraphic lasingThresholdIndicator = new PhetShapeGraphic( component,
                                                                              rect,
                                                                              new Color( 255, 255, 255 ) );
            lasingThresholdIndicator.setLocation( lasingThresholdLocX, 0 );
            addGraphic( lasingThresholdIndicator );

            PhetShapeGraphic dangerThresholdIndicator = new PhetShapeGraphic( component,
                                                                              rect,
                                                                              new Color( 255, 255, 255 ) );
            int dangerThresholdLocX = (int) ( dangerThreshold / scale );
            dangerThresholdIndicator.setLocation( dangerThresholdLocX, 0 );
            addGraphic( dangerThresholdIndicator );

            // Text annotations
            String lasingStr = LasersResources.getString( "PowerMeter.Lasing" );
            Font font = new PhetFont( Font.BOLD, 12 );
            PhetTextGraphic lasingAnnotation = new PhetTextGraphic();
            lasingAnnotation.setComponent( component );
            lasingAnnotation.setFont( font );
            lasingAnnotation.setJustification( PhetTextGraphic.SOUTH );
            lasingAnnotation.setText( lasingStr );
            lasingAnnotation.setColor( Color.green );
            lasingAnnotation.setLocation( ( lasingThresholdLocX + dangerThresholdLocX ) / 2,
                                          (int) background.getHeight() + 15 );
            addGraphic( lasingAnnotation );

            String dangerStr = LasersResources.getString( "PowerMeter.Danger" );
            PhetTextGraphic dangerAnnotation = new PhetTextGraphic();
            dangerAnnotation.setComponent( component );
            dangerAnnotation.setFont( font );
            dangerAnnotation.setJustification( PhetTextGraphic.SOUTH );
            dangerAnnotation.setText( dangerStr );
            dangerAnnotation.setColor( Color.red );
            dangerAnnotation.setLocation( (int) ( dangerThresholdLocX + background.getWidth() ) / 2 + 10,
                                          (int) background.getHeight() + 15 );
            addGraphic( dangerAnnotation );
        }
    }
}
