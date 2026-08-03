import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;

public class OCRTest {

    public static void main(String[] args) {

        try {

            ITesseract tesseract = new Tesseract();

            tesseract.setDatapath("C:\\Users\\vinay.kumar.jamalpur\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata");
            tesseract.setLanguage("eng");


            String text = tesseract.doOCR(new File("C:\\Users\\vinay.kumar.jamalpur\\Downloads\\ocrtest.png"));

            System.out.println(text);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}