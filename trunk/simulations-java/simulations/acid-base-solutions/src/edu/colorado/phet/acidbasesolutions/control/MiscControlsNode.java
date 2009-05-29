package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.dialog.SymbolLegendDialog;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.EquilibriumExpressionsDialog;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.ReactionEquationsDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class MiscControlsNode extends PNode {
    
    private final Frame parentFrame;
    private final AqueousSolution solution;
    
    private final PNode concentrationGraphNode;
    
    private final JCheckBox concentrationGraphCheckBox;
    private final JCheckBox symbolLegendCheckBox;
    private final JCheckBox equilibriumExpressionsCheckBox;
    private final JCheckBox reactionEquationsCheckBox;
    
    private JDialog symbolLegendDialog;
    private Point symbolLegendDialogLocation;
    private JDialog equilibriumExpressionsDialog;
    private Point equilibriumExpressionsDialogLocation;
    private JDialog reactionEquationsDialog;
    private Point reactionEquationsDialogLocation;
    
    public MiscControlsNode( final PNode concentrationGraphNode, Color background, AqueousSolution solution ) {
        super();
        
        this.solution = solution;
        
        this.concentrationGraphNode = concentrationGraphNode;
         
        parentFrame = PhetApplication.getInstance().getPhetFrame();
        
        concentrationGraphCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_CONCENTRATIONS_GRAPH );
        concentrationGraphCheckBox.setSelected( concentrationGraphNode.getVisible() );
        concentrationGraphCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                concentrationGraphNode.setVisible( concentrationGraphCheckBox.isSelected() );
            }
        } );
        
        symbolLegendCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_SYMBOL_LEGEND );
        symbolLegendCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateSymbolLegendDialog();
            }
        });
        
        equilibriumExpressionsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_EQUILIBRIUM_EXPRESSIONS );
        equilibriumExpressionsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateEquilibriumExpressionsDialog();
            }
        });
        
        reactionEquationsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_REACTION_EQUATIONS );
        reactionEquationsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateReactionEquationsDialog();
            }
        });
        
        // border
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_MISC_CONTROLS );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        panel.setBorder( border );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( concentrationGraphCheckBox, row++, column );
        layout.addComponent( symbolLegendCheckBox, row++, column );
        row = 0;
        column++;
        layout.addComponent( equilibriumExpressionsCheckBox, row++, column );
        layout.addComponent( reactionEquationsCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( panel, background );
        
        addChild( new PSwing( panel ) );
    }
    
    public void setConcentrationGraphSelected( boolean b ) {
        concentrationGraphCheckBox.setSelected( b );
        concentrationGraphNode.setVisible( b );
    }
    
    public boolean isConcentrationGraphSelected() {
        return concentrationGraphCheckBox.isSelected();
    }
    
    public void setSymbolLegendSelected( boolean b ) {
        symbolLegendCheckBox.setSelected( b );
        updateSymbolLegendDialog();
    }
    
    public boolean isSymbolLegendSelected() {
        return symbolLegendCheckBox.isSelected();
    }
    
    public void setEquilibriumExpressionsSelected( boolean b ) {
        equilibriumExpressionsCheckBox.setSelected( b );
        updateEquilibriumExpressionsDialog();
    }
    
    public boolean isEquilibriumExpressionsSelected() {
        return equilibriumExpressionsCheckBox.isSelected();
    }
    
    public void setReactionEquationsSelected( boolean b ) {
        reactionEquationsCheckBox.setSelected( b );
        updateReactionEquationsDialog();
    }
    
    public boolean isReactionEquationsSelected() {
        return reactionEquationsCheckBox.isSelected();
    }
    
    private void updateSymbolLegendDialog() {
        if ( symbolLegendCheckBox.isSelected() ) {
            openSymbolLegendDialog();
        }
        else {
            closeSymbolLegendDialog();
        }
    }
    
    private void openSymbolLegendDialog() {
        
        symbolLegendDialog = new SymbolLegendDialog( parentFrame );
        symbolLegendDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeSymbolLegendDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                symbolLegendDialog = null;
                symbolLegendCheckBox.setSelected( false );
            }
        } );
        
        if ( symbolLegendDialogLocation == null ) {
            SwingUtils.centerDialogInParent( symbolLegendDialog );
        }
        else {
            symbolLegendDialog.setLocation( symbolLegendDialogLocation );
        }
        symbolLegendDialog.setVisible( true );
    }
    
    private void closeSymbolLegendDialog() {
        symbolLegendDialogLocation = closeDialog( symbolLegendDialog );
        symbolLegendDialog = null;
    }
    
    private void updateEquilibriumExpressionsDialog() {
        if ( equilibriumExpressionsCheckBox.isSelected() ) {
            openEquilibriumExpressionsDialog();
        }
        else {
            closeEquilibriumExpressionsDialog();
        }
    }
    
    private void openEquilibriumExpressionsDialog() {
        
        equilibriumExpressionsDialog = new EquilibriumExpressionsDialog( parentFrame, solution );
        equilibriumExpressionsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeEquilibriumExpressionsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                equilibriumExpressionsDialog = null;
                equilibriumExpressionsCheckBox.setSelected( false );
            }
        } );
        
        if ( equilibriumExpressionsDialogLocation == null ) {
            SwingUtils.centerDialogInParent( equilibriumExpressionsDialog );
        }
        else {
            equilibriumExpressionsDialog.setLocation( equilibriumExpressionsDialogLocation );
        }
        equilibriumExpressionsDialog.setVisible( true );
    }
    
    private void closeEquilibriumExpressionsDialog() {
        equilibriumExpressionsDialogLocation = closeDialog( equilibriumExpressionsDialog );
        equilibriumExpressionsDialog = null;
    }
    
    private void updateReactionEquationsDialog() {
        if ( reactionEquationsCheckBox.isSelected() ) {
            openReactionEquationsDialog();
        }
        else {
            closeReactionEquationsDialog();
        }
    }
    
    private void openReactionEquationsDialog() {
        
        reactionEquationsDialog = new ReactionEquationsDialog( parentFrame );
        reactionEquationsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeReactionEquationsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                reactionEquationsDialog = null;
                reactionEquationsCheckBox.setSelected( false );
            }
        } );
        
        if ( reactionEquationsDialogLocation == null ) {
            SwingUtils.centerDialogInParent( reactionEquationsDialog );
        }
        else {
            reactionEquationsDialog.setLocation( reactionEquationsDialogLocation );
        }
        reactionEquationsDialog.setVisible( true );
    }
    
    private void closeReactionEquationsDialog() {
        reactionEquationsDialogLocation = closeDialog( reactionEquationsDialog );
        reactionEquationsDialog = null;
    }
    
    /*
     * Closes a dialog and returns its position.
     */
    private static Point closeDialog( JDialog dialog ) {
        Point location = null;
        if ( dialog != null ) {
            location = dialog.getLocation();
            dialog.dispose();
        }
        return location;
    }
}
