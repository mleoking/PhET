//
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
// @author Daeron Meyer
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package org.dm.client;

import org.dm.client.message.BaseMessage;
import java.io.*;
import java.net.*;

public class HttpClient {

    private URL serverURL = null;

    public HttpClient(String serverURL) {

        try {
            this.serverURL = new URL(serverURL);
        } catch (MalformedURLException me) {
            System.out.println("Bad Server URL: " + serverURL);
        }
    }

    public synchronized String sendMessage(BaseMessage msg) {

        URLConnection urlConn;
        DataOutputStream    printout;
        BufferedReader      input;
        String retVal = "";

        System.out.println("Sending Message\n");
        if (serverURL != null) {
            try {

                // URL connection channel.
                urlConn = serverURL.openConnection();
                // Let the run-time system (RTS) know that we want input.
                urlConn.setDoInput (true);
                // Let the RTS know that we want to do output.
                urlConn.setDoOutput (true);
                // No caching, we want the real thing.
                urlConn.setUseCaches (false);
                // Specify the content type.
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                // Send POST output.
                printout = new DataOutputStream (urlConn.getOutputStream ());
                String content = "msgType=" + URLEncoder.encode(msg.getMsgType()) + "&" +
                                 "content=" + URLEncoder.encode(msg.getContent());
                System.out.println(msg.getContent());
                printout.writeBytes (content);
                printout.flush ();
                printout.close ();
                // Get response data.
                input = new BufferedReader(new InputStreamReader(urlConn.getInputStream ()));

                String str;
                while (null != ((str = input.readLine()))) {
                    // System.out.println (str);
                  retVal += str + "\n";
                }
                input.close ();

            } catch (IOException ie) {
            }
        }
        System.out.println("Got back from server:\n" + retVal);
        return retVal;
    }
}