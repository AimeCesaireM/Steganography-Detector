# Phase1 - Image Steganography Decoder

## Overview
The `Phase1` program decodes hidden messages (text or images) embedded within a source image using steganographic techniques. It supports decoding text and images by extracting least significant bits (LSBs) from pixel channels. The program also includes utilities to analyze raster image data. This project was a collaboration between Ahsan Rhandawa and I.

## Features
- Decode hidden text messages from images.
- Decode hidden images embedded in a source image.
- Analyze preliminary data from image rasters.
- Flexible command-line options for decoding configurations.

## Requirements
- Java Development Kit (JDK) 8 or higher.
- Java AWT and ImageIO libraries (included in standard JDK).

## Usage
Go into the /src subfolder
### Command-Line Arguments
```bash
java Phase1 [filename] [HiddenMessageType] [LRTBOrTBLR] [rORfORsLength] [rORfORsData] [numberOfSignificantBits] [ArrayOFleastSignificantBits] [numberOfPixelChannels] [arrayOfPixelChannels]
```

### Parameters
- **filename**: Path to the input image file.
- **HiddenMessageType**: Type of the hidden message (`text`, `png`, or `lw`).
- **LRTBOrTBLR**: Specifies the reading order:
  - `LRTB`: Left-to-right, top-to-bottom.
  - `TBLR`: Top-to-bottom, left-to-right.
- **rORfORsLength**: Specifies how to handle the length of the hidden message:
  - `r`: Reverse bits.
  - `f`: Flip bits.
  - `s`: Keep bits straight.
- **rORfORsData**: Specifies how to handle the data:
  - `r`: Reverse bits.
  - `f`: Flip bits.
  - `s`: Keep bits straight.
- **numberOfSignificantBits**: Number of significant bits in the input data.
- **ArrayOFleastSignificantBits**: Array of LSB indices for bit extraction.
- **numberOfPixelChannels**: Number of pixel channels used for decoding.
- **arrayOfPixelChannels**: Array specifying pixel channels to decode from.

### Example
```bash
java Phase1 example.png text LRTB r s 1 0 3 0 1 2
```

### Hidden Message Types
1. **text**: Decodes hidden text messages.
2. **png**: Decodes a hidden image and outputs it as a PNG file.
3. **lw**: Extracts preliminary raster data for analysis.

## Key Methods
### `getNextIntFromImage`
Extracts the next 32 bits as an integer from the image based on the configured reading order and bit manipulations.

### `getTextData`
Extracts and decodes hidden text from the image based on the provided length.

### `getImageData`
Decodes a hidden image by extracting pixel data and creating a new image.

### `getImageWithLeastSignificantBit`
Generates an image visualizing the least significant bits of the source image's pixels.

## Output
- **Text decoding**: Prints the hidden message to the console.
- **Image decoding**: Outputs the decoded image to the `Decoding/` directory.
- **Raster analysis**: Prints preliminary raster data for LSB configurations.

## Notes
- Ensure the input image is accessible and in a supported format (e.g., PNG).
- The output directory for decoded images (`Decoding/`) must exist or be created manually.

## Development
### File Structure
- `Phase1.java`: Main program file with all decoding logic.
- `Decoding/`: Output directory for decoded images.

### Dependencies
- Java Standard Library (AWT, ImageIO).

## Limitations
- Only supports decoding messages embedded using specific LSB steganography techniques.
- Does not include error handling for unsupported image types or corrupted input.
- I welcome any new contributions to my testing script

## License
This project is licensed under the MIT License.

---
