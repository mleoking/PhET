/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.view.MeasureControlPanel;
import edu.colorado.phet.sound.view.MeterStickGraphic;
import edu.colorado.phet.sound.view.VerticalGuideline;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleSourceMeasureModule extends SingleSourceModule {

    private static final int s_guidelineBaseX = 180;

    protected SingleSourceMeasureModule( ApplicationModel appModel ) {
        super( appModel, "<html>Measure</html>" );

        // Add the ruler
        try {
            BufferedImage bi = ImageLoader.loadBufferedImage( SoundConfig.METER_STICK_IMAGE_FILE );
            PhetImageGraphic ruler = new PhetImageGraphic( getApparatusPanel(), bi );
            ruler.setPosition( SoundConfig.s_meterStickBaseX, SoundConfig.s_meterStickBaseY );
            MeterStickGraphic meterStickGraphic = new MeterStickGraphic( getApparatusPanel(), ruler, new Point2D.Double( 200, 100 ) );
            this.addGraphic( meterStickGraphic, 9 );
        }
        catch( IOException e ) {
            e.printStackTrace();

        }
        VerticalGuideline guideline1 = new VerticalGuideline( getApparatusPanel(), Color.blue, s_guidelineBaseX );
        this.addGraphic( guideline1, 10 );
        VerticalGuideline guideline2 = new VerticalGuideline( getApparatusPanel(), Color.blue, s_guidelineBaseX + 20 );
        this.addGraphic( guideline2, 10 );

//        RepaintDebugGraphic debugger = new RepaintDebugGraphic( getApparatusPanel(), appModel.getClock() );
//        debugger.setActive( true );
//        this.addGraphic( debugger, 8 );

        setControlPanel( new MeasureControlPanel( this, appModel.getClock() ) );
    }
}
