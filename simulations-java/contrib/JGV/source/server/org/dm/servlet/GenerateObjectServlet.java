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

import java.util.Calendar;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateObjectServlet extends HttpServlet {

  public GenerateObjectServlet() {
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String content = "";
    String date = Calendar.getInstance().getTime().toString();
    content += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    content += "<geom>\n";
    content += "  <geom-annotation on=\"nothing\" x=\"0\" y=\"0\" z=\"0\" r=\"1\" g=\"1\" b=\"1\">\n";
    content += "    <note>Server Action Test: " + date + "</note>\n";
    content += "  </geom-annotation>\n";
    content += "</geom>\n";

    response.setContentLength(content.length());
    PrintWriter out = response.getWriter();
    out.println(content);
    out.flush();
  }
}