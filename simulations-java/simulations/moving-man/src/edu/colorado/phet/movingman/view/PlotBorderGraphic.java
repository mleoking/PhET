/*  */
package edu.colorado.phet.movingman.view;

import java.awt.*;

import edu.colorado.phet.common_movingman.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common_movingman.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common_movingman.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common_movingman.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.movingman.plots.MMPlotSuite;

/**
 * User: Sam Reid
 * Date: Apr 18, 2005
 * Time: 10:12:30 PM
 */

public class PlotBorderGraphic extends CompositePhetGraphic {
    private MMPlotSuite plotSuite;
    private PhetShapeGraphic borderGraphic;

    public PlotBorderGraphic( MovingManApparatusPanel movingManApparatusPanel, MMPlotSuite plotSuite ) {
        super( movingManApparatusPanel );
        this.plotSuite = plotSuite;
        borderGraphic = new PhetShapeGraphic( movingManApparatusPanel, null, new BasicStroke( 1 ), Color.gray );
        addGraphic( borderGraphic );
        plotSuite.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                update();
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );
        update();
        setIgnoreMouse( true );

        plotSuite.addListener( new MMPlotSuite.Listener() {
            public void plotVisibilityChanged() {
                update();
            }

            public void valueChanged( double value ) {
            }
        } );
    }

    private void update() {
        Rectangle rect = createRectangle();
        borderGraphic.setShape( rect );
    }

    private Rectangle createRectangle() {
        return plotSuite.getBorderRectangle();
    }

    public void setSelected( boolean selected ) {
        if ( selected ) {
            borderGraphic.setBorderColor( Color.red );
        }
        else {
            borderGraphic.setBorderColor( Color.black );
        }
    }
}
