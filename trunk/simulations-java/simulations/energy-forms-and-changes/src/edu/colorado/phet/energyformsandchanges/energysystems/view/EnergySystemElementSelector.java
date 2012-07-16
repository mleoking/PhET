// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemElementCarousel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that allows the user to select the various elements contained within
 * a carousel by presenting a set of radio-style push buttons, each with an
 * icon image of the selection that it represents.
 *
 * @author John Blanco
 */
public class EnergySystemElementSelector extends PNode {

    private static final double BUTTON_IMAGE_WIDTH = 40; // In screen coordinates, which is close to pixels.

    public EnergySystemElementSelector( final EnergySystemElementCarousel carousel ) {
        List<RadioButtonStripControlPanelNode.Element<Integer>> buttonElementList = new ArrayList<RadioButtonStripControlPanelNode.Element<Integer>>() {{
            for ( int i = 0; i < carousel.getNumElements(); i++ ) {
                Image buttonImage = carousel.getElement( i ).getIconImage();
                PImage buttonImageNode = new PImage( buttonImage ) {{
                    setScale( BUTTON_IMAGE_WIDTH / getFullBoundsReference().getWidth() );
                }};
                add( new RadioButtonStripControlPanelNode.Element<Integer>( buttonImageNode, i, EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton ) );
            }
        }};
        addChild( new RadioButtonStripControlPanelNode<Integer>( carousel.targetIndex, buttonElementList, 0 ) );
    }
}
