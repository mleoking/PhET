// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;

public class GridLineSet extends CompositePhetGraphic {
    private Grid minorGrid;
    private Grid majorGrid;

    public GridLineSet( Chart chart, Orientation orientation ) {
        this( chart, orientation, 1, 2, 0 );
    }

    public void setMinorTickSpacing( double minorTickSpacing ) {
        minorGrid.setSpacing( minorTickSpacing );
    }

    public void setMajorTickSpacing( double majorTickSpacing ) {
        majorGrid.setSpacing( majorTickSpacing );
    }

    public GridLineSet( Chart chart, Orientation orientation, double minorTickSpacing, double majorTickSpacing, double crossesOtherAxisAt ) {
        minorGrid = new Grid( chart, orientation, new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 7, 7 }, 0 ),
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

}
