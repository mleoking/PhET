package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Radio button demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class RadioButtonPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 2 ) );
    private final JRadioButton standardRadioButton = new JRadioButton( "Standard" );
    private final JRadioButton selectedRadioButton = new JRadioButton( "Standard Selected" );
    private final JRadioButton htmlRadioButton = new JRadioButton( "<html><body><b>HTML</b> Formatted <i>Text</i></body></html>" );
    private final JRadioButton borderPaintedRadioButton = new JRadioButton( "Border Painted" );
    private final JRadioButton disabledRadioButton = new JRadioButton( "Disabled" );
    private final JRadioButton disabledSelectedRadioButton = new JRadioButton( "Disabled Selected" );

    public RadioButtonPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        selectedRadioButton.setSelected( true );
        disabledRadioButton.setEnabled( false );
        disabledSelectedRadioButton.setEnabled( false );
        disabledSelectedRadioButton.setSelected( true );
        borderPaintedRadioButton.setBorderPainted( true );

        contentPanel.add( standardRadioButton );
        contentPanel.add( selectedRadioButton );
        contentPanel.add( htmlRadioButton );
        contentPanel.add( borderPaintedRadioButton );
        contentPanel.add( disabledRadioButton );
        contentPanel.add( disabledSelectedRadioButton );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
