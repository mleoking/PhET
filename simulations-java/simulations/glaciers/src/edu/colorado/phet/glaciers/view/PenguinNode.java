/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.umd.cs.piccolo.nodes.PImage;


public class PenguinNode extends PImage {

    public PenguinNode() {
        super( GlaciersResources.getImage( GlaciersConstants.IMAGE_PENGUIN ) );
    }
}
