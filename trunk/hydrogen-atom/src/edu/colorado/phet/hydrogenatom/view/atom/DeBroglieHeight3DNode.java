/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglieViewStrategy;
import edu.umd.cs.piccolo.nodes.PText;


class DeBroglieHeight3DNode extends AbstractDeBroglieViewStrategy {
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        
        PText text = new PText( "\"3D height\" view is not implemented");
        text.setFont( new Font( HAConstants.DEFAULT_FONT_NAME, Font.PLAIN, 18 ) );
        text.setOffset( -text.getWidth()/2, -text.getHeight()/2 );
        text.setTextPaint( Color.WHITE );
        addChild( text );
    }
    
    public void update() {
        //XXX
    }
}