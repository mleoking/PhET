/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

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
    private static final Font ISOTOPE_NUMBER_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 10 );
    private static final Font ISOTOPE_CHEM_SYMBOL_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    
    // Factor by which the font should be scaled.  This allows us to use
    // standard font sizes and the use the Piccolo scaling capabilities,
    // which tends to look better than using non-standard sizes.
    private static final double LABEL_SCALING_FACTOR = 0.30;
    
    // Constants that control the nature of the explosion graphic.
    private static final int   EXPLOSION_COUNTER_RESET_VAL = 10;
    private static final Color EXPLOSION_STROKE_COLOR = new Color(0xffff33);
    private static final Color EXPLOSION_FILL_COLOR = new Color(0xffff33);
    private static final float EXPLOSION_MIN_TRANSPARENCY = 0.4f;
    
    // Constants that control the range of nuclei for which labels are created.
    private static final int MIN_PROTONS_OF_LABELED_NUCLEUS = 80;
    
    // Amount that shadow should be offset from the main text.
    private static final double SHADOW_OFFSET = 0.2;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    protected AtomicNucleus _atomicNucleus;
    private int _currentAtomicWeight;
    private int _explosionCounter = 0;
    private PPath _explosion;
    private Ellipse2D _explosionShape;

    // The following variables represent the four portions of the label,
    // which are the isotope number, the chemical symbol, and a shadow for
    // each.  These were done individually because it turned out that using
    // the HTMLShadowNode class used too much memory and computational power
    // when large quantities of them were moving around the screen.
    private PText _isotopeNumber;
    private PText _isotopeNumberShadow;
    private PText _isotopeChemSymbol;
    private PText _isotopeChemSymbolShadow;
    
    // Adapter for registering to get nucleus events.
    AtomicNucleus.Adapter _atomicNucleusAdapter = new AtomicNucleus.Adapter(){
        
        public void positionChanged(){
            update();
        }
        
        public void atomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                ArrayList byProducts){
            
            handleAtomicWeightChanged( atomicNucleus, numProtons, numNeutrons, byProducts );
        }
    };
    
    // Adapter for registering to receive clock events.
    ClockAdapter _clockAdapter = new ClockAdapter(){
        public void clockTicked(ClockEvent clockEvent){
            handleClockTicked(clockEvent);
        }
    };
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AtomicNucleusNode(AtomicNucleus atomicNucleus)
    {
        _atomicNucleus = atomicNucleus;
        _currentAtomicWeight = _atomicNucleus.getAtomicWeight();
        
        // Initialize the node that is used to display the explosion.
        _explosion = new PPath();
        _explosion.setStrokePaint( EXPLOSION_STROKE_COLOR );
        _explosion.setPaint( EXPLOSION_FILL_COLOR );
        addChild( _explosion );
        
        // Create the shape that will be used to define the explosion.
        _explosionShape = new Ellipse2D.Double();
        
        // Create the labels (initially blank) and add them to this node.  Note
        // that not all nuclei get a label allocated for them.  This is an
        // optimization, since it was found that the allocation and cleanup of
        // these nodes was expensive in terms of memory usage.
        if (_atomicNucleus.getNumProtons() >= MIN_PROTONS_OF_LABELED_NUCLEUS){
            
            _isotopeNumberShadow = new PText();
            _isotopeNumberShadow.setFont( ISOTOPE_NUMBER_FONT );
            _isotopeNumberShadow.setScale( LABEL_SCALING_FACTOR );
            addChild(_isotopeNumberShadow);
            
            _isotopeNumber = new PText();
            _isotopeNumber.setFont( ISOTOPE_NUMBER_FONT );
            _isotopeNumber.setScale( LABEL_SCALING_FACTOR );
            addChild(_isotopeNumber);
            
            _isotopeChemSymbolShadow = new PText();
            _isotopeChemSymbolShadow.setFont( ISOTOPE_CHEM_SYMBOL_FONT );
            _isotopeChemSymbolShadow.setScale( LABEL_SCALING_FACTOR );
            addChild(_isotopeChemSymbolShadow);
            
            _isotopeChemSymbol = new PText();
            _isotopeChemSymbol.setFont( ISOTOPE_CHEM_SYMBOL_FONT );
            _isotopeChemSymbol.setScale( LABEL_SCALING_FACTOR );
            addChild(_isotopeChemSymbol);
        }
        
        // Set the label based on the configuration of the nucleus.
        setLabel(_atomicNucleus.getNumProtons(), _atomicNucleus.getNumNeutrons());
        
        // Register as a listener for the model representation.
        _atomicNucleus.addListener(_atomicNucleusAdapter);
        
        // Register as a listener to the clock that is driving the model.
        _atomicNucleus.getClock().addClockListener( _clockAdapter );
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Perform any cleanup necessary before being garbage collected.
     */
    public void cleanup(){
        // Remove ourself as a listener from any place that we have registered
        // in order to avoid memory leaks.
        _atomicNucleus.removeListener(_atomicNucleusAdapter);
        _atomicNucleus.getClock().removeClockListener( _clockAdapter );
    }

    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the label for the nucleus based on the number of protons and
     * neutrons
     * 
     * @param numProtons - The total number of protons in the nucleus.
     * @param numNeutrons - The total number of neutrons in the nucleus.
     */
    private void setLabel(int numProtons, int numNeutrons){
        
        if (_isotopeChemSymbol == null){
            // Don't bother doing anything if there is no label to set.
            return;
        }
        
        String chemSymbol = "";
        String isotopeNumber = "";
        Color labelColor = Color.GRAY;
        
        switch (numProtons){
        case 92:
            // Uranium
            if (numNeutrons == 143){
                // Uranium 235
                isotopeNumber = NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysics2Constants.URANIUM_235_LABEL_COLOR;
            }
            else if (numNeutrons == 144){
                // Uranium 236
                isotopeNumber = NuclearPhysics2Strings.URANIUM_236_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysics2Strings.URANIUM_236_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysics2Constants.URANIUM_236_LABEL_COLOR;
            }
            else if (numNeutrons == 146){
                // Uranium 238
                isotopeNumber = NuclearPhysics2Strings.URANIUM_238_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysics2Strings.URANIUM_238_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysics2Constants.URANIUM_238_LABEL_COLOR;
            }
            else if (numNeutrons == 147){
                // Uranium 239
                isotopeNumber = NuclearPhysics2Strings.URANIUM_239_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysics2Strings.URANIUM_239_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysics2Constants.URANIUM_239_LABEL_COLOR;
            }
            
            break;
            
        case 84:
            // Polonium
            if (numNeutrons == 127){
            // Polonium 211
                isotopeNumber = NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysics2Constants.POLONIUM_LABEL_COLOR;
            }
            
            break;
            
        case 82:
            // Lead
            if (numNeutrons == 125){
                isotopeNumber = NuclearPhysics2Strings.LEAD_207_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysics2Strings.LEAD_207_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysics2Constants.LEAD_LABEL_COLOR;
            }
            
            break;
            
        case 0:
            // This is a special case that is used to signal that the nucleus
            // should have no label.
            
            chemSymbol = "";
            
            break;
            
        default:
            // Add no label.
            chemSymbol = "";
            
            break;
        }

        _isotopeChemSymbol.setTextPaint( labelColor );
        _isotopeNumber.setTextPaint( labelColor );
        
        if (labelColor == Color.BLACK){
            _isotopeChemSymbolShadow.setTextPaint( Color.WHITE );
            _isotopeNumberShadow.setTextPaint( Color.WHITE );
        }
        else{
            _isotopeChemSymbolShadow.setTextPaint( Color.BLACK );
            _isotopeNumberShadow.setTextPaint( Color.BLACK );
        }
        
        _isotopeChemSymbol.setText( chemSymbol );
        _isotopeChemSymbolShadow.setText( chemSymbol );
        _isotopeNumber.setText( isotopeNumber );
        _isotopeNumberShadow.setText( isotopeNumber );
    }
    
    /**
     * This method updates the positions of the labels.  It is generally
     * called when something has changed, like when the nucleus decays or
     * when the sim window is resized.
     */
    protected void update(){

        // Optimization: Only check one of the label elements, and only do
        // the update if it exists.
        if (_isotopeChemSymbol != null){
            
            double numPosX = _atomicNucleus.getPositionReference().getX() - _atomicNucleus.getDiameter()/2;
            double numPosY = _atomicNucleus.getPositionReference().getY() - _atomicNucleus.getDiameter()/2;
            _isotopeNumber.setOffset( numPosX, numPosY );            
            _isotopeNumberShadow.setOffset( numPosX + SHADOW_OFFSET, numPosY + SHADOW_OFFSET);
            
            double chemPosX = _isotopeNumber.getOffset().getX() + _isotopeNumber.getFullBounds().getWidth();
            double chemPosY = _isotopeNumber.getOffset().getY();
            _isotopeChemSymbol.setOffset( chemPosX, chemPosY );
            _isotopeChemSymbolShadow.setOffset( chemPosX + SHADOW_OFFSET, chemPosY + SHADOW_OFFSET);            
        }
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
            _explosion.setOffset( _atomicNucleus.getPositionReference() );
            _explosion.setVisible( true );
        }
        
        // Save the new weight.
        _currentAtomicWeight = newAtomicWeight;
        
        // Update the label to reflect the new element.
        setLabel(numProtons, numNeutrons);
        update();
    }
}
