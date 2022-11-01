
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

public class Phase1 {

    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("hide_text.png"));
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Height: " + height + " Width: " + width);
        WritableRaster raster = image.getRaster();
        int count = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] pixels = raster.getPixel(c, r, (int[]) null);
                if (count < 32) {
                    System.out.print((pixels[0] & 1) + "" + (pixels[1] & 1) + "" + (pixels[2] & 1));
                    count++;
                }
            }
        }
        System.out.println("");
    }
}
