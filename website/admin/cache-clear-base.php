<?php

    //
    // Clear all the web page caches:
    //    the simulations, the teacher_ideas, and the base site
    //

    include_once("cache-clear-utils.php");

    function clear_cache_all() {
        // Print a nice XHTML header
        print <<<EOD
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>PhET :: Physics Education Technology at CU Boulder</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
</head>
<body>
EOD;

        // Clear all the caches
        cache_clear_explicit('..', true, 'Base website cache');

        // Print a nice XHTML footer
        print <<<EOD
</body>
</html>
EOD;
    }

    clear_cache_all();
?>