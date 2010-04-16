/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
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
    private final ColorControl colorHAControl, colorAControl, colorH3OControl, colorOHControl, colorH2OControl;
    
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
        maxMoleculesControl = new LinearValueControl( maxDotsRange.getMin(), maxDotsRange.getMax(), "max dots:", "####0", "", new HorizontalLayoutStrategy() );
        maxMoleculesControl.setUpDownArrowDelta( 1 );
        maxMoleculesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setMaxMolecules( (int) maxMoleculesControl.getValue() );
            }
        });
        
        // diameter
        DoubleRange diameterRange = MGPConstants.DOT_DIAMETER_RANGE;
        diameterControl = new LinearValueControl( diameterRange.getMin(), diameterRange.getMax(), "diameter:", "#0", "", new HorizontalLayoutStrategy() );
        diameterControl.setUpDownArrowDelta( 1 );
        diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setDotDiameter( diameterControl.getValue() );
            }
        });

        // transparency
        DoubleRange transparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        transparencyControl = new LinearValueControl( transparencyRange.getMin(), transparencyRange.getMax(), "transparency:", "0.00", "", new HorizontalLayoutStrategy() );
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
        maxH2OControl = new LinearValueControl( h2oDotsRange.getMin(), h2oDotsRange.getMax(), h2oDotsLabel, "####0", "", new HorizontalLayoutStrategy() );
        maxH2OControl.setUpDownArrowDelta( 1 );
        maxH2OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setMaxH2O( (int) maxH2OControl.getValue() );
            }
        });
        
        // H2O transparency
        DoubleRange h2oTransparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        String h2oTransparencyLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " transparency:" );
        h2oTransparencyControl = new LinearValueControl( h2oTransparencyRange.getMin(), h2oTransparencyRange.getMax(), h2oTransparencyLabel, "0.00", "", new HorizontalLayoutStrategy() );
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
        
        // colors
        colorHAControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.HA_FRAGMENT ), dotsNode.getColorHA() );
        colorHAControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorHA( colorHAControl.getColor() );
            }
        } );

        colorAControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.A_MINUS_FRAGMENT ), dotsNode.getColorA() );
        colorAControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorA( colorAControl.getColor() );
            }
        } );

        colorH3OControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.H3O_PLUS_FRAGMENT ), dotsNode.getColorH3O() );
        colorH3OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorH3O( colorH3OControl.getColor() );
            }
        } );

        colorOHControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.OH_MINUS_FRAGMENT ), dotsNode.getColorOH() );
        colorOHControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorOH( colorOHControl.getColor() );
            }
        } );
        
        colorH2OControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT ), dotsNode.getColorH2O() );
        colorH2OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorH2O( colorH2OControl.getColor() );
            }
        } );

        JPanel colorsPanel = new JPanel();
        int spacing = 10;
        colorsPanel.add( new JLabel( "colors:" ) );
        colorsPanel.add( Box.createHorizontalStrut( spacing ) );
        colorsPanel.add( colorHAControl );
        colorsPanel.add( Box.createHorizontalStrut( spacing ) );
        colorsPanel.add( colorAControl );
        colorsPanel.add( Box.createHorizontalStrut( spacing ) );
        colorsPanel.add( colorH3OControl );
        colorsPanel.add( Box.createHorizontalStrut( spacing ) );
        colorsPanel.add( colorOHControl );
        colorsPanel.add( Box.createHorizontalStrut( spacing ) );
        colorsPanel.add( colorH2OControl );
        
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
        layout.addComponent( colorsPanel, row++, column );
        
        updateControls();
    }
    
    private void updateControls() {
        maxMoleculesControl.setValue( dotsNode.getMaxMolecules() );
        diameterControl.setValue( dotsNode.getDotDiameter() );
        transparencyControl.setValue( dotsNode.getMoleculeTransparency() );
        maxH2OControl.setValue( dotsNode.getMaxH2O() );
        h2oTransparencyControl.setValue( dotsNode.getH2OTransparency() );
        colorHAControl.setColor( dotsNode.getColorHA() );
        colorAControl.setColor( dotsNode.getColorA() );
        colorH3OControl.setColor( dotsNode.getColorH3O() );
        colorOHControl.setColor( dotsNode.getColorOH() );
        colorH2OControl.setColor( dotsNode.getColorH2O() );
    }
}
