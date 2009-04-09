<?php

    // This file updates the include_path when it is included.
    //
    // There is a long explaination about what is going on,
    // the code that actually executes is tiny and located
    // nearly at the end of the file
    //
    // RANT: Include paths with PHP just KILL me!
    // I want a way to have all web script files include
    // in a generic way, relative to the site's root.
    //
    // Someone has GOT to have solved this problem with
    // this language, but I have yet to see any examlpes.
    // Am I thinking about this in some weird fashion?
    //
    // Lets look at what won't work so I'll remember NOT
    // to keep trying the same wrong broken attempts at
    // a solution more than once...
    //
    // Altering the include_path in the php.ini file is
    // not an option.  Why?  It is a global setting and does
    // not work on a webserver with multilpe sites, like
    // my dev machine, without doing chroot craziness.
    // The production website DOES work in a chroot
    // envirnoment, but what if we moved it?  Everything
    // whould have to change.  Grr!
    //
    // Using a .htaccess file is not on option.  Why?
    // Where PhET is currently hosted, this is not
    // supported.  Grr!
    //
    // How about setting something up in Apache's conifg?
    // Nope, variation of the first, we'd be locked into
    // having to modify the apache config if anything
    // changed and it forces any developer to set up
    // multiple configurations to get it to run locally.
    // Grr!
    //
    // How about looking at some $_SERVER and $GLOBAL vars
    // and some predefined constants like __FILE__?
    // Nope, all these variables are different depending
    // on if you want to access it via the web or CLI,
    // AND it is different depending on which OS it is
    // running on, AND how PHP was compiled, AND which
    // version of PHP is being used.  Too liable
    // for breakage, too much forcing developers into
    // one solution. Grr!
    //
    // What about $_SERVER['DOCUMENT_ROOT']?  What if
    // the website is not run from there, like it isn't
    // on my production machine?  Nope, not an option.
    // Grr!
    //
    // Well maybe you can put something at the top of
    // every PHP file to alter the include_path at run-
    // time?  Sure, but you'd have to check to make sure
    // you don't do it more than once which makes the code
    // span several lines.  And what if you forget a file?
    // Or make a mistake in one file?  Or have to make
    // a change, which would then affect every file?
    // Ugly, messy, nightmarish maintenance, etc, grr!
    //
    // Well maybe you can put something at the top of
    // every PHP file to alter the include_path at run-
    // time?  Sure, but you'd have to check to make sure
    // you don't do it more than once which makes the code
    // span several lines.  And what if you forget a file?
    // Or make a mistake in one file?  Or have to make
    // a change, which would then affect every file?
    // Ugly, messy, nightmarish maintenance, etc, grr!
    //
    // So here's my humble solution, let me know if you
    // canfigure out something better.
    //
    // This 'global.php' file should be included everywhere
    // (with a few exceptions).  Most all files are 1
    // directory deep.  So hardcode setting the include_path
    // to access the root dir 1 directory up, and do it
    // here so there isn't a lot of code duplication.
    //
    // This 'global.php' file will have to be included
    // with this ugly mess, the key is the absolute path:
    // include_once(dirname(dirname(__FILE__))."/include/global.php");
    //
    // Trying to do a relative "../include/global.php"
    // won't work.  Depending on how you execute it, it
    // will try to include relative to any one from a
    // list of several possibilities.  One of which that
    // is particularly relevant to me is when the CLI
    // script is outside of the site root, because it
    // will look realitve to where the script is called
    // from.  NOTE: NOT where the script is located on
    // the filesystem!!! This becomes important with the
    // few unit tests that I have.
    //
    // Any file that ISN'T one directory deep should
    // set the include path as appropriate mostly just
    // copy the code below.  This is less than 1% of all
    // files, at the time of this writing.
    //
    // I _*HATE*_ PHP includes.  >:(
    if (!defined("INCLUDE_PATH_SET")) {
        set_include_path(get_include_path() . PATH_SEPARATOR . dirname(dirname(__FILE__)));
        define("INCLUDE_PATH_SET", "true");
    }

?>