/* Copyright 2002-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import edu.colorado.phet.common.math.MathUtil;

import java.awt.*;
import java.io.IOException;

/**
 * A utility class that supports animation of a set of images read from disk.
 * <p/>
 * The class provides methods for stepping back and forth at will through the
 * set of images.
 * 
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Animation {

    private Image[] frames;
    private int currFrameNum = 0;

    /**
     * @param filePrefix The prefix for the names of all files to be animated
     * @param numFrames  The number of files to be animated
     */
    public Animation( String filePrefix, int numFrames ) throws IOException {
        frames = loadAnimation( filePrefix, numFrames );
    }

    /**
     * Gets the current image of the animation
     *
     * @return The current image of the animation
     */
    public Image getCurrFrame() {
        return frames[currFrameNum];
    }

    /**
     * Gets the next frame in the animation
     *
     * @return The next frame in the animation
     */
    public Image getNextFrame() {
        stepCurrFrameNum( +1 );
        return getCurrFrame();
    }

    /**
     * Gets the previous frame of the animation
     *
     * @return The previous frame of the animation
     */
    public Image getPrevFrame() {
        stepCurrFrameNum( -1 );
        return getCurrFrame();
    }

    /**
     * Steps the animation a specified number of frames
     *
     * @param dir +1 steps forward, -1 steps back
     * @return the new current frame number for the animation
     */
    private int stepCurrFrameNum( int dir ) {
        currFrameNum = ( currFrameNum + frames.length + dir )
                       % frames.length;
        return currFrameNum;
    }

    /**
     */
    public int getCurrFrameNum() {
        return currFrameNum;
    }

    /**
     * @return the number of frames in this animation.
     */
    public int getNumFrames() {
        return frames.length;
    }


    //
    // Static fields and methods
    //
    /**
     *
     */
    private static Image[] loadAnimation( String filePrefix, int numFrames ) throws IOException {
        Image[] frames = new Image[numFrames];
        ImageLoader animationLoader = new ImageLoader();
        for( int i = 1; i <= numFrames; i++ ) {
            String fileName = Animation.genAnimationFileName( filePrefix, i );
            frames[i - 1] = animationLoader.loadImage( fileName );
        }
        return frames;
    }

    /**
     * Generates a complete TIFF file name for a frame in a Poser-generated animation
     */
    private static String genAnimationFileName( String fileNamePrefix, int frameNum ) {
        String zeroStr = "";
        double log10 = MathUtil.logBaseX( frameNum, 10 );
        for( int i = 0; i < 4 - (int) log10 - 1; i++ ) {
            zeroStr = zeroStr.concat( "0" );
        }
        String fileName = fileNamePrefix + "_" + zeroStr + Integer.toString( frameNum ) + ".gif";
        return fileName;
    }

    public Image getFrame( int frameNum ) {
        return this.frames[frameNum];
    }
}
