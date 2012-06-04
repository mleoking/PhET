package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class DefaultForcesAndMotionBasicsCanvas extends AbstractForcesAndMotionBasicsCanvas {
    public DefaultForcesAndMotionBasicsCanvas() {
        addChild( new PImage( Images.MYSTERY_BOX ) );
    }
}