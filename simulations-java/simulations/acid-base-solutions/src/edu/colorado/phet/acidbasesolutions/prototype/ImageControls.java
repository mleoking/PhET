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

/**
 * Controls for the images view of molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImageControls extends JPanel {
    
    public ImageControls( final ImagesNode imagesNode ) {
        setBorder( new TitledBorder( "Images view" ) );
        
        // max images
        IntegerRange maxImagesRange = MGPConstants.MAX_IMAGES_RANGE;
        final LinearValueControl maxImagesControl = new LinearValueControl( maxImagesRange.getMin(), maxImagesRange.getMax(), "max images:", "####0", "", new HorizontalLayoutStrategy() );
        maxImagesControl.setValue( maxImagesRange.getDefault() );
        maxImagesControl.setUpDownArrowDelta( 1 );
        maxImagesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setMaxImages( (int)maxImagesControl.getValue() );
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
                imagesNode.setImageTransparency( (float)transparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> transparencyLabelTable = new Hashtable<Double, JLabel>();
        transparencyLabelTable.put( new Double( transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        transparencyLabelTable.put( new Double( transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        transparencyControl.setTickLabels( transparencyLabelTable );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( maxImagesControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( scaleControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( transparencyControl, row, column++ );
        
        // default state
        imagesNode.setMaxImages( (int) maxImagesControl.getValue() );
        imagesNode.setImageScale( scaleControl.getValue() );
        imagesNode.setImageTransparency( (float)transparencyControl.getValue() );
    }
}
