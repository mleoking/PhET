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
public class NotToScaleNode extends HTMLNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final String FONT_NAME = HAConstants.DEFAULT_FONT_NAME;
    private static final int FONT_STYLE = HAConstants.DEFAULT_FONT_STYLE;
    private static final int DEFAULT_FONT_SIZE = HAConstants.DEFAULT_FONT_SIZE;
    private static final String FONT_SIZE_RESOURCE = "notToScale.font.size";
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NotToScaleNode() {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        int fontSize = SimStrings.getInt( FONT_SIZE_RESOURCE, DEFAULT_FONT_SIZE );
        Font font = new Font( FONT_NAME, FONT_STYLE, fontSize );
        
        setHTML( SimStrings.get( "label.notToScale" ) );
        setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
        setFont( font );
    }
}
