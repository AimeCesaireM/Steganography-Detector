import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExtractFunctionGUI {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Extract Function GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Filename
        JLabel filenameLabel = new JLabel("Filename:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(filenameLabel, gbc);

        JTextField filenameField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(filenameField, gbc);

        JButton browseButton = new JButton("Browse");
        gbc.gridx = 2;
        gbc.gridy = 0;
        frame.add(browseButton, gbc);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                filenameField.setText(fileChooser.getSelectedFile().getPath());
            }
        });

        // Hidden Message Type
        JLabel hiddenMessageTypeLabel = new JLabel("HiddenMessageType:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(hiddenMessageTypeLabel, gbc);

        String[] hiddenMessageTypes = {"text", "png", "lw"};
        JComboBox<String> hiddenMessageTypeCombo = new JComboBox<>(hiddenMessageTypes);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(hiddenMessageTypeCombo, gbc);

        // LRTB or TBLR
        JLabel lrtbOrTblrLabel = new JLabel("LRTBOrTBLR:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(lrtbOrTblrLabel, gbc);

        JTextField lrtbOrTblrField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(lrtbOrTblrField, gbc);

        // rORfORsLength
        JLabel rorfOrsLengthLabel = new JLabel("Reverse, flip, or keep straight the length reading:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(rorfOrsLengthLabel, gbc);

        String[] rorfOptions = {"r", "f", "s"};
        JComboBox<String> rorfOrsLengthCombo = new JComboBox<>(rorfOptions);
        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(rorfOrsLengthCombo, gbc);

        // rORfORsData
        JLabel rorfOrsDataLabel = new JLabel("reverse or flip the Data? use 'r' or 's':");
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(rorfOrsDataLabel, gbc);

        JTextField rorfOrsDataField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(rorfOrsDataField, gbc);

        JLabel arrayOfLeastSignificantBitsLabel = new JLabel(" What Least Significant Bits:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(arrayOfLeastSignificantBitsLabel, gbc);

        JTextField arrayOfLeastSignificantBitsDataField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(arrayOfLeastSignificantBitsDataField, gbc);

        JLabel numberOfPixelChannelsLabel = new JLabel("How many Pixel Channels:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        frame.add(numberOfPixelChannelsLabel, gbc);

        JTextField numberOfPixelChannelsDataField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 6;
        frame.add(numberOfPixelChannelsDataField, gbc);

        JLabel arrayOfPixelChannelLabel = new JLabel("Array Of Pixel Channels in the extracted data:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        frame.add(arrayOfPixelChannelLabel, gbc);

        JTextField arrayOfPixelChanneldDataField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 7;
        frame.add(arrayOfPixelChanneldDataField, gbc);


        // Run Button
        JButton runButton = new JButton("Run Extract");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(runButton, gbc);

        JTextArea outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        frame.add(scrollPane, gbc);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = filenameField.getText();
                String hiddenMessageType = (String) hiddenMessageTypeCombo.getSelectedItem();
                String lrtbOrTblr = lrtbOrTblrField.getText();
                String rorfOrsLength = (String) rorfOrsLengthCombo.getSelectedItem();
                String rorfOrsData = rorfOrsDataField.getText();

                String arrayOfLeastSignificantBits = arrayOfLeastSignificantBitsDataField.getText();
                String numberOfPixelChannels = numberOfPixelChannelsDataField.getText();
                String arrayOfPixelChannels = arrayOfPixelChanneldDataField.getText();


                if (filename.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Filename is required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!hiddenMessageType.equals("text") && !hiddenMessageType.equals("png") && !hiddenMessageType.equals("lw")) {
                    JOptionPane.showMessageDialog(frame, "Invalid HiddenMessageType!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Simulating output (replace with actual extract function call)
                String outputStr = String.format(
                        "Running extract with:\nFilename: %s\nHiddenMessageType: %s\nLRTBOrTBLR: %s\nRORFOrSLength: %s\nRORFOrSData: %s",
                        filename, hiddenMessageType, lrtbOrTblr, rorfOrsLength, rorfOrsData
                );
                try {
                    Phase1.main(new String[]{filename, hiddenMessageType, lrtbOrTblr, rorfOrsLength, rorfOrsData, arrayOfLeastSignificantBits, numberOfPixelChannels, arrayOfPixelChannels});
                    outputArea.setText(outputStr);
                    outputArea.append("Check local directory /Decoding");
                } catch (Exception ex) {
                    outputArea.append(ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}