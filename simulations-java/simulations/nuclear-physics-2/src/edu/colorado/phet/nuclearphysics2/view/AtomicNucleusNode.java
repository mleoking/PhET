/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents the view of an Atomic Nucleus from the model.  Since
 * most of the effort for displaying a nucleus is actually done by the nodes
 * associated with the particles of which it is composed, this class just
 * displays the appropriate label.
 *
 * @author John Blanco
 */
public class AtomicNucleusNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    final private double NUCLEUS_DIAMETER = 11.6;

    public static final Font ISOTOPE_NUMBER_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 3 );
    public static final Font CHEMICAL_SYMBOL_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 7 );
    
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PText _isotopeNumberLabel;
    private PText _chemicalSymbolLabel;
    private PText _isotopeNumberLabelShadow;
    private PText _chemicalSymbolLabelShadow;
    private AtomicNucleus _atom;
    private boolean _showShadow;
    
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AtomicNucleusNode(AtomicNucleus atom)
    {
        _showShadow = true;
        
        _atom = atom;
        
        if (_showShadow){            
            _isotopeNumberLabelShadow = new PText(NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER);
            _isotopeNumberLabelShadow.setFont( ISOTOPE_NUMBER_FONT );
            _isotopeNumberLabelShadow.setTextPaint( Color.BLACK );
            addChild(_isotopeNumberLabelShadow);
        }
        
        _isotopeNumberLabel = new PText(NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER);
        _isotopeNumberLabel.setFont( ISOTOPE_NUMBER_FONT );
        _isotopeNumberLabel.setTextPaint( Color.MAGENTA );
        addChild(_isotopeNumberLabel);
        
        if (_showShadow){            
            _chemicalSymbolLabelShadow = new PText(NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL);
            _chemicalSymbolLabelShadow.setFont( CHEMICAL_SYMBOL_FONT );
            _chemicalSymbolLabelShadow.setTextPaint( Color.BLACK );
            addChild(_chemicalSymbolLabelShadow);
        }
        
        _chemicalSymbolLabel = new PText(NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL);
        _chemicalSymbolLabel.setFont( CHEMICAL_SYMBOL_FONT );
        _chemicalSymbolLabel.setTextPaint( Color.MAGENTA );
        addChild(_chemicalSymbolLabel);
        
        atom.addListener(new AtomicNucleus.Listener(){
            public void positionChanged()
            {
                update();
            }
            
        });
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    private void update(){

        _isotopeNumberLabel.setOffset( _atom.getPosition().getX() - NUCLEUS_DIAMETER/2,  
                _atom.getPosition().getY() - NUCLEUS_DIAMETER/2);
        
        Point2D isotopeLabelOffset = _isotopeNumberLabel.getOffset();

        if (_showShadow){            
            _isotopeNumberLabelShadow.setOffset( isotopeLabelOffset.getX() + 0.15,  
                    isotopeLabelOffset.getY() + 0.15);
        }
        
        _chemicalSymbolLabel.setOffset( isotopeLabelOffset.getX() + _isotopeNumberLabel.getWidth(),  
                isotopeLabelOffset.getY());

        Point2D chemicalSymbolLabelOffset = _chemicalSymbolLabel.getOffset();

        if (_showShadow){            
            _chemicalSymbolLabelShadow.setOffset( chemicalSymbolLabelOffset.getX() + 0.15,  
                    chemicalSymbolLabelOffset.getY() + 0.15);
        }
    }

}
