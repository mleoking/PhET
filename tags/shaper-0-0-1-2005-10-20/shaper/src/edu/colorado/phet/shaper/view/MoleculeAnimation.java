/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.*;
import java.text.MessageFormat;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;


/**
 * MoleculeAnimation
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MoleculeAnimation extends CompositePhetGraphic {

    private static final Dimension BACKGROUND_SIZE = new Dimension( 250, 250 );
    
    private String _closenessFormat;
    private HTMLGraphic _closenessGraphic;
    
    public MoleculeAnimation( Component component ) {
        super( component );
        
        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        background.setColor( Color.LIGHT_GRAY );
        addGraphic( background );
        
        PhetShapeGraphic animationFrame = new PhetShapeGraphic( component );
        animationFrame.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width - 30, BACKGROUND_SIZE.height - 45 ) );
        animationFrame.setColor( Color.WHITE );
        animationFrame.setBorderColor( Color.BLACK );
        animationFrame.setStroke( new BasicStroke(1f) );
        addGraphic( animationFrame );
        animationFrame.setRegistrationPoint( animationFrame.getWidth()/2, 0 ); // top center
        animationFrame.setLocation( BACKGROUND_SIZE.width/2, 15 );
        
        _closenessGraphic = new HTMLGraphic( component );
        _closenessGraphic.setColor( Color.BLACK );
        _closenessGraphic.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        _closenessFormat = SimStrings.get( "ShaperControls.closeness" );
        Object[] args = { "00" };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
        addGraphic( _closenessGraphic );
        _closenessGraphic.setRegistrationPoint( _closenessGraphic.getWidth()/2, _closenessGraphic.getHeight() );
        _closenessGraphic.setLocation( BACKGROUND_SIZE.width/2, BACKGROUND_SIZE.height - 5 );
    }
    
    public void setCloseness( double closeness ) {
        
        // Set the "How close am I?" label.
        if ( closeness < 0 || closeness > 1 ) {
            throw new IllegalArgumentException( "closeness is out of range: " + closeness );
        }
        int percent = (int)( 100 * closeness );
        Object[] args = { new Integer( percent ) };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
        
        //XXX tweak the animation
    }
}
