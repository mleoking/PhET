/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.ScatterPlot;
import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.BufferedGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.common.InteractivePhetGraphic;
import edu.colorado.phet.forces1d.common.PhetGraphicAdapter;
import edu.colorado.phet.forces1d.model.DataSeries;
import edu.colorado.phet.forces1d.model.MMTimer;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:16:32 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DPanel extends ApparatusPanel {
    private Forces1DModule module;
    private BlockGraphic blockGraphic;
    private ModelViewTransform2D transform2D;
    private Chart chart;
    private LinearTransform1d walkwayTransform;
    private BufferedGraphic bufferedGraphic;

    public Force1DPanel( final Forces1DModule module ) throws IOException {
        this.module = module;
        walkwayTransform = new LinearTransform1d( -10, 10, 0, 400 );
        blockGraphic = new BlockGraphic( this, module.getForceModel().getBlock(), transform2D );

        WalkwayGraphic walkwayGraphic = new WalkwayGraphic( module, 21, getWalkwayTransform() );
        addGraphic( walkwayGraphic );
        addGraphic( blockGraphic );

//        addChartTestGraphic();

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }
        } );
        DataSeries ds = new DataSeries();
        ForcePlot plot = new ForcePlot( this, "testplot", module, ds, new MMTimer( "test" ), Color.blue, new BasicStroke( 1 ),
                                        new Rectangle2D.Double( 0, 0, 100, 100 ), 0, "units", "label" );
        addGraphic( plot);
    }

    private void addChartTestGraphic() {

        Range2D range = new Range2D( 0, -10, 20, 10 );
        InteractivePhetGraphic testy = new InteractivePhetGraphic( this );
        chart = new Chart( this, range, new Rectangle( 50, 50 ) );
//        addGraphic( new TestGraphic( this ) );

//        addGraphic( chart, 10 );
        addGraphicsSetup( new BasicGraphicsSetup() );
        PhetGraphicAdapter adapter = new PhetGraphicAdapter( this, chart ) {
            protected Rectangle determineBounds() {
                return chart.getVisibleBounds();
            }
        };
        testy.addGraphic( adapter );
        testy.addCursorHandBehavior();
        testy.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Rectangle r = chart.getViewBounds();
                r.translate( (int)dx, (int)dy );
                chart.setViewBounds( r );
            }
        } );
        addGraphic( testy, 100 );
        chart.addDataSetGraphic( new ScatterPlot( new DataSet() ) );
        Runnable r = new Runnable() {

            public void run() {
                try {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }

                module.getModel().addModelElement( new ModelElement() {
                    double time = 0;

                    public void stepInTime( double dt ) {
                        time += dt;
                        chart.dataSetGraphicAt( 0 ).getDataSet().addPoint( time / 20, 3.5 );
                        repaint( chart.getVisibleBounds() );
                    }
                } );
            }
        };
        new Thread( r ).start();
    }

    private void relayout() {
//        int inset = 20;
//        chart.setViewBounds( new Rectangle( 0 + inset, getHeight() / 2 + inset, getWidth() - inset * 2, getHeight() / 2 - inset * 2 ) );
        int insetX = 50;
        walkwayTransform.setOutput( 0 + insetX, getWidth() - insetX );
    }

    public LinearTransform1d getWalkwayTransform() {
        return walkwayTransform;
    }

    public BufferedGraphic getBufferedGraphic() {
        return bufferedGraphic;
    }
}
