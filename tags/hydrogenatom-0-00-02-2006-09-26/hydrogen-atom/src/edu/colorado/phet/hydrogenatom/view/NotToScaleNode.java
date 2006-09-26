/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.Font;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.HTMLNode;

/**
 * NotToScaleNode displays a note indicating that 
 * the graphics in this sim are not to scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class NotToScaleNode extends PhetPNode {

    public NotToScaleNode() {
        super();
        
        int fontSize = SimStrings.getInt( "notToScale.font.size", HAConstants.NOT_TO_SCALE_FONT_SIZE );
        Font font = new Font( HAConstants.JLABEL_FONT_NAME, Font.PLAIN, fontSize );
        
        HTMLNode htmlNode = new HTMLNode( SimStrings.get( "label.notToScale" ) );
        htmlNode.setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
        htmlNode.setFont( font );
        htmlNode.setPickable( false );
        htmlNode.setChildrenPickable( false );
        
        addChild( htmlNode );
    }
}
