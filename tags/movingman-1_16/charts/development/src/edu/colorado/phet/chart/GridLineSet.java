/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;

import java.awt.*;

public class GridLineSet extends CompositePhetGraphic {
    private Grid minorGrid;
    private Grid majorGrid;

    public GridLineSet( Chart chart, int orientation ) {
        this( chart, orientation, 1, 2, 0 );
    }

    public void setMinorTickSpacing( double minorTickSpacing ) {
        minorGrid.setSpacing( minorTickSpacing );
    }

    public void setMajorTickSpacing( double majorTickSpacing ) {
        majorGrid.setSpacing( majorTickSpacing );
    }

    public GridLineSet( Chart chart, int orientation, int minorTickSpacing, int majorTickSpacing, int crossesOtherAxisAt ) {
        minorGrid = new Grid( chart, orientation, new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{7, 7}, 0 ),
                              Color.black, minorTickSpacing, crossesOtherAxisAt );
        majorGrid = new Grid( chart, orientation, new BasicStroke( 1 ),
                              Color.black, majorTickSpacing, crossesOtherAxisAt );
        minorGrid.setVisible( false );
        addGraphic( minorGrid );
        addGraphic( majorGrid );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
    }

    private void update() {
        minorGrid.update();
        majorGrid.update();
    }

    public void setMajorGridlinesVisible( boolean visible ) {
        majorGrid.setVisible( visible );
    }

    public void setMinorGridlinesVisible( boolean visible ) {
        minorGrid.setVisible( visible );
    }

    public void setMajorGridlinesColor( Color color ) {
        majorGrid.setColor( color );
    }

    public void setMinorGridlinesColor( Color color ) {
        minorGrid.setColor( color );
    }

    public void setMajorGridlinesStroke( Stroke stroke ) {
        majorGrid.setStroke( stroke );
    }

    public void setMinorGridlinesStroke( Stroke stroke ) {
        minorGrid.setStroke( stroke );
    }

    public void setMajorGridlines( double[] lines ) {
        majorGrid.setGridlines( lines );
    }
}
