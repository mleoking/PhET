/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.IMagnet;


/**
 * Compass
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Compass extends PhetGraphic {
    
    private IMagnet _magnetModel;
    private CompassNeedleGraphic _needle;

    public Compass( Component component, IMagnet magnetModel ) {
        super( component );
        
        _magnetModel = magnetModel;
        
        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                translate( e.getDx(), e.getDy() );
            }
        } );
    }
    
    private void 
    updateShape() {
        
    }
    
    /*
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#determineBounds()
     */
    protected Rectangle determineBounds() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#paint(java.awt.Graphics2D)
     */
    public void paint( Graphics2D g ) {
        // TODO Auto-generated method stub
        
    }

}
