/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.BufferedGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.forces1d.Forces1DModule;
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
    private PlotDevice plotDevice;
    private WalkwayGraphic walkwayGraphic;

    public Force1DPanel( final Forces1DModule module ) throws IOException {
        this.module = module;
        walkwayTransform = new LinearTransform1d( -10, 10, 0, 400 );
        blockGraphic = new BlockGraphic( this, module.getForceModel().getBlock(), transform2D );

        walkwayGraphic = new WalkwayGraphic( this, module, 21, getWalkwayTransform() );
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
        plotDevice = new PlotDevice( this, "testplot", module, ds, new MMTimer( "test" ), Color.blue, new BasicStroke( 1 ),
                                       new Rectangle2D.Double( 0, -10, module.getMaxTime(), 20 ), 0, "units", "label" );
        addGraphic( plotDevice );
        relayout();
        addGraphicsSetup( new BasicGraphicsSetup() );
    }

    private void relayout() {
        if( getWidth() > 0 && getHeight() > 0 ) {
            int insetX = 50;
            walkwayTransform.setOutput( 0 + insetX, getWidth() - insetX );
            int plotInsetX = 150;
            int plotWidth=getWidth()-plotInsetX-25;
            int y = walkwayGraphic.getHeight() + 20+walkwayGraphic.getY();
            int yInsetBottom=plotDevice.getChart().getHorizontalTicks().getMajorTickTextBounds().height*2;
            plotDevice.setViewBounds( plotInsetX, y+yInsetBottom, plotWidth, getHeight() - y-yInsetBottom*2 );
        }
    }

    public LinearTransform1d getWalkwayTransform() {
        return walkwayTransform;
    }

    public BufferedGraphic getBufferedGraphic() {
        return bufferedGraphic;
    }
}
