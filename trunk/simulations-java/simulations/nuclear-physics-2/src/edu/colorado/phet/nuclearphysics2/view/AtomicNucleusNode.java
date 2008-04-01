/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;
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
    
    // Fonts for displaying the label on the nucleus.
    private static final Font ISOTOPE_NUMBER_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 6 );
    private static final Font CHEMICAL_SYMBOL_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    
    // Factor by which the font should be scaled.  This allows us to use
    // standard font sizes and the use the Piccolo scaling capabilities,
    // which tends to look better than using non-standard sizes.
    private static final double LABEL_SCALING_FACTOR = 0.4;
    
    // Constants that control the nature of the explosion graphic.
    private static int   EXPLOSION_COUNTER_RESET_VAL = 20;
    private static Color EXPLOSION_STROKE_COLOR = new Color(0xffff33);
    private static Color EXPLOSION_FILL_COLOR = new Color(0xffff33);
    private static float EXPLOSION_MIN_TRANSPARENCY = 0.4f;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PText _isotopeNumberLabel;
    private PText _chemicalSymbolLabel;
    private PText _isotopeNumberLabelShadow;
    private PText _chemicalSymbolLabelShadow;
    private AtomicNucleus _atomicNucleus;
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
        
        // Create the labels (initially blank) and add them to the world.        
        _isotopeNumberLabelShadow = new PText("");
        _isotopeNumberLabelShadow.setFont( ISOTOPE_NUMBER_FONT );
        _isotopeNumberLabelShadow.setTextPaint( Color.BLACK );
        _isotopeNumberLabelShadow.setScale(LABEL_SCALING_FACTOR);
        addChild(_isotopeNumberLabelShadow);
        
        _isotopeNumberLabel = new PText("");
        _isotopeNumberLabel.setFont( ISOTOPE_NUMBER_FONT );
        _isotopeNumberLabel.setTextPaint( Color.MAGENTA );
        _isotopeNumberLabel.setScale(LABEL_SCALING_FACTOR);
        addChild(_isotopeNumberLabel);
        
        _chemicalSymbolLabelShadow = new PText("");
        _chemicalSymbolLabelShadow.setFont( CHEMICAL_SYMBOL_FONT );
        _chemicalSymbolLabelShadow.setTextPaint( Color.BLACK );
        _chemicalSymbolLabelShadow.setScale(LABEL_SCALING_FACTOR);
        addChild(_chemicalSymbolLabelShadow);
        
        _chemicalSymbolLabel = new PText("");
        _chemicalSymbolLabel.setFont( CHEMICAL_SYMBOL_FONT );
        _chemicalSymbolLabel.setTextPaint( Color.MAGENTA );
        _chemicalSymbolLabel.setScale(LABEL_SCALING_FACTOR);
        addChild(_chemicalSymbolLabel);
        
        // Set the label based on the atomic number of the nucleus.
        _currentAtomicWeight = _atomicNucleus.getAtomicWeight();
        setLabel(_currentAtomicWeight);
        
        // Register as a listener for the model representation.
        _atomicNucleus.addListener(new AtomicNucleus.Listener(){
            public void positionChanged(){
                update();
            }
            public void atomicWeightChanged(int newAtomicWeight){
                
                if (_currentAtomicWeight > newAtomicWeight){
                    // This was a decay event, so kick off the explosion graphic.
                    _explosionCounter = EXPLOSION_COUNTER_RESET_VAL;
                }
                
                // Update the label to reflect the new element.
                setLabel(newAtomicWeight);
                update();
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
     * Set the label for the nucleus based on the atomic weight.  Note that
     * this method makes assumptions about the element associated with a given
     * weight because of the nature of the overall simulation.  Strictly
     * speaking, if it wanted to be technically correct, this should look at
     * the number of protons and neutrons to determine the element and the
     * isotope.  This doesn't matter for this sim, but would be important if
     * this class or method was ever reused in another sim.
     * 
     * @param atomicWeight
     */
    private void setLabel(int atomicWeight){
        switch (atomicWeight){
        case 235:
            // Set the label for Uranium 235.
            _isotopeNumberLabel.setText( NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER );
            _isotopeNumberLabel.setTextPaint( NuclearPhysics2Constants.URANIUM_LABEL_COLOR );
            _isotopeNumberLabelShadow.setText( NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER );

            _chemicalSymbolLabel.setText( NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL );
            _chemicalSymbolLabel.setTextPaint( NuclearPhysics2Constants.URANIUM_LABEL_COLOR );
            _chemicalSymbolLabelShadow.setText( NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL );            
            
            break;
            
        case 211:
            // Set the label for Polonium 211.
            _isotopeNumberLabel.setText( NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER );
            _isotopeNumberLabel.setTextPaint( NuclearPhysics2Constants.POLONIUM_LABEL_COLOR );
            _isotopeNumberLabelShadow.setText( NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER );

            _chemicalSymbolLabel.setText( NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL );
            _chemicalSymbolLabel.setTextPaint( NuclearPhysics2Constants.POLONIUM_LABEL_COLOR );
            _chemicalSymbolLabelShadow.setText( NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL );            
            
            break;
            
        case 207:
            // Set the label for Lead 207.
            _isotopeNumberLabel.setText( NuclearPhysics2Strings.LEAD_207_ISOTOPE_NUMBER );
            _isotopeNumberLabel.setTextPaint( NuclearPhysics2Constants.LEAD_LABEL_COLOR );
            _isotopeNumberLabelShadow.setText( NuclearPhysics2Strings.LEAD_207_ISOTOPE_NUMBER );

            _chemicalSymbolLabel.setText( NuclearPhysics2Strings.LEAD_207_CHEMICAL_SYMBOL );
            _chemicalSymbolLabel.setTextPaint( NuclearPhysics2Constants.LEAD_LABEL_COLOR );
            _chemicalSymbolLabelShadow.setText( NuclearPhysics2Strings.LEAD_207_CHEMICAL_SYMBOL );
            
            break;
            
        case 0:
            // This is a special case that is used to signal that the nucleus
            // should have no label.
            
            _isotopeNumberLabel.setText( "" );
            _isotopeNumberLabelShadow.setText( "" );

            _chemicalSymbolLabel.setText( "" );
            _chemicalSymbolLabelShadow.setText( "" );
            
            break;
            
        default:
            // Issue a warning and set the label to nothing.

            System.err.println( "Unable to set label for nucleus with atomic weight " + atomicWeight );

            _isotopeNumberLabel.setText( "" );
            _isotopeNumberLabelShadow.setText( "" );
    
            _chemicalSymbolLabel.setText( "" );
            _chemicalSymbolLabelShadow.setText( "" );
            
            break;
        }
    }
    
    /**
     * This method updates the positions of the labels.  It is generally
     * called when something has changed, like when the nucleus decays or
     * when the sim window is resized.
     */
    private void update(){

        _isotopeNumberLabel.setOffset( _atomicNucleus.getPosition().getX() - _atomicNucleus.getDiameter()/2,  
                _atomicNucleus.getPosition().getY() - _atomicNucleus.getDiameter()/2);
        
        Point2D isotopeLabelOffset = _isotopeNumberLabel.getOffset();

            _isotopeNumberLabelShadow.setOffset( isotopeLabelOffset.getX() + 0.15,  
                    isotopeLabelOffset.getY() + 0.15);
        
        _chemicalSymbolLabel.setOffset( isotopeLabelOffset.getX() + 
                (_isotopeNumberLabel.getWidth() * _isotopeNumberLabel.getScale()),  
                isotopeLabelOffset.getY());

        Point2D chemicalSymbolLabelOffset = _chemicalSymbolLabel.getOffset();

        _chemicalSymbolLabelShadow.setOffset( chemicalSymbolLabelOffset.getX() + 0.15,  
                chemicalSymbolLabelOffset.getY() + 0.15);
    }

    /**
     * Handle the ticking of the simulation clock.
     * 
     * @param clockEvent
     */
    private void handleClockTicked(ClockEvent clockEvent){

        if (_explosionCounter > 0){
            // Step the explosion graphic.
            double explosionRadius = (double)(EXPLOSION_COUNTER_RESET_VAL - _explosionCounter + 1) * 4;
            _explosionShape.setFrameFromCenter( 0, 0, explosionRadius, explosionRadius );
            _explosion.setPathTo( _explosionShape );
            _explosion.setTransparency( 
                    EXPLOSION_MIN_TRANSPARENCY * (float)_explosionCounter / (float)EXPLOSION_COUNTER_RESET_VAL);
            _explosionCounter--;
        }
    }
}
