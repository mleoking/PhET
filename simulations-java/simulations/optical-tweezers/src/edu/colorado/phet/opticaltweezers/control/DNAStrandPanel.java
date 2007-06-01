/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.opticaltweezers.model.DNAStrand;


public class DNAStrandPanel extends JPanel {

    private DNAStrand _dnaStrand;
    
    public DNAStrandPanel( Font titleFont, Font controlFont, DNAStrand dnaStrand ) {
        super();
        
        _dnaStrand = dnaStrand;
        
        TitledBorder border = new TitledBorder( "DNA stand behavior:" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
    }
}
