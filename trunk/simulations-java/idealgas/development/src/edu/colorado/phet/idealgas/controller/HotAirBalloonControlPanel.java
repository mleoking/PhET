/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 4, 2003
 * Time: 1:33:50 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.idealgas.physics.HotAirBalloon;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Hashtable;

public class HotAirBalloonControlPanel extends JPanel {

    public HotAirBalloonControlPanel() {
        init();
    }

    private void init() {
        addHotAirBallonControls();
    }

    /**
     * Create a panel for controlling the stove
     */
    private void addHotAirBallonControls() {
        JPanel stovePanel = new JPanel( );

        JPanel iconPanel = new JPanel( new GridLayout( 2, 1 ));
        ResourceLoader iconLoader = new ResourceLoader();
        Image stoveAndFlameImage = iconLoader.loadImage( IdealGasConfig.STOVE_AND_FLAME_ICON_FILE ).getImage();
        Image stoveImage = iconLoader.loadImage( IdealGasConfig.STOVE_ICON_FILE ).getImage();
        Icon stoveAndFlameIcon = new ImageIcon( stoveAndFlameImage );
        Icon stoveIcon = new ImageIcon( stoveImage );
        iconPanel.add( new JLabel( stoveAndFlameIcon ));
        iconPanel.add( new JLabel( stoveIcon ));
        stovePanel.add( iconPanel );
        iconPanel.setPreferredSize( new Dimension( 24, 50 ));

        final JSlider stoveSlider = new JSlider( JSlider.VERTICAL, 0, 30, 0 );
        stoveSlider.setMajorTickSpacing( 5 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( "0" ) );
        labelTable.put( new Integer( 40 ), new JLabel( "Add" ) );
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks(true);

        stoveSlider.setPaintLabels( true );

        stoveSlider.setPreferredSize( new Dimension( 76, 50 ) );
        stoveSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                setHotAirBalloonHeat( stoveSlider.getValue() );
            }
        } );
        stovePanel.add( stoveSlider );

        stovePanel.setBorder( new TitledBorder( "Hot Air Balloon" ) );
        this.add( stovePanel );
    }


    /**
     *
     */
    private void setHotAirBalloonHeat( int value ) {
        HotAirBalloon.s_heatSource = value;
    }
}
