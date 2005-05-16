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
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierComponent;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.Vector2D;
import edu.colorado.phet.fourier.view.AmplitudesGraphic;
import edu.colorado.phet.fourier.view.ComponentsGraphic;
import edu.colorado.phet.fourier.view.SineWaveGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


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
    private static final double AMPLITUDES_LAYER = 1;
    private static final double COMPONENTS_LAYER = 2;
    private static final double SUM_LAYER = 3;

    // Locations
    private static final Point AMPLITUDES_LOCATION = new Point( 60, 150 );
    private static final Point COMPONENTS_LOCATION = new Point( 60, 340 );
    private static final Point SUM_LOCATION = new Point( 60, 530 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 260, 80 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
    private static final Color WIGGLE_ME_COLOR = Color.RED;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_COMPONENTS = 7;
    private static final int WAVE_TYPE = SineWaveGraphic.WAVE_TYPE_SINE;
  
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private ComponentsGraphic _componentsGraphic;
    private SumGraphic _sumGraphic;
    private DiscreteControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public DiscreteModule( AbstractClock clock ) {
        
        super( SimStrings.get( "DiscreteModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Fourier Series
        _fourierSeriesModel = new FourierSeries();
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        // Amplitudes view
        AmplitudesGraphic amplitudesGraphic = new AmplitudesGraphic( apparatusPanel, _fourierSeriesModel );
        amplitudesGraphic.setLocation( AMPLITUDES_LOCATION );
        apparatusPanel.addGraphic( amplitudesGraphic, AMPLITUDES_LAYER );
        
        // Components view
        _componentsGraphic = new ComponentsGraphic( apparatusPanel, _fourierSeriesModel );
        _componentsGraphic.setLocation( COMPONENTS_LOCATION );
        apparatusPanel.addGraphic( _componentsGraphic, COMPONENTS_LAYER );
        
        // Sum view
        _sumGraphic = new SumGraphic( apparatusPanel, _fourierSeriesModel );
        _sumGraphic.setLocation( SUM_LOCATION );
        apparatusPanel.addGraphic( _sumGraphic, SUM_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new DiscreteControlPanel( this, _fourierSeriesModel, _componentsGraphic, _sumGraphic );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );
         
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _fourierSeriesModel );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        _fourierSeriesModel.setFundamentalFrequency( FUNDAMENTAL_FREQUENCY );
        _fourierSeriesModel.setNumberOfComponents( NUMBER_OF_COMPONENTS );
        _fourierSeriesModel.getComponent( 0 ).setAmplitude( 1.0 );
        for ( int i = 1; i < _fourierSeriesModel.getNumberOfComponents(); i++ ) {
            ( (FourierComponent) _fourierSeriesModel.getComponent( i ) ).setAmplitude( 0 );
        }
        
        _componentsGraphic.setWaveType( WAVE_TYPE );
        
        _sumGraphic.setWaveType( WAVE_TYPE );
        
        _controlPanel.update();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic {

        private FourierSeries _fourierSeriesModel;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param fourierSeriesModel
         */
        public ThisWiggleMeGraphic( final Component component, BaseModel model, FourierSeries fourierSeriesModel ) {
            super( component, model );

            _fourierSeriesModel = fourierSeriesModel;
            
            setText( SimStrings.get( "DiscreteModule.wiggleMe" ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.MIDDLE_LEFT, new Vector2D( -40, 30 ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.TOP_LEFT, new Vector2D( -40, -30 ), WIGGLE_ME_COLOR );
            setRange( 20, 10 );
            setEnabled( true );
            
            // Disable the wiggle me when the mouse is pressed.
            component.addMouseListener( new MouseInputAdapter() { 
                public void mousePressed( MouseEvent event ) {
                    // Disable
                    setEnabled( false );
                    // Unwire
                    component.removeMouseListener( this );
                }
           } );
        }
    }
}
