package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.SkaterCharacter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 1:16:29 PM
 */
public class ChooseCharacterDialog extends JDialog {
    private JPanel contentPanel = new JPanel( new GridBagLayout() );
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    private ArrayList characterPanels = new ArrayList();
    private EnergySkateParkModule module;

    public ChooseCharacterDialog( EnergySkateParkModule module ) {
        super( module.getPhetFrame(), "Choose Character", false );
        this.module = module;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;

        setContentPane( contentPanel );

        JButton ok = new JButton( "Ok" );
        ok.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        SkaterCharacter[]sk=module.getSkaterCharacters();
        for( int i = 0; i < sk.length; i++ ) {
            addCharacterPanel( new CharacterPanel( module, sk[i]) );
        }

        contentPanel.add( ok, gridBagConstraints );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private void addCharacterPanel( final CharacterPanel characterPanel ) {
        contentPanel.add( characterPanel, gridBagConstraints );
        characterPanel.addMouseListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                System.out.println( "e = " + e );
                setSelection( characterPanel );
            }
        } );
        characterPanel.setSelected( characterPanel.getSkaterCharacter()==module.getSkaterCharacter());
        characterPanels.add( characterPanel );
    }

    private void setSelection( CharacterPanel characterPanel ) {
        for( int i = 0; i < characterPanels.size(); i++ ) {
            CharacterPanel panel = (CharacterPanel)characterPanels.get( i );
            panel.setSelected( panel == characterPanel );
        }
        pack();
    }

    static class CharacterPanel extends JPanel {
        private boolean selected = false;
        private EnergySkateParkModule module;
        private SkaterCharacter skaterCharacter;

        public CharacterPanel( EnergySkateParkModule module, SkaterCharacter skaterCharacter ) {
            super( new GridBagLayout() );
            this.module = module;
            this.skaterCharacter = skaterCharacter;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            try {
                JLabel label = new JLabel( skaterCharacter.getName() + " (" + skaterCharacter.getMass() + " kg)", new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( skaterCharacter.getImageURL() ), 100 ) ), JLabel.TRAILING );
                add( label, gridBagConstraints );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setSelected( false );
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
            setBorder( selected ? getSelectionBorder() : BorderFactory.createEtchedBorder() );
            if( selected ) {
                module.setSkaterCharacter( skaterCharacter );
            }
        }

        private Border getSelectionBorder() {
            return BorderFactory.createLineBorder( Color.blue, 3 );
        }

        public SkaterCharacter getSkaterCharacter() {
            return skaterCharacter;
        }
    }

}
