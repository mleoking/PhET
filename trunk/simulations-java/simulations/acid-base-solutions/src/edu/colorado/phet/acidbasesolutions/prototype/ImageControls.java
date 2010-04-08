/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Controls for the images view of molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImageControls extends JPanel {
    
    public ImageControls( final ImagesNode imagesNode ) {
        setBorder( new TitledBorder( "Images" ) );
        
        // max images
        IntegerRange maxImagesRange = MGPConstants.MAX_IMAGES_RANGE;
        final LinearValueControl maxImagesControl = new LinearValueControl( maxImagesRange.getMin(), maxImagesRange.getMax(), "max images:", "####0", "", new HorizontalLayoutStrategy() );
        maxImagesControl.setValue( maxImagesRange.getDefault() );
        maxImagesControl.setUpDownArrowDelta( 1 );
        maxImagesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setMaxMolecules( (int)maxImagesControl.getValue() );
            }
        });
        
        // H2O images
        IntegerRange h2OImagesRange = MGPConstants.MAX_H2O_IMAGES_RANGE;
        String h2oImagesLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " images:" );
        final LinearValueControl h2oImagesControl = new LinearValueControl( h2OImagesRange.getMin(), h2OImagesRange.getMax(), h2oImagesLabel, "####0", "", new HorizontalLayoutStrategy() );
        h2oImagesControl.setValue( h2OImagesRange.getDefault() );
        h2oImagesControl.setUpDownArrowDelta( 1 );
        h2oImagesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setCountH2O( (int) h2oImagesControl.getValue() );
            }
        });

        // scale
        DoubleRange scaleRange = MGPConstants.IMAGE_SCALE_RANGE;
        final LinearValueControl scaleControl = new LinearValueControl( scaleRange.getMin(), scaleRange.getMax(), "scale:", "0.00", "", new HorizontalLayoutStrategy() );
        scaleControl.setValue( scaleRange.getDefault() );
        scaleControl.setUpDownArrowDelta( 0.01 );
        scaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setImageScale( scaleControl.getValue() );
            }
        });

        // transparency
        DoubleRange transparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        final LinearValueControl transparencyControl = new LinearValueControl( transparencyRange.getMin(), transparencyRange.getMax(), "transparency:", "0.00", "", new HorizontalLayoutStrategy() );
        transparencyControl.setValue( transparencyRange.getDefault() );
        transparencyControl.setUpDownArrowDelta( 0.01 );
        transparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setMoleculeTransparency( (float)transparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> transparencyLabelTable = new Hashtable<Double, JLabel>();
        transparencyLabelTable.put( new Double( transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        transparencyLabelTable.put( new Double( transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        transparencyControl.setTickLabels( transparencyLabelTable );
        
        // H2O transparency
        DoubleRange h2oTransparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        String h2oTransparencyLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " transparency:" );
        final LinearValueControl h2oTransparencyControl = new LinearValueControl( h2oTransparencyRange.getMin(), h2oTransparencyRange.getMax(), h2oTransparencyLabel, "0.00", "", new HorizontalLayoutStrategy() );
        h2oTransparencyControl.setValue( h2oTransparencyRange.getDefault() );
        h2oTransparencyControl.setUpDownArrowDelta( 0.01 );
        h2oTransparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setH2OTransparency( (float) h2oTransparencyControl.getValue() );
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
        layout.addComponent( maxImagesControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( h2oImagesControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( scaleControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( transparencyControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( h2oTransparencyControl, row, column++ );
        
        // default state
        imagesNode.setMaxMolecules( (int) maxImagesControl.getValue() );
        imagesNode.setCountH2O( (int) h2oImagesControl.getValue() );
        imagesNode.setImageScale( scaleControl.getValue() );
        imagesNode.setMoleculeTransparency( (float) transparencyControl.getValue() );
        imagesNode.setH2OTransparency( (float) h2oTransparencyControl.getValue() );
    }
}
