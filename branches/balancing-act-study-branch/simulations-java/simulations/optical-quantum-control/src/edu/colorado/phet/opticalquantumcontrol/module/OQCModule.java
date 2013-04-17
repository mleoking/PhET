// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.module;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel3;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;
import edu.colorado.phet.opticalquantumcontrol.enums.MoleculeEnum;
import edu.colorado.phet.opticalquantumcontrol.help.HelpBubble;
import edu.colorado.phet.opticalquantumcontrol.model.FourierSeries;
import edu.colorado.phet.opticalquantumcontrol.view.*;


/**
 * OQCModule is the sole module in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OQCModule extends AbstractModule implements ActionListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Fourier Components
    private static final int NUMBER_OF_HARMONICS = OQCConstants.MAX_HARMONICS;
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _inputFourierSeries;
    private FourierSeries _userFourierSeries;
    private FourierSeries _outputFourierSeries;
    
    private AmplitudesView _amplitudesView;
    private InputPulseView _inputPulseView;
    private OutputPulseView _outputPulseView;
    private MoleculeAnimation _animation;
    private JButton _newButton;
    private CheatPanel _cheatPanel;
    private int _moleculeIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public OQCModule() {
        
        super( OQCResources.MODULE_TITLE, new SwingClock(  OQCConstants.CLOCK_DELAY, OQCConstants.CLOCK_STEP ) );

        setLogoPanel( null );
        setHelpPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );

        // Fourier series that describes the input pulse
        _inputFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        for ( int i = 0; i < _inputFourierSeries.getNumberOfHarmonics(); i++ ) {
            _inputFourierSeries.getHarmonic( i ).setAmplitude( 1 );
        }
        
        // Fourier Series that the user constructs
        _userFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        
        // Fourier Series that describes the output pulse
        _outputFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        // Use ApparatusPanel3 to avoid scaling problems on low resolution screens, see #2860
        ApparatusPanel2 apparatusPanel = new ApparatusPanel3( getClock(), 1008, 699 );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        // Input Pulse view
        _inputPulseView = new InputPulseView( apparatusPanel, _inputFourierSeries );
        apparatusPanel.addGraphic( _inputPulseView );
        _inputPulseView.setLocation( 470, 15 );
      
        // Output Pulse view
        _outputPulseView = new OutputPulseView( apparatusPanel, _userFourierSeries, _outputFourierSeries );
        apparatusPanel.addGraphic( _outputPulseView );
        _outputPulseView.setLocation( 470, 490 );
        
        // "New Output Pulse" button
        {     
            _newButton = new JButton( OQCResources.NEW_OUTPUT_PULSE );
            _newButton.setOpaque( false );
            _newButton.addActionListener( this );
            PhetGraphic newButtonGraphic = PhetJComponent.newInstance( apparatusPanel, _newButton );
            newButtonGraphic.setRegistrationPoint( newButtonGraphic.getWidth(), 0 );
            newButtonGraphic.setLocation( 1000, 493 ); // upper right, for i18n
            apparatusPanel.addGraphic( newButtonGraphic );
        }
        
        // Light rays
        LightRays lightRays = new LightRays( apparatusPanel, _userFourierSeries );
        apparatusPanel.addGraphic( lightRays );
        lightRays.setLocation( 86, 25 );

        // Input mirror
        {
            Mirror inputMirror = new Mirror( apparatusPanel );
            apparatusPanel.addGraphic( inputMirror );
            inputMirror.centerRegistrationPoint();
            inputMirror.rotate( Math.toRadians( 180 ) );
            inputMirror.setLocation( 210, 40 );

            HTMLGraphic inputMirrorLabel = new HTMLGraphic( apparatusPanel );
            inputMirrorLabel.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            inputMirrorLabel.setFont( new PhetFont( Font.PLAIN, 18 ) );
            inputMirrorLabel.setColor( Color.BLACK );
            inputMirrorLabel.setHTML( OQCResources.MIRROR );
            inputMirrorLabel.setLocation( 55, 15 );
            inputMirrorLabel.setIgnoreMouse( true );
            apparatusPanel.addGraphic( inputMirrorLabel );
        }
        
        // Output mirror
        {
            Mirror outputMirror = new Mirror( apparatusPanel );
            apparatusPanel.addGraphic( outputMirror );
            outputMirror.setLocation( 50, 630 );

            HTMLGraphic outputMirrorLabel = new HTMLGraphic( apparatusPanel );
            outputMirrorLabel.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            outputMirrorLabel.setFont( new PhetFont( Font.PLAIN, 18 ) );
            outputMirrorLabel.setColor( Color.BLACK );
            outputMirrorLabel.setHTML( OQCResources.MIRROR );
            outputMirrorLabel.setLocation( 55, 655 );
            outputMirrorLabel.setIgnoreMouse( true );
            apparatusPanel.addGraphic( outputMirrorLabel );
        }
        
        // Diffraction gratings
        {
            DiffractionGrating inputGrating = new DiffractionGrating( apparatusPanel );
            apparatusPanel.addGraphic( inputGrating );
            inputGrating.centerRegistrationPoint();
            inputGrating.rotate( Math.toRadians( -15 ) );
            inputGrating.setLocation( 440, 250 );

            DiffractionGrating outputGrating = new DiffractionGrating( apparatusPanel );
            apparatusPanel.addGraphic( outputGrating );
            outputGrating.centerRegistrationPoint();
            outputGrating.rotate( Math.toRadians( 195 ) );
            outputGrating.setLocation( 440, 445 );

            HTMLGraphic gratingsLabel = new HTMLGraphic( apparatusPanel );
            gratingsLabel.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            gratingsLabel.setFont( new PhetFont( Font.PLAIN, 18 ) );
            gratingsLabel.setColor( Color.LIGHT_GRAY );
            gratingsLabel.setHTML( OQCResources.DIFFRACTION_GRATING );
            gratingsLabel.setLocation( 400, 325 );
            gratingsLabel.setIgnoreMouse( true );
            apparatusPanel.addGraphic( gratingsLabel );

            Arrow upArrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( -10, -40 ), 10, 10, 4 );
            PhetShapeGraphic upArrowGraphic = new PhetShapeGraphic( apparatusPanel );
            upArrowGraphic.setShape( upArrow.getShape() );
            upArrowGraphic.setColor( Color.LIGHT_GRAY );
            upArrowGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            upArrowGraphic.setLocation( 440, 315 );
            upArrowGraphic.setIgnoreMouse( true );
            apparatusPanel.addGraphic( upArrowGraphic );
            
            Arrow downArrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( -10, 40 ), 10, 10, 4 );
            PhetShapeGraphic downArrowGraphic = new PhetShapeGraphic( apparatusPanel );
            downArrowGraphic.setShape( downArrow.getShape() );
            downArrowGraphic.setColor( Color.LIGHT_GRAY );
            downArrowGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            downArrowGraphic.setIgnoreMouse( true );
            downArrowGraphic.setLocation( 440, 380 );
            apparatusPanel.addGraphic( downArrowGraphic );
        }
      
        // Amplitude sliders
        _amplitudesView = new AmplitudesView( apparatusPanel, _userFourierSeries );
        apparatusPanel.addGraphic( _amplitudesView );
        _amplitudesView.setLocation( 15, 215 );
        
        // Amplitudes "Reset" button
        {
            JButton _resetButton = new JButton( OQCResources.RESET );
            _resetButton.setOpaque( false );
            // Reset button
            _resetButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setWaitCursorEnabled( true );
                    resetAmplitudes();
                    setWaitCursorEnabled( false );
                }
            } );
            
            PhetGraphic _resetButtonGraphic = PhetJComponent.newInstance( apparatusPanel, _resetButton );
            _resetButtonGraphic.setLocation( _amplitudesView.getX() + 5, _amplitudesView.getY() + 5 );
            _resetButtonGraphic.scale( 0.7 );
            apparatusPanel.addGraphic( _resetButtonGraphic );
        }
        
        // "Mask" label
        {
            HTMLGraphic maskLabel = new HTMLGraphic( apparatusPanel );
            maskLabel.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            maskLabel.setFont( new PhetFont( Font.PLAIN, 18 ) );
            maskLabel.setColor( Color.LIGHT_GRAY );
            maskLabel.setHTML( OQCResources.MASK );
            maskLabel.setLocation( 10, 535 );
            maskLabel.setIgnoreMouse( true );
            apparatusPanel.addGraphic( maskLabel );
            
            Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 10, -40 ), 10, 10, 4 );
            PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( apparatusPanel );
            arrowGraphic.setShape( arrow.getShape() );
            arrowGraphic.setColor( Color.LIGHT_GRAY );
            arrowGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            arrowGraphic.setLocation( 30, 530 );
            arrowGraphic.setIgnoreMouse( true );
            apparatusPanel.addGraphic( arrowGraphic );
        }
        
        // Cheat panel
        _cheatPanel = new CheatPanel( apparatusPanel, _outputFourierSeries );
        apparatusPanel.addGraphic( _cheatPanel );
        _cheatPanel.setLocation( 68, 130 );
        _cheatPanel.setVisible( false );

        // Molecule animation
        _animation = new MoleculeAnimation( apparatusPanel, this, _userFourierSeries, _outputFourierSeries );
        apparatusPanel.addGraphic( _animation );
        _animation.setLocation( 515, 222 );
        model.addModelElement( _animation );// TODO separate molecule model & view !
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // no control panel in this simulation
        
        // hide the clock controls
        getClockControlPanel().setVisible( false );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
   
        // Instructions
        HTMLGraphic instructions = new HTMLGraphic( apparatusPanel );
        instructions.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        instructions.setHTML( OQCResources.INSTRUCTIONS );
        instructions.setFont( new PhetFont( Font.PLAIN, 18 ) );
        instructions.setColor( OQCConstants.OUTPUT_PULSE_COLOR );
        instructions.setRegistrationPoint( 0, instructions.getHeight()/2 ); // left center
        instructions.setLocation( 790, 350 );
        instructions.setIgnoreMouse( true );
        apparatusPanel.addGraphic( instructions );
        
        // Help Items
        HelpBubble slidersHelp = new HelpBubble( apparatusPanel, OQCResources.HELP_AMPLITUDE_SLIDERS );
        slidersHelp.pointAt( new Point( 125, 445 ), HelpBubble.TOP_LEFT, 45 );
        addHelpItem( slidersHelp );
        
        HelpBubble textfieldsHelp = new HelpBubble( apparatusPanel, OQCResources.HELP_AMPLITUDE_TEXTFIELDS );
        textfieldsHelp.pointAt( new Point( 146, 238 ), HelpBubble.BOTTOM_LEFT, 30 );
        addHelpItem( textfieldsHelp );
        
        HelpBubble resetHelp = new HelpBubble( apparatusPanel, OQCResources.HELP_AMPLITUDE_RESET );
        resetHelp.pointAt( new Point( 47, 221 ), HelpBubble.BOTTOM_LEFT, 80 );
        addHelpItem( resetHelp );
        
        HelpBubble newPulseHelp = new HelpBubble( apparatusPanel, OQCResources.HELP_NEW_PULSE );
        newPulseHelp.pointAt( new Point( 970, 495 ), HelpBubble.BOTTOM_RIGHT, 30 );
        addHelpItem( newPulseHelp );
        
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
        // Starting with a randomly-selected molecule, generate a new "game".
        Random random = new Random();
        _moleculeIndex = random.nextInt( MoleculeEnum.size() );
        newGame();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setCheatEnabled( boolean enabled ) {
        _cheatPanel.setVisible( enabled );
    }
    
    //----------------------------------------------------------------------------
    // ActionListener implementation
    //----------------------------------------------------------------------------
    
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == _newButton ) {
            newGame();
        }
    }
    
    public void newGame() {
        setWaitCursorEnabled( true );
        
        _userFourierSeries.setAdjusting( true );
        _outputFourierSeries.setAdjusting( true );
        
        resetAmplitudes();
        
        // Set the output pulse amplitudes to the next molecule.
        _moleculeIndex++;
        if ( _moleculeIndex >= MoleculeEnum.size() ) {
            _moleculeIndex = 0;
        }
        MoleculeEnum molecule = MoleculeEnum.getByIndex( _moleculeIndex );
        double[] amplitudes = MoleculeEnum.getAmplitudes( molecule );
        for ( int i = 0; i < _outputFourierSeries.getNumberOfHarmonics(); i++ ) {
            _outputFourierSeries.getHarmonic( i ).setAmplitude( amplitudes[i] );
        }
        
        // Reset the animation
        _animation.setMolecule( _moleculeIndex );
        _animation.reset();
        
        // Hide the "cheat" panel
        _cheatPanel.setVisible( false );
        
        _userFourierSeries.setAdjusting( false );
        _outputFourierSeries.setAdjusting( false );
        
        setWaitCursorEnabled( false );
    }
    
    /*
     * Resets the user's amplitudes to match the input pulse.
     */
    private void resetAmplitudes() {
        for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
            double amplitude = _inputFourierSeries.getHarmonic( i ).getAmplitude();
            _userFourierSeries.getHarmonic( i ).setAmplitude( amplitude );
        }
    }
}
