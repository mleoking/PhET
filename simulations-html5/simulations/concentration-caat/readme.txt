All of the sources are contained directly in src/ because when I tried adding sub directories, it failed to work under CocoonJS

Notes:
Cocoon-js would not run on android with caat.js in a contrib/ directory

to run httpd server, use
"C:\Program Files (x86)\Apache Software Foundation\Apache2.2\bin\httpd.exe"

Glitches when running with CAAT.ShapeActor, where some rendering gets copied in CocoonJS

Lots of interaciton problems on android.  When running in Browser in portrait, it works. but in landscape or chrome (landscape or portrait) it fails.  Seems to have to do with the location of the objects vertically,
maybe they are going out of some bounds.