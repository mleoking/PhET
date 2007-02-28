/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * BohrEnergyDiagram is the energy diagram for the Bohr model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BohrEnergyDiagram extends AbstractEnergyDiagram implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Margins inside the drawing area
    private static final double X_MARGIN = 20;
    private static final double Y_MARGIN = 20;
    
    // Horizontal space between a state's line and its label
    private static final double LINE_LABEL_SPACING = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    private ClockListener _clockListener;
    
    private BohrModel _atom; // atom we're observing
    private int _nPrevious; // previous value of n (electron state)
    private EnergySquiggle _squiggle; // the state change squiggle
    private double _squiggleLifetime; // how long the squiggle has been visible
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param canvas
     * @param clock
     */
    public BohrEnergyDiagram( PSwingCanvas canvas, IClock clock ) {
        super( BohrModel.getNumberOfStates(), canvas );
        
        assert( BohrModel.getGroundState() == 1 ); // n=1 must be ground state
        assert( BohrModel.getNumberOfStates() == 6 ); // 6 states

        for ( int n = 1; n <= BohrModel.getNumberOfStates(); n++ ) { 
            PNode levelNode = createStateNode( n );
            double x = getXOffset( n );
            double y = getYOffset( n );
            levelNode.setOffset( x, y );
            getStateLayer().addChild( levelNode );
        }
        
        _clockListener = new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                checkSquiggleLifetime( clockEvent.getWallTimeChange() );
            }
        };
        _clock = clock;
        _clock.addClockListener( _clockListener );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        clearAtom();
        _clock.removeClockListener( _clockListener );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the atom associated with the diagram.
     * Initializes the electron's position, based on it's current state.
     * 
     * @param atom
     */
    public void setAtom( BohrModel atom ) {

        // remove any existing squiggle
        removeSquiggle();
        
        // remove association with existing atom
        if ( _atom != null ) {
            _atom.deleteObserver( this );
            _atom = null;
        }
        
        // Electron is invisible if there is no associated atom
        ElectronNode electronNode = getElectronNode();
        electronNode.setVisible( atom != null );
        
        if ( atom != null ) {
            
            // observe the atom
            _atom = atom;
            _atom.addObserver( this );

            // Set electron's initial position
            updateElectronPosition();
            
            // Remember electron's initial state
            _nPrevious = atom.getElectronState();
        }
    }
    
    /**
     * Removes the association between this diagram and any atom.
     */
    public void clearAtom() {
        setAtom( null );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                updateElectronPosition();
                updateSquiggle();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Updates the squiggle to show a state change.
     */
    private void updateSquiggle() {

        // remove any existing squiggle
        removeSquiggle();
        
        // if the diagram isn't visible, don't draw the new squiggle
        if ( !isVisible() ) {
            return;
        }

        // create the new squiggle for photon absorption/emission
        final int n = _atom.getElectronState();
        if ( n != _nPrevious ) {
            double wavelength = 0;
            if ( n > _nPrevious ) {
                // a photon has been absorbed
                wavelength = BohrModel.getWavelengthAbsorbed( _nPrevious, n );
            }
            else {
                // a photon has been emitted
                wavelength = BohrModel.getWavelengthAbsorbed( n, _nPrevious );
            }
            double x1 = getXOffset( _nPrevious ) + ( LINE_LENGTH / 2 );
            double y1 = getYOffset( _nPrevious );
            double x2 = getXOffset( n ) + ( LINE_LENGTH / 2 );
            double y2 = getYOffset( n );
            _squiggle = new EnergySquiggle( x1, y1, x2, y2, wavelength );
            getSquiggleLayer().addChild( _squiggle );
            
            // Remember electron's state
            _nPrevious = n;
        }
    }
    
    /*
     * Removes the squiggle, if there is one.
     */
    private void removeSquiggle() {
        if ( _squiggle != null ) {
            getSquiggleLayer().removeChild( _squiggle );
            _squiggleLifetime = 0;
            _squiggle = null;
        }
    }
    
    /*
     * Checks to see how long the squiggle has been visible.
     * When the time exceeds some maximum, remove the squiggle.
     *  
     * @param wallTimeChange
     */
    private void checkSquiggleLifetime( double wallTimeChange ) {
        if ( _squiggle != null ) {
            _squiggleLifetime += wallTimeChange;
            if ( _squiggleLifetime > SQUIGGLE_LIFETIME ) {
                removeSquiggle();
            }
        }
    }
    
    /*
     * Updates the position of the electron in the diagram
     * to match the electron's state.
     */
    private void updateElectronPosition() {
        ElectronNode electronNode = getElectronNode();
        final int n = _atom.getElectronState();
        final double x = getXOffset( n ) + ( LINE_LENGTH / 2 );
        final double y = getYOffset( n ) - ( electronNode.getFullBounds().getHeight() / 2 );
        electronNode.setOffset( x, y );
    }
    
    /*
     * Gets the x-offset that corresponds to a specified state.
     * This is used for positioning both the state lines and the electron.
     * 
     * @param state
     * @return double
     */
    private double getXOffset( int state ) {
        return getDrawingArea().getX() + X_MARGIN;
    }
    
    /*
     * Gets the y-offset that corresponds to a specific state.
     * This is used for positioning both the state lines and the electron.
     * 
     * @param state
     * @return double
     */
    private double getYOffset( int state ) {
        final double minE = getEnergy( 1 );
        final double maxE = getEnergy( BohrModel.getNumberOfStates() );
        final double rangeE = maxE - minE;
        Rectangle2D drawingArea = getDrawingArea();
        final double height = drawingArea.getHeight() - ( 2 * Y_MARGIN );
        double y = drawingArea.getY() + Y_MARGIN + ( height * ( maxE - getEnergy( state ) ) / rangeE );
        return y;
    }
    
    /*
     * Creates a node that represents a state (or level) of the electron.
     * This node consists of a horizontal line with an "n=" label to the 
     * right of the line.
     * 
     * @param state
     * @return PNode
     */
    private static PNode createStateNode( int state ) {
        
        PNode lineNode = createStateLineNode();
        PNode labelNode = createStateLabelNode( state );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( lineNode );
        parentNode.addChild( labelNode );
        
        // vertically align centers of label and line
        lineNode.setOffset( 0, 0 );
        double x = lineNode.getWidth() + LINE_LABEL_SPACING;
        double y = -( ( lineNode.getHeight() / 2 ) + ( labelNode.getHeight() / 2 ) );
        if ( state == 6 ) {
            // HACK requested by Sam McKagan: for n=6, move label up a bit to prevent overlap with n=5
            labelNode.setOffset( x, y - 3.5 );
        }
        else {
            labelNode.setOffset( x, y );
        }
        
        return parentNode;
    }
}
