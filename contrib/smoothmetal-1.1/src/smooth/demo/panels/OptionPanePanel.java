package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Option pane demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class OptionPanePanel
        extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 4 ) );
    private final JButton infoButton = new JButton( "Information" );
    private final JButton htmlInfoButton = new JButton(
            "<html><body><b>HTML</b> Information</body></html>" );
    private final JButton questionButton = new JButton( "Question" );
    private final JButton warningButton = new JButton( "Warning" );
    private final JButton errorButton = new JButton( "Error" );
    private final JButton internalInfoButton = new JButton(
            "Internal Information" );
    private final JButton internalHtmlInfoButton = new JButton(
            "<html><b>HTML</b> Internal Information" );
    private final JButton internalQuestionButton = new JButton(
            "Internal Question" );
    private final JButton internalWarningButton = new JButton(
            "Internl Warning" );
    private final JButton internalErrorButton = new JButton( "Internal Error" );

    public OptionPanePanel() {
        initialiseComponents();
        initialiseControllers();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );

        contentPanel.add( infoButton );
        contentPanel.add( htmlInfoButton );
        contentPanel.add( questionButton );
        contentPanel.add( warningButton );
        contentPanel.add( errorButton );
        contentPanel.add( internalInfoButton );
        contentPanel.add( internalHtmlInfoButton );
        contentPanel.add( internalQuestionButton );
        contentPanel.add( internalWarningButton );
        contentPanel.add( internalErrorButton );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0,
                                                   GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                   new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

    private void initialiseControllers() {
        infoButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                infoButtonPressed();
            }
        } );
        htmlInfoButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                htmlInfoButtonPressed();
            }
        } );
        questionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                questionButtonPressed();
            }
        } );
        warningButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                warningButtonPressed();
            }
        } );
        errorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                errorButtonPressed();
            }
        } );
        internalInfoButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                internalInfoButtonPressed();
            }
        } );
        internalHtmlInfoButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                internalHtmlInfoButtonPressed();
            }
        } );
        internalQuestionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                internalQuestionButtonPressed();
            }
        } );
        internalWarningButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                internalWarningButtonPressed();
            }
        } );
        internalErrorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                internalErrorButtonPressed();
            }
        } );
    }

    private void errorButtonPressed() {
        JOptionPane.showMessageDialog( this, "Error message", "Error",
                                       JOptionPane.ERROR_MESSAGE );
    }

    private void warningButtonPressed() {
        JOptionPane.showMessageDialog( this, "Warning message", "Warning",
                                       JOptionPane.WARNING_MESSAGE );
    }

    private void questionButtonPressed() {
        JOptionPane.showConfirmDialog( this, "Do you want to answer?",
                                       "Question", JOptionPane.YES_NO_CANCEL_OPTION );
    }

    private void htmlInfoButtonPressed() {
        JOptionPane.showMessageDialog( this,
                                       "<html><body><b>HTML</b> <i>formatted</i> Information message</body></html>",
                                       "Information", JOptionPane.INFORMATION_MESSAGE );
    }

    private void infoButtonPressed() {
        JOptionPane.showMessageDialog( this, "Information message",
                                       "Information", JOptionPane.INFORMATION_MESSAGE );
    }

    private void internalErrorButtonPressed() {
        JOptionPane.showInternalMessageDialog( this, "Internal error message",
                                               "Error", JOptionPane.ERROR_MESSAGE );
    }

    private void internalWarningButtonPressed() {
        JOptionPane.showInternalMessageDialog( this, "Internal warning message",
                                               "Warning", JOptionPane.WARNING_MESSAGE );
    }

    private void internalQuestionButtonPressed() {
        JOptionPane.showInternalConfirmDialog( this, "Do you want to answer?",
                                               "Internal question", JOptionPane.YES_NO_CANCEL_OPTION );
    }

    private void internalHtmlInfoButtonPressed() {
        JOptionPane.showInternalMessageDialog( this,
                                               "<html><b>HTML</b> <i>formatted</i> Internal information message",
                                               "Information", JOptionPane.INFORMATION_MESSAGE );
    }

    private void internalInfoButtonPressed() {
        JOptionPane.showInternalMessageDialog( this,
                                               "Internal information message", "Information",
                                               JOptionPane.INFORMATION_MESSAGE );
    }
}
