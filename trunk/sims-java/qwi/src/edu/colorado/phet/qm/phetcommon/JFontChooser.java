/****************************************************************************************
 * 		*********************************************************		*
 *		* $Package: ZoeloeSoft.projects.JFontChooser		*		*
 *		* $Id: JFontChooser.java		 		*		*
 * 		* $Date: 23:39 19/04/2004				*		*
 *		*							*		*
 *	 	* $Creator: Tim Eeckhaut				*		*
 *		* $Alias: zoeloeboeloe					*		*
 *		*							*		*
 * 		* $Company: ZoeloeSoft					*		*
 *	 	* $Website: http://users.pandora.be/ZoeloeSoft		*		*
 *		*********************************************************		*
 *											*
 * Copyright (c) 2004, Tim Eeckhaut							*
 * All rights reserved.									*
 *											*
 * Redistribution and use in source and binary forms, with or without			*
 * modification, are permitted provided that the following conditions			*
 * are met:										*
 *											*
 * 	1. Redistributions of source code must retain the above copyright		*
 * notice, this list of conditions and the following disclaimer.			*
 *											*
 * 	2. Redistributions in binary form must reproduce the above copyright		*
 * notice, this list of conditions and the following disclaimer in the			*
 * documentation and/or other materials provided with the distribution.			*
 *											*
 * 	3. Neither the name of the company nor the names of its 			*
 * contributors may be used to endorse or promote products derived from this		*
 * software without specific prior written permission.					*
 *											*
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS			*
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT			*
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR		*
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR		*
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,		*
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,			*
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR			*
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF		*
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING			*
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS			*
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.				*
 *											*
 ***************************************************************************************/


package edu.colorado.phet.qm.phetcommon;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class JFontChooser extends JDialog {
    public static int OK_OPTION = 0;
    public static int CANCEL_OPTION = 1;

    private JList fontList, sizeList;
    private JCheckBox cbBold, cbItalic;
    private JTextArea txtSample;

    private int OPTION;

    private String[] sizes = new String[]
            {"2", "4", "6", "8", "10", "12", "14", "16", "18", "20", "22", "24", "30", "36", "48", "72"};

    public int showDialog( Font font ) {
        setFont( font );
        return showDialog();
    }

    public int showDialog() {
        setVisible( true );

        return OPTION;
    }

    public JFontChooser( Frame parent ) {
        super( parent, true );
        setTitle( "JFontChooser" );

        OPTION = JFontChooser.CANCEL_OPTION;

        // create all components
//
//		JButton btnOK = new JButton("OK");
//		btnOK.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				JFontChooser.this.OPTION = JFontChooser.OK_OPTION;
//				JFontChooser.this.setVisible(false);
//			}
//		});
//
//
//		JButton btnCancel = new JButton("Cancel");
//		btnCancel.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				JFontChooser.this.OPTION = JFontChooser.CANCEL_OPTION;
//				JFontChooser.this.setVisible(false);
//			}
//		});


        fontList = new JList( GraphicsEnvironment.getLocalGraphicsEnvironment().
                getAvailableFontFamilyNames() ) {
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension( 150, 144 );
            }
        };
        fontList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );


        sizeList = new JList( sizes ) {
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension( 25, 144 );
            }
        };
        sizeList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );


        cbBold = new JCheckBox( "Bold" );

        cbItalic = new JCheckBox( "Italic" );


        txtSample = new JTextArea() {
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension( 385, 80 );
            }
        };
        txtSample.setText( "The quick brown fox jumped over the fence" );

        // set the default font

        setFont( null );

        // add the listeners

        ListSelectionListener listListener = new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                updateFont();
            }
        };

        fontList.addListSelectionListener( listListener );
        sizeList.addListSelectionListener( listListener );


        ActionListener cbListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateFont();
            }
        };

        cbBold.addActionListener( cbListener );
        cbItalic.addActionListener( cbListener );

        // build the container

        getContentPane().setLayout( new java.awt.BorderLayout() );

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout( new java.awt.BorderLayout() );

        leftPanel.add( new JScrollPane( fontList ), java.awt.BorderLayout.CENTER );
        leftPanel.add( new JScrollPane( sizeList ), java.awt.BorderLayout.EAST );

        getContentPane().add( leftPanel, java.awt.BorderLayout.CENTER );


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout( new java.awt.BorderLayout() );

        JPanel rightPanelSub1 = new JPanel();
        rightPanelSub1.setLayout( new java.awt.FlowLayout() );

        rightPanelSub1.add( cbBold );
        rightPanelSub1.add( cbItalic );

        rightPanel.add( rightPanelSub1, java.awt.BorderLayout.NORTH );

        JPanel rightPanelSub2 = new JPanel();
        rightPanelSub2.setLayout( new java.awt.GridLayout( 2, 1 ) );

//					rightPanelSub2.add(btnOK);
//					rightPanelSub2.add(btnCancel);

        rightPanel.add( rightPanelSub2, java.awt.BorderLayout.SOUTH );

        getContentPane().add( rightPanel, java.awt.BorderLayout.EAST );

        getContentPane().add( new JScrollPane( txtSample ), java.awt.BorderLayout.SOUTH );

        setSize( 200, 200 );
        setResizable( false );

        pack();
    }

    private void updateFont() {
        txtSample.setFont( getCurrentFont() );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.fontChanged( getCurrentFont() );
        }
    }

    public static interface Listener {
        void fontChanged( Font font );
    }

    ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setFont( Font font ) {
        if( font == null && txtSample != null ) {
            font = txtSample.getFont();
        }
        if( fontList != null && sizeList != null && cbBold != null && cbItalic != null ) {
            fontList.setSelectedValue( font.getName(), true );
            fontList.ensureIndexIsVisible( fontList.getSelectedIndex() );
            sizeList.setSelectedValue( "" + font.getSize(), true );
            sizeList.ensureIndexIsVisible( sizeList.getSelectedIndex() );

            cbBold.setSelected( font.isBold() );
            cbItalic.setSelected( font.isItalic() );
        }
    }

    public Font getFont() {
        if( OPTION == OK_OPTION ) {
            return getCurrentFont();
        }
        else {
            return null;
        }
    }

    private Font getCurrentFont() {
        if( fontList == null || cbBold == null || cbItalic == null || sizeList == null || sizeList.getSelectedValue() == null ) {
            return new Font( "Lucida Sans", Font.PLAIN, 12 );
        }
        String fontFamily = (String)fontList.getSelectedValue();
        int fontSize = Integer.parseInt( (String)sizeList.getSelectedValue() );

        int fontType = Font.PLAIN;

        if( cbBold.isSelected() ) {
            fontType += Font.BOLD;
        }
        if( cbItalic.isSelected() ) {
            fontType += Font.ITALIC;
        }

        return new Font( fontFamily, fontType, fontSize );
    }
}
