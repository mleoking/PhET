// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;

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
        
        int fontSize = HAResources.getInt( FONT_SIZE_RESOURCE, DEFAULT_FONT_SIZE );
        Font font = new PhetFont( FONT_STYLE, fontSize );
        
        setHTML( HAResources.getString( "label.notToScale" ) );
        setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
        setFont( font );
    }
}
