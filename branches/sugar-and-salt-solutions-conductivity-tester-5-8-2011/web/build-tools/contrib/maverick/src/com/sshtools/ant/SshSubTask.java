
				/******************************************************************************
 * Copyright (c) 2003-2004 3SP Ltd. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code and
 * its use is subject to the terms of the 3SP Software License. You may not use
 * this file except in compliance with the license terms.
 *
 * You should have received a copy of the 3SP Software License along with this
 * software; see the file LICENSE.html.  If not, write to or contact:
 *
 * 3SP Ltd, No. 3 The Glade Business Center, Forum Road,
 * Nottingham, NG5 9RW, ENGLAND
 *
 * Email:     support@3sp.com
 * Telephone: +44 (0)115 962 4446
 * WWW:       http://3sp.com
 *****************************************************************************/

			
package com.sshtools.ant;

import org.apache.tools.ant.BuildException;

import com.maverick.ssh.SshClient;

public class SshSubTask {
    protected String taskString = "";
    protected Ssh parent;

    protected void setParent(Ssh parent) {
        this.parent = parent;
    }

    protected void log(String msg) {
        parent.log(msg);
    }

    protected void log(String msg, int i) {
        parent.log(msg, i);
    }

    public void execute(SshClient ssh) throws BuildException {
        throw new BuildException(
            "Shouldn't be able to instantiate an SshSubTask directly");
    }
}
