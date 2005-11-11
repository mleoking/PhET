/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.module;

import java.awt.Dimension;
import java.awt.Font;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * QTModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension CANVAS_SIZE = new Dimension( 900, 800 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * @param title
     * @param clock
     */
    public QTModule( AbstractClock clock ) {
        super( SimStrings.get( "QTModule.title" ), clock );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        PhetPCanvas canvas = new PhetPCanvas( CANVAS_SIZE );
        setPhetPCanvas( canvas );
        
        //XXX piccolo tests
        {
            PText energyTitle = new PText( SimStrings.get( "EnergyView.title") );
            energyTitle.setFont( new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 ) );
            energyTitle.translate( 15, 200 );
            energyTitle.rotate( Math.toRadians( -90 ) );
            energyTitle.scale( 2 );
            canvas.addWorldChild( energyTitle );

            PText waveFunctionTitle = new PText( SimStrings.get( "WaveFunctionView.title") );
            waveFunctionTitle.setFont( new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 ) );
            waveFunctionTitle.translate( 15, 440 );
            waveFunctionTitle.rotate( Math.toRadians( -90 ) );
            waveFunctionTitle.scale( 2 );
            canvas.addWorldChild( waveFunctionTitle );
            
            PText probabilityDensityTitle = new PText( SimStrings.get( "ProbabilityDensityView.title") );
            probabilityDensityTitle.setFont( new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 ) );
            probabilityDensityTitle.translate( 15, 710 );
            probabilityDensityTitle.scale( 2 );
            probabilityDensityTitle.rotate( Math.toRadians( -90 ) );
            canvas.addWorldChild( probabilityDensityTitle );
            
            PText positionAxisTitle = new PText( SimStrings.get( "PositionAxis.title") );
            positionAxisTitle.setFont( new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 ) );
            positionAxisTitle.translate( 400, 755 );
            positionAxisTitle.scale( 2 );
            canvas.addWorldChild( positionAxisTitle );
            
            int xMargin = 55;
            int yMargin = 10;
            int graphWidth = CANVAS_SIZE.width - xMargin - 10;
            int graphHeight = ( CANVAS_SIZE.height - ( 4 * yMargin ) - 50 ) / 3;
            
            PPath energyGraph = new PPath();
            energyGraph.setPathToRectangle( 0, 0, graphWidth, graphHeight );
            energyGraph.translate( xMargin, yMargin );
            canvas.addWorldChild( energyGraph );
            
            PPath waveFunctionGraph = new PPath();
            waveFunctionGraph.setPathToRectangle( 0, 0, graphWidth, graphHeight );
            waveFunctionGraph.translate( xMargin, yMargin + graphHeight + yMargin );
            canvas.addWorldChild( waveFunctionGraph );
            
            PPath probabilityDensityGraph = new PPath();
            probabilityDensityGraph.setPathToRectangle( 0, 0, graphWidth, graphHeight );
            probabilityDensityGraph.translate( xMargin, yMargin + graphHeight + yMargin + graphHeight + yMargin );
            canvas.addWorldChild( probabilityDensityGraph );
        }
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new QTControlPanel( this );
        setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        reset();
    }

    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the module to its initial state.
     */
    public void reset() {
        _controlPanel.reset();
    }
    
    //XXX hack, remove this!
    public boolean hasHelp() {
        return true;
    }
}
