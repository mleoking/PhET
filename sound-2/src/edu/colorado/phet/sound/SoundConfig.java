/**
 * Class: SoundConfig
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.sound.view.SoundApparatusPanel;

import java.awt.*;

public class SoundConfig {
    public String getTitle() {
        return TITLE;
    }

    public double getTimeStep() {
        return s_timeStep;
    }

    public int getWaitTime() {
        return s_waitTime;
    }

    // Title and version number
    public static final String TITLE = "Sound";
    public static final String VERSION = "0.01";

    // Physical constants
    public static final double s_timeStep = 5;
    //    public static final double s_timeStep = 0.005f;
    public static final int s_waitTime = 50;

    public static final int s_maxFrequency = 1000;
    public static final int s_defaultFrequency = 500;
    public static final int s_maxAmplitude = 1;
    //    public static final int s_maxAmplitude = 10;
    public static final int s_defaultAmplitude = 5;

    // This parameter defines how tightly spaced the waves are on the screen. If it is too small,
    // the displayed wavelength will not get monotonically shorter as the frequency is raised. Note
    // that the best value for this is dependent on the clock's dt.
    public static final double s_frequencyDisplayFactor = 4000;
    //    public static final float s_frequencyDisplayFactor = 4;

    public static final int s_defaultAudioSource = SoundApparatusPanel.SPEAKER_SOURCE;

    // Image stuff
    public static final Color MIDDLE_GRAY = new Color( 128, 128, 128 );
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String SPEAKER_FRAME_IMAGE_FILE = IMAGE_DIRECTORY + "speaker-frame.gif";
    public static final String SPEAKER_CONE_IMAGE_FILE = IMAGE_DIRECTORY + "speaker-cone.gif";
    public static final String HEAD_IMAGE_FILE = IMAGE_DIRECTORY + "head.gif";
    public static final String LISTENER_W_EARS_IMAGE_FILE = IMAGE_DIRECTORY + "listener-w-ears.gif";
    public static final String METER_STICK_IMAGE_FILE = IMAGE_DIRECTORY + "five-meter-stick.gif";

    // Animation images

    // Offset for locating objects in the apparatus panel
    public static final int X_BASE_OFFSET = 100;
    public static final int Y_BASE_OFFSET = -50;

    // Sounds
    public static final String SOUND_DIRECTORY = "http://sounds/";

    // Screen locations
    public static int s_speakerBaseX = 100;
    //    public static int s_speakerBaseX = 215;
    public static int s_speakerBaseY = 248;

    public static int s_wavefrontHeight = 150;
    public static int s_wavefrontRadius = 100;

    // the +7 gets the wavefront to look right vis a vis the speaker
    public static int s_wavefrontBaseX = s_speakerBaseX + 7;
    public static int s_wavefrontBaseY = s_speakerBaseY + s_wavefrontHeight / 2;

    public static int s_headBaseX = s_speakerBaseX + 250;
    public static int s_headBaseY = s_speakerBaseY;
    public static int s_headAudioOffsetX = -12;

    public static int s_meterStickBaseX = 400;
    public static int s_meterStickBaseY = 500;
}
