// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.trackButton;

/**
 * Button that shows a track; when pressed it selects the track.
 *
 * @author Sam Reid
 */
public class TrackButton extends PNode {

    private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

    public TrackButton( final EnergySkateParkBasicsModule module, final String trackName, final Vector2D offset ) {
        PImage image = new PImage( createIcon( module, trackName, offset ) );
        addChild( image );
        final PPath selectedIndicator = new PhetPPath( image.getFullBoundsReference().getBounds2D(), new BasicStroke( 3 ), INVISIBLE_COLOR );
        addChild( selectedIndicator );

        //When pressed, load the track
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {

            @Override public void mousePressed( PInputEvent event ) {
                SimSharingManager.sendUserMessage( chain( trackButton, trackName ), UserComponentTypes.button, pressed, ParameterSet.parameterSet( EnergySkateParkSimSharing.ParameterKeys.trackName, trackName ) );
                module.loadTrack( trackName, offset );
            }
        } );

        //When selected, turn on highlight.
        module.currentTrackFileName.addObserver( new VoidFunction1<String>() {
            public void apply( String fileName ) {
                selectedIndicator.setStrokePaint( fileName.equalsIgnoreCase( trackName ) ? Color.YELLOW : INVISIBLE_COLOR );
            }
        } );
    }

    //Creates an icon that displays the track.
    private BufferedImage createIcon( EnergySkateParkBasicsModule module, String location, Vector2D offset ) {

        //To create the icon, load the module and render it to an image
        module.loadTrack( location, offset );
        BufferedImage icon = new BufferedImage( 1024, 768, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = icon.createGraphics();
        module.getEnergySkateParkSimulationPanel().setSize( 1024, 768 );

        //Have to call update background since the size changed without the background changing
        module.getEnergySkateParkSimulationPanel().getRootNode().updateBackground();

        //Render into the image
        module.getEnergySkateParkSimulationPanel().getRootNode().fullPaint( new PPaintContext( g2 ) );

        //Resize to a small icon size using multi-scaling so the quality will be high
        return BufferedImageUtils.multiScaleToWidth( icon, 100 );
    }
}