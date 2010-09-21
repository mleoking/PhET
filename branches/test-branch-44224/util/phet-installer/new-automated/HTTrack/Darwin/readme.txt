This built version of httrack runs only on Mac and requires the following libraries to be located in the following locations:

    libhttrack.1.dylib              /sw/lib/
    dyld                            /usr/lib/
    libSystem.B.dylib               /usr/lib/
    libgcc_s.1.dylib                /usr/lib/
    
Another way to get a working copy of httrack on the Mac is to use 'Fink':

   'fink install httrack'
   
Be sure to configure Fink ('fink configure') to look in unstable branches.