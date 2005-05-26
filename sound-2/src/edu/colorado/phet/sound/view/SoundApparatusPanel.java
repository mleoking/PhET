/**
 * Class: SoundApparatusPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.sound.model.SoundModel;

import java.awt.*;
import java.awt.geom.Point2D;

public class SoundApparatusPanel extends ApparatusPanel2 {
    private int audioSource = SPEAKER_SOURCE;
    private double frequency = 0;
    private double amplitude = 0;
    // The point for which audio shoud be generated
    Point2D.Double audioReferencePt;

    //
    // Static fields and methods
    //
    public static int s_speakerConeOffsetX = 34;
    public static int s_speakerConeOffsetY = 2;
    public static int s_maxSpeakcerConeExcursion = 13;
    public final static int SPEAKER_SOURCE = 1;
    public final static int LISTENER_SOURCE = 2;

    private SoundModel model;
    public SoundApparatusPanel( SoundModel model, AbstractClock clock ) {
        super( model, clock );
//        this.setUseOffscreenBuffer( true );
    }
}
