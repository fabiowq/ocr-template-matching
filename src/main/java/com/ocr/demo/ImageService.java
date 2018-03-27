package com.ocr.demo;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageLoader imageLoader;
    private final TextService textService;
    private final TemplateService templateService;
    private final String outputDir;

    ImageService(
            ImageLoader imageLoader,
            TextService textService,
            TemplateService templateService,
            @Value("${output.dir}") String outputDir) {
        this.imageLoader = imageLoader;
        this.textService = textService;
        this.templateService = templateService;
        this.outputDir = outputDir;
    }

    public Mat grayScale(Mat image) {
        Mat grayMat = new Mat();
        Imgproc.cvtColor(image, grayMat, Imgproc.COLOR_RGB2GRAY);
        //Imgproc.adaptiveThreshold(grayMat, grayMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        return grayMat;
    }

    public void process() {
        imageLoader.list().stream().forEach(
            imageFile -> {
                LOGGER.info("Image={}", imageFile.getAbsolutePath());
                Mat imageMat = grayScale(Imgcodecs.imread(imageFile.getAbsolutePath()));
                String filename = FilenameUtils.concat(
                        outputDir,
                        FilenameUtils.getBaseName(imageFile.getName()) + "_" + System.currentTimeMillis() + "_%s" + ".png"
                );

                String name = extractText(
                        String.format(filename, "name"),
                        imageMat,
                        grayScale(Imgcodecs.imread("templates/cnh_name.png")),
                        Imgproc.TM_SQDIFF, 0, 15, -50, 25);
                LOGGER.info("Name={}", name);

                String rg = extractText(
                        String.format(filename, "rg"),
                        imageMat,
                        grayScale(Imgcodecs.imread("templates/cnh_rg.png")),
                        Imgproc.TM_SQDIFF, 10, 20, -230, 15);
                LOGGER.info("RG={}", rg.replaceAll(" ", ""));

                String cpf = extractText(
                        String.format(filename, "cpf"),
                        imageMat,
                        grayScale(Imgcodecs.imread("templates/cnh_cpf.png")),
                        Imgproc.TM_SQDIFF, 5, 20, 0, 15);
                LOGGER.info("CPF={}", cpf.replaceAll("[^0-9\\.\\-]", ""));

                String dob = extractText(
                        String.format(filename, "dob"),
                        imageMat,
                        grayScale(Imgcodecs.imread("templates/cnh_dob.png")),
                        Imgproc.TM_SQDIFF, 5, 20, 0, 10);
                LOGGER.info("DoB={}", dob);

                String parents = extractText(
                        String.format(filename, "parents"),
                        imageMat,
                        grayScale(Imgcodecs.imread("templates/cnh_parents.png")),
                        Imgproc.TM_SQDIFF, 10, 30, -20, 120);
                LOGGER.info("Parents={}", parents);

                String doe = extractText(
                        String.format(filename, "doe"),
                        imageMat,
                        grayScale(Imgcodecs.imread("templates/cnh_doe.png")),
                        Imgproc.TM_SQDIFF, 5, 20, 0, 10);
                LOGGER.info("DoE={}", doe);

            }
        );
    }

    private String extractText(String resultFileName, Mat imageMat, Mat templateMat, int matchMethod, int x, int y, int width, int height) {
        Mat imageMatched = templateService.match(imageMat, templateMat, Imgproc.TM_SQDIFF, x, y, width, height);
        Imgcodecs.imwrite(resultFileName, imageMatched, new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION));
        return textService.extract(resultFileName);
    }

}
