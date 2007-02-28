package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Check box demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class CheckBoxPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 2 ) );
    private final JCheckBox standardCheckBox = new JCheckBox( "Standard" );
    private final JCheckBox selectedCheckBox = new JCheckBox( "Standard Selected" );
    private final JCheckBox htmlCheckBox = new JCheckBox( "<html><body><b>HTML</b> Formatted <i>Text</i></body></html>" );
    private final JCheckBox borderPaintedCheckBox = new JCheckBox( "Border Painted" );
    private final JCheckBox disabledCheckBox = new JCheckBox( "Disabled" );
    private final JCheckBox disabledSelectedCheckBox = new JCheckBox( "Disabled Selected" );

    public CheckBoxPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        selectedCheckBox.setSelected( true );
        disabledCheckBox.setEnabled( false );
        disabledSelectedCheckBox.setEnabled( false );
        disabledSelectedCheckBox.setSelected( true );
        borderPaintedCheckBox.setBorderPainted( true );

        contentPanel.add( standardCheckBox );
        contentPanel.add( selectedCheckBox );
        contentPanel.add( htmlCheckBox );
        contentPanel.add( borderPaintedCheckBox );
        contentPanel.add( disabledCheckBox );
        contentPanel.add( disabledSelectedCheckBox );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
