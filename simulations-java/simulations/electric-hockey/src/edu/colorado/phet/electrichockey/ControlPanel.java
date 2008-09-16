package edu.colorado.phet.electrichockey;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ControlPanel extends JPanel {
    private ElectricHockeyApplication electricHockeyApplication;
    private JButton startBtn, resetBtn;
    private JButton clearBtn; //pauseBtn;
    private JCheckBox pauseChkBox;
    private JButton traceBtn;
    private boolean toggleTrace, showField;    //togglePause,
    private JCheckBox traceChkBox;
    private JCheckBox fieldGridChkBox;

    private JRadioButton radio0;
    private JRadioButton radio1;
    private JRadioButton radio2;
    private JRadioButton radio3;
    private JLabel difficultyLbl;

    private int nbrTries;
    private JLabel nbrTriesLbl;            //nbr of tries at present level
    private JLabel nbrChargesLbl;        //nbr of charges on the playing field

    private JLabel massLbl;
    private JTextField massText;
    private JSlider massSlider;

    public static final int LEVEL_0 = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;

    private int levelState;            //0, 1, 2, or 3 depending on BarrierState

    private ButtonGroup btnGroup;

    private JPanel panelTop;
    private JPanel panelLeft, panelMid, panelRight;
    private JPanel panelBottom;
    private JPanel panelBottomLeft, panelBottomRight;

    public ControlPanel( final ElectricHockeyApplication electricHockeyApplication ) {
        this.electricHockeyApplication = electricHockeyApplication;
        startBtn = new JButton( ElectricHockeyStrings.getString( "HockeyControlPanel.Start" ) );
        resetBtn = new JButton( ElectricHockeyStrings.getString( "HockeyControlPanel.Reset" ) );
        nbrTries = 0;
        nbrTriesLbl = new JLabel( ElectricHockeyStrings.getString( "HockeyControlPanel.Tries" ) + nbrTries );

        clearBtn = new JButton( ElectricHockeyStrings.getString( "HockeyControlPanel.Clear" ) );
        //pauseBtn = new JButton("Pause");
        pauseChkBox = new JCheckBox( ElectricHockeyStrings.getString( "HockeyControlPanel.Pause" ), false );
        pauseChkBox.setBackground( Color.yellow );
        //togglePause = true;

        // traceChkBox = new JCheckBox( "Trace ", false ); gmwb - extra space at end of Trace ?
        traceChkBox = new JCheckBox( ElectricHockeyStrings.getString( "HockeyControlPanel.Trace" ), false );
        traceChkBox.setBackground( Color.yellow );
        //traceBtn = new JButton("Trace On");
        toggleTrace = false;

        final JCheckBox positivePuck = new JCheckBox( ElectricHockeyStrings.getString( "HockeyControlPanel.PuckIsPositive" ), true );
        positivePuck.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean sel = positivePuck.isSelected();
                Charge c = electricHockeyApplication.getModel().getPuck();
                if( sel ) {
                    c.setSign( Charge.POSITIVE );
                }
                else {
                    c.setSign( Charge.NEGATIVE );
                }
                electricHockeyApplication.getPlayingField().repaint();
                electricHockeyApplication.getModel().updatePath();
                electricHockeyApplication.getModel().updateForceList();
            }
        } );
        positivePuck.setBackground( Color.yellow );

        fieldGridChkBox = new JCheckBox( ElectricHockeyStrings.getString( "HockeyControlPanel.Field" ), false );
        fieldGridChkBox.setBackground( Color.yellow );
        showField = false;

        final JCheckBox antialiasButton = new JCheckBox( "Antialias", electricHockeyApplication.getFieldGrid().isAntialias() );
        antialiasButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                electricHockeyApplication.getFieldGrid().setAntialias( antialiasButton.isSelected() );
            }
        } );
        antialiasButton.setBackground( Color.yellow );

        radio0 = new JRadioButton( ElectricHockeyStrings.getString( "HockeyControlPanel.Practice" ), true );
        radio1 = new JRadioButton( "1", false );
        radio2 = new JRadioButton( "2", false );
        radio3 = new JRadioButton( "3", false );

        difficultyLbl = new JLabel( ElectricHockeyStrings.getString( "HockeyControlPanel.Difficulty" ) );

        String str = ElectricHockeyStrings.getString( "HockeyControlPanel.Charges" ) +
                     electricHockeyApplication.getModel().getChargeListSize();

        nbrChargesLbl = new JLabel( str );
        nbrChargesLbl.setBackground( Color.green );

        levelState = LEVEL_0;

        radio0.setBackground( Color.green );
        radio1.setBackground( Color.green );
        radio2.setBackground( Color.green );
        radio3.setBackground( Color.green );

        btnGroup = new ButtonGroup();
        btnGroup.add( radio0 );
        btnGroup.add( radio1 );
        btnGroup.add( radio2 );
        btnGroup.add( radio3 );

        massLbl = new JLabel( ElectricHockeyStrings.getString( "HockeyControlPanel.Mass" ) );
        massLbl.setBackground( Color.green );
        // gmwb - leading space?
        // massText = new JTextField( " 25", 3 );
        // massSlider = new JSlider( 1, 100, 25 );
        int massInitValue = 25;
        massText = new JTextField( Integer.toString( massInitValue ), 3 );
        massSlider = new JSlider( 1, 100, massInitValue );
        massSlider.setMajorTickSpacing( 10 );
        massSlider.setMinorTickSpacing( 1 );
        massSlider.setBackground( Color.green );


        panelTop = new JPanel();
        panelLeft = new JPanel();
        panelMid = new JPanel();
        panelRight = new JPanel();
        panelLeft.setBackground( Color.yellow );
        panelMid.setBackground( Color.yellow );
        panelRight.setBackground( Color.yellow );

        panelBottom = new JPanel();
        panelBottom.setBackground( Color.green );
        panelBottomLeft = new JPanel();
        panelBottomRight = new JPanel();
        panelBottomLeft.setBackground( Color.green );
        panelBottomRight.setBackground( Color.green );

        startBtn.addActionListener( new StartBtnHandler() );
        resetBtn.addActionListener( new ResetBtnHandler() );
        //pauseBtn.addActionListener(new PauseBtnHandler());
        pauseChkBox.addActionListener( new PauseChkBoxHandler() );
        clearBtn.addActionListener( new ClearBtnHandler() );
        traceChkBox.addActionListener( new TraceChkBoxHandler() );
        fieldGridChkBox.addActionListener( new FieldGridChkBoxHandler() );
        massText.addActionListener( new MassTextListener() );
        massSlider.addChangeListener( new SliderHandler() );
        //traceBtn.addActionListener(new TraceBtnHandler());

        resetBtn.setEnabled( false );

        radio0.addItemListener( new LevelBtnHandler() );
        radio1.addItemListener( new LevelBtnHandler() );
        radio2.addItemListener( new LevelBtnHandler() );
        radio3.addItemListener( new LevelBtnHandler() );

        panelLeft.add( startBtn );
        panelLeft.add( resetBtn );
        panelLeft.add( nbrTriesLbl );

        //panelMid.add(pauseBtn);
        panelMid.add( pauseChkBox );
        panelMid.add( clearBtn );

        panelTop.setBackground( Color.yellow );
        panelRight.setBackground( Color.yellow );
        panelRight.add( positivePuck );
        panelRight.add( traceChkBox );
        panelRight.add( fieldGridChkBox );
        panelRight.add( antialiasButton );

        panelBottomLeft.add( radio0 );
        panelBottomLeft.add( difficultyLbl );
        panelBottomLeft.add( radio1 );
        panelBottomLeft.add( radio2 );
        panelBottomLeft.add( radio3 );

        panelBottomLeft.add( nbrChargesLbl );

        panelBottomRight.add( massLbl );
        panelBottomRight.add( massText );
        panelBottomRight.add( massSlider );

//        panelTop.setLayout( new GridLayout( 1, 3 ) );
        panelTop.setLayout( new FlowLayout() );

        panelTop.add( panelLeft );
        panelTop.add( panelMid );
        panelTop.add( panelRight );

        panelBottom.setLayout( new GridLayout( 1, 2 ) );
        panelBottom.add( panelBottomLeft );
        panelBottom.add( panelBottomRight );

        setLayout( new GridLayout( 2, 1 ) );
        add( panelTop );
        add( panelBottom );

    }

    private class StartBtnHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            if( aevt.getSource() == startBtn ) {
                prt( "Start button pushed" );
            }
            resetBtn.setEnabled( true );
            startBtn.setEnabled( false );
            electricHockeyApplication.getModel().startTimer();
        }
    }

    private class ResetBtnHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            if( aevt.getSource() == resetBtn ) {
                prt( "Reset button pushed" );
            }
            electricHockeyApplication.getModel().resetTimer();
            nbrTries += 1;
            setNbrTriesLbl();
            resetBtn.setEnabled( false );
            startBtn.setEnabled( true );
        }
    }


    private class PauseChkBoxHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            //if(aevt.getSource() == pauseBtn){prt("Pause button pushed.");}
            if( pauseChkBox.isSelected() ) {
                electricHockeyApplication.getModel().stopTimer();
                //togglePause = false;
                //pauseBtn.setText("Unpause");
                startBtn.setEnabled( false );
                resetBtn.setEnabled( false );
                clearBtn.setEnabled( false );
            }
            else {
                electricHockeyApplication.getModel().startTimer();
                //togglePause = true;
                //pauseBtn.setText("Pause");
                startBtn.setEnabled( true );
                resetBtn.setEnabled( true );
                clearBtn.setEnabled( true );
            }
        }
    }

    private class ClearBtnHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            //if(aevt.getSource() == clearBtn){prt("Clear button pushed.");}

            int listLength = electricHockeyApplication.getModel().getChargeListSize();
            prt( "ChargeList length = " + listLength );

            for( int i = ( listLength - 1 ); i >= 0; i-- ) {
                electricHockeyApplication.getModel().removeChargeAt( i );
            }
            electricHockeyApplication.getFieldGrid().updateGridForceArray();
            prt( "Cleared chargelist length = " + electricHockeyApplication.getModel().getChargeListSize() );

            electricHockeyApplication.getModel().stopTimer();
            electricHockeyApplication.getPlayingField().paintAgain();

        }
    }

    private class TraceChkBoxHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            if( aevt.getSource() == traceChkBox ) {
                //prt("CheckBox");
                if( traceChkBox.isSelected() ) {
                    toggleTrace = true;
                    electricHockeyApplication.getModel().setPathStarted( false );
                    electricHockeyApplication.getModel().getPath().reset();
                }
                else {
                    toggleTrace = false;
                }

            }
        }
    }

    private class FieldGridChkBoxHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            if( aevt.getSource() == fieldGridChkBox ) {
                if( fieldGridChkBox.isSelected() ) {
                    showField = true;
                    electricHockeyApplication.getPlayingField().paintAgain();
                }
                else {
                    showField = false;
                    electricHockeyApplication.getPlayingField().paintAgain();
                }
            }
        }
    }

    private class LevelBtnHandler implements ItemListener {
        public void itemStateChanged( ItemEvent ie ) {
            if( ie.getSource() == radio0 ) {
                //prt("Practice pushed");
                levelState = LEVEL_0;
            }
            else if( ie.getSource() == radio1 ) {
                //prt("Level 1 pushed");
                levelState = LEVEL_1;
            }
            else if( ie.getSource() == radio2 ) {
                //prt("Level 2 pushed");
                levelState = LEVEL_2;
            }
            else if( ie.getSource() == radio3 ) {
                //prt("Level 3 pushed");
                levelState = LEVEL_3;
            }
            nbrTries = 0;
            setNbrTriesLbl();
            electricHockeyApplication.getModel().setBarrierState( levelState );
            electricHockeyApplication.getPlayingField().paintAgain();
        }
    }

    private class MassTextListener implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            if( aevt.getSource() == massText ) {
                try {
                    double m = Double.parseDouble( massText.getText() );
                    electricHockeyApplication.getModel().setMass( m );
                    if( m >= 1.0 && m <= 99.0 ) {
                        massSlider.setValue( (int)m );
                    }
                    else if( m < 1.0 ) {
                        massSlider.setValue( 1 );
                    }
                    else if( m > 100.0 ) {
                        massSlider.setValue( 99 );
                    }

                }
                catch( NumberFormatException ne ) {
                    prt( "You must enter a number." );
                }
            }
        }
    }

    private class SliderHandler implements ChangeListener {
        public void stateChanged( ChangeEvent cevt ) {
            if( cevt.getSource() == massSlider ) {
                int m = (int)massSlider.getValue();
                electricHockeyApplication.getModel().setMass( m );
                massText.setText( new Integer( m ).toString() );
                //prt("Mass is " + hockeyModule.getModel().getMass());
            }
        }
    }

    public boolean getTraceState() {
        return toggleTrace;
    }

    public boolean getShowField() {
        return showField;
    }

    public int getLevelState() {
        return levelState;
    }

    public void setNbrTriesLbl() {
        nbrTriesLbl.setText( ElectricHockeyStrings.getString( "HockeyControlPanel.Tries" ) + new Integer( nbrTries ).toString() );
    }

    public void setNbrChargesLbl( int n ) {
        nbrChargesLbl.setText( ElectricHockeyStrings.getString( "HockeyControlPanel.Charges" ) + new Integer( n ).toString() );
    }

    public void prt( String str ) {
        System.out.println( str );
    }

}//end of public class
