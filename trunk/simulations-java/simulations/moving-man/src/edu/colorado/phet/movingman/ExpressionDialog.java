package edu.colorado.phet.movingman;

import bsh.EvalError;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.movingman.model.ExpressionEvaluator;
import edu.colorado.phet.movingman.view.GoButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Sam Reid
 */
public class ExpressionDialog extends JDialog {
    protected final JTextField expressionTextField;
    private MovingManModule module;
    protected final JLabel errorLabel;
    protected JDialog helpDialog;

    public ExpressionDialog(PhetFrame frame, MovingManModule module) {
        super(frame, "Expression Evaluator");
        this.module = module;
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        {
            contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            contentPane.add(new JLabel("Enter an expression as a function of time t"));
            JPanel expressionPanel = new JPanel();
            {
                expressionPanel.add(new JLabel("x = "));
                expressionTextField = new JTextField("7 * sin(t) + 2", 14);
                expressionTextField.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setExpressionToModule();
                    }
                });
                expressionTextField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        setExpressionToModule();
                    }
                });
                expressionPanel.add(expressionTextField);
            }
            contentPane.add(expressionPanel);
            PhetPCanvas goButtonCanvas = new PhetPCanvas();//The go button is a PNode, so we must be embedded in a phetpcanvas 
            {
                GoButton goButton = new GoButton(module.recordAndPlaybackModel, module.getMovingManModel().getPositionMode());
                goButtonCanvas.addScreenChild(goButton);
                Dimension buttonDim = new Dimension((int) goButton.getFullBounds().getWidth(), (int) goButton.getFullBounds().getHeight());
                goButtonCanvas.setPreferredSize(buttonDim);
                goButtonCanvas.setMaximumSize(buttonDim);
                goButtonCanvas.setBorder(null);
                goButtonCanvas.setBackground(contentPane.getBackground());
            }
            contentPane.setFillNone();//Center the go button
            contentPane.add(goButtonCanvas);
            contentPane.add(Box.createVerticalStrut(40));

            JPanel errorAndHelpPanel = new JPanel();
            {
                errorLabel = new JLabel("Error");
                {
                    errorLabel.setFont(new PhetFont(PhetFont.getDefaultFontSize(), true));
                    errorLabel.setForeground(Color.red);
                    errorLabel.setVisible(false);
                }
                JButton helpButton = new JButton("Help");
                {
                    helpButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            showHelp();
                        }
                    });
                }
                errorAndHelpPanel.add(errorLabel);
                errorAndHelpPanel.add(helpButton);
            }
            contentPane.add(errorAndHelpPanel);
        }
        setContentPane(contentPane);
        pack();
        SwingUtils.centerDialogInParent(this);
    }

    private void showHelp() {
        if (helpDialog == null)
            helpDialog = createHelpDialog();
        helpDialog.setVisible(true);
    }

    private JDialog createHelpDialog() {
        JDialog helpDialog = new JDialog(this, "Help");
        {
            JPanel contentPane = new JPanel();
            {
                contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                JEditorPane helpArea = new JEditorPane("text/html", "<html>Here are some examples of valid expressions:<br>" +
                        " x = 3 * t <br>" +
                        " x = 2*sin(t)<br>" +
                        " x = pow(t,2)<br>" +
                        "<br>" +
                        "Note that multiplication must be specified<br>" +
                        "explicitly as in 3 * t, instead of implicitly as in 3t. </html>");
                helpArea.setEditable(false);
                contentPane.add(helpArea);
            }
            helpDialog.setContentPane(contentPane);
            helpDialog.pack();
            helpDialog.setLocation(getX(), getY() + getHeight());//todo: make sure this doesn't go offscreen
        }
        return helpDialog;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            setExpressionToModule();
        } else {
            module.setExpression(null);
        }
    }

    private void setExpressionToModule() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(expressionTextField.getText());
        try {
            evaluator.evaluate(0);
            evaluator.evaluate(1.0);
            errorLabel.setVisible(false);
            expressionTextField.setBorder(BorderFactory.createLineBorder(Color.gray));
        } catch (EvalError evalError) {
            evalError.printStackTrace();
            errorLabel.setVisible(true);
            expressionTextField.setBorder(BorderFactory.createLineBorder(Color.red));
        }
        module.setExpression(evaluator);
    }
}