/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.ContainmentVessel;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This class represents a Piccolo PNode that can display a textual message
 * and a graphical image that depicts an atomic bomb explosion.  The image
 * enlarges over time to fill the screen to give the overall impression of an
 * explosion.
 *
 * @author John Blanco
 */
public class AtomicBombGraphicNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    private static final Font LABEL_FONT = new PhetFont(32, true, true);
    private static final Color LABEL_COLOR = Color.RED;
    private static final double INITAL_WIDTH = 20;
    
    // Values for variable that tracks explosion state.
    private static final int STATE_IDLE      = 0;
    private static final int STATE_EXPLODING = 1;
    private static final int STATE_EXPLODED  = 2;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    // Variables needed to register for and respond to clock ticks.
    private SwingClock   _clock;
    private ClockAdapter _clockAdapter;
    
    // Text node that contains the text displayed over the image.
    private ShadowPText _explosionLabel;
    
    // Node containing the explosion graphic.
    private PImage _explosionGraphic;
    
    // State variable that tracks if we are exploding.
    private int _explodingState;
    
    // Size of the container.
    double _containerWidth;
    double _containerHeight;
    
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
   
    /**
     * Constructor for this node.
     * 
     *  @param containmentVessel - The containment vessel within the model.
     *  @param clock - The clock that is driving the simulation.
     */
    public AtomicBombGraphicNode(ContainmentVessel containmentVessel, SwingClock clock){
        
        _clock = clock;
        
        // Initialize local variables.
        _explodingState = STATE_IDLE;
        
        // Register as a listener for clock ticks.
        _clockAdapter = new ClockAdapter(){
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
        };
        _clock.addClockListener( _clockAdapter );
        
        // Register as a listener to the containment vessel.
        containmentVessel.addListener( new ContainmentVessel.Adapter(){
            public void explosionOccurred(){
            	_explosionGraphic.setScale(1);
            	_explosionGraphic.setScale(INITAL_WIDTH / _explosionGraphic.getFullBoundsReference().width);
                _explodingState = STATE_EXPLODING;
                AtomicBombGraphicNode.this.setVisible( true );
            }
            public void resetOccurred(){
                handleResetOccurred();
            }
        });
        
        // Make ourself initially invisible.
        setVisible( false );
        
        // Create the node with the graphic of the bomb.
        _explosionGraphic = new PImage(NuclearPhysicsResources.getImage( "castle_romeo2.jpg" ));
        addChild(_explosionGraphic);
        
        // Create the node with textual label but don't make it visible yet.
        _explosionLabel = new ShadowPText(NuclearPhysicsStrings.EXPLOSION_LABEL, LABEL_COLOR, LABEL_FONT);
        addChild(_explosionLabel);
    }
    
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Sets the size of the container in which this node resides.  This is
     * used by this object to size itself correctly so that the graphic can
     * fill the screen.
     */
    public void setContainerSize(double width, double height){
        
        _containerWidth = width;
        _containerHeight = height;
        
        updateLayout();
    }
    
    /**
     * Update the size and position of the various subnodes that make up this
     * node.
     */
    public void updateLayout(){
        
        if (_explodingState == STATE_EXPLODED){
            
            setOffset( 0, 0 );
            
            _explosionGraphic.setScale( 1.0 );
            _explosionGraphic.setBounds( 0, 0, _containerWidth, _containerHeight );
            
            PBounds graphicBounds = _explosionGraphic.getFullBoundsReference();
            
            _explosionLabel.setOffset( graphicBounds.width / 2 - _explosionLabel.getFullBoundsReference().width / 2,
            		graphicBounds.height * 0.1);
        }
        else{
            PBounds graphicBounds = _explosionGraphic.getFullBoundsReference();
            
            setOffset( (_containerWidth/ 2) - (graphicBounds.width / 2),
                    (_containerHeight / 2) - (graphicBounds.height / 2) );
            
            _explosionGraphic.setOffset( 
            		graphicBounds.width/2 - _explosionGraphic.getFullBoundsReference().width/2,
            		graphicBounds.height/2 - _explosionGraphic.getFullBoundsReference().height/2 );
            
            _explosionLabel.setOffset( graphicBounds.width / 2 - _explosionLabel.getFullBoundsReference().width / 2,
            		graphicBounds.height * 0.1);
        }
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent ce){
        if (_explodingState == STATE_EXPLODING){
            if ((_explosionGraphic.getFullBounds().getHeight() < _containerHeight) ||
                (_explosionGraphic.getFullBounds().getWidth() < _containerWidth	)){
                // Expand the explosion graphic.
                _explosionGraphic.setScale( _explosionGraphic.getScale() + 0.05 );
                updateLayout();
                System.out.println("Scaling...");
            }
            else{
                // We have finished the expansion.
                _explodingState = STATE_EXPLODED;
            }
        }
    }
    
    private void handleResetOccurred(){
        _explodingState = STATE_IDLE;
        setVisible( false );
        _explosionGraphic.setScale( 1.0 );
        updateLayout();
    }
}
