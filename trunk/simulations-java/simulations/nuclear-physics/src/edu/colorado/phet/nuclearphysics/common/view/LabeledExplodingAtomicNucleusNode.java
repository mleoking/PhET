/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
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
public class LabeledExplodingAtomicNucleusNode extends AbstractAtomicNucleusNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Fonts for displaying the label on the nucleus.
    private static final Font ISOTOPE_NUMBER_FONT = new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.BOLD, 10 );
    private static final Font ISOTOPE_CHEM_SYMBOL_FONT = new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    
    // Factors by which the font should be scaled.  This allows us to use
    // standard font sizes and the use the Piccolo scaling capabilities,
    // which tends to look better than using non-standard sizes.
    private static final double NORMAL_LABEL_SCALING_FACTOR = 0.30;
    
    // Constants that control the nature of the explosion graphic.
    static final int   EXPLOSION_COUNTER_RESET_VAL = 10;
    private static final Color EXPLOSION_STROKE_COLOR = new Color(0xffff33);
    private static final Color EXPLOSION_FILL_COLOR = new Color(0xffff33);
    private static final float EXPLOSION_MIN_TRANSPARENCY = 0.4f;
    
    // Amount that shadow should be offset from the main text.
    private static final double SHADOW_OFFSET = 0.2;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    int _explosionCounter = 0;
    PPath _explosion;
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
    
    // Adapter for registering to receive clock events.
    ClockAdapter _clockAdapter = new ClockAdapter(){
        public void clockTicked(ClockEvent clockEvent){
            handleClockTicked(clockEvent);
        }
    };
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public LabeledExplodingAtomicNucleusNode(AtomicNucleus atomicNucleus)
    {
        super(atomicNucleus);
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
            
        _isotopeNumberShadow = new PText();
        _isotopeNumberShadow.setFont( ISOTOPE_NUMBER_FONT );
        _isotopeNumberShadow.setScale( NORMAL_LABEL_SCALING_FACTOR );
        addChild(_isotopeNumberShadow);
        
        _isotopeNumber = new PText();
        _isotopeNumber.setFont( ISOTOPE_NUMBER_FONT );
        _isotopeNumber.setScale( NORMAL_LABEL_SCALING_FACTOR );
        addChild(_isotopeNumber);
        
        _isotopeChemSymbolShadow = new PText();
        _isotopeChemSymbolShadow.setFont( ISOTOPE_CHEM_SYMBOL_FONT );
        _isotopeChemSymbolShadow.setScale( NORMAL_LABEL_SCALING_FACTOR );
        addChild(_isotopeChemSymbolShadow);
        
        _isotopeChemSymbol = new PText();
        _isotopeChemSymbol.setFont( ISOTOPE_CHEM_SYMBOL_FONT );
        _isotopeChemSymbol.setScale( NORMAL_LABEL_SCALING_FACTOR );
        addChild(_isotopeChemSymbol);
        
        // Set the label based on the configuration of the nucleus.
        setLabel(_atomicNucleus.getNumProtons(), _atomicNucleus.getNumNeutrons());
        
        // Register as a listener for the model representation.
        _atomicNucleus.addListener(_atomicNucleusAdapter);
        
        // Register as a listener to the clock that is driving the model.
        _atomicNucleus.getClock().addClockListener( _clockAdapter );
        
        // Make sure nothing is pickable so we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        updateLabelPositions();
        updatePosition();
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the label for the nucleus based on the number of protons and
     * neutrons
     * 
     * @param numProtons - The total number of protons in the nucleus.
     * @param numNeutrons - The total number of neutrons in the nucleus.
     */
    void setLabel(int numProtons, int numNeutrons){
        
        if (_isotopeChemSymbol == null){
            // Don't bother doing anything if there is no label to set.
            return;
        }
        
        String chemSymbol = "";
        String isotopeNumber = "";
        Color labelColor = Color.GRAY;
        
        switch (numProtons){
        case 6:
        	// Carbon
            if (numNeutrons == 8){
                // Carbon 14
                isotopeNumber = NuclearPhysicsStrings.CARBON_14_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.CARBON_14_LABEL_COLOR;
            }
            
            break;
        	
        case 7:
        	// Nitrogen
            if (numNeutrons == 7){
                // Nitrogen 14
                isotopeNumber = NuclearPhysicsStrings.NITROGEN_14_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.NITROGEN_14_LABEL_COLOR;
            }
            
            break;
        	
        case 92:
            // Uranium
            if (numNeutrons == 143){
                // Uranium 235
                isotopeNumber = NuclearPhysicsStrings.URANIUM_235_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.URANIUM_235_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.URANIUM_235_LABEL_COLOR;
            }
            else if (numNeutrons == 144){
                // Uranium 236
                isotopeNumber = NuclearPhysicsStrings.URANIUM_236_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.URANIUM_236_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.URANIUM_236_LABEL_COLOR;
            }
            else if (numNeutrons == 146){
                // Uranium 238
                isotopeNumber = NuclearPhysicsStrings.URANIUM_238_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.URANIUM_238_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.URANIUM_238_LABEL_COLOR;
            }
            else if (numNeutrons == 147){
                // Uranium 239
                isotopeNumber = NuclearPhysicsStrings.URANIUM_239_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.URANIUM_239_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.URANIUM_239_LABEL_COLOR;
            }
            
            break;
            
        case 84:
            // Polonium
            if (numNeutrons == 127){
            // Polonium 211
                isotopeNumber = NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.POLONIUM_LABEL_COLOR;
            }
            
            break;
            
        case 83:
            // Bismuth, which is used as the pre-decay "custom" nucleus.
            if (numNeutrons == 125){
                // Undecayed Bismuth.
                isotopeNumber = "";
                chemSymbol = NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR;
            }
            
            break;
            
        case 82:
            // Lead
            if (numNeutrons == 125){
                isotopeNumber = NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.LEAD_LABEL_COLOR;
            }
            else if (numNeutrons == 124){
                isotopeNumber = NuclearPhysicsStrings.LEAD_206_ISOTOPE_NUMBER;
                chemSymbol = NuclearPhysicsStrings.LEAD_206_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.LEAD_LABEL_COLOR;
            }
            
            break;
            
        case 81:
            // Thallium, which is used as the post-decay "custom" nucleus.
            if (numNeutrons == 123){
                isotopeNumber = "";
                chemSymbol = NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL;
                labelColor = NuclearPhysicsConstants.CUSTOM_NUCLEUS_POST_DECAY_LABEL_COLOR;
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

        // Scale the label so that it fits roughly within the nucleus.
    	_isotopeChemSymbol.setScale( 1 );
    	_isotopeChemSymbolShadow.setScale( 1 );
    	_isotopeNumber.setScale( 1 );
    	_isotopeNumberShadow.setScale( 1 );
    	double scale = 1;
        if (isotopeNumber == "" && chemSymbol != ""){
        	// Set the scale a little smaller if there is no isotope number so
        	// that the label doesn't dominate the image.
        	scale = Math.min(_atomicNucleus.getDiameter() / _isotopeChemSymbol.getFullBoundsReference().getWidth() * 0.9,
        			_atomicNucleus.getDiameter() / _isotopeChemSymbol.getFullBoundsReference().getHeight() * 0.8); 
        }
        else if (chemSymbol != ""){
        	scale = _atomicNucleus.getDiameter() / ( _isotopeChemSymbol.getFullBoundsReference().getWidth() + 
        			_isotopeNumber.getFullBoundsReference().getWidth() );
        }
    	_isotopeChemSymbol.setScale( scale );
    	_isotopeChemSymbolShadow.setScale( scale );
    	_isotopeNumber.setScale( scale );
    	_isotopeNumberShadow.setScale( scale );
    }
    
    /**
     * Update the position of the labels within the node.  This is generally
     * called when something 
     */
    void updateLabelPositions(){
    	
    	double totalWidth = _isotopeNumber.getFullBoundsReference().getWidth() +
    		_isotopeChemSymbol.getFullBoundsReference().getWidth();
    	double totalHeight = _isotopeChemSymbol.getFullBoundsReference().getHeight();
    	
    	double xPos = -totalWidth / 2;
    	double yPos = -totalHeight / 2;
    	
    	if (_isotopeNumber.getText().length() == 0){
    		// Handle the case where no isotope number is present.
        	_isotopeChemSymbol.setOffset(xPos, yPos);
        	_isotopeChemSymbolShadow.setOffset( _isotopeChemSymbol.getOffset().getX() + SHADOW_OFFSET, 
        			_isotopeChemSymbol.getOffset().getY() + SHADOW_OFFSET );
    	}
    	else{
        	_isotopeNumber.setOffset(xPos, yPos);
            _isotopeNumberShadow.setOffset( _isotopeNumber.getOffset().getX() + SHADOW_OFFSET, 
            		_isotopeNumber.getOffset().getY() + SHADOW_OFFSET );
        	_isotopeChemSymbol.setOffset(_isotopeNumber.getFullBoundsReference().getMaxX(), yPos);
        	_isotopeChemSymbolShadow.setOffset( _isotopeChemSymbol.getOffset().getX() + SHADOW_OFFSET, 
        			_isotopeChemSymbol.getOffset().getY() + SHADOW_OFFSET );
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
            double explosionRadius = (double)(EXPLOSION_COUNTER_RESET_VAL - _explosionCounter + 1) * 4;
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

	@Override
	protected void handleNucleusChangedEvent(AtomicNucleus atomicNucleus,
			int numProtons, int numNeutrons, ArrayList byProducts) {
		
		if ( atomicNucleus instanceof AbstractDecayNucleus ){
			if (((AbstractDecayNucleus)atomicNucleus).hasDecayed()){
				// Kick off the explosion graphic.
	            _explosionCounter = EXPLOSION_COUNTER_RESET_VAL;
	            _explosion.setVisible( true );
	            _explosion.setPickable(false);
			}
			else{
				_explosion.setVisible(false);
				_explosion.setPathTo(new Ellipse2D.Double(0,0,0,0));
			}
		}
		else {
	        int newAtomicWeight = numProtons + numNeutrons;
	        if ((newAtomicWeight < _currentAtomicWeight) && (newAtomicWeight != 0) && (byProducts != null)){
	            // This was a decay event, so kick off the explosion graphic.
	            _explosionCounter = EXPLOSION_COUNTER_RESET_VAL;
	            _explosion.setVisible( true );
	            _explosion.setPickable(false);
	        }
	    }
	    
	    // Save the new weight.
	    _currentAtomicWeight = numProtons + numNeutrons;
	    
	    // Update the label to reflect the new element.
	    setLabel(numProtons, numNeutrons);
	    updateLabelPositions();
	}

	@Override
	public void cleanup() {
		super.cleanup();
	    _atomicNucleus.getClock().removeClockListener( _clockAdapter );
	}
}
