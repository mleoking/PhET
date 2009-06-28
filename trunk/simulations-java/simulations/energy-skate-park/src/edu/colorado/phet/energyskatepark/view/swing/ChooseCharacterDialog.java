package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
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
import java.text.MessageFormat;

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
        super( module.getPhetFrame(), EnergySkateParkStrings.getString( "controls.choose-character" ), false );
        this.module = module;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;

        setContentPane( contentPanel );

        JButton ok = new JButton( PhetCommonResources.getString("Common.choice.ok") );
        ok.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        SkaterCharacter[] sk = module.getSkaterCharacters();
        CharacterPanel[] cp = new CharacterPanel[sk.length];
        for( int i = 0; i < sk.length; i++ ) {
            cp[i] = new CharacterPanel( module, sk[i] );
        }
        Dimension max = new Dimension( 0, 0 );
        for( int i = 0; i < cp.length; i++ ) {
            max.width = Math.max( max.width, cp[i].getPreferredSize().width );
            max.height = Math.max( max.height, cp[i].getPreferredSize().height );

        }
        for( int i = 0; i < sk.length; i++ ) {
            cp[i].setPreferredSize( max );
            addCharacterPanel( cp[i] );
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
        characterPanel.setSelected( characterPanel.getSkaterCharacter() == module.getSkaterCharacter() );
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
                String pattern=EnergySkateParkStrings.getString("controls.choose-character.label.pattern");
                String labelStr=MessageFormat.format(pattern,skaterCharacter.getName(),skaterCharacter.getMass(), EnergySkateParkStrings.getString("units.kg"));
                JLabel label = new JLabel(labelStr,
                                           new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio(
                                                   ImageLoader.loadBufferedImage( skaterCharacter.getImageURL() ), (int)( skaterCharacter.getModelHeight() * 75 ) ) ), JLabel.TRAILING );
                add( label, gridBagConstraints );
//                label.setPreferredSize( new Dimension( label.getPreferredSize().width, 200 ) );
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
