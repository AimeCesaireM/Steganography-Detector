
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

public class Phase1 {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            //error because we need two arguments
            System.err.println("Usage: decoder [filename] [HiddenMessageType]");
            System.err.println("HiddenMessageType: 'png' or 'text'");
            System.exit (1);
        }
        else if (args[0].equals("") || (!args[1].equals("text") && !args[1].equals("png"))) {
            //error because we need two arguments
            System.err.println("Usage: decoder [filename] [HiddenMessageType]");
            System.err.println("HiddenMessageType: 'png' or 'text'");
            System.exit(1);
        }
        String pathname = args[0];
        BufferedImage image = ImageIO.read(new File(pathname));
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Height: " + height + " Width: " + width);
        WritableRaster raster = image.getRaster();
        int lengthOrHeightOfHidden = getLengthOrHeight(raster, width, height);
        System.out.println(lengthOrHeightOfHidden);

        if (args[1].equals("text")){
            // run the code to get the hidden text
            String data = getTextData(lengthOrHeightOfHidden, raster, width, height);
            System.out.println(data);
        }
        if (args[1].equals("png")){
            int widthOfHidden = getWidth(raster, width, height);
            // run the code to get hidden image
            //getData
        }

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

    public static int getLengthOrHeight(WritableRaster raster, int width, int height)
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

    public static int getWidth(WritableRaster raster, int width, int height)
    {
        int r = 0;
        int c = 10;
        int[] pixels = raster.getPixel(c, r, (int[]) null);
        int length =  (pixels[2] & 1) << 31;
        int count = 30;
        c++;
        for (; r < height; r++) {
            for (; c < width; c++) {

                pixels = raster.getPixel(c, r, (int[]) null);
                for(int i = 0; i < 3; i++)
                {
                    if(count < 0)
                        return length;

                    int bitMask = (pixels[i] & 1) << count;
                    length = length | bitMask;
                    count--;
                }
            }
            c = 0;
        }
        return -1;
    }

    public static String getTextData(int length, WritableRaster raster, int width, int height)
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
                        data += (char) thisByte;
                        thisByte = 0;
                    }
                    countedBits++;
                }
            }
            c = 0;
        }
        return data;
    }
}
