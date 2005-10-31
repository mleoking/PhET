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
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JButton;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.enum.MoleculeEnum;
import edu.colorado.phet.shaper.help.HelpBubble;
import edu.colorado.phet.shaper.model.FourierSeries;
import edu.colorado.phet.shaper.view.*;


/**
 * ShaperModule is the sole module in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperModule extends AbstractModule implements ActionListener {

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
        
        // Input Pulse view
        _inputPulseView = new InputPulseView( apparatusPanel );
        apparatusPanel.addGraphic( _inputPulseView );
        _inputPulseView.setLocation( 470, 15 );
      
        // Output Pulse view
        _outputPulseView = new OutputPulseView( apparatusPanel, _userFourierSeries, _outputFourierSeries );
        apparatusPanel.addGraphic( _outputPulseView );
        _outputPulseView.setLocation( 470, 490 );
        
        // "New Output Pulse" button
        {     
            _newButton = new JButton( SimStrings.get( "newOutputPulse" ) );
            _newButton.setOpaque( false );
            _newButton.addActionListener( this );
            PhetGraphic newButtonGraphic = PhetJComponent.newInstance( apparatusPanel, _newButton );
            newButtonGraphic.setRegistrationPoint( newButtonGraphic.getWidth(), 0 );
            newButtonGraphic.setLocation( 1000, 490 ); // upper right, for i18n
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
            inputMirrorLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
            inputMirrorLabel.setColor( Color.BLACK );
            inputMirrorLabel.setHTML( SimStrings.get( "Mirror.label" ) );
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
            outputMirrorLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
            outputMirrorLabel.setColor( Color.BLACK );
            outputMirrorLabel.setHTML( SimStrings.get( "Mirror.label" ) );
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
            gratingsLabel.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
            gratingsLabel.setColor( Color.LIGHT_GRAY );
            gratingsLabel.setHTML( SimStrings.get( "DiffractionGratings.label" ) );
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
        _amplitudesView.setLocation( 15, 250 );
        
        // Cheat panel
        _cheatPanel = new CheatPanel( apparatusPanel, _outputFourierSeries );
        apparatusPanel.addGraphic( _cheatPanel );
        _cheatPanel.setLocation( 68, 160 );
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
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
   
        // Instructions
        HTMLGraphic instructions = new HTMLGraphic( apparatusPanel );
        instructions.setHTML( SimStrings.get( "instructions" ) );
        instructions.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        instructions.setColor( ShaperConstants.OUTPUT_PULSE_COLOR );
        instructions.setRegistrationPoint( 0, instructions.getHeight()/2 ); // left center
        instructions.setLocation( 790, 350 );
        instructions.setIgnoreMouse( true );
        apparatusPanel.addGraphic( instructions );
        
        // Help Items
        HelpBubble slidersHelp = new HelpBubble( apparatusPanel, SimStrings.get( "Help.amplitude.sliders" ) );
        slidersHelp.pointAt( new Point( 125, 415 ), HelpBubble.TOP_LEFT, 40 );
        addHelpItem( slidersHelp );
        
        HelpBubble textfieldsHelp = new HelpBubble( apparatusPanel, SimStrings.get( "Help.amplitude.textfields" ) );
        textfieldsHelp.pointAt( new Point( 146, 273 ), HelpBubble.BOTTOM_LEFT, 30 );
        addHelpItem( textfieldsHelp );
        
        HelpBubble resetHelp = new HelpBubble( apparatusPanel, SimStrings.get( "Help.amplitude.reset" ) );
        resetHelp.pointAt( new Point( 47, 256 ), HelpBubble.BOTTOM_LEFT, 80 );
        addHelpItem( resetHelp );
        
        HelpBubble newPulseHelp = new HelpBubble( apparatusPanel, SimStrings.get( "Help.newPulse" ) );
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
        
        // Set the user's amplitudes to zero.
        for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
            _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
        }
        
        // Set the output pulse amplitudes to the next molecule.
        MoleculeEnum molecule = MoleculeEnum.getByIndex( _moleculeIndex );
        double[] amplitudes = MoleculeEnum.getAmplitudes( molecule );
        for ( int i = 0; i < _outputFourierSeries.getNumberOfHarmonics(); i++ ) {
            _outputFourierSeries.getHarmonic( i ).setAmplitude( amplitudes[i] );
        }
        _moleculeIndex++;
        if ( _moleculeIndex >= MoleculeEnum.size() ) {
            _moleculeIndex = 0;
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
}
