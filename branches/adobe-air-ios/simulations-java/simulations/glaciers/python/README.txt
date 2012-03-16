--
Python code, written by Archie Paulson to test the model.
This is a "Hollywood" model that approximates published data.
This is the model used in the glaciers simulation.

FILES:
Climate.py - climate model
HollywoodGlacier.py - glacier model
Mountain.py - valley model
lib.py - ancillary code that is imported by other files
run_hollywood.py - run script

DEPENDENCIES:
- Numerical libs are imported from pylab, which is part of http://matplotlib.sourceforge.net.
- ipython is useful (but not required) for interactively running tests

USAGE:
(1) enable one or more of the tests at the bottom of model.py
(2) run "python model.py" or "ipython -pylab model.py" from a command line shell

NOTES:
- If you're not using ipython, you'll need to edit model.py to enable test code
- plots won't display unless you add a show() call to model.py, or type "show()" in ipython

MAC OS X INSTALLATION STEPS:
(1) set up local files
  a. mkdir ~/bin
  b. add ~/bin to PATH in .bashrc or similar
  c. mkdir -p ~/Library/Python/2.5/site-packages
  d. create ~/.pydistutils.cfg and put this in it:
[install]
install_lib = ~/Library/Python/$py_version_short/site-packages
install_scripts = ~/bin
(2) install easy_install
  a. download ez_setpup.py from http://peak.telecommunity.com
  b. run "python ez_setup.py"
(3) install required packages
  a. download matplotlib egg from http://matplotlib.sourceforge.net
  b. run "easy_install <filename>.egg"
(4) install ipython
  a. download readline OS X egg from http://ipython.scipy.org
  b. run "python <filename>.egg"
  c. download ipython tgz file from http://ipython.scipy.org
  d. install ipython:
tar -xvzf ipython-0.7.3.tar.gz
cd ipython-0.7.3
python setup.py build
sudo python setup.py install
(5) test
  a. test ipython by running "ipython model.py"

--