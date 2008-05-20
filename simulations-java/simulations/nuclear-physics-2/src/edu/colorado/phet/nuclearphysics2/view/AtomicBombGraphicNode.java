/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.model.ContainmentVessel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class represents a Piccolo PNode that can display a textual message
 * and a graphical image that depicts an atomic bomb explosion.  The image
 * enlarges to fill the screen to give the overall impression of an explosion.
 *
 * @author John Blanco
 */
public class AtomicBombGraphicNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    private static final Font LABEL_FONT = new PhetFont(32, true, true);
    private static final Color LABEL_COLOR = Color.RED;
    
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
                _explodingState = STATE_EXPLODING;
                _explosionLabel.setVisible( true );
                _explosionGraphic.setVisible( true );
            }
            public void resetOccurred(){
                handleResetOccurred();
            }
        });
        
        // Create the node with the graphic of the bomb.
        _explosionGraphic = new PImage(NuclearPhysics2Resources.getImage( "mushroom_cloud.jpg" ));
        _explosionGraphic.setVisible( false );
        addChild(_explosionGraphic);
        
        // Create the node with textual label but don't make it visible yet.
        _explosionLabel = new ShadowPText(NuclearPhysics2Strings.EXPLOSION_LABEL, LABEL_COLOR, LABEL_FONT);
        _explosionLabel.setVisible( false );
        addChild(_explosionLabel);
        
        // Make sure we don't intercept any mouse clicks.
        setPickable( false );
        setChildrenPickable( false );
    }
    
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public void setContainerSize(double width, double height){
        
        _containerWidth = width;
        _containerHeight = height;
        
        updateLayout();
    }
    
    public void updateLayout(){
        
        if (_explodingState == STATE_EXPLODED){
            
            setOffset( 0, 0 );
            
            _explosionGraphic.setScale( 1.0 );
            _explosionGraphic.setBounds( 0, 0, _containerWidth, _containerHeight ); 
            
            _explosionLabel.setOffset( 10, 10 );
        }
        else{
            
            setOffset( (_containerWidth/ 2) - (getFullBoundsReference().width / 2),
                    (_containerHeight / 2) - (getFullBoundsReference().height / 2) );
            
            _explosionGraphic.setOffset( 
                    getFullBoundsReference().width/2 - _explosionGraphic.getFullBoundsReference().width/2,
                    getFullBoundsReference().height/2 - _explosionGraphic.getFullBoundsReference().height/2 );
            
            _explosionLabel.setOffset( 10, 10 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent ce){
        if (_explodingState == STATE_EXPLODING){
            if (_explosionGraphic.getFullBounds().getHeight() < _containerHeight){
                // Expand the explosion graphic.
                _explosionGraphic.setScale( _explosionGraphic.getScale() + 0.20 );
                setContainerSize( _containerWidth, _containerHeight );
            }
            else{
                // We have finished the expansion.
                _explodingState = STATE_EXPLODED;
            }
        }
    }
    
    private void handleResetOccurred(){
        _explodingState = STATE_IDLE;
        _explosionLabel.setVisible( false );
        _explosionGraphic.setVisible( false );
        _explosionGraphic.setScale( 1.0 );
        updateLayout();
    }
}
