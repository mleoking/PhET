package smooth.demo;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author James Shiell
 * @version 1.0
 */
public class ControlInternalFrame
        extends JInternalFrame {

    private final ButtonGroup buttonGroup = new ButtonGroup();

    private final JPanel radioButtonPanel = new JPanel( new GridLayout( 0, 1 ) );
    private final JRadioButton smoothMetalRadioButton = new JRadioButton(
            "Smooth Metal" );
    private final JRadioButton smoothWindowsRadioButton = new JRadioButton(
            "Smooth Windows" );
    private final JRadioButton metalRadioButton = new JRadioButton( "Metal" );
    private final JRadioButton windowsRadioButton = new JRadioButton( "Windows" );
    private final JRadioButton motifRadioButton = new JRadioButton( "Motif" );
    private final JRadioButton macOSRadioButton = new JRadioButton( "MacOS" );
    private final JRadioButton gtkRadioButton = new JRadioButton( "GTK" );

    private final JPanel controlPanel = new JPanel( new GridBagLayout() );
    private final JCheckBox antiAliasCheckBox = new JCheckBox( "Anti-alias" );
    private final JCheckBox fractionalMetricsCheckBox = new JCheckBox(
            "Fractional Metrics" );

    public ControlInternalFrame() {
        intialiseComponents();
        initialiseControllers();

        smoothMetalRadioButton.setSelected( true );
        smoothMetalLNFSelected();
    }

    private void intialiseComponents() {
        buttonGroup.add( smoothMetalRadioButton );
        buttonGroup.add( smoothWindowsRadioButton );
        buttonGroup.add( metalRadioButton );
        buttonGroup.add( windowsRadioButton );
        buttonGroup.add( motifRadioButton );
        buttonGroup.add( macOSRadioButton );
        buttonGroup.add( gtkRadioButton );

        radioButtonPanel.add( smoothMetalRadioButton );
        radioButtonPanel.add( smoothWindowsRadioButton );
        radioButtonPanel.add( metalRadioButton );
        radioButtonPanel.add( windowsRadioButton );
        radioButtonPanel.add( motifRadioButton );
        radioButtonPanel.add( macOSRadioButton );
        radioButtonPanel.add( gtkRadioButton );

        getContentPane().add( radioButtonPanel, BorderLayout.NORTH );

        int y = -1;
        controlPanel.add( new JSeparator( SwingConstants.HORIZONTAL ),
                          new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                  0, 0 ) );
        controlPanel.add( antiAliasCheckBox,
                          new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                  0, 0 ) );
        controlPanel.add( fractionalMetricsCheckBox,
                          new GridBagConstraints( 0, ++y, 1, 1, 1.0, 0.0,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                  0, 0 ) );
        controlPanel.add( Box.createVerticalGlue(),
                          new GridBagConstraints( 0, ++y, 1, 1, 1.0, 1.0,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 2, 4, 2, 4 ),
                                                  0, 0 ) );

        getContentPane().add( controlPanel, BorderLayout.CENTER );

        pack();
    }

    private void enableSmoothControls( final boolean enable ) {
        antiAliasCheckBox.setSelected( SmoothUtilities.isAntialias() );
        fractionalMetricsCheckBox
                .setSelected( SmoothUtilities.isFractionalMetrics() );
        antiAliasCheckBox.setEnabled( enable );
        fractionalMetricsCheckBox.setEnabled( enable );
    }

    private void initialiseControllers() {
        antiAliasCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                toggleAACheckBoxChanged();
            }
        } );
        fractionalMetricsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                toggleFractionalFontsCheckBoxChanged();
            }
        } );
        smoothMetalRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                smoothMetalLNFSelected();
            }
        } );
        smoothWindowsRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                smoothWindowsLNFSelected();
            }
        } );
        windowsRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                windowsLNFSelected();
            }
        } );
        macOSRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                macosLNFSelected();
            }
        } );
        metalRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                metalLNFSelected();
            }
        } );
        motifRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                motifLNFSelected();
            }
        } );
        gtkRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                gtkLNFSelected();
            }
        } );
    }

    private void motifLNFSelected() {
        setLookAndFeel( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" );
        enableSmoothControls( false );
    }

    private void gtkLNFSelected() {
        setLookAndFeel( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );
        enableSmoothControls( false );
    }

    private void metalLNFSelected() {
        setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        enableSmoothControls( false );
    }

    private void macosLNFSelected() {
        setLookAndFeel( "com.sun.java.swing.plaf.mac.MacLookAndFeel" );
        enableSmoothControls( false );
    }

    private void windowsLNFSelected() {
        setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
        enableSmoothControls( false );
    }

    private void smoothWindowsLNFSelected() {
        setLookAndFeel( "smooth.windows.SmoothLookAndFeel" );
        enableSmoothControls( true );
    }

    private void smoothMetalLNFSelected() {
        setLookAndFeel( "smooth.metal.SmoothLookAndFeel" );
        enableSmoothControls( true );
    }

    private void toggleAACheckBoxChanged() {
        SmoothUtilities.setAntialias( antiAliasCheckBox.isSelected() );
        updateRoot();
    }

    private void toggleFractionalFontsCheckBoxChanged() {
        SmoothUtilities
                .setFractionalMetrics( fractionalMetricsCheckBox.isSelected() );
        updateRoot();
    }

    /**
     * Sets the current look and feel.
     *
     * @param lookAndFeel the class to set the look and feel to.
     */
    protected void setLookAndFeel( final String lookAndFeel ) {
        try {
            UIManager.setLookAndFeel( lookAndFeel );
            updateRoot();

        }
        catch( Exception e ) {
            JOptionPane.showMessageDialog( this, "Look and feel not supported ("
                                                 + lookAndFeel + ')', "LnF not supported",
                                                                      JOptionPane.ERROR_MESSAGE );
        }
    }

    private void updateRoot() {
        final Component root = getComponentRoot( this );
        SwingUtilities.updateComponentTreeUI( root );
        root.repaint();
    }

    private static Component getComponentRoot( final Component comp ) {
        if( null == comp.getParent() ) {
            return comp;
        }

        return getComponentRoot( comp.getParent() );
    }
}
