/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.prototype.IntegerLabel.ScientificIntegerLabel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Displays counts (actual and displayed) for the various particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MoleculeCountPanel extends JPanel {
    
    private final IntegerLabel displayedHA, displayedA, displayedH3O, displayedOH, displayedH2O;
    private final ScientificIntegerLabel actualHA, actualA, actualH3O, actualOH, actualH2O;
    
    public MoleculeCountPanel() {
        super();
        setBorder( new TitledBorder( "Molecule counts" ) );
        
        displayedHA = new IntegerLabel();
        displayedA = new IntegerLabel();
        displayedH3O = new IntegerLabel();
        displayedOH = new IntegerLabel();
        displayedH2O = new IntegerLabel();
        
        actualHA = new ScientificIntegerLabel();
        actualA = new ScientificIntegerLabel();
        actualH3O = new ScientificIntegerLabel();
        actualOH = new ScientificIntegerLabel();
        actualH2O = new ScientificIntegerLabel();
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.EAST );
        int minColumnWidth = 75;
        layout.setMinimumWidth( 0, minColumnWidth );
        layout.setMinimumWidth( 1, minColumnWidth );
        layout.setMinimumWidth( 2, minColumnWidth );
        int row = 0;
        int column = 0;
        layout.addComponent( new JLabel( "<html><u>molecule</u></html>" ), row, column++ );
        layout.addComponent( new JLabel( "<html><u>displayed</u></html>" ), row, column++ );
        layout.addComponent( new JLabel( "<html><u>actual</u></html>" ), row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( "HA" ), row, column++ );
        layout.addComponent( displayedHA, row, column++ );
        layout.addComponent( actualHA, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( "<html>A<sup>-</sub></html>" ), row, column++ );
        layout.addComponent( displayedA, row, column++ );
        layout.addComponent( actualA, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( "<html>H<sub>3</sub>O<sup>+</sup></html>" ), row, column++ );
        layout.addComponent( displayedH3O, row, column++ );
        layout.addComponent( actualH3O, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( "<html>OH<sup>-</sup></html>" ), row, column++ );
        layout.addComponent( displayedOH, row, column++ );
        layout.addComponent( actualOH, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( "<html>H<sub>2</sub>O</html>" ), row, column++ );
        layout.addComponent( displayedH2O, row, column++ );
        layout.addComponent( actualH2O, row, column++ );
        
        // default state
        //XXX
    }
    
    public void setActualValues( int HA, int A, int H3O, int OH, int H2O ) {
        actualHA.setValue( HA );
        actualA.setValue( A );
        actualH3O.setValue( H3O );
        actualOH.setValue( OH );
        actualH2O.setValue( H2O );
    }
    
    public void setDisplayedValues( int HA, int A, int H3O, int OH, int H2O ) {
        displayedHA.setValue( HA );
        displayedA.setValue( A );
        displayedH3O.setValue( H3O );
        displayedOH.setValue( OH );
        displayedH2O.setValue( H2O );
    }
}
