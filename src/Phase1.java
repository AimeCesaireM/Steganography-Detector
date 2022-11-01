
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
        int length = lengthOfData(raster, width, height);
        System.out.println(length);
        String data = getData(length, raster, width, height);
        System.out.println(data);
        //testCode(raster, width, height);

    }

    public static void testCode(WritableRaster raster, int width, int height)
    {

        int count = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] pixels = raster.getPixel(c, r, (int[]) null);
                if (count < 32){
                    System.out.print((pixels[0] & 1) + "" +  (pixels[1] & 1) + "" +  (pixels[2] & 1));
                    count ++;
                }
            }
        }
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

    public static String getData(int length, WritableRaster raster,  int width, int height)
    {
        String data = "";
        int r = 0;
        int c = 10;
        int[] pixels = raster.getPixel(c, r, (int[]) null);
        int thisByte =  (pixels[2] & 1) << 7;
        long countedBits = 2;
        c++;
        for (; r < height; r++) {
            for (; c < width; c++) {

                pixels = raster.getPixel(c, r, (int[]) null);
                for(int i = 0; i < 3; i++)
                {
                    if(countedBits > 8L * length)
                        return data;

                    int bitMask = (pixels[i] & 1) << ((8 - (countedBits % 8)) %8);
                    thisByte = thisByte | bitMask;
                    if (countedBits % 8 == 0)
                    {
                        //System.out.println(thisByte);
                        data += (char) thisByte;
                        thisByte = 0;
                    }
                    countedBits++;
                }
            }
        }
        return data;
    }
}
