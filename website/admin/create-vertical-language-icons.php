<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    $path_to_images = SITE_ROOT."images/languages/";

    foreach (glob($path_to_images."*.png") as $image) {
        $basename = basename($image);

        if (strpos($basename, "vertical") !== 0) {
            $vertical_icon_location = dirname($image)."/vertical-$basename";

            print "Rotating image '$image' to '$vertical_icon_location' <br/>";

            $source = imagecreatefrompng($image);

            if (!$source) { /* See if it failed */
                $source  = imagecreatetruecolor(150, 30); /* Create a blank image */
                $bgc     = imagecolorallocate($source, 255, 255, 255);
                $tc      = imagecolorallocate($source, 0, 0, 0);
                imagefilledrectangle($source, 0, 0, 150, 30, $bgc);
                /* Output an errmsg */
                imagestring($source, 1, 5, 5, "Error loading $image", $tc);
            }

            $rotate = imagerotate($source, 90, 0);

            imagepng($rotate, $vertical_icon_location, 9);
        }
    }

?>