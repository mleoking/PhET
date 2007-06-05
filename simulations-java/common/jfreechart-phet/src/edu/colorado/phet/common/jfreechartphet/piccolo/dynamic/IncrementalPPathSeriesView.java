package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;

import java.awt.*;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 6:04:09 PM
 */
public class IncrementalPPathSeriesView extends SeriesView {
    //    private BasicStroke stroke = new BasicStroke( 4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
    private IncrementalPPath incrementalPPath;

    public IncrementalPPathSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
        incrementalPPath = new IncrementalPPath( dynamicJFreeChartNode.getPhetPCanvas() );
        incrementalPPath.setStroke( new BasicStroke( 1) );
        incrementalPPath.setStrokePaint( Color.blue);
    }

    public void dataAdded() {
        int itemCount = getSeries().getItemCount();
        if( incrementalPPath.getPathReference().getCurrentPoint() == null ) {
            incrementalPPath.moveTo( (float)getNodePoint( itemCount - 1 ).getX(), (float)getNodePoint( itemCount - 1 ).getY() );
        }
        else if( getSeries().getItemCount() >= 2 ) {
            incrementalPPath.lineTo( (float)getNodePoint( itemCount - 1 ).getX(), (float)getNodePoint( itemCount - 1 ).getY() );
        }
    }

    public void uninstall() {
        super.uninstall();
        super.getDynamicJFreeChartNode().removeChild( incrementalPPath );
    }

    public void install() {
        super.install();
        getDynamicJFreeChartNode().addChild( incrementalPPath);
    }
}
