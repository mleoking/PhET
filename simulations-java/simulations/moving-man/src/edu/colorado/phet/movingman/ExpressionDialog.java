package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
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
        }
        setContentPane(contentPane);
        pack();
        SwingUtils.centerDialogInParent(this);
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
        module.setExpression(new ExpressionEvaluator(expressionTextField.getText()));
    }
}