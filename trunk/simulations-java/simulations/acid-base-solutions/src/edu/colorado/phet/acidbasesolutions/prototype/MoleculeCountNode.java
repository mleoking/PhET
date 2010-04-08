/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.NumberNode.IntegerNode;
import edu.colorado.phet.acidbasesolutions.prototype.NumberNode.ScientificIntegerNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays counts (actual and displayed) for the various molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MoleculeCountNode extends PComposite {
    
    private final WeakAcid solution;
    private final MagnifyingGlassNode magnifyingGlassNode;
    
    private final PImage imageHA, imageA, imageH3O, imageOH, imageH2O;
    private final HTMLNode symbolHA, symbolA, symbolH3O, symbolOH, symbolH2O;
    private final IntegerNode dotsHA, dotsA, dotsH3O, dotsOH, dotsH2O;
    private final IntegerNode imagesHA, imagesA, imagesH3O, imagesOH, imagesH2O;
    private final ScientificIntegerNode actualHA, actualA, actualH3O, actualOH, actualH2O;

    public MoleculeCountNode( WeakAcid solution, MagnifyingGlassNode magnifyingGlassNode ) {
        super();
        
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
        
        imageHA = new PImage( MGPConstants.HA_IMAGE );
        imageA = new PImage( MGPConstants.A_MINUS_IMAGE );
        imageH3O = new PImage( MGPConstants.H3O_PLUS_IMAGE );
        imageOH = new PImage( MGPConstants.OH_MINUS_IMAGE );
        imageH2O = new PImage( MGPConstants.H2O_IMAGE );
        
        symbolHA = new HTMLNode( MGPConstants.HA_FRAGMENT );
        symbolA = new HTMLNode( MGPConstants.A_MINUS_FRAGMENT );
        symbolH3O = new HTMLNode( MGPConstants.H3O_PLUS_FRAGMENT );
        symbolOH = new HTMLNode( MGPConstants.OH_MINUS_FRAGMENT );
        symbolH2O = new HTMLNode( MGPConstants.H2O_FRAGMENT );
       
        dotsHA = new IntegerNode();
        dotsA = new IntegerNode();
        dotsH3O = new IntegerNode();
        dotsOH = new IntegerNode();
        dotsH2O = new IntegerNode();
        
        imagesHA = new IntegerNode();
        imagesA = new IntegerNode();
        imagesH3O = new IntegerNode();
        imagesOH = new IntegerNode();
        imagesH2O = new IntegerNode();
        
        actualHA = new ScientificIntegerNode();
        actualA = new ScientificIntegerNode();
        actualH3O = new ScientificIntegerNode();
        actualOH = new ScientificIntegerNode();
        actualH2O = new ScientificIntegerNode();
        
        PText imagesLabel = new PText( "images:" );
        PText dotsLabel = new PText( "dots:" );
        PText actualLabel = new PText( "actual:" );
        
        // layout
        GridBagLayout layout = new GridBagLayout();
        final int columnWidth = 65;
        layout.columnWidths = new int[] { 0, columnWidth, columnWidth, columnWidth, columnWidth, columnWidth };
        SwingLayoutNode layoutNode = new SwingLayoutNode( layout );
        addChild( layoutNode );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 1;
        constraints.gridy = 0;
        layoutNode.addChild( imageHA, constraints );
        constraints.gridx++;
        layoutNode.addChild( imageA, constraints );
        constraints.gridx++;
        layoutNode.addChild( imageH3O, constraints );
        constraints.gridx++;
        layoutNode.addChild( imageOH, constraints );
        constraints.gridx++;
        layoutNode.addChild( imageH2O, constraints );
        addChild( layoutNode );
        constraints.gridx = 1;
        constraints.gridy++;
        layoutNode.addChild( symbolHA, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolA, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolH3O, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolOH, constraints );
        constraints.gridx++;
        layoutNode.addChild( symbolH2O, constraints );
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 0;
        constraints.gridy++;
        layoutNode.addChild( imagesLabel, constraints );
        constraints.gridx++;
        layoutNode.addChild( imagesHA, constraints );
        constraints.gridx++;
        layoutNode.addChild( imagesA, constraints );
        constraints.gridx++;
        layoutNode.addChild( imagesH3O, constraints );
        constraints.gridx++;
        layoutNode.addChild( imagesOH, constraints );
        constraints.gridx++;
        layoutNode.addChild( imagesH2O, constraints );
        constraints.gridx = 0;
        constraints.gridy++;
        layoutNode.addChild( dotsLabel, constraints );
        constraints.gridx++;
        layoutNode.addChild( dotsHA, constraints );
        constraints.gridx++;
        layoutNode.addChild( dotsA, constraints );
        constraints.gridx++;
        layoutNode.addChild( dotsH3O, constraints );
        constraints.gridx++;
        layoutNode.addChild( dotsOH, constraints );
        constraints.gridx++;
        layoutNode.addChild( dotsH2O, constraints );
        constraints.gridx = 0;
        constraints.gridy++;
        layoutNode.addChild( actualLabel, constraints );
        constraints.gridx++;
        layoutNode.addChild( actualHA, constraints );
        constraints.gridx++;
        layoutNode.addChild( actualA, constraints );
        constraints.gridx++;
        layoutNode.addChild( actualH3O, constraints );
        constraints.gridx++;
        layoutNode.addChild( actualOH, constraints );
        constraints.gridx++;
        layoutNode.addChild( actualH2O, constraints );

        update();
    }
    
    private void update() {
        
        // dots
        DotsNode dotsNode = magnifyingGlassNode.getDotsNode();
        dotsHA.setValue( dotsNode.getCountHA() );
        dotsA.setValue( dotsNode.getCountA() );
        dotsH3O.setValue( dotsNode.getCountH3O() );
        dotsOH.setValue( dotsNode.getCountOH() );
        dotsH2O.setValue( dotsNode.getCountH2O() );
        
        // images
        ImagesNode imagesNode = magnifyingGlassNode.getImagesNode();
        imagesHA.setValue( imagesNode.getCountHA() );
        imagesA.setValue( imagesNode.getCountA() );
        imagesH3O.setValue( imagesNode.getCountH3O() );
        imagesOH.setValue( imagesNode.getCountOH() );
        imagesH2O.setValue( imagesNode.getCountH2O() );
        
        // actual
        actualHA.setValue( solution.getMoleculeCountHA() );
        actualA.setValue( solution.getMoleculeCountA() );
        actualH3O.setValue( solution.getMoleculeCountH3O() );
        actualOH.setValue( solution.getMoleculeCountOH() );
        actualH2O.setValue( solution.getMoleculeCountH2O() );
    }
    
    
}
