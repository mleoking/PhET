package smooth.demo;

import smooth.demo.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The internal frame containing demo components.
 *
 * @author James Shiell
 * @version 1.0
 */
public class DemoInternalFrame
        extends JInternalFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel tabControlPanel = new JPanel( new GridBagLayout() );
    private final ButtonGroup tabControlButtonGroup = new ButtonGroup();
    private final JLabel tabControlLabel = new JLabel( "Tab Position" );
    private final JRadioButton tabLeftRadioButton = new JRadioButton( "Left" );
    private final JRadioButton tabRightRadioButton = new JRadioButton( "Right" );
    private final JRadioButton tabTopRadioButton = new JRadioButton( "Top" );
    private final JRadioButton tabBottomRadioButton = new JRadioButton(
            "Bottom" );

    private final ButtonPanel buttonPanel = new ButtonPanel();
    private final ToggleButtonPanel toggleButtonPanel = new ToggleButtonPanel();
    private final CheckBoxPanel checkBoxPanel = new CheckBoxPanel();
    private final RadioButtonPanel radioButtonPanel = new RadioButtonPanel();
    private final ListSplitPanePanel listSplitPanePanel
            = new ListSplitPanePanel();
    private final ProgressBarPanel progressBarPanel = new ProgressBarPanel();
    private final SliderPanel sliderPanel = new SliderPanel();
    private final TextFieldPanel textFieldPanel = new TextFieldPanel();
    private final SpinnerPanel spinnerPanel = new SpinnerPanel();
    private final ComboBoxPanel comboBoxPanel = new ComboBoxPanel();
    private final TreePanel treePanel = new TreePanel();
    private final OptionPanePanel optionPanePanel = new OptionPanePanel();
    private final FileChooserPanel fileChooserPanel = new FileChooserPanel();
    private final ColorChooserPanel colorChooserPanel = new ColorChooserPanel();

    public DemoInternalFrame() {
        super( "Demonstration", true, false, true, true );
        initialiseComponents();
        initialiseControllers();
    }

    private void initialiseComponents() {
        tabControlButtonGroup.add( tabLeftRadioButton );
        tabControlButtonGroup.add( tabRightRadioButton );
        tabControlButtonGroup.add( tabTopRadioButton );
        tabControlButtonGroup.add( tabBottomRadioButton );

        int y = -1;
        tabControlPanel.setBorder( new EmptyBorder( 4, 4, 4, 4 ) );
        tabControlPanel.add( tabControlLabel,
                             new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                     GridBagConstraints.NORTHWEST,
                                                     GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                     0, 0 ) );
        tabControlPanel.add( tabTopRadioButton,
                             new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                     GridBagConstraints.NORTHWEST,
                                                     GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                     0, 0 ) );
        tabControlPanel.add( tabLeftRadioButton,
                             new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                     GridBagConstraints.NORTHWEST,
                                                     GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                     0, 0 ) );
        tabControlPanel.add( tabBottomRadioButton,
                             new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                     GridBagConstraints.NORTHWEST,
                                                     GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                     0, 0 ) );
        tabControlPanel.add( tabRightRadioButton,
                             new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                     GridBagConstraints.NORTHWEST,
                                                     GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                     0, 0 ) );
        tabControlPanel.add( Box.createVerticalGlue(),
                             new GridBagConstraints( 0, ++y, 1, 1, 1.0, 1.0,
                                                     GridBagConstraints.NORTHWEST,
                                                     GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                     0, 0 ) );

        tabbedPane.addTab( "JList/JSplitPane", listSplitPanePanel );
        tabbedPane.addTab( "JButton", buttonPanel );
        buttonPanel.setDefaultButton( getRootPane() );
        tabbedPane.addTab( "JToggleButton", toggleButtonPanel );
        tabbedPane.addTab( "JCheckBox", checkBoxPanel );
        tabbedPane.addTab( "JRadioButton", radioButtonPanel );
        tabbedPane.addTab( "JProgressBar", progressBarPanel );
        tabbedPane.addTab( "JSlider", sliderPanel );
        tabbedPane.addTab( "JTextField", textFieldPanel );
        tabbedPane.addTab( "JSpinner", spinnerPanel );
        tabbedPane.addTab( "JComboBox", comboBoxPanel );
        tabbedPane.addTab( "JTree", treePanel );
        tabbedPane.addTab( "JOptionPane", optionPanePanel );
        tabbedPane.addTab( "JFileChooser", fileChooserPanel );
        tabbedPane.addTab( "JColorChooser", colorChooserPanel );

        tabbedPane.setBorder( new EmptyBorder( 4, 4, 4, 4 ) );
        getContentPane().add( tabbedPane, BorderLayout.CENTER );
        getContentPane().add( tabControlPanel, BorderLayout.EAST );

        pack();
    }

    private void initialiseControllers() {
        tabTopRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tabbedPane.setTabPlacement( JTabbedPane.TOP );
            }
        } );
        tabLeftRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tabbedPane.setTabPlacement( JTabbedPane.LEFT );
            }
        } );
        tabBottomRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tabbedPane.setTabPlacement( JTabbedPane.BOTTOM );
            }
        } );
        tabRightRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tabbedPane.setTabPlacement( JTabbedPane.RIGHT );
            }
        } );
    }

}
