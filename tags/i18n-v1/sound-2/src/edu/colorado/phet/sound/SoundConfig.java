/**
 * Class: SoundConfig
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.sound.view.SoundApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;

public class SoundConfig {

    public String getTitle() {
        return SimStrings.get( "SoundApplication.title" );
    }

    public double getTimeStep() {
        return s_timeStep;
    }

    public int getWaitTime() {
        return s_waitTime;
    }

    // Localization
    public static final String localizedStringsPath = "localization/SoundStrings";

    // Physical constants
    public static final double s_timeStep = 5;
    //    public static final double s_timeStep = 0.005f;
    public static final int s_waitTime = 75;

    // The number of pixels a wavefront moves in a time step
    public static final int PROPOGATION_SPEED = 3;
    // Conversion factor needed to scale the clock for measurements. This
    // is based on the propogation speed, the clock's time step, and the
    // size of the ruler graphic that is used to measure waves
    private static final double METERS_PER_PIXEL = 5.0 / 222.0;        // the 5 meter stick is 222 pixels long
    private static final double PIXELS_PER_SECOND = PROPOGATION_SPEED / ( s_timeStep * 1E-3 );
    public static final double SPEED_OF_SOUND = 331.4;
    private static final double SCREEN_SPEED = METERS_PER_PIXEL * PIXELS_PER_SECOND;
    public static final double s_clockScaleFactor = SCREEN_SPEED / SPEED_OF_SOUND;

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
    public static final String HEAD_IMAGE_FILE = IMAGE_DIRECTORY + "head-1-small.gif";
    public static final String LISTENER_W_EARS_IMAGE_FILE = IMAGE_DIRECTORY + "head-1-small.gif";
    //    public static final String HEAD_IMAGE_FILE = IMAGE_DIRECTORY + "head-1.gif";
    //    public static final String LISTENER_W_EARS_IMAGE_FILE = IMAGE_DIRECTORY + "head-1.gif";
    public static final String METER_STICK_IMAGE_FILE = IMAGE_DIRECTORY + "five-meter-stick.gif";
    public static final String[] HEAD_IMAGE_FILES = {IMAGE_DIRECTORY + "head-1-small.gif" /*,
                                                     IMAGE_DIRECTORY + "head-2-small.gif" */ };

    // Animation images

    // Offset for locating objects in the apparatus panel
    public static final int X_BASE_OFFSET = 100;
    public static final int Y_BASE_OFFSET = -50;

    // Sounds
    public static final String SOUND_DIRECTORY = "http://sounds/";

    // Screen locations
    public static int s_speakerBaseX = 100;
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

    public static double HELP_LAYER = Double.MAX_VALUE;
}
