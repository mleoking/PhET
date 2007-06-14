/************************************************
    Copyright 2005 Jeff Chapman

    This file is part of BrowserLauncher2.

    BrowserLauncher2 is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BrowserLauncher2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BrowserLauncher2; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 ************************************************/
// $Id: BrowserLauncherErrorHandler.java,v 1.1 2005/02/03 00:58:23 jchapman0 Exp $
package edu.stanford.ejalbert.exceptionhandler;

/**
 * This is an interface to be used by the BrowserLauncherRunner for handling
 * exceptions. Applications should implement this interface to handle
 * exceptions in an application specific manner.
 *
 * @author Jeff Chapman
 */
public interface BrowserLauncherErrorHandler {

    /**
     * Takes an exception and does something with it. Usually the implementing
     * class will want to log the exception or display some information about
     * it to the user.
     *
     * @param ex Exception
     */
    public void handleException(Exception ex);
}
