/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid.skin;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
//import java.net.URL;

public class SecretLoader
{
    
    // previously was a JPanel, changed this 2003-5-22
    // because of the PanelUI-not-found-bug.
    static Component component = new Label();
    static byte buffer[]=new byte[4096];
    
    static Image loadImage(String fileName)
    {
        //URL url = SecretLoader.class.getResource("/com/birosoft/liquid/icons/" + fileName);
        byte[] byteArray=null;
        
        try
        {
            InputStream fis = SecretLoader.class.getResourceAsStream("/com/birosoft/liquid/icons/" + fileName);
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            
            //fis = url.openStream();
            
            int read=fis.read(buffer);
            while (read!=-1)
            {
                bos.write(buffer,0,read);
                read=fis.read(buffer);
            }
            
            byteArray=bos.toByteArray();
            read=fis.read(byteArray);
            
            Image img = java.awt.Toolkit.getDefaultToolkit().createImage(byteArray,0,byteArray.length);
            
            MediaTracker tracker = new MediaTracker(component);
            tracker.addImage(img, 0);
            try
            {
                tracker.waitForID(0);
            } catch (InterruptedException ignore)
            {}
            
            return img;
        } catch (Throwable t)
        {
            throw new IllegalArgumentException("File " + fileName + " could not be loaded.");
        }
    }
}
