package edu.colorado.phet.cck.controls;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;

//import com.lowagie.text.DocumentException;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 1:05:36 AM
 */

public class OptionsMenu extends JMenu {
    public OptionsMenu( PhetApplication application, final ICCKModule cck ) {
        super( SimStrings.getInstance().getString( "OptionsMenu.Title" ) );
        setMnemonic( SimStrings.getInstance().getString( "OptionsMenu.TitleMnemonic" ).charAt( 0 ) );
//        cck.setFrame( application.getApplicationView().getPhetFrame() );
        add( new BackgroundColorMenuItem( application, cck ) );
        add( new ToolboxColorMenuItem( application, cck ) );
//        JMenuItem item = new JMenuItem( "Screenshot (PDF)" );
//        item.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                try {
//                    PDFUtil.exportPDF( new File( "C:\\Users\\Sam\\Desktop\\phet-pdf_out.pdf"),cck.getSimulationPanel() );
//                }
//                catch( DocumentException e1 ) {
//                    e1.printStackTrace();
//                }
//                catch( FileNotFoundException e1 ) {
//                    e1.printStackTrace();
//                }
//            }
//        } );
//        add( item );
    }
}
