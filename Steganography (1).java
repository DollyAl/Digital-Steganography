import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Steganography {

    public static void main(String[] args) {
        String coverImagePath = "path/to/cover/image.jpg";
        String secretMessage = "Hello, this is a secret message!";
        String outputImagePath = "path/to/output/stegoImage.png";

        hideMessage(coverImagePath, secretMessage, outputImagePath);

        String extractedMessage = extractMessage(outputImagePath);
        System.out.println("Extracted Message: " + extractedMessage);
    }

    // Hide message in an image using LSB method
    public static void hideMessage(String coverImagePath, String message, String outputImagePath) {
        try {
            BufferedImage coverImage = ImageIO.read(new File(coverImagePath));
            int width = coverImage.getWidth();
            int height = coverImage.getHeight();
            int messageLength = message.length();

            for (int y = 0, messageIndex = 0; y < height && messageIndex < messageLength; y++) {
                for (int x = 0; x < width && messageIndex < messageLength; x++) {
                    int rgb = coverImage.getRGB(x, y);
                    int bit = (message.charAt(messageIndex) >> 7) & 1; // Get the MSB of the character
                    rgb = (rgb & 0xFEFFFFFF) | (bit << 24); // Set the last bit of the red component

                    coverImage.setRGB(x, y, rgb);
                    messageIndex++;
                }
            }

            ImageIO.write(coverImage, "png", new File(outputImagePath));
            System.out.println("Message hidden successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extract message from an image using LSB method
    public static String extractMessage(String stegoImagePath) {
        StringBuilder extractedMessage = new StringBuilder();

        try {
            BufferedImage stegoImage = ImageIO.read(new File(stegoImagePath));
            int width = stegoImage.getWidth();
            int height = stegoImage.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = stegoImage.getRGB(x, y);
                    int bit = (rgb >> 24) & 1; // Get the LSB of the red component
                    extractedMessage.append(bit);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertBinaryToText(extractedMessage.toString());
    }

    // Convert binary string to text
    public static String convertBinaryToText(String binary) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < binary.length(); i += 8) {
            String byteString = binary.substring(i, i + 8);
            int asciiValue = Integer.parseInt(byteString, 2);
            text.append((char) asciiValue);
        }

        return text.toString();
    }
}
