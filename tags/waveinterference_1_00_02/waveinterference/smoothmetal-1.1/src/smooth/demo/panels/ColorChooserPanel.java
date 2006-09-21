package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Color chooser demonstration panel.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version 1.0
 */
public class ColorChooserPanel
        extends JPanel {
    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 4 ) );
    private final JButton colorButton = new JButton( "Show Color Dialogue" );

    public ColorChooserPanel() {
        initialiseComponents();
        initialiseControllers();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );

        contentPanel.add( colorButton );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0,
                                                   GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                   new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

    private void initialiseControllers() {
        colorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                colorButtonPressed();
            }
        } );
    }

    private void colorButtonPressed() {
        JColorChooser.showDialog( this, "Color Dialogue", Color.white );
    }
}
