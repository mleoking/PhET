package edu.colorado.phet.boundstates.test.schmidt_lee;

/*
    Copyright (C) 1998 Kevin E. Schmidt and Michael A. Lee

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

    We can be contacted at the addresses given in the Schroedinger.tex
    or other forms of the documentation.
*/
/**
Exception class used to indicate the the integrator could not
find an upper or a lower bound
*/
public class BoundException extends Exception {

    public BoundException() {
        super();
    }
    
    public BoundException( String message ) {
        super( message );
    }
}
