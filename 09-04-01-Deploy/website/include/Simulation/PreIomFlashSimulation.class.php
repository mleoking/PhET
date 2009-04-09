<?php


class PreIomFlashSimulation extends FlashSimulation {
    public function getThumbnailFilename() {
        $thumbnail_filename = parent::getThumbnailFilename();
        if (file_exists($thumbnail_filename)) {
            return $thumbnail_filename;
        }

        // TODO: too much in 1 function, refactor
        // TODO: abstract the file_name_hash, there is a naming convention between this and a 'resounce name'
        $image_url = $this->getScreenshotUrl();
        $file_name_hash = md5($image_url).'.jpg';
        if (cache_has_valid_page(self::thumbnail_cache, $file_name_hash, self::thumbnail_cache_lifespan)) {
            return cache_get_file_location(self::thumbnail_cache, $file_name_hash);
        }

        // Load image -- assume image is in png format (for now):
        @$img = imagecreatefrompng($image_url);
        if ($img == false) {
            return;
        }

        $existing_width  = imagesx($img);
        $existing_height = imagesy($img);

        // Scale to thumbnail width, preserving aspect ratio:
        $new_width  = self::thumbnail_width;
        $new_height = self::thumbnail_height;
        //old method is proportional: floor($existing_height * ( thumbnail_width / $existing_width ));

        $tmp_img = imagecreatetruecolor($new_width, $new_height);

        imagecopyresampled($tmp_img, $img, 0, 0, 0, 0, $new_width, $new_height, $existing_width, $existing_height);

        $temp_file = tempnam('/tmp', 'sim-thumbnail');

        // Output image to cached image location:
        imagejpeg($tmp_img, $temp_file);

        cache_put(self::thumbnail_cache, $file_name_hash, file_get_contents($temp_file));

        unlink($temp_file);

        return cache_get_file_location(self::thumbnail_cache, $file_name_hash);
    }

    public function getVersion() {
        // PreIOM sims have all blank version info
        $version = parent::getVersion();

        // If the version is greater than 2, it is post-IOM
        if (!empty($version['major']) && ($version['major'] >= 2)) {
            return $version;
        }

        // Otherwise, clear the sim version so it will be ignored
        foreach ($version as $key => $value) {
            $version[$key] = '';
        }

        return $version;
    }
}

?>