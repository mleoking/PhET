package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Progress bar demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class ProgressBarPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridBagLayout() );
    private final JProgressBar standardProgressBar = new JProgressBar( JProgressBar.HORIZONTAL, 0, 100 );
    private final JProgressBar verticalStandardProgressBar = new JProgressBar( JProgressBar.VERTICAL, 0, 100 );
    private final JLabel standardLabel = new JLabel( "Standard" );
    private final JProgressBar stringPaintedProgressBar = new JProgressBar( JProgressBar.HORIZONTAL, 0, 100 );
    private final JProgressBar verticalStringPaintedProgressBar = new JProgressBar( JProgressBar.VERTICAL, 0, 100 );
    private final JLabel stringPaintedLabel = new JLabel( "String Painted" );
    private final JProgressBar disabledProgressBar = new JProgressBar( JProgressBar.HORIZONTAL, 0, 100 );
    private final JProgressBar verticalDisabledProgressBar = new JProgressBar( JProgressBar.VERTICAL, 0, 100 );
    private final JLabel disabledLabel = new JLabel( "Disabled" );

    public ProgressBarPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );

        standardProgressBar.setValue( 25 );
        verticalStandardProgressBar.setValue( 25 );
        stringPaintedProgressBar.setValue( 50 );
        verticalStringPaintedProgressBar.setValue( 50 );
        disabledProgressBar.setValue( 75 );
        verticalDisabledProgressBar.setValue( 75 );
        stringPaintedProgressBar.setStringPainted( true );
        verticalStringPaintedProgressBar.setStringPainted( true );
        disabledProgressBar.setEnabled( false );
        verticalDisabledProgressBar.setEnabled( false );

        int y = -1;
        contentPanel.add( standardProgressBar, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 1.0,
                                                                       GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( standardLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                                 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( verticalStandardProgressBar, new GridBagConstraints( 2, y, 1, 3, 0.0, 0.0,
                                                                               GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( verticalStringPaintedProgressBar, new GridBagConstraints( 3, y, 1, 3, 0.0, 0.0,
                                                                                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( verticalDisabledProgressBar, new GridBagConstraints( 4, y, 1, 3, 0.0, 0.0,
                                                                               GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );

        contentPanel.add( stringPaintedProgressBar, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                                            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( stringPaintedLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                                      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( disabledProgressBar, new GridBagConstraints( 0, ++y, 1, 1, 1.0, 1.0,
                                                                       GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
        contentPanel.add( disabledLabel, new GridBagConstraints( 1, y, 1, 1, 0.0, 0.0,
                                                                 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
