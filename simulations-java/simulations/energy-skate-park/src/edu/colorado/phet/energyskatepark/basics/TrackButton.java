// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Button that shows a track; when pressed it selects the track.
 *
 * @author Sam Reid
 */
public class TrackButton extends PNode {
    public TrackButton( final EnergySkateParkBasicsModule module, final String location, final boolean rollerCoasterMode ) {
        addChild( new PImage( createIcon( module, location, rollerCoasterMode ) ) );

        //When pressed, load the track
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                module.loadTrack( location, rollerCoasterMode );
            }
        } );
    }

    //Creates an icon that displays the track.
    private BufferedImage createIcon( EnergySkateParkBasicsModule module, String location, boolean rollerCoasterMode ) {

        //To create the icon, load the module and render it to an image
        module.loadTrack( location, rollerCoasterMode );
        BufferedImage icon = new BufferedImage( 800, 600, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = icon.createGraphics();
        module.getEnergySkateParkSimulationPanel().setSize( 800, 600 );

        //Have to call update background since the size changed without the background changing
        module.getEnergySkateParkSimulationPanel().getRootNode().updateBackground();

        //Render into the image
        module.getEnergySkateParkSimulationPanel().getRootNode().fullPaint( new PPaintContext( g2 ) );

        //Resize to a small icon size using multi-scaling so the quality will be high
        return BufferedImageUtils.multiScaleToWidth( icon, 100 );
    }
}