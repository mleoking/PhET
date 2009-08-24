package edu.colorado.phet.titration.prototype;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class TPControlPanel extends JPanel {
    
    private static final String VOLUME_UNITS = "mL";
    private static final String K_UNITS = "";
    private static final String CONCENTRATION_UNITS = "M";
    
    private static final double X_MIN = TPConstants.TITRANT_VOLUME_RANGE.getMin();
    private static final double X_MAX = TPConstants.TITRANT_VOLUME_RANGE.getMax();
    private static final double X_DELTA = TPConstants.TITRANT_VOLUME_DELTA;
    
    private static final String MODEL_NAME_STRONG_BASE = "strong base";
    private static final String MODEL_NAME_WEAK_BASE = "weak base";
    private static final String MODEL_NAME_STRONG_ACID = "strong acid";
    private static final String MODEL_NAME_WEAK_ACID = "weak acid";
    private static final String MODEL_NAME_DIPROTIC_ACID = "diprotic acid";
    private static final String MODEL_NAME_TRIPROTIC_ACID = "triprotic acid";
    private static final Object[] CHOICES = {
        MODEL_NAME_STRONG_BASE,
        MODEL_NAME_WEAK_BASE, 
        MODEL_NAME_STRONG_ACID,
        MODEL_NAME_WEAK_ACID,
        MODEL_NAME_DIPROTIC_ACID,
        MODEL_NAME_TRIPROTIC_ACID
    };
    
    private static class ConcentrationControl extends LogarithmicValueControl {
        public ConcentrationControl( String label ) {
            super( TPConstants.CONCENTRATION_RANGE.getMin(), TPConstants.CONCENTRATION_RANGE.getMax(), label, "0.0E0", CONCENTRATION_UNITS );
            setValue( TPConstants.CONCENTRATION_RANGE.getMin() );
        }
    }
    
    private static class KControl extends LogarithmicValueControl {
        public KControl( String label ) {
            super( TPConstants.K_RANGE.getMin(), TPConstants.K_RANGE.getMax(), label, "0.0E0", K_UNITS );
            setValue( TPConstants.K_RANGE.getMin() );
        }
    }
    
    private final TPChart chart;
    private final JComboBox modelComboBox;
    private final ConcentrationControl solutionConcentrationControl, titrantConcentrationControl;
    private final KControl k1Control, k2Control, k3Control;
    
    public TPControlPanel( TPChart chart ) {
        super();
        setBorder( new LineBorder( Color.BLACK, 1 ) );
        
        this.chart = chart;
        
        // model choice
        modelComboBox = new JComboBox( CHOICES );
        modelComboBox.addItemListener(  new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                    updateChart();
                }
            }
        });
        
        // solution concentration is constant
        JLabel solutionVolumeLabel = new JLabel( "solution: " + TPConstants.SOLUTION_VOLUME + " " + VOLUME_UNITS );
        solutionVolumeLabel.setBorder( new EmptyBorder( 0, 5, 0, 0 ) );
        
        // soution and titrant concentrations are variable 
        solutionConcentrationControl = new ConcentrationControl( "solution:" );
        solutionConcentrationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateChart();
            }
        });
        titrantConcentrationControl = new ConcentrationControl( "titrant:" );
        titrantConcentrationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateChart();
            }
        });
        
        // disassociation constants are variable
        k1Control = new KControl( "K1:" );
        k1Control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateChart();
            }
        });
        k2Control = new KControl( "K2:" );
        k1Control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateChart();
            }
        });
        k3Control = new KControl( "K3:" );
        k1Control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateChart();
            }
        });
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setAnchor( GridBagConstraints.WEST );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( modelComboBox, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( solutionVolumeLabel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( solutionConcentrationControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( titrantConcentrationControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( k1Control, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( k2Control, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( k3Control, row++, column );
        
        updateChart();
    }
    
    private void updateChart() {
        
        Object choice = modelComboBox.getSelectedItem();
       
        if ( choice == MODEL_NAME_STRONG_BASE ) {
            updateStrongBase();
         }
        else if ( choice == MODEL_NAME_WEAK_BASE ) {
           updateWeakBase();
        }
        else if ( choice == MODEL_NAME_STRONG_ACID ) {
            updateStrongAcid();
         }
        else if ( choice == MODEL_NAME_WEAK_ACID ) {
            updateWeakAcid();
        }
        else if ( choice == MODEL_NAME_DIPROTIC_ACID ) {
            updateDiproticAcid();
        }
        else if ( choice == MODEL_NAME_TRIPROTIC_ACID ) {
            updateTriproticAcid();
        }
    }
    
    private void updateStrongBase() {
        // controls
        k1Control.setEnabled( false );
        k2Control.setEnabled( false );
        k3Control.setEnabled( false );
        adjustKControls();
        // data points
        chart.clear();
        for ( double x = X_MIN; x <= X_MAX; x += X_DELTA ) {
            double Ca = titrantConcentrationControl.getValue();
            double Cb = solutionConcentrationControl.getValue();
            double Va = x;
            double Vb = TPConstants.SOLUTION_VOLUME;
            double y = TPModel.strongBase( Ca, Cb, Va, Vb );
            chart.addPoint( x, y);
        }
    }
    
    private void updateWeakBase() {
        // controls
        k1Control.setEnabled( true );
        k2Control.setEnabled( false );
        k3Control.setEnabled( false );
        adjustKControls();
        // data points
        chart.clear();
        for ( double x = X_MIN; x <= X_MAX; x += X_DELTA ) {
            double Ca = titrantConcentrationControl.getValue();
            double Cb = solutionConcentrationControl.getValue();
            double Va = x;
            double Vb = TPConstants.SOLUTION_VOLUME;
            double Ka = k1Control.getValue();
            double y = TPModel.weakBase( Ca, Cb, Va, Vb, Ka );
            chart.addPoint( x, y );
        }
    }
    
    private void updateStrongAcid() {
        // controls
        k1Control.setEnabled( false );
        k2Control.setEnabled( false );
        k3Control.setEnabled( false );
        adjustKControls();
        // data points
        chart.clear();
        for ( double x = X_MIN; x <= X_MAX; x += X_DELTA ) {
            double Ca = solutionConcentrationControl.getValue();
            double Cb = titrantConcentrationControl.getValue();
            double Va = TPConstants.SOLUTION_VOLUME;
            double Vb = x;
            double y = TPModel.strongAcid( Ca, Cb, Va, Vb );
            chart.addPoint( y, x );
        }
    }
    
    private void updateWeakAcid() {
        // controls
        k1Control.setEnabled( true );
        k2Control.setEnabled( false );
        k3Control.setEnabled( false );
        adjustKControls();
        // data points
        chart.clear();
        for ( double x = X_MIN; x <= X_MAX; x += X_DELTA ) {
            double Ca = solutionConcentrationControl.getValue();
            double Cb = titrantConcentrationControl.getValue();
            double Va = TPConstants.SOLUTION_VOLUME;
            double Vb = x;
            double Ka = k1Control.getValue();
            double y = TPModel.weakAcid( Ca, Cb, Va, Vb, Ka );
            chart.addPoint( x, y );
        }
    }
    
    private void updateDiproticAcid() {
        // controls
        k1Control.setEnabled( true );
        k2Control.setEnabled( true );
        k3Control.setEnabled( false );
        adjustKControls();
        // data points
        chart.clear();
        for ( double x = X_MIN; x <= X_MAX; x += X_DELTA ) {
            double Ca = solutionConcentrationControl.getValue();
            double Cb = titrantConcentrationControl.getValue();
            double Va = TPConstants.SOLUTION_VOLUME;
            double Vb = x;
            double Ka1 = k1Control.getValue();
            double Ka2 = k2Control.getValue();
            double y = TPModel.diproticAcid( Ca, Cb, Va, Vb, Ka1, Ka2 );
            chart.addPoint( y, x );
        }
    }
    
    private void updateTriproticAcid() {
        // controls
        k1Control.setEnabled( true );
        k2Control.setEnabled( true );
        k3Control.setEnabled( true );
        adjustKControls();
        // data points
        chart.clear();
        for ( double x = X_MIN; x <= X_MAX; x += X_DELTA ) {
            double Ca = solutionConcentrationControl.getValue();
            double Cb = titrantConcentrationControl.getValue();
            double Va = TPConstants.SOLUTION_VOLUME;
            double Vb = x;
            double Ka1 = k1Control.getValue();
            double Ka2 = k2Control.getValue();
            double Ka3 = k3Control.getValue();
            double y = TPModel.triproticAcid( Ca, Cb, Va, Vb, Ka1, Ka2, Ka3 );
            chart.addPoint( y, x );
        }
    }
    
    /*
     * Ensure that K3 <= K2 <= K1
     */
    private void adjustKControls() {
        //XXX
    }
}
