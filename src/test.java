import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;

class Phase1Test {

    private WritableRaster mockRaster;
    private BufferedImage mockImage;

    @BeforeEach
    void setUp() {
        // Setup mock objects for BufferedImage and WritableRaster
        mockImage = mock(BufferedImage.class);
        mockRaster = mock(WritableRaster.class);
        
        when(mockImage.getRaster()).thenReturn(mockRaster);
    }

    @Test
    void testGetNextIntFromImage() {
        // Assume the method is returning an integer after reading 32 bits
        // We'll mock the `getNextXBits` method to control the returned value.

        Phase1.isLRTB = true; // or false depending on the test scenario

        // Mocking the expected behavior of the `getNextXBits` method
        int mockValue = 123456789;
        when(mockRaster.getPixel(anyInt(), anyInt(), any(int[].class))).thenReturn(new int[] {0, 0, 0}); // Mock pixel data
        
        // Simulate getNextIntFromImage
        int result = Phase1.getNextIntFromImage(mockRaster, 10, 10);
        
        assertEquals(mockValue, result);
    }

    @Test
    void testFlipMethod() {
        // Test the flip functionality
        int input = 255; // example value
        int bits = 8;
        int expectedResult = 0; // Flipping 255 (11111111) should result in 0 (00000000)

        int result = Phase1.flip(input, bits);
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetTextData() throws IOException {
        int length = 5; // assuming 5 bytes
        String expectedData = "Hello";

        // Mocking `getNextByteFromImage` to simulate text extraction
        when(mockRaster.getPixel(anyInt(), anyInt(), any(int[].class))).thenReturn(new int[] {72, 101, 108, 108, 111}); // "Hello"

        String result = Phase1.getTextData(length, mockRaster, 10, 10);

        assertEquals(expectedData, result);
    }

    @Test
    void testReverseMethod() {
        // Test the reverse functionality
        int input = 123456;
        int bits = 32; // Reverse 32 bits
        int expectedResult = 654321; // We expect a reversed bit pattern (this is a mock result)

        int result = Phase1.reverse(input, bits);
        assertEquals(expectedResult, result);
    }
}
