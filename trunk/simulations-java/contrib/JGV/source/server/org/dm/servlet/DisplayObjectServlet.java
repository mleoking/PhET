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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DisplayObjectServlet extends HttpServlet {

  public DisplayObjectServlet() {
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    // Get the object name as a parameter
    String objectName = request.getParameter("object");
    // Are we just viewing or can we edit the object? (true|false)
    String editor = request.getParameter("editor");
    if (!"true".equals(editor)) {
      editor = "false";
    }
    // Construct a server URL relative to this servlet
    String serverURL = request.getRequestURL().toString() + "/../server";
    if (objectName != null) {
      displayObject(out, objectName, editor, serverURL);
    } else {
      displayError(out);
    }


  }

  void displayObject(PrintWriter out,
                     String objectName,
                     String editor,
                     String serverURL) throws IOException {
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Display Object " + objectName + "</title>");
    out.println("</head>");
    out.println("<body><table><tr><td>");
    out.println("<applet code=\"geom.jgv.JGVApplet\"");
    out.println("archive=\"JGV.jar\"");
    out.println("width=400 height=400>");
    out.println("<param name=\"background\" value=\"0x749ece\">");
    out.println("<param name=\"xmlfile\" value=\"" + objectName + "\">");
    out.println("<param name=\"server\" value=\"" + serverURL + "\">");
    out.println("<param name=\"editor\" value=\"" + editor + "\">");
    out.println("</applet>");
    out.println("</td><td>&nbsp;</td><td valign=\"top\">");
    out.println("Instructions:<br>");
    out.println("Click in the applet and try the following key commands:<br>");
    out.println("'R' (Rotate)<br>");
    out.println("'T' (Translate)<br>");
    out.println("'S' (Scale)<br>");
    out.println("'H' (Home/Reset)<br>");
    out.println("'P' (Print - Send print message to server)<br><br>");
    out.println("<a href=\"" + objectName + "\">Object XML Representation</a>");
    out.println("</td></tr></table>");
    out.println("</body>");
    out.println("</html>");
  }

  void displayError(PrintWriter out) {
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Error</title>");
    out.println("<h2>Error</h2><br>");
    out.println("An object name must be specified in the \"object\" parameter to this servlet");
    out.println("</body>");
    out.println("</html>");
  }

}