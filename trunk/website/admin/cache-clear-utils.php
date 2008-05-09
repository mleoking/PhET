<?php

    /**
     * Clear the cache, optionally printing information is it goes.
     *
     * @param string $base_directory name of the base directory to clear
     * @param bool $verbose true if result should be printed
     * @param string $print_header (optional) human readable header, such as "Simulations"
     */
    function cache_clear_explicit($base_directory, $verbose = true, $print_header = "") {
        // Print the header, if necessary
        if ($verbose) {
            print "<p>\n";

            if ($print_header != "") {
                print "  <strong>$print_header</strong><br />\n";
            }

            print "  Clearing caches in directory: $base_directory<br />\n";
        }

        // Get the cache directories to clear
        $clear_dirs = glob($base_directory.'cached-*');

        // Nothing to do?
        if (count($clear_dirs) == 0) {
            if ($verbose) {
                print "  No directories found\n";
                print "</p>\n";
            }

            return;
        }

        if ($verbose) {
            // Prepare to print the list of directories affected
            print "  <ul>\n";
        }

        foreach ($clear_dirs as $dir) {
            if ($verbose) {
                // Print the command used
                print "    <li>rm -rf $dir</li>\n";
            }

            // Do the deed
            exec("rm -rf $dir");
        }

        if ($verbose) {
            // print wrap-up info
            print "  </ul>\n";
            print "  Finished clearing caches in directory: $base_directory\n";
            print "</p>\n";

            // Flush the output buffer
            flush();
        }
    }

?>