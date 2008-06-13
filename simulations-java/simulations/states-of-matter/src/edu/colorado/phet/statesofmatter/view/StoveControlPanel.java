package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.BevelBorder;
import java.util.Hashtable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StoveControlPanel extends JPanel {
    private static final int s_stoveSliderHeight = 60;
    private Color controlColor = new Color( 240, 230, 255 );

    public StoveControlPanel() {

        // This panel will be put on the ApparatusPanel, which has a null LayoutManager.
        // When a JPanel is added to a JPanel with a null LayoutManager, the nested panel
        // doesn't lay out properly if it is at all complicated. To get it to lay out properly,
        // it must be put into an intermediate JPanel with a simple layout manager (in this case
        // we use the default), and that intermediate panel is then added to the ApparatusPanel.
        JPanel stovePanel = this;
        int maxStoveSliderValue = 40;
        final JSlider stoveSlider = new JSlider( JSlider.VERTICAL, -maxStoveSliderValue,
                                                 maxStoveSliderValue, 0 );
        stoveSlider.setMajorTickSpacing( maxStoveSliderValue );
        stoveSlider.setMinorTickSpacing( 10 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 40 ), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ADD_LABEL));
        labelTable.put( new Integer( 0 ), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_ZERO_LABEL));
        labelTable.put( new Integer( -40 ), new JLabel(StatesOfMatterStrings.STOVE_CONTROL_PANEL_REMOVE_LABEL));
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks( true );
        stoveSlider.setSnapToTicks( true );
        stoveSlider.setPaintLabels( true );
        stoveSlider.setPreferredSize( new Dimension( 100, s_stoveSliderHeight ) );
//        stoveSlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent event ) {
//                module.setStove( stoveSlider.getValue() );
//            }
//        } );
        stoveSlider.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                stoveSlider.setValue( 0 );
            }
        } );

        Border border = new TitledBorder( new EtchedBorder( BevelBorder.RAISED,
                                                            new Color( 40, 20, 255 ),
                                                            Color.black ),
                                          IdealGasResources.getString( "IdealGasControlPanel.Heat_Control" ) );
        stovePanel.setBorder( border );
        stoveSlider.setBackground(controlColor);
//        stovePanel.setPreferredSize( new Dimension( 115, 85 ) );
        stovePanel.setBackground(controlColor);
//        PhetGraphic panelPJC = PhetJComponent.newInstance( module.getApparatusPanel(), stovePanel );
//        this.addGraphic( panelPJC );

//        stoveSlider.setBackground( controlColor );
//        PhetGraphic sliderPJC = PhetJComponent.newInstance( module.getApparatusPanel(), stoveSlider );
//        sliderPJC.setCursorHand();
//        this.addGraphic( sliderPJC );
//        sliderPJC.setLocation( 10, 20 );

        // Add a listener to the model that will disable the slider when the model is in contant temperature mode
//        final IdealGasModel model = module.getIdealGasModel();
//        model.addChangeListener( new IdealGasModel.ChangeListener() {
//            public void stateChanged( IdealGasModel.ChangeEvent event ) {
//                if( model.getConstantProperty() == IdealGasModel.CONSTANT_TEMPERATURE ) {
//                    stoveSlider.setEnabled( false );
//                }
//                else {
//                    stoveSlider.setEnabled( true );
//                }
//            }
//        } );
        add(stoveSlider);
    }

}
