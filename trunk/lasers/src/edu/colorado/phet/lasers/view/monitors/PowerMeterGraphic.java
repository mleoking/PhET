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

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
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

/**
 * PowerMeter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PowerMeterGraphic extends GraphicLayerSet {
    private Paint belowLasingPaint = Color.green;
    private Paint aboveLasingPaint = Color.red;

    public PowerMeterGraphic( Component component, LaserModel model, final PartialMirror rightMirror ) throws HeadlessException {
        super( component );

        PhetImageGraphic bezelImageGraphic = new PhetImageGraphic( component, LaserConfig.POWER_METER_IMAGE );
        addGraphic( bezelImageGraphic );

        int leftInset = 20;
        int topInset = 30;
        int middleInset = 25;
        final Meter cavityMeter = new Meter( component,
                                       new Dimension( 200, 30),
                                       Meter.HORIZONTAL,
                                       0, LaserConfig.KABOOM_THRESHOLD * 1.1 );
        cavityMeter.setLocation( leftInset, topInset );
        addGraphic( cavityMeter );

        final Meter outsideMeter = new Meter( component,
                                       new Dimension( 200, 30),
                                       Meter.HORIZONTAL,
                                       0, LaserConfig.KABOOM_THRESHOLD * 1.1 );
        outsideMeter.setLocation( leftInset, (int)cavityMeter.getLocation().getY() + cavityMeter.getHeight() + middleInset );
        addGraphic( outsideMeter );

        model.addLaserListener( new LaserModel.ChangeListenerAdapter() {
            public void lasingPopulationChanged( LaserModel.ChangeEvent event ) {
                super.lasingPopulationChanged( event );
                cavityMeter.update( event.getLasingPopulation() );
                outsideMeter.update( event.getLasingPopulation() * ( 1 - rightMirror.getReflectivity() ));
            }
        });

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
        private Rectangle2D background;
        private Paint backgroundColor = Color.black;
        private Paint readoutColor = Color.red;
        private Rectangle readoutBar;

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
                throw new IllegalArgumentException( );
            }

            setRange( min, max );

            // The background
            background = new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() );
            PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component, background, backgroundColor );
            addGraphic( backgroundGraphic);

            // The readout bar
            readoutBar = new Rectangle();
            PhetShapeGraphic readoutGraphic = new PhetShapeGraphic( component, readoutBar, readoutColor );
            addGraphic( readoutGraphic );


            setBoundsDirty();
            repaint();
        }

        public void setRange( double min, double max ) {
            this.min = min;
            this.max = max;
        }

        public void paint( Graphics2D g2 ) {
            super.paint( g2 );
        }

        public void update( double value ) {
            double displayValue = Math.min( value, max );

            if( orientation == HORIZONTAL ) {
                int barLength = (int)( background.getWidth() * ( displayValue / (max - min)));
                readoutBar.setRect( background.getX() + insets.left ,
                                    background.getY() + insets.top,
                                    barLength - insets.right,
                                    background.getHeight() - insets.top - insets.bottom );
            }
            if( orientation == VERTICAL ) {
                int barLength = (int)( background.getHeight() * ( displayValue / (max - min)));
                readoutBar.setRect( background.getX() + insets.left,
                                    background.getY() + background.getHeight() - barLength + insets.top,
                                    background.getWidth() - insets.left - insets.right,
                                    barLength - insets.bottom );
            }
            setBoundsDirty();
            repaint();
        }
    }
}
