package com.ocr.demo;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.IntBuffer;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;

@Component
public class TextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextService.class);

    public String extract(String imagePath) {
        TessBaseAPI tesseractAPI = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (tesseractAPI.Init(".", "eng") != 0) {
            RuntimeException e = new RuntimeException("Could not initialize tesseract");
            LOGGER.error("Error initializing tesseract", e);
            throw e;
        }
        // Open input image with leptonica library
        lept.PIX image = pixRead(imagePath);
        tesseractAPI.SetImage(image);

//        BOXA boxes = tesseractAPI.GetComponentImages(RIL_TEXTLINE, true, (PIXA) null, (IntBuffer) null);
//
//        System.out.printf("Found %d textline image components.%n", boxes.n());
//
//        for (int i = 0; i < boxes.n(); i++) {
//            BOX box = boxaGetBox(boxes, i, L_CLONE);
//            tesseractAPI.SetRectangle(box.x(), box.y(), box.w(), box.h());
//            BytePointer ocrResult = tesseractAPI.GetUTF8Text();
//            String ocrText = ocrResult.getString().trim();
//            ocrResult.deallocate();
//            int conf = tesseractAPI.MeanTextConf();
//            System.out.printf("Box[%d]: x=%d, y=%d, w=%d, h=%d, confidence: %d, text: %s%n", i, box.x(), box.y(), box.w(), box.h(), conf, ocrText);
//        }

        // Get OCR result
        BytePointer outText = tesseractAPI.GetUTF8Text();

        String string = outText.getString().trim();
        //LOGGER.info("OCR output:\n{}", string);

        // Destroy used object and release memory
        tesseractAPI.End();
        tesseractAPI.close();
        outText.deallocate();
        pixDestroy(image);

        return string;
    }


}
