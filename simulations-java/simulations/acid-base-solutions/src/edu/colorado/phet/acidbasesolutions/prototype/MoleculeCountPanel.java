/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.NumberLabel.IntegerLabel;
import edu.colorado.phet.acidbasesolutions.prototype.NumberLabel.ScientificIntegerLabel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Displays counts (actual and displayed) for the various particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MoleculeCountPanel extends JPanel {
    
    private final WeakAcid solution;
    private final MagnifyingGlassNode magnifyingGlassNode;
    private final IntegerLabel dotsHA, dotsA, dotsH3O, dotsOH, dotsH2O;
    private final IntegerLabel imagesHA, imagesA, imagesH3O, imagesOH, imagesH2O;
    private final ScientificIntegerLabel actualHA, actualA, actualH3O, actualOH, actualH2O;
    
    public MoleculeCountPanel( WeakAcid solution, MagnifyingGlassNode magnifyingGlassNode ) {
        super();
        setBorder( new TitledBorder( "Molecule counts" ) );
        
        this.solution = solution;
        this.magnifyingGlassNode = magnifyingGlassNode;
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        solution.addChangeListener( changeListener );
        magnifyingGlassNode.getDotsNode().addChangeListener( changeListener );
        magnifyingGlassNode.getImagesNode().addChangeListener( changeListener );
        
        dotsHA = new IntegerLabel();
        dotsA = new IntegerLabel();
        dotsH3O = new IntegerLabel();
        dotsOH = new IntegerLabel();
        dotsH2O = new IntegerLabel();
        
        imagesHA = new IntegerLabel();
        imagesA = new IntegerLabel();
        imagesH3O = new IntegerLabel();
        imagesOH = new IntegerLabel();
        imagesH2O = new IntegerLabel();
        
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
        int row = 0;
        int column = 0;
        layout.setMinimumWidth( column, minColumnWidth );
        layout.addComponent( new JLabel( "<html><u>molecule</u></html>" ), row, column++ );
        layout.setMinimumWidth( column, minColumnWidth );
        layout.addComponent( new JLabel( "<html><u>dots</u></html>" ), row, column++ );
        layout.setMinimumWidth( column, minColumnWidth );
        layout.addComponent( new JLabel( "<html><u>images</u></html>" ), row, column++ );
        layout.setMinimumWidth( column, minColumnWidth );
        layout.addComponent( new JLabel( "<html><u>actual</u></html>" ), row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( HTMLUtils.toHTMLString( MGPConstants.HA_FRAGMENT ) ), row, column++ );
        layout.addComponent( dotsHA, row, column++ );
        layout.addComponent( imagesHA, row, column++ );
        layout.addComponent( actualHA, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( HTMLUtils.toHTMLString( MGPConstants.A_MINUS_FRAGMENT ) ), row, column++ );
        layout.addComponent( dotsA, row, column++ );
        layout.addComponent( imagesA, row, column++ );
        layout.addComponent( actualA, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( HTMLUtils.toHTMLString( MGPConstants.H3O_PLUS_FRAGMENT ) ), row, column++ );
        layout.addComponent( dotsH3O, row, column++ );
        layout.addComponent( imagesH3O, row, column++ );
        layout.addComponent( actualH3O, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( HTMLUtils.toHTMLString( MGPConstants.OH_MINUS_FRAGMENT ) ), row, column++ );
        layout.addComponent( dotsOH, row, column++ );
        layout.addComponent( imagesOH, row, column++ );
        layout.addComponent( actualOH, row, column++ );
        row++;
        column = 0;
        layout.addComponent( new JLabel( HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT ) ), row, column++ );
        layout.addComponent( dotsH2O, row, column++ );
        layout.addComponent( imagesH2O, row, column++ );
        layout.addComponent( actualH2O, row, column++ );
        
        // default state
        update();
    }
    
    private void update() {
        
        // dots
        DotsNode dotsNode = magnifyingGlassNode.getDotsNode();
        dotsHA.setValue( dotsNode.getCountHA() );
        dotsA.setValue( dotsNode.getCountA() );
        dotsH3O.setValue( dotsNode.getCountH3O() );
        dotsOH.setValue( dotsNode.getCountOH() );
        //TODO H2O
        
        // images
        ImagesNode imagesNode = magnifyingGlassNode.getImagesNode();
        imagesHA.setValue( imagesNode.getCountHA() );
        imagesA.setValue( imagesNode.getCountA() );
        imagesH3O.setValue( imagesNode.getCountH3O() );
        imagesOH.setValue( imagesNode.getCountOH() );
        //TODO H2O
        
        // actual
        actualHA.setValue( solution.getMoleculeCountHA() );
        actualA.setValue( solution.getMoleculeCountA() );
        actualH3O.setValue( solution.getMoleculeCountH3O() );
        actualOH.setValue( solution.getMoleculeCountOH() );
        actualH2O.setValue( solution.getMoleculeCountH2O() );
    }
}
