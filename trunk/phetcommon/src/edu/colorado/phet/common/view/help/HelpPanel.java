/**
 * Class: HelpPanel
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 25, 2004
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpPanel extends JPanel {
    private boolean miniHelpShowing = false;
    private String showHelpStr = "Help!";
    private String hideHelpStr = "Hide Help";
    private String megaHelpStr = "Megahelp";
    private GridBagLayout layout = new GridBagLayout();
    private JButton miniHelpBtn;
    private JButton megaHelpBtn;
    private Module module;


    public HelpPanel( final Module module ) {
        this.module = module;
        miniHelpBtn = new JButton( showHelpStr );
        megaHelpBtn = new JButton( megaHelpStr );
        miniHelpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                miniHelpShowing = !miniHelpShowing;
                if( miniHelpShowing ) {
                    setTwoButtonMode();
                }
                else {
                    setOneButtonMode();
                }
                module.setHelpEnabled( miniHelpShowing );
            }
        } );
        setLayout( this.layout );
        setOneButtonMode();
        Dimension dim = megaHelpBtn.getPreferredSize();
        setPreferredSize( new Dimension( (int)( dim.width * 1.2 ), (int)( dim.height * 1.2 ) ) );
    }

    private void setOneButtonMode() {
        super.remove( miniHelpBtn );
        super.remove( megaHelpBtn );
        miniHelpBtn.setText( showHelpStr );

        try {
            GraphicsUtil.addGridBagComponent( this, miniHelpBtn, 0, 0,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER,
                                              new Insets( 4, 0, 4, 0 ) );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

        relayout();
    }

    private void setTwoButtonMode() {
        super.remove( miniHelpBtn );
        super.remove( megaHelpBtn );
        miniHelpBtn.setText( hideHelpStr );
        try {
            Insets insets = new Insets( 4, 0, 4, 0 );
            GraphicsUtil.addGridBagComponent( HelpPanel.this,
                                              miniHelpBtn,
                                              0, 0,
                                              1, 2,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER, insets );

            GraphicsUtil.addGridBagComponent( HelpPanel.this,
                                              megaHelpBtn,
                                              0, 2,
                                              1, 2,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER, insets );
        }
        catch( AWTException e1 ) {
            e1.printStackTrace();
        }
        relayout();

    }

    private void relayout() {
        layout.layoutContainer( this );
        invalidate();
        validate();
        repaint();
    }

}
