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
    
    public DotControls( JFrame parentFrame, final DotsNode dotsNode ) {
        setBorder( new TitledBorder( "Dot view" ) );
        
        // max dots
        IntegerRange maxDotsRange = MGPConstants.MAX_DOTS_RANGE;
        final LinearValueControl maxDotsControl = new LinearValueControl( maxDotsRange.getMin(), maxDotsRange.getMax(), "max dots:", "####0", "", new HorizontalLayoutStrategy() );
        maxDotsControl.setValue( maxDotsRange.getDefault() );
        maxDotsControl.setUpDownArrowDelta( 1 );
        maxDotsControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setMaxDots( (int)maxDotsControl.getValue() );
            }
        });

        // diameter
        DoubleRange diameterRange = MGPConstants.DOT_DIAMETER_RANGE;
        final LinearValueControl diameterControl = new LinearValueControl( diameterRange.getMin(), diameterRange.getMax(), "diameter:", "#0", "", new HorizontalLayoutStrategy() );
        diameterControl.setValue( diameterRange.getDefault() );
        diameterControl.setUpDownArrowDelta( 1 );
        diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setDotDiameter( diameterControl.getValue() );
            }
        });

        // transparency
        DoubleRange transparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        final LinearValueControl transparencyControl = new LinearValueControl( transparencyRange.getMin(), transparencyRange.getMax(), "transparency:", "0.00", "", new HorizontalLayoutStrategy() );
        transparencyControl.setValue( transparencyRange.getDefault() );
        transparencyControl.setUpDownArrowDelta( 0.01 );
        transparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setDotTransparency( (float)transparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> transparencyLabelTable = new Hashtable<Double, JLabel>();
        transparencyLabelTable.put( new Double( transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        transparencyLabelTable.put( new Double( transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        transparencyControl.setTickLabels( transparencyLabelTable );
        
        // colors
        JPanel colorsPanel = new JPanel();
        {
            final ColorControl colorHAControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.HA_FRAGMENT ), MGPConstants.COLOR_HA );
            colorHAControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    dotsNode.setColorHA( colorHAControl.getColor() );
                }
            } );
            
            final ColorControl colorAControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.A_MINUS_FRAGMENT ), MGPConstants.COLOR_A_MINUS );
            colorAControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    dotsNode.setColorA( colorAControl.getColor() );
                }
            } );
            
            final ColorControl colorH3OControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.H3O_PLUS_FRAGMENT ), MGPConstants.COLOR_H3O_PLUS );
            colorH3OControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    dotsNode.setColorH3O( colorH3OControl.getColor() );
                }
            } );
            
            final ColorControl colorOHControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.OH_MINUS_FRAGMENT ), MGPConstants.COLOR_OH_MINUS );
            colorOHControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    dotsNode.setColorOH( colorOHControl.getColor() );
                }
            } );
            
            int spacing = 15;
            colorsPanel.add( new JLabel( "colors:" ) );
            colorsPanel.add( Box.createHorizontalStrut( spacing ) );
            colorsPanel.add( colorHAControl );
            colorsPanel.add( Box.createHorizontalStrut( spacing ) );
            colorsPanel.add( colorAControl );
            colorsPanel.add( Box.createHorizontalStrut( spacing ) );
            colorsPanel.add( colorH3OControl );
            colorsPanel.add( Box.createHorizontalStrut( spacing ) );
            colorsPanel.add( colorOHControl );
        }
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( maxDotsControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( diameterControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( transparencyControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( colorsPanel, row, column++ );
        
        // default state
        dotsNode.setMaxDots( (int) maxDotsControl.getValue() );
        dotsNode.setDotDiameter( diameterControl.getValue() );
        dotsNode.setDotTransparency( (float)transparencyControl.getValue() );
    }
}
