package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_GRAY;

/**
 * @author Sam Reid
 */
public class StarSetNode extends PNode {
    public StarSetNode() {
        final BufferedImage starGray = BufferedImageUtils.multiScaleToWidth( STAR_GRAY, 30 );
        addChild( new ControlPanelNode( new HBox( new PImage( starGray ), new PImage( starGray ), new PImage( starGray ) ), Color.white ) );
    }
}