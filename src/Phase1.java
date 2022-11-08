
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

public class Phase1 {

    static int globalRowIndex;
    static int globalColIndex;
    static int globalPixelIndex;
    static int numberOfPixelChannels;
    static boolean isLRTB;
    static boolean isGRB;
    static int leastSignificantBits;

    public static void main(String[] args) throws Exception {

        if (args.length != 6) {
            //error because we need six arguments
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [GRBorBRG] [#pixelChannels] [leastSignificantBits]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.exit (1);
        }
        else if (args[0].equals("") || (!args[1].equals("text") && !args[1].equals("png") && !args[1].equals("lw"))) {
            //error because we need two arguments
            System.err.println("Usage: Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [GRBorBRG] [#pixelChannels] [leastSignificantBits]");
            System.err.println("HiddenMessageType: 'png' or 'text' or 'lw'");
            System.exit(1);
        }
        String pathname = args[0];
        BufferedImage image = ImageIO.read(new File(pathname));
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Height: " + height + " Width: " + width);
        WritableRaster raster = image.getRaster();

        isLRTB = args[2].equals("LRTB");
        isGRB = args[3].equals("GRB");
        numberOfPixelChannels = Integer.parseInt(args[4]);
        leastSignificantBits = Integer.parseInt(args[5]);

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
            BufferedImage decodedImage = getImageData(lengthOrHeightOfHidden, widthOfHidden, raster, width, height);
            String path = "Decoding/";
            path += args[0].split("\\.")[0];
            path += "/";
            path += args[2] + " " + args[3] + " " + args[4] + " " + args[5] + ".png";

            System.out.println(path);

            ImageIO.write(decodedImage, "png", new File(path));
            //getData
        }
        else if (args[1].equals("lw")){
            int widthOfHidden = getNextIntFromImage(raster, width, height);
            System.out.println(" Width of hidden message: " + widthOfHidden);
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
        {
            if(isGRB)
                return LRTBAndGRB(raster, width, height, xBits);

            return LRTBAndBRG(raster, width, height, xBits);
        }

        //So it is TBLR
        if(isGRB)
            return TBLRAndGRB(raster, width, height, xBits);

        return TBLRAndBRG(raster, width, height, xBits);

    }


    public static int LRTBAndGRB(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBits;
        int returnValue = 0;
        for (; globalRowIndex < height; globalRowIndex++) {
            for (; globalColIndex < width; globalColIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex < numberOfPixelChannels; globalPixelIndex++)
                {
                    if(xBits < 0)
                        return returnValue;

                    int bitMask = (pixels[globalPixelIndex] & getMasker(leastSignificantBits)) << xBits;
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

    public static int TBLRAndGRB(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBits;
        int returnValue = 0;
        for (; globalColIndex < width; globalColIndex++) {
            for (; globalRowIndex < height; globalRowIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex < numberOfPixelChannels; globalPixelIndex++)
                {
                    if(xBits < 0)
                        return returnValue;

                    int bitMask = (pixels[globalPixelIndex] & getMasker(leastSignificantBits)) << xBits;
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

    public static int TBLRAndBRG(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBits;
        int returnValue = 0;
        for (; globalColIndex < width; globalColIndex++) {
            for (; globalRowIndex < height; globalRowIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex >= 0; globalPixelIndex--)
                {
                    if(xBits < 0)
                        return returnValue;

                    int bitMask = (pixels[globalPixelIndex] & getMasker(leastSignificantBits)) << xBits;
                    returnValue = returnValue | bitMask;
                    xBits -= leastSignificantBits;
                }
                globalPixelIndex = numberOfPixelChannels - 1;
            }
            globalRowIndex = 0;
        }
        System.err.println("getNextBits exited Unexpectedly");
        return -1;
    }
    public static int LRTBAndBRG(WritableRaster raster, int width, int height, int xBits)
    {
        xBits -= leastSignificantBits;
        int returnValue = 0;
        for (; globalRowIndex < height; globalRowIndex++) {
            for (; globalColIndex < width; globalColIndex++) {

                int[] pixels = raster.getPixel(globalColIndex, globalRowIndex, (int[]) null);
                for(; globalPixelIndex >= 0; globalPixelIndex--)
                {
                    if(xBits < 0)
                        return returnValue;

                    int bitMask = (pixels[globalPixelIndex] & getMasker(leastSignificantBits)) << xBits;
                    returnValue = returnValue | bitMask;
                    xBits -= leastSignificantBits;
                }
                globalPixelIndex = numberOfPixelChannels - 1;
            }
            globalColIndex = 0;
        }
        System.err.println("getNextBits exited Unexpectedly");
        return -1;
    }

    public static int getMasker(int bits)
    {
        if (bits == 1) return 1;
        if (bits == 2) return 3;
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

    public static BufferedImage getImageData(int heightOfHidden, int widthOfHidden, WritableRaster raster, int width, int height)
    {
        BufferedImage hiddenImage = new BufferedImage(widthOfHidden, heightOfHidden, BufferedImage.TYPE_INT_RGB);
        WritableRaster hiddenImageRaster = hiddenImage.getRaster();
        int[] setPixels;//Pixels of outputImage
        for(int outputImageRow = 0; outputImageRow < heightOfHidden; outputImageRow++)
        {
            for(int outputImageCol = 0; outputImageCol < widthOfHidden; outputImageCol++)
            {
                setPixels = new int[3];
                for(int outputPixelChannel  = 0; outputPixelChannel < 3; outputPixelChannel++)
                {
                    setPixels[outputPixelChannel] = getNextByteFromImage(raster, width, height);
                }
                hiddenImageRaster.setPixel(outputImageCol, outputImageRow, setPixels);
            }
        }

        return hiddenImage;
    }
}