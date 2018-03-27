package com.ocr.demo;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class TemplateService {

    public Mat match(Mat img, Mat template, int matchMethod, int x, int y, int width, int height) {

        Mat result = new Mat();

        int result_cols =  img.cols() - template.cols() + 1;
        int result_rows = img.rows() - template.rows() + 1;

        result.create(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, template, result, matchMethod);

        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat() );

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        /// For SQDIFF and SQDIFF_NORMED, the best matches are lower values.
        //  For all the other methods, the higher the better
        Point matchLoc;
        if (matchMethod  == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        Rect rectCrop = new Rect((int) matchLoc.x + x, (int) matchLoc.y + y, template.cols() + width, template.rows() + height);
        Mat croppedImage = new Mat(img, rectCrop);

        return croppedImage;

    }

}
