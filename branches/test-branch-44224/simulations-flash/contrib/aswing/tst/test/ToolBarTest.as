/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.BorderLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JFrame;
import org.aswing.JRadioButton;
import org.aswing.JToggleButton;
import org.aswing.JToolBar;

import test.ColorIcon;

/**
 *
 * @author iiley
 */
class test.ToolBarTest extends JFrame {
    
    private var toolBar:JToolBar;
    
    
    private var jbutton1:JButton;
    private var toggleButton1:JToggleButton;
    private var radioButton1:JRadioButton;
    private var radioButton2:JRadioButton;
    private var radioButton3:JRadioButton;
    private var checkBox1:JCheckBox;
    private var checkBox2:JCheckBox;
    private var checkBox3:JCheckBox;
    
    public function ToolBarTest() {
        super("ToolBarTest");
        
        toolBar = new JToolBar("", JToolBar.HORIZONTAL);
        toolBar.setFloatable(true);
        toolBar.append(this.getJButton1());
        var colorButton:JButton = new JButton(new ColorIcon(null, ASColor.RED, 12, 12));
        colorButton.setMargin(new Insets(4, 4, 4, 4));
        toolBar.append(colorButton);
        toolBar.append(this.getRadioButton1());
        //toolBar.append(this.getRadioButton2());
        //toolBar.append(this.getRadioButton3());
        toolBar.append(this.getCheckBox1());
        //toolBar.append(this.getCheckBox2());
        //toolBar.append(this.getCheckBox3());
        toolBar.append(this.getToggleButton1());
        
        this.getContentPane().append(toolBar, BorderLayout.NORTH);
    }


    public function getJButton1():JButton{
        if(jbutton1 == null){
            jbutton1 = new JButton("Create A New JFrame");
            //jbutton1.addEventListener(JButton.ON_RELEASE, _jbutton1Released, this);
        }
        return jbutton1;
    }
    public function getToggleButton1():JToggleButton{
        if(toggleButton1 == null){
            toggleButton1 = new JToggleButton("JToggleButton1");
            //toggleButton1.addEventListener(JButton.ON_RELEASE, _jtoggleButton1Released, this);
        }
        return toggleButton1;
    }
    public function getRadioButton1():JRadioButton{
        if(radioButton1 == null){
            radioButton1 = new JRadioButton("JRadioButton1");
        }
        return radioButton1;
    }
    public function getRadioButton2():JRadioButton{
        if(radioButton2 == null){
            radioButton2 = new JRadioButton("JRadioButton2");
        }
        return radioButton2;
    }
    public function getRadioButton3():JRadioButton{
        if(radioButton3 == null){
            radioButton3 = new JRadioButton("JRadioButton3");
        }
        return radioButton3;
    }
    public function getCheckBox1():JCheckBox{
        if(checkBox1 == null){
            checkBox1 = new JCheckBox("JCheckBox1");
        }
        return checkBox1;
    }
    public function getCheckBox2():JCheckBox{
        if(checkBox2 == null){
            checkBox2 = new JCheckBox("JCheckBox2");
        }
        return checkBox2;
    }
    public function getCheckBox3():JCheckBox{
        if(checkBox3 == null){
            checkBox3 = new JCheckBox("JCheckBox3");
        }
        return checkBox3;
    }
    
    public static function main():Void{
        Stage.scaleMode = "noScale";
        Stage.align = "T";
        try{
            trace("try ToolBarTest");
            
            var p:ToolBarTest = new ToolBarTest();
            p.setLocation(50, 50);
            p.setSize(800, 400);
            p.show();
            trace("done ToolBarTest");
        }catch(e){
            trace("error : " + e);
        }
    }
}
