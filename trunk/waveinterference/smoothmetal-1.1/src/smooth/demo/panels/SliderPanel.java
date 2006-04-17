package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Sliderdemonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class SliderPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridBagLayout() );
    private final JSlider standardSlider = new JSlider( JSlider.HORIZONTAL, 0, 40, 20 );
    private final JSlider verticalStandardSlider = new JSlider( JSlider.VERTICAL, 0, 40, 20 );
    private final JLabel standardLabel = new JLabel( "Standard" );
    private final JSlider filledSlider = new JSlider( JSlider.HORIZONTAL, 0, 40, 20 );
    private final JSlider verticalFilledSlider = new JSlider( JSlider.VERTICAL, 0, 40, 20 );
    private final JLabel filledLabel = new JLabel( "Filled" );
    private final JSlider ticksLabelsSlider = new JSlider( JSlider.HORIZONTAL, 0, 40, 20 );
    private final JSlider verticalTicksLabelsSlider = new JSlider( JSlider.VERTICAL, 0, 40, 20 );
    private final JLabel ticksLabelsLabel = new JLabel( "Ticks & Labels" );
    private final JSlider disabledSlider = new JSlider( JSlider.HORIZONTAL, 0, 40, 20 );
    private final JSlider verticalDisabledSlider = new JSlider( JSlider.VERTICAL, 0, 40, 20 );
    private final JLabel disabledLabel = new JLabel( "Disabled" );

    public SliderPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );

        disabledSlider.setEnabled( false );
        verticalDisabledSlider.setEnabled( false );
        setTickLabelOn( disabledSlider );
        setTickLabelOn( verticalDisabledSlider );
        setTickLabelOn( ticksLabelsSlider );
        setTickLabelOn( verticalTicksLabelsSlider );
        filledSlider.putClientProperty( "JSlider.isFilled", Boolean.TRUE );
        verticalFilledSlider.putClientProperty( "JSlider.isFilled", Boolean.TRUE );

        standardSlider.setMinimumSize( new Dimension( 100, standardSlider.getPreferredSize().height ) );
        filledSlider.setMinimumSize( new Dimension( 100, filledSlider.getPreferredSize().height ) );
        ticksLabelsSlider.setMinimumSize( new Dimension( 100, ticksLabelsSlider.getPreferredSize().height ) );
        disabledSlider.setMinimumSize( new Dimension( 100, disabledSlider.getPreferredSize().height ) );

        int y = -1;
        contentPanel.add( standardSlider, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 1.0,
                                                                  GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( standardLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                                 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );

        contentPanel.add( verticalStandardSlider, new GridBagConstraints( 2, y, 1, 4, 0.0, 0.0,
                                                                          GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( verticalFilledSlider, new GridBagConstraints( 3, y, 1, 4, 0.0, 0.0,
                                                                        GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( verticalTicksLabelsSlider, new GridBagConstraints( 4, y, 1, 4, 0.0, 0.0,
                                                                             GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( verticalDisabledSlider, new GridBagConstraints( 5, y, 1, 4, 0.0, 0.0,
                                                                          GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );

        contentPanel.add( filledSlider, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( filledLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                               GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( ticksLabelsSlider, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                                     GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( ticksLabelsLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( disabledSlider, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 1.0,
                                                                  GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( disabledLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                                 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

    private void setTickLabelOn( final JSlider slider ) {
        slider.setMajorTickSpacing( 10 );
        slider.setMinorTickSpacing( 1 );
        slider.setPaintLabels( true );
        slider.setPaintTicks( true );
    }

}
