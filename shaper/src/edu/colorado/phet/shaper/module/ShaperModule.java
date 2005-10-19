/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.module;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.control.ShaperControlPanel;
import edu.colorado.phet.shaper.help.ShaperHelpItem;
import edu.colorado.phet.shaper.model.FourierSeries;
import edu.colorado.phet.shaper.view.AmplitudesView;
import edu.colorado.phet.shaper.view.DiffractionGrating;
import edu.colorado.phet.shaper.view.Mirror;
import edu.colorado.phet.shaper.view.Rainbow;


/**
 * ShaperModule is the sole module in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperModule extends BaseModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double CHARTS_LAYER = 1;

    // Locations
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = ShaperConstants.MAX_HARMONICS;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _userFourierSeries;
    private FourierSeries _randomFourierSeries;
    
    private AmplitudesView _amplitudesView;
    
    private ShaperControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public ShaperModule( AbstractClock clock ) {
        
        super( SimStrings.get( "ShaperModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );

        // The user's Fourier Series
        _userFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
            _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
        }
        
        // The randomly generated Fourier Series
        _randomFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        Rainbow rainbow = new Rainbow( apparatusPanel, _userFourierSeries );
        apparatusPanel.addGraphic( rainbow );
        rainbow.setLocation( 86, 25 );
        
        Mirror inputMirror = new Mirror( apparatusPanel );
        apparatusPanel.addGraphic( inputMirror );
        inputMirror.rotate( Math.toRadians( 180 ) );
        inputMirror.setLocation( 210, 40 );
        
        PhetTextGraphic inputMirrorLabel = new PhetTextGraphic( apparatusPanel );
        inputMirrorLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        inputMirrorLabel.setColor( Color.BLACK );
        inputMirrorLabel.setText( SimStrings.get( "Mirror.label" ) );
        apparatusPanel.addGraphic( inputMirrorLabel );
        inputMirrorLabel.setLocation( 55, 25 );
        
        _amplitudesView = new AmplitudesView( apparatusPanel, _userFourierSeries );
        apparatusPanel.addGraphic( _amplitudesView );
        _amplitudesView.setLocation( 15, 250 );
        
        Mirror outputMirror = new Mirror( apparatusPanel );
        apparatusPanel.addGraphic( outputMirror );
        inputMirror.centerRegistrationPoint();
        outputMirror.setLocation( 50, 630 );
        
        PhetTextGraphic outputMirrorLabel = new PhetTextGraphic( apparatusPanel );
        outputMirrorLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        outputMirrorLabel.setColor( Color.BLACK );
        outputMirrorLabel.setText( SimStrings.get( "Mirror.label" ) );
        apparatusPanel.addGraphic( outputMirrorLabel );
        outputMirrorLabel.setLocation( 55, 660 );
        
        DiffractionGrating inputGrating = new DiffractionGrating( apparatusPanel );
        apparatusPanel.addGraphic( inputGrating );
        inputGrating.centerRegistrationPoint();
        inputGrating.rotate( Math.toRadians( -15 ) );
        inputGrating.setLocation( 440, 250 );
        
        DiffractionGrating outputGrating = new DiffractionGrating( apparatusPanel );
        apparatusPanel.addGraphic( outputGrating );
        outputGrating.centerRegistrationPoint();
        outputGrating.rotate( Math.toRadians( 15 ) );
        outputGrating.setLocation( 440, 445 );
        
        HTMLGraphic gratingsLabel = new HTMLGraphic( apparatusPanel );
        gratingsLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        gratingsLabel.setColor( Color.LIGHT_GRAY );
        gratingsLabel.setHTML( SimStrings.get( "DiffractionGratings.label" ) );
        apparatusPanel.addGraphic( gratingsLabel );
        gratingsLabel.setLocation( 415, 330 );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
                
        // Control Panel
        _controlPanel = new ShaperControlPanel( this );
        setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
   
        // Help Items
        ShaperHelpItem slidersToolHelp = new ShaperHelpItem( apparatusPanel, "Help goes here" );
        slidersToolHelp.pointAt( new Point( 252, 117 ), ShaperHelpItem.UP, 30 );
        addHelpItem( slidersToolHelp );
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        reset();
    }
    
    //----------------------------------------------------------------------------
    // BaseModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
    }
}
