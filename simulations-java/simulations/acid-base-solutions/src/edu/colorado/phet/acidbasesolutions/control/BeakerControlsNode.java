package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.RatioCheckBox.HydroniumHydroxideRatioCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Various "view" options related to the beaker.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BeakerControlsNode extends PhetPNode {
    
    private final JPanel panel;
    private final RatioCheckBox dissociatedComponentsRatioCheckBox;
    private final JCheckBox hyroniumHydroxideRatioCheckBox;
    private final JCheckBox moleculeCountsCheckBox;
    private final JCheckBox labelCheckBox;
    private final ArrayList<BeakerViewChangeListener> listeners;
    
    protected BeakerControlsNode( Color background ) {
        super();
        
        listeners = new ArrayList<BeakerViewChangeListener>();
        
        dissociatedComponentsRatioCheckBox = new RatioCheckBox();
        dissociatedComponentsRatioCheckBox.setText( HTMLUtils.toHTMLString( ABSStrings.CHECK_BOX_DISASSOCIATED_COMPONENTS_RATIO ) );
        dissociatedComponentsRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyDisassociatedComponentsRatioChanged();
            }
        });

        hyroniumHydroxideRatioCheckBox = new HydroniumHydroxideRatioCheckBox();
        hyroniumHydroxideRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyHydroniumHydroxideRatioChanged();
            }
        });
        
        moleculeCountsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_MOLECULE_COUNTS );
        moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyMoleculeCountsChanged();
            }
        });
        
        labelCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_BEAKER_LABEL );
        labelCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
               notifyLabelChanged();
            }
        });

        // border
        panel = new JPanel();
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_VIEW );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        panel.setBorder( border );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( dissociatedComponentsRatioCheckBox, row++, column );
        layout.addComponent( hyroniumHydroxideRatioCheckBox, row++, column );
        layout.addComponent( moleculeCountsCheckBox, row++, column );
        layout.addComponent( labelCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( panel, background );
        
        addChild( new PSwing( panel ) );
    }
    
    protected void setDissociatedComponents( String symbol1, Color color1, String symbol2, Color color2 ) {
        dissociatedComponentsRatioCheckBox.setComponents( symbol1, color1, symbol2, color2 );
    }
    
    protected void setDissociatedComponentsCheckBoxVisible( boolean visible ) {
        dissociatedComponentsRatioCheckBox.setVisible( visible );
    }
    
    public void setDissociatedComponentsRatioSelected( boolean b ) {
        if ( b != isDissociatedComponentsRatioSelected() ) {
            dissociatedComponentsRatioCheckBox.setSelected( b );
            notifyDisassociatedComponentsRatioChanged();
        }
    }
    
    public boolean isDissociatedComponentsRatioSelected() {
        return dissociatedComponentsRatioCheckBox.isSelected();
    }
    
    public void setHydroniumHydroxideRatioSelected( boolean b ) {
        if ( b != isHydroniumHydroxideRatioSelected() ) {
            hyroniumHydroxideRatioCheckBox.setSelected( b );
            notifyHydroniumHydroxideRatioChanged();
        }
    }
    
    public boolean isHydroniumHydroxideRatioSelected() {
        return hyroniumHydroxideRatioCheckBox.isSelected();
    }
    
    public void setMoleculeCountsSelected( boolean b ) {
        if ( b != isMoleculeCountsSelected() ) {
            moleculeCountsCheckBox.setSelected( b );
            notifyMoleculeCountsChanged();
        }
    }
    
    public boolean isMoleculeCountsSelected() {
        return moleculeCountsCheckBox.isSelected();
    }
    
    public void setLabelSelected( boolean b ) {
        if ( b != isLabelSelected() ) {
            labelCheckBox.setSelected( b );
            notifyLabelChanged();
        }
    }
    
    public boolean isLabelSelected() {
        return labelCheckBox.isSelected();
    }
    
    public interface BeakerViewChangeListener {
        public void disassociatedComponentsRatioChanged( boolean selected );
        public void hydroniumHydroxideRatioChanged( boolean selected );
        public void moleculeCountsChanged( boolean selected );
        public void labelChanged( boolean selected );
    }
    
    public void addBeakerViewChangeListener( BeakerViewChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeBeakerViewChangeListener( BeakerViewChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyDisassociatedComponentsRatioChanged() {
        final boolean selected = dissociatedComponentsRatioCheckBox.isSelected();
        Iterator<BeakerViewChangeListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().disassociatedComponentsRatioChanged( selected );
        }
    }
    
    private void notifyHydroniumHydroxideRatioChanged() {
        final boolean selected = hyroniumHydroxideRatioCheckBox.isSelected();
        Iterator<BeakerViewChangeListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().hydroniumHydroxideRatioChanged( selected );
        }
    }
    
    private void notifyMoleculeCountsChanged() {
        final boolean selected = moleculeCountsCheckBox.isSelected();
        Iterator<BeakerViewChangeListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().moleculeCountsChanged( selected );
        }
    }
    
    private void notifyLabelChanged() {
        final boolean selected = labelCheckBox.isSelected();
        Iterator<BeakerViewChangeListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().labelChanged( selected );
        }
    }
}
