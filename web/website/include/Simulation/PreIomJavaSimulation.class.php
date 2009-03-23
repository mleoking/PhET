<?php

class PreIomJavaSimulation extends JavaSimulation {
    private function getPreIomProjectFilename() {
        return self::sim_root."{$this->project_name}/{$this->sim_name}.jar";
    }

    public function getSize() {
        try {
            return parent::getSize();
        }
        catch (PhetSimException $e) {
            // Ignore the exception and keep on trucking
        }

        $filename = $this->getPreIomProjectFilename();
        if (!file_exists($filename)) {
            return 0;
            // Don't throw until file size checkers catch it
            //throw new PhetSimException("Cannot get size, project file '{$filename}' does not exist");
        }

        return (int) (filesize($filename) / 1024);
    }

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

    private function getPreIomLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        if (Locale::inst()->isDefault($locale)) {
            return self::sim_root."{$this->project_name}/{$this->sim_name}.jnlp";
        }
        else {
            return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jnlp";
        }
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        $filename = parent::getLaunchFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        $filename = $this->getPreIomLaunchFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        return '';
    }

    private function getPreIomDownloadFilename($locale = Locale::DEFAULT_LOCALE) {
        if (Locale::inst()->isDefault($locale)) {
            return self::sim_root."{$this->project_name}/{$this->sim_name}.jar";
        }
        else {
            return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jar";
        }
    }

    public function getDownloadFilename($locale = Locale::DEFAULT_LOCALE) {
        // Try straight post IOM
        $filename = parent::getDownloadFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        // Try pre iom
        $filename = $this->getPreIomDownloadFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        return '';
    }

    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE) {
        $filename = $this->getDownloadFilename($locale);
        if (!file_exists($filename)) {
            return '';
        }

        return SITE_ROOT."admin/get-run-offline.php?sim_id={$this->getId()}&locale={$locale}";
;
    }
}

?>