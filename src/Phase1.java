
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class Phase1 {

    static int globalRowIndex;
    static int globalColIndex;
    static int globalPixelIndex;
    static int[] pixelChannels;
    static boolean isLRTB;
    static int[] leastSignificantBitsArray;
    static boolean reverseLength;
    static boolean reverseData;
    static boolean flippedLength;
    static boolean flippedData;

    public static void main(String[] args) throws Exception {
        //System.out.println(Integer.toBinaryString(reverseChannel(Integer.parseInt("11111110", 2))));
        if (args.length < 2) {
            //error because we need at least six arguments
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [rORfORsLength] [rORfORsData]" +
                    " [numberOfSignificantBits] [ArrayOFleastSignificantBits] [numberOfPixelChannels] [arrayOfPixelChannels]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.err.println("[rORfORs] means whether we should reverse, flip, or keep straight the length or pixels");
            System.exit (1);

        }
        else if (args[0].equals("") || (!args[1].equals("text") && !args[1].equals("png") && !args[1].equals("lw"))) {
            //
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [rORfORsLength] [rORfORsData]" +
                    " [sizeOfIncomingArray] [ArrayOFleastSignificantBits] [sizeOfIncomingArray] [arrayOfPixelChannels]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.err.println("[rORfORs] means whether we should reverse, flip, or keep straight the length or pixels");
            System.exit (1);
        }
        else if (args.length < 5 && !args[1].equals("lw"))
        {
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [rORfORsLength] [rORfORsData]" +
                    " [sizeOfIncomingArray] [ArrayOFleastSignificantBits] [sizeOfIncomingArray] [arrayOfPixelChannels]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.err.println("[rORfORs] means whether we should reverse, flip, or keep straight the length or pixels");
            System.exit (1);
        }
        int indexArgs = 0;
        String pathname = args[indexArgs];
        BufferedImage image = ImageIO.read(new File(pathname));
        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();
        System.out.println("Height: " + height + " Width: " + width +  " Type: " + type);
        WritableRaster raster = image.getRaster();


        if (args[1].equals("lw")){
            getPreliminaryData(raster, width, height, type);
            return;
        }
        indexArgs += 2;
        setCommandLineArgs(args, indexArgs);

        globalRowIndex = 0;
        globalColIndex = 0;
        globalPixelIndex = 0;


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
            System.out.println("The next 32 bit integer is: " + getNextIntFromImage(raster, width, height));
            //getData
        }

    }

    public static void setCommandLineArgs(String[] args, int indexArgs)
    {
        isLRTB = args[indexArgs].equals("LRTB");
        indexArgs++;
        reverseLength = args[indexArgs].equals("r");
        flippedLength = args[indexArgs].equals("f");
        indexArgs++;
        reverseData = args[indexArgs].equals("r");
        flippedData = args[indexArgs].equals("f");
        indexArgs++;
        leastSignificantBitsArray = new int[Integer.parseInt(args[indexArgs])];
        indexArgs++;
        for (int i = 0; i < leastSignificantBitsArray.length; i++) {
            leastSignificantBitsArray[i] = Integer.parseInt(args[indexArgs]);
            indexArgs++;
        }
        pixelChannels = new int[Integer.parseInt(args[indexArgs])];
        indexArgs++;
        for (int i = 0; i < pixelChannels.length; i++) {
            pixelChannels[i] = Integer.parseInt(args[indexArgs]);
            indexArgs++;
        }
    }

    public static void getPreliminaryData(WritableRaster raster, int width, int height, int type)
    {
        boolean[] boolArray = {true, false};
        String[] t = {"s", "r", "f"};
        int[][] lsb2dArray = {{1}, {2}, {3}, {0}, {0, 1}, {0, 1, 2}, {0, 2}, {1, 2}};

        int[][] arr = {{0}, {1}, {2}, {0, 1}, {1, 2}, {0, 2}, {1, 0}, {2, 1}, {2, 0}, {0, 1, 2}, {2, 1, 0}};
        ArrayList<int[]> pixelChannelArrays = new ArrayList<int[]>();
        pixelChannelArrays.addAll(Arrays.asList(arr));
        if (type == 6)
        {
            int[][] arr2  = {{0, 1, 2, 3}, {3, 2, 1, 0}, {3}, {0, 3}, {1, 3}, {2, 3}, {0, 2, 1, 3}, {0, 3, 2, 1}, {3, 0}, {3, 1}, {3, 2}};
            pixelChannelArrays.addAll(Arrays.asList(arr2));
            System.out.println("Added Channels");
        }

        for (boolean lrtb: boolArray) {
            isLRTB = lrtb;
            for (String a: t)
            {
                reverseLength = a.equals("r");
                flippedLength = a.equals("f");
                for (int[] lsbArray: lsb2dArray) {
                    leastSignificantBitsArray = lsbArray;
                    for (int[] pixelChannelArray: pixelChannelArrays) {
                        pixelChannels = pixelChannelArray;

                        globalRowIndex = 0;
                        globalColIndex = 0;
                        globalPixelIndex = 0;
                        int l = getNextIntFromImage(raster, width, height);

                        if (l < 2000 && l > 0)
                        {
                            System.out.print("With LRTB = " + lrtb + " and rORfORsLength = " + a + " and lsbArray = { ");
                            for (int j : leastSignificantBitsArray) {
                                System.out.print(j + " ");
                            }
                            System.out.print("} and pixel channels being { ");

                            for (int j : pixelChannelArray) {
                                System.out.print(j + " ");
                            }
                            System.out.println("} l is " + l);
                            int w = getNextIntFromImage(raster, width, height);
                            if (w < 2000 && w > 0)
                            {
                                System.out.println("And w is " + w);
                            }
                        }

                    }
                }
            }


        }
    }

    public static int getNextIntFromImage(WritableRaster raster, int width, int height)
    {
        int returnValue = getNextXBits(raster, width, height, 32);
        if(reverseLength)
            return reverse(returnValue, 32);
        if(flippedLength)
            return flip(returnValue, 32);
        return returnValue;
    }

    public static int getNextByteFromImage(WritableRaster raster, int width, int height)
    {
        int returnValue = getNextXBits(raster, width, height, 8);
        if(reverseData)
            return reverse(returnValue, 8);
        if(flippedData)
            return flip(returnValue, 8);
        return returnValue;
    }
    public static int getNextXBits(WritableRaster raster, int width, int height, int xBits)
    {
        if(isLRTB)
            return LRTB(raster, width, height, xBits);
        return TBLR(raster, width, height, xBits);

    }


    public static int LRTB(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBitsArray.length;
        int returnValue = 0;
        for (; globalRowIndex < height; globalRowIndex++) {
            for (; globalColIndex < width; globalColIndex++){

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex < pixelChannels.length; globalPixelIndex++)
                {
                    if(xBits < 0)
                        return returnValue;
                    String bitsToGet = "";
                    for (int i = 0; i < leastSignificantBitsArray.length; i++) {
                        bitsToGet = getIthBitFromInt( pixels[pixelChannels[globalPixelIndex]], leastSignificantBitsArray[i]) + bitsToGet;
                    }
                    int bitMask = Integer.parseInt(bitsToGet, 2) << xBits;
                    returnValue = returnValue | bitMask;
                    xBits -= leastSignificantBitsArray.length; //Works well only if .length is 1, 2, 4, or 8
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
        xBits -= leastSignificantBitsArray.length;
        int returnValue = 0;
        for (; globalColIndex < width; globalColIndex++) {
            for (; globalRowIndex < height; globalRowIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex < pixelChannels.length; globalPixelIndex++)
                {
                    if(xBits < 0)
                        return returnValue;
                    String bitsToGet = "";
                    for (int i = 0; i < leastSignificantBitsArray.length; i++) {
                        bitsToGet = getIthBitFromInt( pixels[pixelChannels[globalPixelIndex]], leastSignificantBitsArray[i]) + bitsToGet;
                    }
                    int bitMask = Integer.parseInt(bitsToGet, 2) << xBits;
                    returnValue = returnValue | bitMask;
                    xBits -= leastSignificantBitsArray.length; //Works well only if .length is 1, 2, 4, or 8
                }
                globalPixelIndex = 0;
            }
            globalRowIndex = 0;
        }
        System.err.println("getNextBits exited Unexpectedly");
        return -1;
    }

    public static String getIthBitFromInt(int n, int i)
    {
        return Integer.toString((n >> i) & 1);
    }

    public static int getMasker(int bits)
    {
        if (bits == 32) return -1;
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
                    int channel = outputPixelChannel;
                    setPixels[channel] = getNextByteFromImage(raster, width, height);
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
    private static int flip(int n, int bits)
    {
        return n ^ getMasker(bits);
    }

    private static int reverse(int n, int bits) {
        int result = 0;
        long divisor = (long)Math.pow(2, bits);
        for (int i = 0; i < bits; i++) {
            result += ((n % (divisor)) / (divisor/2)) * (int)Math.pow(2, i);
            divisor = divisor / 2;
        }
        return result;

//        int leading = (n % 256) / 128;
//        int leading_1 = (n % 128) / 64;
//        int leading_2 = (n % 64) / 32;
//        int leading_3 = (n % 32) / 16;
//        int leading_4 = (n % 16) / 8 ;
//        int leading_5 = (n % 8) / 4;
//        int leading_6 = (n % 4) / 2;
//        int ones = n % 2;
//        int result = (leading) + (leading_1 * 2) + (leading_2 * 4) + (leading_3 * 8) + (leading_4 * 16)
//                + (leading_5 * 32) + (leading_6 * 64) + (ones * 128);
//
//        return result;
    }
}