
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

public class Phase1 {

    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("src/hide_text.png"));
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Height: " + height + " Width: " + width);
        WritableRaster raster = image.getRaster();
        int count = 31;
        long lengthOfData = 0;
        String data = "";
        int thisByte = 0;
        int countedBits = 1;

        System.out.println(lengthOfData(raster, width, height));
    }

    public static int lengthOfData(WritableRaster raster, int width, int height)
    {
        int count = 31;
        int length = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {

                int[] pixels = raster.getPixel(c, r, (int[]) null);
                for(int i = 0; i < 3; i++)
                {
                    if(count < 0)
                        return length;

                    int bitMask = (pixels[i] & 1) << count;
                    length = length | bitMask;
                    count--;
                }
            }
        }
        return -1;
    }
}
