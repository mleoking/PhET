<?php

    foreach (glob('../*/cached-*') as $dir) {
        print "Deleting $dir<br/>";

        exec("rm -rf $dir");
        flush();
    }

?>