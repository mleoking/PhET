/**
 * Class: SoundConfig
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound;

import java.awt.Color;

import edu.colorado.phet.sound.view.SoundApparatusPanel;

public class SoundConfig {
    
    public static final String PROJECT_NAME = "sound";
    
    public static final double HELP_LAYER_NUMBER = Double.POSITIVE_INFINITY;

    // Physical constants
    // The time step is set so that the waves look reasonable on the screen. It is NOT set so that
    // the simulation clock bears any certain relationship to the speed of sound
    public static final double s_timeStep = 5;
    public static final int s_waitTime = 50;

    // The number of pixels a wavefront moves in a time step
    public static final int PROPOGATION_SPEED = 3;
    // Conversion factor needed to scale the clock for measurements. This
    // is based on the propogation speed, the clock's time step, and the
    // size of the ruler graphic that is used to measure waves
    private static final double METERS_PER_PIXEL = 5.0 / 222.0;        // the 5 meter stick is 222 pixels long
    // Speed of sound at room temperature at sea level
    public static final double SPEED_OF_SOUND = 335;
    // Factor to apply to time reported by the simulation clock to get seconds. This gives results
    // that correspond to the speed of sound
    public static final double CLOCK_SCALE_FACTOR = PROPOGATION_SPEED * ( 1 / s_timeStep ) * METERS_PER_PIXEL * ( 1 / SPEED_OF_SOUND );

    public static final int s_maxFrequency = 1000;
    public static final int s_defaultFrequency = 500;
    public static final int s_maxAmplitude = 1;
    public static final int s_defaultAmplitude = 5;


    // This parameter defines how tightly spaced the waves are on the screen. If it is too small,
    // the displayed wavelength will not get monotonically shorter as the frequency is raised. Note
    // that the best value for this is dependent on the clock's dt.
    public static final double s_frequencyDisplayFactor = 4000;

    public static final int s_defaultAudioSource = SoundApparatusPanel.SPEAKER_SOURCE;

    // Image stuff
    public static final Color MIDDLE_GRAY = new Color( 128, 128, 128 );
    public static final String IMAGE_DIRECTORY = "sound/images/";
    public static final String SPEAKER_FRAME_IMAGE_FILE = IMAGE_DIRECTORY + "speaker-frame.gif";
    public static final String SPEAKER_CONE_IMAGE_FILE = IMAGE_DIRECTORY + "speaker-cone.gif";
    public static final String HEAD_IMAGE_FILE = IMAGE_DIRECTORY + "head-1-small.gif";
    public static final String LISTENER_W_EARS_IMAGE_FILE = IMAGE_DIRECTORY + "head-1-small.gif";
    public static final String METER_STICK_IMAGE_FILE = IMAGE_DIRECTORY + "five-meter-stick.gif";
    public static final String[] HEAD_IMAGE_FILES = {IMAGE_DIRECTORY + "head-1-small.gif",
            IMAGE_DIRECTORY + "head-2-small.gif"};

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

    // These two constants place the origin of the wavefront so the graphic centered
    // on the horizontal axis of the speaker, and the first waves appear right at the
    // right edge of the speaker. This is important for making accurate measurements
    public static int s_wavefrontBaseX = s_speakerBaseX + 11;
    public static int s_wavefrontBaseY = s_speakerBaseY + s_wavefrontHeight / 2;

    public static int s_headBaseX = s_speakerBaseX + 250;
    public static int s_headBaseY = s_speakerBaseY;
    public static int s_headAudioOffsetX = -12;

    public static int s_meterStickBaseX = 200;
    public static int s_meterStickBaseY = 100;

    public static double HELP_LAYER = Double.MAX_VALUE;
}
