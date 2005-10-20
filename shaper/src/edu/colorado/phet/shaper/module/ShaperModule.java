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

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.control.ShaperControls;
import edu.colorado.phet.shaper.help.ShaperHelpItem;
import edu.colorado.phet.shaper.model.FourierSeries;
import edu.colorado.phet.shaper.view.*;


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
    private FourierSeries _outputFourierSeries;
    
    private AmplitudesView _amplitudesView;
    private InputPulseView _inputPulseView;
    private OutputPulseView _outputPulseView;
    private MoleculeAnimation _animation;
    
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
        
        // The Fourier Series that describes the output pulse
        _outputFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        _inputPulseView = new InputPulseView( apparatusPanel );
        apparatusPanel.addGraphic( _inputPulseView );
        _inputPulseView.setLocation( 470, 15 );
        
        _outputPulseView = new OutputPulseView( apparatusPanel, _userFourierSeries, _outputFourierSeries );
        apparatusPanel.addGraphic( _outputPulseView );
        _outputPulseView.setLocation( 470, 490 );
              
        _animation = new MoleculeAnimation( apparatusPanel );
        apparatusPanel.addGraphic( _animation );
        _animation.setLocation( 515, 222 );
        
        // Rainbow of light
        {
            RainbowLight rainbow = new RainbowLight( apparatusPanel, _userFourierSeries );
            apparatusPanel.addGraphic( rainbow );
            rainbow.setLocation( 86, 25 );
        }
        
        // Input mirror
        {
            Mirror inputMirror = new Mirror( apparatusPanel );
            apparatusPanel.addGraphic( inputMirror );
            inputMirror.centerRegistrationPoint();
            inputMirror.rotate( Math.toRadians( 180 ) );
            inputMirror.setLocation( 210, 40 );

            HTMLGraphic inputMirrorLabel = new HTMLGraphic( apparatusPanel );
            inputMirrorLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
            inputMirrorLabel.setColor( Color.BLACK );
            inputMirrorLabel.setHTML( SimStrings.get( "Mirror.label" ) );
            apparatusPanel.addGraphic( inputMirrorLabel );
            inputMirrorLabel.setLocation( 55, 15 );
        }
        
        // Output mirror
        {
            Mirror outputMirror = new Mirror( apparatusPanel );
            apparatusPanel.addGraphic( outputMirror );
            outputMirror.setLocation( 50, 630 );

            HTMLGraphic outputMirrorLabel = new HTMLGraphic( apparatusPanel );
            outputMirrorLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
            outputMirrorLabel.setColor( Color.BLACK );
            outputMirrorLabel.setHTML( SimStrings.get( "Mirror.label" ) );
            apparatusPanel.addGraphic( outputMirrorLabel );
            outputMirrorLabel.setLocation( 55, 655 );
        }
        
        // White light rays
        {
            PhetShapeGraphic inputLight = new PhetShapeGraphic( apparatusPanel );
            inputLight.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            inputLight.setShape( new Rectangle( 0, 0, 10, 130 ) );
            inputLight.setColor( Color.WHITE );
            inputLight.rotate( Math.toRadians( 8 ) );
            inputLight.setLocation( 447, 124 );
            apparatusPanel.addGraphic( inputLight );
            
            PhetShapeGraphic outputLight = new PhetShapeGraphic( apparatusPanel );
            outputLight.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            outputLight.setShape( new Rectangle( 0, 0, 10, 130 ) );
            outputLight.setColor( Color.WHITE );
            outputLight.rotate( Math.toRadians( -180 - 8 ) );
            outputLight.setLocation( 447 + 10, 570 );
            apparatusPanel.addGraphic( outputLight );
        }
        
        // Diffusion gratings
        {
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
            gratingsLabel.setLocation( 415, 325 );

            Arrow upArrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( -10, -40 ), 10, 10, 4 );
            PhetShapeGraphic upArrowGraphic = new PhetShapeGraphic( apparatusPanel );
            upArrowGraphic.setShape( upArrow.getShape() );
            upArrowGraphic.setColor( Color.LIGHT_GRAY );
            upArrowGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            apparatusPanel.addGraphic( upArrowGraphic );
            upArrowGraphic.setLocation( 440, 315 );
            
            Arrow downArrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( -10, 40 ), 10, 10, 4 );
            PhetShapeGraphic downArrowGraphic = new PhetShapeGraphic( apparatusPanel );
            downArrowGraphic.setShape( downArrow.getShape() );
            downArrowGraphic.setColor( Color.LIGHT_GRAY );
            downArrowGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            apparatusPanel.addGraphic( downArrowGraphic );
            downArrowGraphic.setLocation( 440, 380 );
        }
        
        _amplitudesView = new AmplitudesView( apparatusPanel, _userFourierSeries );
        apparatusPanel.addGraphic( _amplitudesView );
        _amplitudesView.setLocation( 15, 250 );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Controls on the apparatus panel
        ShaperControls controlPanel = new ShaperControls( apparatusPanel, 
                _userFourierSeries, _outputFourierSeries, _outputPulseView );
        apparatusPanel.addGraphic( controlPanel );
        controlPanel.setLocation( 800, 360 );

        // Game manager
        GameManager gameManager = new GameManager( _userFourierSeries, _outputFourierSeries, _animation, controlPanel );

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
   
        // Instructions
        HTMLGraphic instructions = new HTMLGraphic( apparatusPanel );
        instructions.setHTML( SimStrings.get( "instructions" ) );
        instructions.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        instructions.setColor( ShaperConstants.OUTPUT_PULSE_COLOR );
        apparatusPanel.addGraphic( instructions );
        instructions.setLocation( 800, 250 );
        
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
        // This simulation has no Reset button.
    }
}
