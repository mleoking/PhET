package edu.colorado.phet.common.tests.uitest;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;

public class PlayfulLookAndFeel extends MetalLookAndFeel {
    Font font;
    Color foregroundColor;
    Color backgroundColor;
    private ArrayList def;

    public PlayfulLookAndFeel() {
        this( new Font( "Lucida Sans", 0, 26 ), Color.black, Color.yellow );
    }

    public PlayfulLookAndFeel( Font font, Color foregroundColor, Color backgroundColor ) {
        this.font = font;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        //        install();
    }

    String[] types = new String[]{
        "Button", "MenuItem", "Panel", "Dialog",
        "CheckBox", "RadioButton", "ComboBox",
        "Menu", "MenuItem", "MenuBar",
        "Slider", "Spinner", "Border", "TextField", "TextArea",
        "Label", "TextPane", "FormattedTextField", "List", "Spinner"
    };

    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource foreground = new ColorUIResource( foregroundColor );
        FontUIResource fontResource = new FontUIResource( font );
        InsetsUIResource insets = new InsetsUIResource( 2, 2, 2, 2 );

        def = new ArrayList();
        for( int i = 0; i < types.length; i++ ) {
            String type = types[i];
            add( type + ".font", fontResource );
            add( type + ".foreground", foreground );
            add( type + ".background", background );
            add( type + ".margin", insets );
            add( type + ".inactiveForeground", foreground );
            add( type + ".select", background );
        }

        UIDefaults.ProxyLazyValue val = new UIDefaults.ProxyLazyValue( "javax.swing.plaf.metal.MetalIconFactory",
                                                                       "getHorizontalSliderThumbIcon" );
        //        ImageIcon ii=new ImageIcon( getClass().getClassLoader().getResource( "images/components/webt/arrow_down_md_wht.gif"));
//        ImageIcon i2 = ( new ImageIcon( getClass().getClassLoader().getResource( "images/components/slidershadow.png" ) ) );
//        IconUIResource iconResource = new IconUIResource( i2 );
//        add( "Slider.horizontalThumbIcon", iconResource );

        //        MetalButtonUI buttonUI=new MetalButtonUI();
        add( "ButtonUI", MyButtonUI.class.getName() );
        add( "CheckBoxUI", MyCheckBoxUI.class.getName() );
        add( "SpinnerUI", MySpinnerUI.class.getName() );
        //"SliderUI", metalPackageName + "MetalSliderUI",
        add( "SliderUI", MySliderUI.class.getName() );
        add( "TitledBorder.font", fontResource );
        add( "TitledBorder.titleColor", foreground );
        add( "Spinner.arrowButtonSize", new Dimension( 40, 40 ) );

        add( "TextFieldUI", MyTextFieldUI.class.getName() );
        add( "PanelUI", MyPanelUI.class.getName() );

        //        "Spinner.arrowButtonSize", new Dimension(16, 5),

        Object[] defaults = def.toArray();
        table.putDefaults( defaults );
        Object out = table.get( "ButtonUI" );
        System.out.println( "out = " + out );
    }

    private void add( String s, Object o ) {
        def.add( s );
        def.add( o );
    }

}