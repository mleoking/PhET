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

package org.dm.client.message;

import geom.jgv.util.HMatrix3D;

public class PrintMessage extends BaseMessage {

    public PrintMessage(String postscript, HMatrix3D O2W) {
        super();
        this.msgType = "print";
        this.content = this.getXMLHeader() + "<transform>\n";
        for (int i = 0; i < O2W.T.length; i++) {
            this.content += O2W.T[i] + " ";
        }
        this.content += "</transform>\n<postscript>\n" +
                        postscript +
                        "\n</postscript>\n" +
                        this.getXMLFooter();
    }
}