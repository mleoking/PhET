JADE - JAva Dynamics Engine
Release 0.6.1 alpha 2005-12-28
Readme.txt
Copyright 2005 Raymond Sheh
  A Java port of Flade - Flash Dynamics Engine, 
  Copyright 2004, 2005 Alec Cove

This file is part of JADE. The JAva Dynamics Engine. 

JADE is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

JADE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JADE; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


========================
        Welcome!
========================

Welcome to JADE, the JAva Dynamics Engine! JADE is currently a port 
of Flade, the Flash Dynamics Engine, by Alec Cove. JADE/Flade is a 2D 
physics simulator that simulates moving objects, linked together with 
constraints, that can collide with fixed objects in the world, under 
the influence of gravity, momentum and friction. 

The documentation for JADE is maintained in JavaDoc format, with the main 
documentation currently part of the DynamicsEngine class documentation. 
Please see ./docs/index.html and, in particular 
./docs/org/cove/jade/DynamicsEngine.html for more information. 


========================
      Quickstart
========================

To compile JADE, issue the following command. This assumes that 
the Java SDK has been set up on your system (tested with J2SE 1.4.2), 
that you have a blank classpath (or one with "." in it) and that 
you're in the jade_0.6.1_a directory (ie. you can see the directory 
"org"). This also assumes you're pretty familiar with Java to begin 
with. 

"javac -d . @srcfiles.txt"

To compile the car example, issue the following command. 

"javac -classpath . CarExample.java"

Then you can run it with the following command. 

"java CarExample"

To (re)make the documentation, issue the following command. 

"javadoc -d docs @srcfiles.txt"


I was going to do up a makefile but that means more stuffing around 
for Windows users. Maybe later :-)


========================
        Changes
========================

Please see changes.txt and ./docs/org/cove/jade/DynamicsEngine.html 
for the JADE revision history and other information. 


========================
        Contact
========================

The maintainers can be contacted at their respective websites. 

JADE, The JAva Dynamics Engine, is maintained by Raymond Sheh
http://rsheh.web.cse.unsw.edu.au/jade

Flade, The Flash Dynamics Engine, is maintained by Alec Cove
http://www.cove.org/flade


2005-12-28
