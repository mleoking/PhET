/**
 * Class: StoveControlPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Oct 4, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class StoveControlPanel extends JPanel {
    private static final int s_stoveSliderHeight = 60;

    public StoveControlPanel( final IdealGasModule module ) {
        JPanel stovePanel = new JPanel();
        JPanel stoveSliderPanel = new JPanel();
//        JPanel iconPanel = new JPanel( new GridLayout( 3, 1 ) );
//        Image stoveAndFlameImage = null;
//        Image stoveImage = null;
//        Image stoveAndIceImage = null;
//        try {
//            stoveAndFlameImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_AND_FLAME_ICON_FILE );
//            stoveImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_ICON_FILE );
//            stoveAndIceImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_AND_ICE_ICON_FILE );
//        }
//        catch( IOException e ) {
//            e.printStackTrace();
//        }

//        Icon stoveAndFlameIcon = new ImageIcon( stoveAndFlameImage );
//        Icon stoveIcon = new ImageIcon( stoveImage );
//        Icon stoveAndIceIcon = new ImageIcon( stoveAndIceImage );
//        iconPanel.add( new JLabel( stoveAndFlameIcon ) );
//        iconPanel.add( new JLabel( stoveIcon ) );
//        iconPanel.add( new JLabel( stoveAndIceIcon ) );
//        stoveSliderPanel.add( iconPanel );
//        iconPanel.setPreferredSize( new Dimension( 24, s_stoveSliderHeight ) );

        final JSlider stoveSlider = new JSlider( JSlider.VERTICAL, -40, 40, 0 );
        stoveSlider.setMajorTickSpacing( 10 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( -40 ), new JLabel( SimStrings.get( "Common.Remove" ) ) );
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Common.0" ) ) );
        labelTable.put( new Integer( 40 ), new JLabel( SimStrings.get( "Common.Add" ) ) );
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks( true );
        stoveSlider.setSnapToTicks( true );
        stoveSlider.setPaintLabels( true );
        stoveSlider.setPreferredSize( new Dimension( 100, s_stoveSliderHeight ) );
        stoveSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                //            setFlames( stoveSlider.getValue() );
                module.setStove( stoveSlider.getValue() );
            }
        } );
        stoveSliderPanel.add( stoveSlider );

        final JCheckBox heatSourceCB = new JCheckBox( SimStrings.get( "IdealGasControlPanel.Add_remove_heat_from_floor_only" ) );
        heatSourceCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IdealGasConfig.heatOnlyFromFloor = heatSourceCB.isSelected();
            }
        } );

        // Put the panel together
        stovePanel.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        stovePanel.add( stoveSlider, gbc );
        stovePanel.add( stoveSliderPanel, gbc );
        gbc = new GridBagConstraints( 1, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        stovePanel.add( heatSourceCB, gbc );

//        stovePanel.setBorder( new EtchedBorder( SimStrings.get( "IdealGasControlPanel.Heat_Control" ) ) ));
//        BorderFactory.createCompoundBorder( new BevelBorder( BevelBorder.RAISED), new TitledBorder( ) )
        Border border = new TitledBorder( new EtchedBorder( BevelBorder.RAISED, Color.blue, Color.black ), SimStrings.get( "IdealGasControlPanel.Heat_Control" ) );
        stovePanel.setBorder( border );

        Color background = new Color( 230, 230, 255 );
        stovePanel.setBackground( background );
        stoveSlider.setBackground( background );
        heatSourceCB.setBackground( background );
        stovePanel.getLayout().layoutContainer( this );
//        stovePanel.setBackground( new Color( 200, 255, 160 ));
        this.setOpaque( false );
        this.add( stovePanel );

//        invalidate();
        revalidate();
        repaint();
    }

}
