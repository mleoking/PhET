/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;
import java.awt.Point;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicAmplitudeGraphic;


/**
 * DiscreteModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteModule extends FourierModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double SOME_LAYER = 1;

    // Locations
    private static final Point SOME_LOCATION = new Point( 400, 300 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public DiscreteModule( AbstractClock clock ) {
        
        super( SimStrings.get( "DiscreteModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Harmonic
        Harmonic harmonic1 = new Harmonic( 1 ); //XXX
        Harmonic harmonic2 = new Harmonic( 2 ); //XXX
        Harmonic harmonic3 = new Harmonic( 3 ); //XXX
        Harmonic harmonic4 = new Harmonic( 4 ); //XXX
        Harmonic harmonic5 = new Harmonic( 5 ); //XXX
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Harmonic slider
        HarmonicAmplitudeGraphic hag1 = new HarmonicAmplitudeGraphic( apparatusPanel, harmonic1 );
        hag1.setBarColor( Color.RED );
        hag1.setLocation( 300, 300 ); // XXX
        apparatusPanel.addGraphic( hag1, 1 ); //XXX
        
        // Harmonic slider
        HarmonicAmplitudeGraphic hag2 = new HarmonicAmplitudeGraphic( apparatusPanel, harmonic2 );
        hag2.setBarColor( Color.GREEN );
        hag2.setLocation( 350, 300 ); // XXX
        apparatusPanel.addGraphic( hag2, 1 ); //XXX
        
        // Harmonic slider
        HarmonicAmplitudeGraphic hag3 = new HarmonicAmplitudeGraphic( apparatusPanel, harmonic3 );
        hag3.setBarColor( Color.BLUE );
        hag3.setLocation( 400, 300 ); // XXX
        apparatusPanel.addGraphic( hag3, 1 ); //XXX
        
        // Harmonic slider
        HarmonicAmplitudeGraphic hag4 = new HarmonicAmplitudeGraphic( apparatusPanel, harmonic4 );
        hag4.setBarColor( Color.YELLOW );
        hag4.setLocation( 450, 300 ); // XXX
        apparatusPanel.addGraphic( hag4, 1 ); //XXX
        
        // Harmonic slider
        HarmonicAmplitudeGraphic hag5 = new HarmonicAmplitudeGraphic( apparatusPanel, harmonic5 );
        hag5.setBarColor( Color.ORANGE );
        hag5.setLocation( 500, 300 ); // XXX
        apparatusPanel.addGraphic( hag5, 1 ); //XXX
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            //XXX add subpanels
            setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
}
