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

import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.category.BarRenderer;
//import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.ui.RectangleInsets;
//import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * PowerMeter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PowerMeterGraphic extends GraphicLayerSet {

    public PowerMeterGraphic( Component component, LaserModel model, final PartialMirror rightMirror ) throws HeadlessException {
        super( component );

        PhetImageGraphic bezelImageGraphic = new PhetImageGraphic( component, LaserConfig.POWER_METER_IMAGE );
        addGraphic( bezelImageGraphic );

        int leftInset = 70;
//        int leftInset = 20;
        int topInset = 30;
        int middleInset = 25;

        Dimension meterWindowSize = new Dimension( 200, 30 );

        final Meter cavityMeter = new InsideMeter( component,
                                             meterWindowSize,
                                             Meter.HORIZONTAL,
                                             0, LaserConfig.KABOOM_THRESHOLD * 1.1 );
        cavityMeter.setLocation( leftInset, topInset );
        addGraphic( cavityMeter );

        final Meter outsideMeter = new OutsideMeter( component,
                                              meterWindowSize,
                                              Meter.HORIZONTAL,
                                              0, LaserConfig.KABOOM_THRESHOLD * 1.1 );
        outsideMeter.setLocation( leftInset,
                                  (int)(cavityMeter.getLocation().getY() + meterWindowSize.getHeight() + middleInset ));
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
    private static class Meter extends CompositePhetGraphic {
        final static Object HORIZONTAL = new Object();
        final static Object VERTICAL = new Object();

        Object orientation;
        private double max;
        private double min;
        private Insets insets;
        Rectangle2D background;
        Paint backgroundColor = Color.black;
        PhetShapeGraphic[] segments;
        Paint belowLasingPaint = Color.white;
        Paint lasingPaint = Color.green;
        Paint aboveLasingPaint = Color.red;
        int segmentWidth = 3;
        int interSegmentSpace = 1;
        double dangerThreshold = LaserConfig.KABOOM_THRESHOLD * 0.8;
        double scale;

        public Meter( Component component, Dimension size, Object orientation, double min, double max ) {
            super( component );

            boolean validOrientation = false;
            if( orientation == VERTICAL ) {
                validOrientation = true;
                insets = new Insets( 0, 5, 0, 5 );
            }
            if( orientation == HORIZONTAL ) {
                validOrientation = true;
                insets = new Insets( 5, 0, 5, 0 );
            }
            if( validOrientation ) {
                this.orientation = orientation;
            }
            else {
                throw new IllegalArgumentException();
            }

            setRange( min, max );

            // The background
            background = new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() );
            PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component, background, backgroundColor );
            addGraphic( backgroundGraphic );

            // Determine the scale
            scale = LaserConfig.KABOOM_THRESHOLD / background.getWidth();

            // Create the segments
            int numSegments = (int)( background.getWidth() / ( segmentWidth + interSegmentSpace ) );
            segments = new PhetShapeGraphic[numSegments];
            for( int i = 0; i < segments.length; i++ ) {
                RoundRectangle2D rr = new RoundRectangle2D.Double( background.getX() + i * ( segmentWidth + interSegmentSpace ),
                                                                   background.getY() + insets.top,
                                                                   segmentWidth,
                                                                   background.getHeight() - insets.top - insets.bottom,
                                                                   3, 3 );
                PhetShapeGraphic psg = new PhetShapeGraphic( component, rr, backgroundColor );
                segments[i] = psg;
                addGraphic( psg );
            }
//                double segmentValue = i * ( segmentWidth + interSegmentSpace ) * scale;
//                Paint paint = null;
//                if( segmentValue < LaserConfig.LASING_THRESHOLD ) {
//                    paint = belowLasingPaint;
//                }
//                else if( segmentValue >= LaserConfig.LASING_THRESHOLD
//                         && segmentValue < dangerThreshold ) {
//                    paint = lasingPaint;
//                }
//                else if( segmentValue >= dangerThreshold ) {
//                    paint = aboveLasingPaint;
//                }
//                else {
//                    paint = backgroundColor;
//                }
//                segments[i].setPaint( paint );
//            }

            update( 0 );
            setBoundsDirty();
            repaint();
        }

        public void setRange( double min, double max ) {
            this.min = min;
            this.max = max;
        }

        public void update( double value ) {
            for( int i = 0; i < segments.length; i++ ) {
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
        public OutsideMeter( Component component, Dimension size, Object orientation, double min, double max ) {
            super( component, size, orientation, min, max );

            // Color the segments
            for( int i = 0; i < segments.length; i++ ) {
                segments[i].setPaint( Color.white );
            }
        }
    }

    private static class InsideMeter extends Meter {
        public InsideMeter( Component component, Dimension size, Object orientation, double min, double max ) {
            super( component, size, orientation, min, max );

            // Color the segments
            for( int i = 0; i < segments.length; i++ ) {
                double segmentValue = i * ( segmentWidth + interSegmentSpace ) * scale;
                Paint paint = null;
                if( segmentValue < LaserConfig.LASING_THRESHOLD ) {
                    paint = belowLasingPaint;
                }
                else if( segmentValue >= LaserConfig.LASING_THRESHOLD
                         && segmentValue < dangerThreshold ) {
                    paint = lasingPaint;
                }
                else if( segmentValue >= dangerThreshold ) {
                    paint = aboveLasingPaint;
                }
                else {
                    paint = backgroundColor;
                }
                segments[i].setPaint( paint );
            }

            // Add threshold lines to the bezel
            int lasingThresholdLocX = (int)(LaserConfig.LASING_THRESHOLD / scale);
            Rectangle2D.Double rect = new Rectangle2D.Double(0,background.getHeight(),1,15);
            PhetShapeGraphic lasingThresholdIndicator = new PhetShapeGraphic( component,
                                                                              rect,
                                                                              new Color( 255, 255, 255 ));
            lasingThresholdIndicator.setLocation( lasingThresholdLocX, 0);
            addGraphic( lasingThresholdIndicator );

            PhetShapeGraphic dangerThresholdIndicator = new PhetShapeGraphic( component,
                                                                              rect,
                                                                              new Color( 255, 255, 255 ));
            int dangerThresholdLocX = (int)(dangerThreshold / scale);
            dangerThresholdIndicator.setLocation( dangerThresholdLocX, 0);
            addGraphic( dangerThresholdIndicator );

            // Text annotations
            String lasingStr = "Lasing";
            Font font = new Font( "Lucida Sans", Font.PLAIN, 10 );
            PhetTextGraphic lasingAnnotation = new PhetTextGraphic();
            lasingAnnotation.setComponent( component );
            lasingAnnotation.setFont( font );
            lasingAnnotation.setJustification( PhetTextGraphic.SOUTH );
            lasingAnnotation.setText( lasingStr );
            lasingAnnotation.setColor( Color.white );
            lasingAnnotation.setLocation( ( lasingThresholdLocX + dangerThresholdLocX ) / 2,
                                         (int)background.getHeight()+ 15 );
            addGraphic( lasingAnnotation );

            String dangerStr = "Danger";
            PhetTextGraphic dangerAnnotation = new PhetTextGraphic();
            dangerAnnotation.setComponent( component );
            dangerAnnotation.setFont( font );
            dangerAnnotation.setJustification( PhetTextGraphic.SOUTH );
            dangerAnnotation.setText( dangerStr );
            dangerAnnotation.setColor( Color.white );
            dangerAnnotation.setLocation( (int)( dangerThresholdLocX + background.getWidth() ) / 2,
                                         (int)background.getHeight()+ 15 );
            addGraphic( dangerAnnotation );
        }
    }
}
