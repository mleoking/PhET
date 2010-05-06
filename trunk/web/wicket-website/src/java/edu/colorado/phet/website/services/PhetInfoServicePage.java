package edu.colorado.phet.website.services;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;
import edu.colorado.phet.website.util.PhetRequestCycle;


public class PhetInfoServicePage extends WebPage {

    private static Logger logger = Logger.getLogger( PhetInfoServicePage.class.getName() );

    public PhetInfoServicePage(  ) {
        super(  );

        add( new Label( "response", "test" ) );

        HttpServletRequest request = ((PhetRequestCycle) getRequestCycle()).getWebRequest().getHttpServletRequest();
        String rawData = (String) request.getAttribute( "raw-data" );

        logger.warn( "rawData: " + rawData );

        try {
            Document document = XMLUtils.toDocument( rawData );

            //NodeList strings = document.getElementsByTagName( "string" );
        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }


    }
}
