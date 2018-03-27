package com.ocr.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class ImageLoader {

    private final String inputDir;

    ImageLoader(@Value("${input.dir}") String inputDir) {
        this.inputDir = inputDir;
    }

    public List<File> list() {
        java.io.FileFilter filter = file -> !file.isHidden() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"));
        File[] files = new File(inputDir).listFiles(filter);
        return Arrays.asList(files);
    }

}
