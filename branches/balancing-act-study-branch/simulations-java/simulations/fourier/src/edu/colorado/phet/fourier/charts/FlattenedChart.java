// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;


/**
 * FlattenedChart flattens a Chart into a static image.
 * The image is updated whenever the Chart's size or range is changed.
 * If you make other changes to the Chart (eg, adding or removing
 * DataSetGraphics, changing tickmarks and gridlines) then you 
 * must call flatten to refresh the image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FlattenedChart extends PhetImageGraphic {

    private Chart _chart;
    private int _xOffset, _yOffset;
    
    /**
     * Sole constructor.
     * 
     * @param component parent Component
     * @param chart     Chart to be flattened
     * @param xOffset   offset between the left edge of the Chart's bounds and its x registration point
     * @param yOffset   offset between the top edge of the Chart's bounds and its y registration point
     */
    public FlattenedChart( Component component, Chart chart, int xOffset, int yOffset ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        assert( chart != null );
        _chart = chart;
        _chart.setLocation( 0, 0 );
        _chart.setRegistrationPoint( 0, 0 );
        _chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                flatten();
            }
        });
        
        _xOffset = xOffset;
        _yOffset = yOffset;
        
        flatten();
    }
    
    /**
     * Flattens the Chart into a static image.
     */
    public void flatten() {
        _chart.setBoundsDirty();
        int width = _chart.getWidth() + _xOffset;
        int height = _chart.getHeight() + _yOffset;
        if ( width > 0 && height > 0 ) {
            BufferedImage bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g2 = bufferedImage.createGraphics();
            RenderingHints hints = getRenderingHints();
            if ( hints != null ) {
                g2.setRenderingHints( getRenderingHints() );
            }
            g2.translate( _xOffset, _yOffset );
            _chart.paint( g2 );
            setImage( bufferedImage );
        }
        else {
            setImage( null );
        }
    }
}
