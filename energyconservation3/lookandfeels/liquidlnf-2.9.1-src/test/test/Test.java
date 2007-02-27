/*
 * 创建日期 2006-2-10
 */
package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

/**
 * 程序员  ♂草龍
 * 好心情伴随每一天
 * 请登录: http://blog.hexun.com/xqsoft
 * 或者E-mail: xqmusic@msn.com
 */
public class Test {

    private JFrame frame;

    public static void main(String args[]) {
        try {            
            UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        Test window = new Test();
        window.frame.setVisible(true);
    }

    public Test() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.getContentPane().setLayout(null);
        frame.setBounds(100, 100, 500, 375);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(46, 45, 303, 242);
        frame.getContentPane().add(scrollPane);

        final JTextPane textPane = new JTextPane();
        scrollPane.setViewportView(textPane);

        final JLabel label = new JLabel();
        label.setBounds(59, 22, 110, 25);
        frame.getContentPane().add(label);
        label.setText("请输入中文：");

        final JButton button = new JButton();
        button.setBounds(370, 60, 90, 30);
        frame.getContentPane().add(button);
        button.setText("确定");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(new JPanel(),textPane.getText());
            }
        });
    }
}

