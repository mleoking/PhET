// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Controls for the dot view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class DotControls extends JPanel {
    
    private final DotsNode dotsNode;
    
    private final LinearValueControl maxMoleculesControl, diameterControl, transparencyControl, maxH2OControl, h2oTransparencyControl;
    
    public DotControls( JFrame parentFrame, final DotsNode dotsNode ) {
        setBorder( new TitledBorder( "Dots" ) );
        
        this.dotsNode = dotsNode;
        dotsNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        // max dots
        IntegerRange maxDotsRange = MGPConstants.MAX_DOTS_RANGE;
        maxMoleculesControl = new LinearValueControl( maxDotsRange.getMin(), maxDotsRange.getMax(), "max dots:", "####0", "" );
        maxMoleculesControl.setBorder( new EtchedBorder() );
        maxMoleculesControl.setUpDownArrowDelta( 1 );
        maxMoleculesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setMaxMolecules( (int) maxMoleculesControl.getValue() );
            }
        });
        
        // diameter
        DoubleRange diameterRange = MGPConstants.DOT_DIAMETER_RANGE;
        diameterControl = new LinearValueControl( diameterRange.getMin(), diameterRange.getMax(), "diameter:", "#0", "" );
        diameterControl.setBorder( new EtchedBorder() );
        diameterControl.setUpDownArrowDelta( 1 );
        diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setDotDiameter( diameterControl.getValue() );
            }
        });

        // transparency
        DoubleRange transparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        transparencyControl = new LinearValueControl( transparencyRange.getMin(), transparencyRange.getMax(), "transparency:", "0.00", "" );
        transparencyControl.setBorder( new EtchedBorder() );
        transparencyControl.setUpDownArrowDelta( 0.01 );
        transparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setMoleculeTransparency( (float) transparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> transparencyLabelTable = new Hashtable<Double, JLabel>();
        transparencyLabelTable.put( new Double( transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        transparencyLabelTable.put( new Double( transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        transparencyControl.setTickLabels( transparencyLabelTable );
        
        // H2O dots
        IntegerRange h2oDotsRange = MGPConstants.MAX_H2O_DOTS_RANGE;
        String h2oDotsLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " dots:" );
        maxH2OControl = new LinearValueControl( h2oDotsRange.getMin(), h2oDotsRange.getMax(), h2oDotsLabel, "####0", "" );
        maxH2OControl.setBorder( new EtchedBorder() );
        maxH2OControl.setUpDownArrowDelta( 1 );
        maxH2OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setMaxH2O( (int) maxH2OControl.getValue() );
            }
        });
        
        // H2O transparency
        DoubleRange h2oTransparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        String h2oTransparencyLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " transparency:" );
        h2oTransparencyControl = new LinearValueControl( h2oTransparencyRange.getMin(), h2oTransparencyRange.getMax(), h2oTransparencyLabel, "0.00", "" );
        h2oTransparencyControl.setBorder( new EtchedBorder() );
        h2oTransparencyControl.setUpDownArrowDelta( 0.01 );
        h2oTransparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setH2OTransparency( (float) h2oTransparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> h2oTransparencyLabelTable = new Hashtable<Double, JLabel>();
        h2oTransparencyLabelTable.put( new Double( h2oTransparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        h2oTransparencyLabelTable.put( new Double( h2oTransparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        h2oTransparencyControl.setTickLabels( h2oTransparencyLabelTable );
        
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( maxMoleculesControl, row++, column );
        layout.addComponent( diameterControl, row++, column );
        layout.addComponent( transparencyControl, row++, column );
        layout.addComponent( maxH2OControl, row++, column );
        layout.addComponent( h2oTransparencyControl, row++, column );
        
        updateControls();
    }
    
    private void updateControls() {
        maxMoleculesControl.setValue( dotsNode.getMaxMolecules() );
        diameterControl.setValue( dotsNode.getDotDiameter() );
        transparencyControl.setValue( dotsNode.getMoleculeTransparency() );
        maxH2OControl.setValue( dotsNode.getMaxH2O() );
        h2oTransparencyControl.setValue( dotsNode.getH2OTransparency() );
    }
}
