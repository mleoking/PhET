/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceView;
import edu.colorado.phet.forces1d.model.Force1DModel;

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
    private Force1DModule module;
    private BlockGraphic blockGraphic;
    private ArrowSetGraphic arrowSetGraphic;
    private ModelViewTransform2D transform2D;
    private Chart chart;
    private LinearTransform1d walkwayTransform;
    private BufferedPhetGraphic bufferedPhetGraphic;
    private PlotDevice appliedForcePlotDevice;
    private WalkwayGraphic walkwayGraphic;
    private ManGraphic manGraphic;
    private Force1DModel model;
    private PlotDeviceView plotDeviceView;

    public Force1DPanel( final Force1DModule module ) throws IOException {
        this.module = module;
        this.model = module.getForceModel();
        addGraphicsSetup( new BasicGraphicsSetup() );
        walkwayTransform = new LinearTransform1d( -10, 10, 0, 400 );
        walkwayGraphic = new WalkwayGraphic( this, module, 21, getWalkwayTransform() );
        blockGraphic = new BlockGraphic( this, module.getForceModel().getBlock(), model, transform2D, walkwayTransform );
        arrowSetGraphic = new ArrowSetGraphic( this, blockGraphic, model, transform2D );
        manGraphic = new ManGraphic( this );
        addGraphic( walkwayGraphic );
        addGraphic( blockGraphic );
//        addGraphic( manGraphic, 1000 );
        manGraphic.setLocation( 400, 100 );

        addGraphic( arrowSetGraphic );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }

            public void componentHidden( ComponentEvent e ) {
                relayout();
            }
        } );

        plotDeviceView = new Force1DPlotDeviceView( module, this );
        PlotDevice.ParameterSet params = new PlotDevice.ParameterSet( this, "Applied Force", model.getPlotDeviceModel(),
                                                                      plotDeviceView, model.getAppliedForceDataSeries().getSmoothedDataSeries(), model.getPlotDeviceModel().getRecordingTimer(),
                                                                      Color.blue, new BasicStroke( 2 ),
                                                                      new Rectangle2D.Double( 0, -10, model.getPlotDeviceModel().getMaxTime(), 20 ),
                                                                      0, "N", "applied force" );
        appliedForcePlotDevice = new PlotDevice( params );
        appliedForcePlotDevice.setLabelText( "<html>Applied<br>Force</html>");

        appliedForcePlotDevice.addDataSeries( model.getAppliedForceDataSeries().getSmoothedDataSeries(), Color.green, "applied f",new BasicStroke( 2) );
        addGraphic( appliedForcePlotDevice );
        appliedForcePlotDevice.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                double appliedForce = value;
                model.setAppliedForce( appliedForce );
            }
        } );

        bufferedPhetGraphic = new BufferedPhetGraphic( this, new Graphic() {
            public void paint( Graphics2D g ) {
            }
        }, Color.white );
        relayout();
    }

//    public void repaint( int x, int y, int width, int height ) {
//        new Exception("Repaint").printStackTrace( );
//        super.repaint( x, y, width, height );
//
//    }
//
//    public void repaint( Rectangle r ) {
//        new Exception("Repaint").printStackTrace( );
//        super.repaint( r );
//    }

    public void relayout() {
        if( getWidth() > 0 && getHeight() > 0 ) {
            bufferedPhetGraphic.setSize( getWidth(), getHeight() );
            int insetX = 50;
            walkwayTransform.setOutput( 0 + insetX, getWidth() - insetX );
            int plotInsetX = 200;
            int plotWidth = getWidth() - plotInsetX - 25;
            int y = walkwayGraphic.getHeight() + 20 + walkwayGraphic.getY();
            int yInsetBottom = appliedForcePlotDevice.getChart().getHorizontalTicks().getMajorTickTextBounds().height * 2;
            Rectangle newViewBounds = new Rectangle( plotInsetX, y + yInsetBottom, plotWidth, getHeight() - y - yInsetBottom * 2 );
//            System.out.println( "newViewBounds = " + newViewBounds );
            appliedForcePlotDevice.setViewBounds( newViewBounds );
            updateGraphics();
            repaint();
        }
    }

    public LinearTransform1d getWalkwayTransform() {
        return walkwayTransform;
    }

    public BufferedPhetGraphic getBufferedGraphic() {
        return bufferedPhetGraphic;
    }

    public WalkwayGraphic getWalkwayGraphic() {
        return walkwayGraphic;
    }

    public void updateGraphics() {
        arrowSetGraphic.updateGraphics();
        blockGraphic.update();
    }

    public void reset() {
        appliedForcePlotDevice.reset();
    }
}
