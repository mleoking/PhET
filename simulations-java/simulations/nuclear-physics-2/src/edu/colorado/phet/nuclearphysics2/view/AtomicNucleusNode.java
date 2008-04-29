/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents the view of an Atomic Nucleus from the model.  Note
 * that most of the effort for displaying a nucleus is actually done by the
 * nodes associated with the individual particles of which it is composed, 
 * so this class does things associated with the nucleus as a whole, such as
 * displaying the label and showing the explosion if and when it decays.
 *
 * @author John Blanco
 */
public class AtomicNucleusNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Font for displaying the label on the nucleus.
    private static final Font ISOTOPE_LABEL_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    
    // Factor by which the font should be scaled.  This allows us to use
    // standard font sizes and the use the Piccolo scaling capabilities,
    // which tends to look better than using non-standard sizes.
    private static final double LABEL_SCALING_FACTOR = 0.35;
    
    // Constants that control the nature of the explosion graphic.
    private static int   EXPLOSION_COUNTER_RESET_VAL = 10;
    private static Color EXPLOSION_STROKE_COLOR = new Color(0xffff33);
    private static Color EXPLOSION_FILL_COLOR = new Color(0xffff33);
    private static float EXPLOSION_MIN_TRANSPARENCY = 0.4f;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private ShadowHTMLNode _isotopeLabel;
    protected AtomicNucleus _atomicNucleus;
    private int _currentAtomicWeight;
    private int _explosionCounter = 0;
    private PPath _explosion;
    private Ellipse2D _explosionShape;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AtomicNucleusNode(AtomicNucleus atomicNucleus)
    {
        _atomicNucleus = atomicNucleus;
        
        // Initialize the node that is used to display the explosion.
        _explosion = new PPath();
        _explosion.setStrokePaint( EXPLOSION_STROKE_COLOR );
        _explosion.setPaint( EXPLOSION_FILL_COLOR );
        addChild( _explosion );
        
        // Create the shape that will be used to define the explosion.
        _explosionShape = new Ellipse2D.Double();
        
        // Create the labels (initially blank) and add them to this node.        
        _isotopeLabel = new ShadowHTMLNode("");
        _isotopeLabel.setFont( ISOTOPE_LABEL_FONT );
        _isotopeLabel.setScale( LABEL_SCALING_FACTOR );
        _isotopeLabel.setShadowOffset( 0.5, 0.5 );
        addChild(_isotopeLabel);
        
        // Set the label based on the configuration of the nucleus.
        _currentAtomicWeight = _atomicNucleus.getAtomicWeight();
        setLabel(_atomicNucleus.getNumProtons(), _atomicNucleus.getNumNeutrons());
        
        // Register as a listener for the model representation.
        _atomicNucleus.addListener(new AtomicNucleus.Adapter(){
            public void positionChanged(){
                update();
            }
            public void atomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                
                handleAtomicWeightChanged( atomicNucleus, numProtons, numNeutrons, byProducts );
            }
        });
        
        // Register as a listener to the clock that is driving the model.
        _atomicNucleus.getClock().addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Ignore.
            }
        });
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    /**
     * Set the label for the nucleus based on the number of protons and
     * neutrons
     * 
     * @param numProtons - The total number of protons in the nucleus.
     * @param numNeutrons - The total number of neutrons in the nucleus.
     */
    private void setLabel(int numProtons, int numNeutrons){
        
        String labelText = "";
        Color labelColor = Color.GRAY;
        
        switch (numProtons){
        case 92:
            // Uranium
            if (numNeutrons == 143){
                // Uranium 235
                labelText = "<html><sup><font size=-2>" + NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER +
                " </font></sup>" + NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL + "</html>";
                labelColor = NuclearPhysics2Constants.URANIUM_235_LABEL_COLOR;
            }
            else if (numNeutrons == 144){
                // Uranium 236
                // TODO: JPB TBD - Make these into strings if we decide to keep it.
                labelText = "<html><sup><font size=-2>" + "236" +
                " </font></sup>" + NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL + "</html>";
                labelColor = Color.ORANGE;
            }
            else if (numNeutrons == 146){
                // Uranium 238
                labelText = "<html><sup><font size=-2>" + NuclearPhysics2Strings.URANIUM_238_ISOTOPE_NUMBER +
                " </font></sup>" + NuclearPhysics2Strings.URANIUM_238_CHEMICAL_SYMBOL + "</html>";
                labelColor = NuclearPhysics2Constants.URANIUM_238_LABEL_COLOR;
            }
            else if (numNeutrons == 147){
                // Uranium 239
                labelText = "<html><sup><font size=-2>" + NuclearPhysics2Strings.URANIUM_239_ISOTOPE_NUMBER +
                " </font></sup>" + NuclearPhysics2Strings.URANIUM_239_CHEMICAL_SYMBOL + "</html>";
                labelColor = NuclearPhysics2Constants.URANIUM_239_LABEL_COLOR;
            }
            
            break;
            
        case 84:
            // Polonium
            if (numNeutrons == 127){
            // Polonium 211
                labelText = "<html><sup><font size=-2>" + NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER +
                " </font></sup>" + NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL + "</html>";
                labelColor = NuclearPhysics2Constants.POLONIUM_LABEL_COLOR;
            }
            
            break;
            
        case 82:
            // Lead
            if (numNeutrons == 125){
                labelText = "<html><sup><font size=-2>" + NuclearPhysics2Strings.LEAD_207_ISOTOPE_NUMBER +
                " </font></sup>" + NuclearPhysics2Strings.LEAD_207_CHEMICAL_SYMBOL + "</html>";
                labelColor = NuclearPhysics2Constants.LEAD_LABEL_COLOR;
            }
            
            break;
            
        case 0:
            // This is a special case that is used to signal that the nucleus
            // should have no label.
            
            labelText = "";
            
            break;
            
        default:
            // Add no label.
            labelText = "";
            
            break;
        }

        _isotopeLabel.setColor( labelColor );
        if (labelColor == Color.BLACK){
            _isotopeLabel.setShadowColor( Color.WHITE );
        }
        else{
            _isotopeLabel.setShadowColor( Color.BLACK );            
        }
        
        _isotopeLabel.setHtml( labelText );
    }
    
    /**
     * This method updates the positions of the labels.  It is generally
     * called when something has changed, like when the nucleus decays or
     * when the sim window is resized.
     */
    protected void update(){

        _isotopeLabel.setOffset( _atomicNucleus.getPositionRef().getX() - _atomicNucleus.getDiameter()/2,  
                _atomicNucleus.getPositionRef().getY() - _atomicNucleus.getDiameter()/2);
        
    }

    /**
     * Handle the ticking of the simulation clock.
     * 
     * @param clockEvent
     */
    private void handleClockTicked(ClockEvent clockEvent){

        if (_explosionCounter > 0){
            // Step the explosion graphic.
            double explosionRadius = (double)(EXPLOSION_COUNTER_RESET_VAL - _explosionCounter + 1) * 3;
            _explosionShape.setFrameFromCenter( 0, 0, explosionRadius, explosionRadius );
            _explosion.setPathTo( _explosionShape );
            _explosion.setTransparency( 
                    EXPLOSION_MIN_TRANSPARENCY * (float)_explosionCounter / (float)EXPLOSION_COUNTER_RESET_VAL);
            _explosionCounter--;
        }
        else{
            _explosion.setVisible( false );
        }
    }

    /**
     * Handle the notification that says that the atomic nucleus that this
     * node represents has changed its atomic weight.
     * 
     * @param atomicNucleus
     * @param numProtons
     * @param numNeutrons
     * @param byProducts
     */
    protected void handleAtomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
        
        int newAtomicWeight = numProtons + numNeutrons;
        if ((newAtomicWeight < _currentAtomicWeight) && (newAtomicWeight != 0) && (byProducts != null)){
            // This was a decay event, so kick off the explosion graphic.
            _explosionCounter = EXPLOSION_COUNTER_RESET_VAL;
            _explosion.setOffset( _atomicNucleus.getPositionRef() );
            _explosion.setVisible( true );
        }
        
        // Save the new weight.
        _currentAtomicWeight = newAtomicWeight;
        
        // Update the label to reflect the new element.
        setLabel(numProtons, numNeutrons);
        update();
    }
}
