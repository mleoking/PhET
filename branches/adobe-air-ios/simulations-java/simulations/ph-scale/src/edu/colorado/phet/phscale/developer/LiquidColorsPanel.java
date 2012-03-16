// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.developer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.model.LiquidDescriptor;

/**
 * LiquidColorsPanel is the developer control panel for experimenting with liquid colors.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidColorsPanel extends JPanel {

    public LiquidColorsPanel( Frame dialogOwner, LiquidDescriptor[] liquids ) {
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int col = 0;
        
        // add the choices
        for ( int i = 0; i < liquids.length; i++ ) {
            LiquidColorControl colorControl = new LiquidColorControl( dialogOwner, liquids[i] );
            layout.addAnchoredComponent( colorControl, row++, col, GridBagConstraints.EAST );
        }
    }

    private static class LiquidColorControl extends JPanel {
        
        private final ColorControl _colorControl;
        private final JSpinner _alphaSpinner;
        private final JLabel _rgbaLabel;
        
        public LiquidColorControl( Frame owner, final LiquidDescriptor liquidDescriptor ) {
            
            String label = liquidDescriptor.getName() + " =  RGB:";
            Color rgbaColor = liquidDescriptor.getColor();
            Color rgbColor = new Color( rgbaColor.getRed(), rgbaColor.getGreen(), rgbaColor.getBlue() );
            _colorControl = new ColorControl( owner, label, rgbColor );
            _colorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Color color = ColorUtils.createColor( _colorControl.getColor(), liquidDescriptor.getColor().getAlpha() );
                    liquidDescriptor.dev_setColor( color );
                    setRGBALabel( color );
                }
            } );
            add( _colorControl );
            
            add( new JLabel( "A:" ) );
            
            SpinnerModel model = new SpinnerNumberModel( rgbaColor.getAlpha(), 0, 255, 1 );
            _alphaSpinner = new JSpinner( model );
            _alphaSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    int alpha = ((Integer)_alphaSpinner.getValue()).intValue();
                    Color color = ColorUtils.createColor( liquidDescriptor.getColor(), alpha );
                    liquidDescriptor.dev_setColor( color );
                    setRGBALabel( color );
                }
            });
            add( _alphaSpinner );
            
            _rgbaLabel = new JLabel();
            setRGBALabel( rgbaColor );
            add( _rgbaLabel );
        }
        
        private void setRGBALabel( Color c ) {
            String s = " = [" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha() + "]";
            _rgbaLabel.setText( s );
        }
    }
}
