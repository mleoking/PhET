The PhET Build GUI (PBG) is used for building, testing and deploying PhET simulations, it can be launched using the build.bat or build.sh shell scripts.

To avoid entering information manually, you can create a build-local.properties file in this directory with the following keys:

deploy.dev.username
deploy.dev.password
deploy.prod.username
deploy.prod.password
svn.username
svn.password
browser

#Flash
wine (boolean)
flash.exe
browser.exe  (TODO: consolidate with browser above or rename to flash-browser)
autobuild-swf (boolean)

Sam Reid
PhET
2009