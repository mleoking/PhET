package edu.colorado.phet.phscale.developer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.model.LiquidDescriptor;


public class LiquidColorsPanel extends JPanel {

    public LiquidColorsPanel( Frame dialogOwner, LiquidDescriptor[] liquids ) {
        
        // sort the choices by ascending pH value
        LiquidDescriptor[] choices = LiquidDescriptor.getAllInstances();
        Comparator comparator = new Comparator() {
            public int compare( Object o1, Object o2 ) {
                int rval = 0;
                final double pH1 = ((LiquidDescriptor)o1).getPH();
                final double pH2 = ((LiquidDescriptor)o2).getPH();
                if ( pH1 == pH2 ) {
                    rval = 0;
                }
                else if ( pH1 < pH2 ) {
                    rval = -1;
                }
                else {
                    rval = 1;
                }
                return rval;
            }
        };
        Arrays.sort( choices, comparator );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int col = 0;
        // add the choices in descending pH value
        for ( int i = choices.length - 1; i >= 0; i-- ) {
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
