package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.common_force1d.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.force1d_tag_chart.Chart;
import edu.colorado.phet.force1d_tag_chart.Range2D;
import edu.colorado.phet.forces1d.common.phetcomponents.PhetTextField;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 27, 2004
 * Time: 12:14:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlotDevice2 extends GraphicLayerSet {
    private GoPauseClear goPauseClear;
    private Chart chart;
    private PhetTextField textField;
    private static final Font textFieldFont = new Font( PhetDefaultFont.LUCIDA_SANS, 0, 28 );

    public PlotDevice2( Component component, Range2D chartRange ) {
        super( component );
        goPauseClear = new GoPauseClear( component );
        Rectangle viewBounds = new Rectangle( 50, 50, 200, 200 );
        chart = new Chart( component, chartRange, viewBounds );
        textField = new PhetTextField( component, textFieldFont, "", Color.black );


        addGraphic( chart );
        addGraphic( textField );
        addGraphic( goPauseClear );

//        goPauseClear.setLocation( 0,0);
        textField.setLocation( 0, 0 );
        int insetY = 5;
        goPauseClear.setLocation( 0, textField.getHeight() + insetY );
        int maxX = Math.max( textField.getWidth(), goPauseClear.getWidth() );
        int insetX = 10;
        chart.setLocation( maxX + insetX, 0 );
    }
}
