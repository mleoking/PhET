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

package org.dm.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AcceptMessageServlet extends HttpServlet {

  public AcceptMessageServlet() {
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws java.io.IOException {
    String msgType = request.getParameter("msgType");
    String content = request.getParameter("content");

    // Print messages from the applet to the console
    System.out.println("Dumping message to server console....");
    System.out.println("===================================================================");
    System.out.println("Message type is: " + msgType);
    System.out.println("===================================================================");
    System.out.println("Content is:\n" + content);
    System.out.println("===================================================================\n\n");
    if ("action".equals(msgType)) {
      // If this was a server action, send back the following action for the applet to perform:
      PrintWriter out = response.getWriter();
      String loadGeomAction = "<action type=\"load-geom\">generate.xml</action>";
      System.out.println("Sending back: " + loadGeomAction );
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      out.println(loadGeomAction);
    }
  }

}