
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Phase1 {

    static int globalRowIndex;
    static int globalColIndex;
    static int globalPixelIndex;
    static int[] pixelChannels;
    static boolean isLRTB;
    static int leastSignificantBits;

    public static void main(String[] args) throws Exception {
        //System.out.println(Integer.toBinaryString(reverseChannel(Integer.parseInt("11111110", 2))));
        if (args.length < 2) {
            //error because we need at least six arguments
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [leastSignificantBits] [arrayOfPixelChannels]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.exit (1);

        }
        else if (args[0].equals("") || (!args[1].equals("text") && !args[1].equals("png") && !args[1].equals("lw"))) {
            //
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [leastSignificantBits] [arrayOfPixelChannels]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.exit(1);
        }
        else if (args.length < 5 && !args[1].equals("lw"))
        {
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [leastSignificantBits] [arrayOfPixelChannels]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.exit(1);
        }

        String pathname = args[0];
        BufferedImage image = ImageIO.read(new File(pathname));
//        BufferedImage testImage = getImageWithLeastSignificantBit(image);
//        ImageIO.write(testImage, "png", new File("leastSigBitTestImage.png"));
        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();
        System.out.println("Height: " + height + " Width: " + width +  " Type: " + type);
        WritableRaster raster = image.getRaster();


        if (args[1].equals("lw")){
            boolean[] lrtbArray = {true, false};
            int[] lsbArray = {1, 2, 3, 4, 5, 6};
            int[][] pixelChannelArrays = {{0}, {1}, {2}, {0, 1}, {1, 2}, {0, 2}, {1, 0}, {2, 1}, {2, 0}, {0, 1, 2}, {2, 1, 0},
                    {0, 1, 2, 3},{3}, {0, 1, 3}, {0, 2, 3}, {0, 3}, {0, 3}, {1, 3}, {2, 3}, {0, 1, 2, 3} };
            for (boolean lrtb: lrtbArray) {
                isLRTB = lrtb;
                for (int lsb: lsbArray) {
                    leastSignificantBits = lsb;
                    for (int[] pixelChannelArray: pixelChannelArrays) {
                        pixelChannels = pixelChannelArray;

                        globalRowIndex = 0;
                        globalColIndex = 0;
                        globalPixelIndex = 0;
                        int l = getNextIntFromImage(raster, width, height);

                        if (l < 10000 && l > 0)
                        {
                            System.out.print("With LRTB = " + lrtb + " and  lsb = " + lsb + " and pixel channels being { ");
                            for (int j : pixelChannelArray) {
                                System.out.print(j + " ");
                            }
                            System.out.println("} l is " + l);
                            int w = getNextIntFromImage(raster, width, height);
                            if (w < 10000 && w > 0)
                            {
                                System.out.println("And w is " + w);
                            }
                        }

                    }
                }

            }

            return;
        }
        isLRTB = args[2].equals("LRTB");
        leastSignificantBits = Integer.parseInt(args[3]);

        globalRowIndex = 0;
        globalColIndex = 0;
        globalPixelIndex = 0;

        pixelChannels = new int[args.length - 4];
        for (int i = 0; i < pixelChannels.length; i++) {
            pixelChannels[i] = Integer.parseInt(args[4+ i]);
        }

        int lengthOrHeightOfHidden = getNextIntFromImage(raster, width, height);
        System.out.println("Length/Height of hidden message: " + lengthOrHeightOfHidden);

        if (args[1].equals("text")){
            // run the code to get the hidden text
            String data = getTextData(lengthOrHeightOfHidden, raster, width, height);
            System.out.println("Hidden Text: \n" + data);
        }
        else if (args[1].equals("png")){
            int widthOfHidden = getNextIntFromImage(raster, width, height);
            System.out.println(" Width of hidden message: " + widthOfHidden);
            // run the code to get hidden image
            BufferedImage decodedImage = getImageData(lengthOrHeightOfHidden, widthOfHidden, raster, width, height, type);
            String path = "Decoding/";
            path += args[0].split("\\.")[0];
            path += "/";
            for (int i = 2; i < args.length - 1; i++) {
                path += args[i] + " ";
            }
            path += args[args.length - 1] + ".png";

            System.out.println(path);

            ImageIO.write(decodedImage, "png", new File(path));
            //getData
        }

    }

    public static int getNextIntFromImage(WritableRaster raster, int width, int height)
    {
        return getNextXBits(raster, width, height, 32);
    }

    public static int getNextByteFromImage(WritableRaster raster, int width, int height)
    {

        return getNextXBits(raster, width, height, 8);
    }
    public static int getNextXBits(WritableRaster raster, int width, int height, int xBits)
    {
        if(isLRTB)
            return LRTB(raster, width, height, xBits);
        return TBLR(raster, width, height, xBits);

    }


    public static int LRTB(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBits;
        int returnValue = 0;
        for (; globalRowIndex < height; globalRowIndex++) {
            for (; globalColIndex < width; globalColIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex < pixelChannels.length; globalPixelIndex++)
                {
                    if(xBits < 0)
                        return returnValue;

                    int bitMask = (pixels[pixelChannels[globalPixelIndex]] & getMasker(leastSignificantBits)) << xBits;
                    returnValue = returnValue | bitMask;
                    xBits -= leastSignificantBits;
                }
                globalPixelIndex = 0;
            }
            globalColIndex = 0;
        }
        System.err.println("getNextBits exited Unexpectedly");
        return -1;
    }

    public static int TBLR(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBits;
        int returnValue = 0;
        for (; globalColIndex < width; globalColIndex++) {
            for (; globalRowIndex < height; globalRowIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex < pixelChannels.length; globalPixelIndex++)
                {
                    if(xBits < 0)
                        return returnValue;

                    int bitMask = (pixels[pixelChannels[globalPixelIndex]] & getMasker(leastSignificantBits)) << xBits;
                    returnValue = returnValue | bitMask;
                    xBits -= leastSignificantBits;
                }
                globalPixelIndex = 0;
            }
            globalRowIndex = 0;
        }
        System.err.println("getNextBits exited Unexpectedly");
        return -1;
    }

    public static int getMasker(int bits)
    {
        if (bits > 0 && bits < 9)
        {
            String s = "";
            for (int i = 0; i < bits; i++) {
                s+= '1';
            }
            return Integer.parseInt(s, 2);
        }
        System.err.println("Unexpected bits given to getMasker");
        return -1;
    }

    public static String getTextData(int length, WritableRaster raster, int width, int height)
    {
        String data = "";
        int thisByte;
        long countedBytes = 0;
        while(countedBytes < length)
        {
            thisByte = getNextByteFromImage(raster, width, height);
            data += (char) thisByte;
            countedBytes++;
        }
        return data;
    }

    public static BufferedImage getImageData(int heightOfHidden, int widthOfHidden, WritableRaster raster, int width, int height, int type)
    {
        BufferedImage hiddenImage = new BufferedImage(widthOfHidden, heightOfHidden, type);
        WritableRaster hiddenImageRaster = hiddenImage.getRaster();
        int[] setPixels;//Pixels of outputImage
        for(int outputImageCol = 0; outputImageCol < widthOfHidden; outputImageCol++)
        {
            for(int outputImageRow = 0; outputImageRow < heightOfHidden; outputImageRow++)

            {
                setPixels = new int[3];
                for(int outputPixelChannel  = 0; outputPixelChannel < 3; outputPixelChannel++)
                {
                    int channel = 2 - outputPixelChannel;
                    setPixels[channel] = getNextByteFromImage(raster, width, height);
                    setPixels[channel] = reverseChannel(setPixels[channel]);
                }
                hiddenImageRaster.setPixel(outputImageCol, outputImageRow, setPixels);
            }
        }

        return hiddenImage;
    }
    public static BufferedImage getImageWithLeastSignificantBit(BufferedImage image) throws IOException {
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        WritableRaster outputRaster = outputImage.getRaster();

        int[] pixel;
        for (int r = 0; r < image.getHeight(); r ++)
        {
            for (int c = 0; c < image.getWidth(); c++)
            {
                pixel = image.getRaster().getPixel(c, r, (int[]) null);

                pixel[0] = (pixel[0] & 1) ;
                pixel[1] = (pixel[1] & 1) ;
                pixel[2] = (pixel[2] & 1) ;

                pixel[0] = pixel[0] > 0 ? 255: 0;
                pixel[1] = pixel[1] > 0 ? 255: 0;
                pixel[2] = pixel[2] > 0 ? 255: 0;
          //      System.out.println(Arrays.toString(pixel));
                outputRaster.setPixel(c, r, pixel);

            }
        }
        return outputImage;

    }
    private static int reverseChannel (int n) {
        //int result = 0;
        int leading = n / 128;
        int leading_1 = (n % 128) / 64 ;
        int leading_2 = (n % 64) / 32;
        int leading_3 = (n % 32) / 16;
        int leading_4 = (n % 16) / 8 ;
        int leading_5 = (n % 8) / 4;
        int leading_6 = (n % 4) / 2;
        int ones = n % 2;
        int result = (leading) + (leading_1 * 2) + (leading_2 * 4) + (leading_3 * 8) + (leading_4 * 16)
                + (leading_5 * 32) + (leading_6 * 64) + (ones * 128);

        return result;
    }
}