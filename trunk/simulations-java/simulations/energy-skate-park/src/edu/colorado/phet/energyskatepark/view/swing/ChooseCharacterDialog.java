// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;
import edu.colorado.phet.energyskatepark.view.SkaterCharacter;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 1:16:29 PM
 */
public class ChooseCharacterDialog extends PaintImmediateDialog {
    private final JPanel contentPanel = new JPanel( new GridBagLayout() );
    private final GridBagConstraints gridBagConstraints = new GridBagConstraints();
    private final ArrayList characterPanels = new ArrayList();
    private final AbstractEnergySkateParkModule module;

    public ChooseCharacterDialog( AbstractEnergySkateParkModule module ) {
        super( module.getPhetFrame(), EnergySkateParkResources.getString( "controls.choose-character" ), false );
        this.module = module;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;

        setContentPane( contentPanel );

        JButton ok = new JButton( PhetCommonResources.getString( "Common.choice.ok" ) );
        ok.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        SkaterCharacter[] sk = module.getSkaterCharacters();
        CharacterPanel[] cp = new CharacterPanel[sk.length];
        for ( int i = 0; i < sk.length; i++ ) {
            cp[i] = new CharacterPanel( module, sk[i] );
        }
        Dimension max = new Dimension( 0, 0 );
        for ( int i = 0; i < cp.length; i++ ) {
            max.width = Math.max( max.width, cp[i].getPreferredSize().width );
            max.height = Math.max( max.height, cp[i].getPreferredSize().height );

        }
        for ( int i = 0; i < sk.length; i++ ) {
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
            public void mousePressed( MouseEvent e ) {
                EnergySkateParkLogging.println( "e = " + e );
                setSelection( characterPanel );
            }
        } );
        characterPanel.setSelected( characterPanel.getSkaterCharacter() == module.getSkaterCharacter() );
        characterPanels.add( characterPanel );
    }

    private void setSelection( CharacterPanel characterPanel ) {
        for ( int i = 0; i < characterPanels.size(); i++ ) {
            CharacterPanel panel = (CharacterPanel) characterPanels.get( i );
            panel.setSelected( panel == characterPanel );
        }
        pack();
    }

    static class CharacterPanel extends JPanel {
        private boolean selected = false;
        private final AbstractEnergySkateParkModule module;
        private final SkaterCharacter skaterCharacter;

        public CharacterPanel( AbstractEnergySkateParkModule module, SkaterCharacter skaterCharacter ) {
            super( new GridBagLayout() );
            this.module = module;
            this.skaterCharacter = skaterCharacter;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            String pattern = EnergySkateParkResources.getString( "controls.choose-character.label.pattern" );
            String labelStr = MessageFormat.format( pattern, skaterCharacter.getName(), skaterCharacter.getMass(), EnergySkateParkResources.getString( "units.kg" ) );
            JLabel label = new JLabel( labelStr,
                                       new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio(
                                               skaterCharacter.getImage(), (int) ( skaterCharacter.getModelHeight() * 75 ) ) ), JLabel.TRAILING );
            add( label, gridBagConstraints );
            setSelected( false );
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
            setBorder( selected ? getSelectionBorder() : BorderFactory.createEtchedBorder() );
            if ( selected ) {
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
