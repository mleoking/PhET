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

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PText;


public class ExperimentAtomNode extends PhetPNode {

    private static final Font FONT = new Font( HAConstants.JLABEL_FONT_NAME, Font.PLAIN, 200 );
    private static final Color COLOR = Color.WHITE;
    
    public ExperimentAtomNode() {
        super();
        
        PText questionMarkText = new PText( "?" );
        questionMarkText.setFont( FONT );
        questionMarkText.setTextPaint( COLOR );
        addChild( questionMarkText );
    }
}
