/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 4, 2003
 * Time: 1:33:50 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.HotAirBalloon;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

public class HotAirBalloonControlPanel extends JPanel {

    private HotAirBalloon hotAirBalloon;

    public HotAirBalloonControlPanel( HotAirBalloon hotAirBalloon ) {
        this.hotAirBalloon = hotAirBalloon;
        addHotAirBallonControls();
    }

    /**
     * Create a panel for controlling the stove
     */
    private void addHotAirBallonControls() {
        JPanel stovePanel = this;

        JPanel iconPanel = new JPanel( new GridLayout( 2, 1 ) );
        try {
            BufferedImage stoveAndFlameImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_AND_FLAME_ICON_FILE );
            BufferedImage stoveImage = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_ICON_FILE );
            Icon stoveAndFlameIcon = new ImageIcon( stoveAndFlameImage );
            Icon stoveIcon = new ImageIcon( stoveImage );
            iconPanel.add( new JLabel( stoveAndFlameIcon ) );
            iconPanel.add( new JLabel( stoveIcon ) );
            stovePanel.add( iconPanel );
            iconPanel.setPreferredSize( new Dimension( 24, 50 ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        final JSlider stoveSlider = new JSlider( JSlider.VERTICAL, 0, 30, 0 );
        stoveSlider.setMajorTickSpacing( 5 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Common.0" ) ) );
        labelTable.put( new Integer( 30 ), new JLabel( SimStrings.get( "Common.Add" ) ) );
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks( true );

        stoveSlider.setPaintLabels( true );

        stoveSlider.setPreferredSize( new Dimension( 76, 50 ) );
        stoveSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                setHotAirBalloonHeat( stoveSlider.getValue() );
            }
        } );
        hotAirBalloon.addChangeListener( new HotAirBalloon.ChangeListener() {
            public void heatSourceChanged( HotAirBalloon.ChangeEvent event ) {
                stoveSlider.setValue( (int)event.getHotAirBalloon().getHeatSource() );
            }
        } );
        stovePanel.add( stoveSlider );

        stovePanel.setBorder( new TitledBorder( SimStrings.get( "ModuleTitle.HotAirBalloon" ) ) );
    }


    /**
     *
     */
    private void setHotAirBalloonHeat( int value ) {
        hotAirBalloon.setHeatSource( value );
    }
}
