/**
 * StarDateConverter.java
 *
 * Voyager-themed LCARS interface for converting Earth dates (YYYY-MM-DD)
 * into Star Trek style stardates using a simplified formula.
 *
 * Author: Dakota Leahy (1st project)
 * Date: October 21st, 2025 — revised Oct 22–25, 2025
 *
 * Notes:
 * - This file contains UI construction, LCARS styling constants, input handling,
 *   stardate conversion logic and basic error handling.
 */

import javax.swing.*;           // Swing UI components
import java.awt.*;              // AWT for Color, Dimension, Layouts
import java.time.LocalDate;     // Java Time API for date parsing and fields
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.format.DateTimeParseException;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;

public class StarDateConverter extends JFrame {

    // ---------------------------------------------------------------------
    // Window and panel size constants
    // ---------------------------------------------------------------------
    private static final int WIDTH = 800;          // total window width in pixels
    private static final int HEIGHT = 600;         // total window height in pixels
    private static final int PANEL_WIDTH = 300;    // width used for input/output panels
    private static final int PANEL_HEIGHT = 100;   // height used for input/output panels

    // ---------------------------------------------------------------------
    // LCARS color palette (constants for clarity and reuse)
    // ---------------------------------------------------------------------
    private static final Color LCARS_BACKGROUND = new Color(0, 0, 0);                    // main background (black)
    private static final Color LCARS_ACCENT_ORANGE = new Color(255, 153, 0);            // LCARS orange accent
    private static final Color LCARS_ACCENT_VIOLET = new Color(204, 153, 255);         // LCARS violet accent
    private static final Color LCARS_INPUT_BG = new Color(40, 40, 40);                  // input field background
    private static final Color LCARS_INPUT_TEXT = LCARS_ACCENT_ORANGE;                 // input text color
    private static final Color LCARS_OUTPUT_BG = new Color(30, 30, 30);                 // output field background
    private static final Color LCARS_OUTPUT_TEXT = LCARS_ACCENT_VIOLET;                // output text color
    private static final Color LCARS_PANEL_INPUT = new Color(255, 153, 0);              // panel accent for input node
    private static final Color LCARS_PANEL_OUTPUT = new Color(204, 153, 255);           // panel accent for output node

    // ---------------------------------------------------------------------
    // Constructor builds UI and event wiring
    // ---------------------------------------------------------------------
    public StarDateConverter() {
        // Window basic setup
        setTitle("StarDateConverter Interface");               // window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        // exit app when window closed
        setResizable(false);                                   // keep fixed size for LCARS layout

        // Use a JLayeredPane so we can place panels at specific positions and layers
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // enforce preferred window size
        layeredPane.setLayout(null);                                // absolute positioning

        // Background and thin border accents (added at lower z-index)
        layeredPane.add(createLCARSPanel(0, 0, WIDTH, HEIGHT, LCARS_BACKGROUND, null), Integer.valueOf(0));
        layeredPane.add(createLCARSPanel(0, 0, WIDTH, 6, LCARS_ACCENT_ORANGE, null), Integer.valueOf(1)); // top strip
        layeredPane.add(createLCARSPanel(0, HEIGHT - 6, WIDTH, 6, LCARS_ACCENT_ORANGE, null), Integer.valueOf(1)); // bottom strip
        layeredPane.add(createLCARSPanel(0, 0, 6, HEIGHT, LCARS_ACCENT_ORANGE, null), Integer.valueOf(1)); // left strip
        layeredPane.add(createLCARSPanel(WIDTH - 6, 0, 6, HEIGHT, LCARS_ACCENT_ORANGE, null), Integer.valueOf(1)); // right strip

        // Accent vertical strips to evoke LCARS panels (higher z-index than base)
        layeredPane.add(createLCARSPanel(50, 150, 12, 420, LCARS_ACCENT_ORANGE, null), Integer.valueOf(1));
        layeredPane.add(createLCARSPanel(400, 150, 12, 420, LCARS_ACCENT_VIOLET, null), Integer.valueOf(1));

        // Panel headers (input and output labels placed above respective node panels)
        layeredPane.add(createLCARSPanel(50, 50, PANEL_WIDTH, PANEL_HEIGHT, LCARS_PANEL_INPUT, "LCARS INPUT NODE"), Integer.valueOf(2));
        layeredPane.add(createLCARSPanel(400, 50, PANEL_WIDTH, PANEL_HEIGHT, LCARS_PANEL_OUTPUT, "LCARS OUTPUT NODE"), Integer.valueOf(2));

        // Input and output components (text fields and button)
        JTextField earthDateField = new JTextField();     // user-entered Earth date (YYYY-MM-DD)
        JTextField starDateField = new JTextField();      // non-editable field that shows the stardate
        JButton convertButton = new JButton("Convert to StarDate"); // conversion trigger

        // Button styling to match LCARS aesthetics
        convertButton.setBackground(LCARS_BACKGROUND);                 // dark button background
        convertButton.setForeground(LCARS_ACCENT_ORANGE);              // orange text
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));       // readable bold font
        convertButton.setFocusPainted(false);                          // remove focus outline for cleaner look
        convertButton.setBorder(BorderFactory.createLineBorder(LCARS_ACCENT_ORANGE, 2)); // orange border

        // Add input/output panels to layeredPane with higher z-index so they sit above headers and accents
        layeredPane.add(createInputPanel(earthDateField, convertButton), Integer.valueOf(2));
        layeredPane.add(createOutputPanel(starDateField), Integer.valueOf(2));

        // Conversion action: parse the Earth date and display the computed stardate
        // Using a lambda ActionListener for succinctness; runs when button clicked
        convertButton.addActionListener(e -> {
            String input = earthDateField.getText();            // read raw input text
            starDateField.setText(convertToStardate(input));    // compute and set the output text
        });

        // Final window setup
        setContentPane(layeredPane);   // set JLayeredPane as the content pane
        pack();                        // size window to preferred sizes
        setLocationRelativeTo(null);   // center window on screen
        setVisible(true);              // show the UI
    }

    // ---------------------------------------------------------------------
    // Helper: createLCARSPanel
    // - Creates a JPanel at (x,y) with specified size and background color.
    // - If text is provided, create a centered JLabel with black text.
    // - Panels use BorderLayout to center the label.
    // ---------------------------------------------------------------------
    private JPanel createLCARSPanel(int x, int y, int width, int height, Color color, String text) {
        JPanel panel = new JPanel();              // new panel instance
        panel.setBounds(x, y, width, height);     // absolute position and size
        panel.setBackground(color);               // set panel color
        panel.setLayout(new BorderLayout());      // layout useful when adding centered label

        // If a text header was passed, create label and add to panel center
        if (text != null && !text.isEmpty()) {
            JLabel label = new JLabel(text, SwingConstants.CENTER); // centered label
            label.setForeground(Color.BLACK);                      // black label color for contrast
            label.setFont(new Font("Arial", Font.BOLD, 18));       // bold font for header
            panel.add(label, BorderLayout.CENTER);                 // position label in the panel center
        }

        return panel;
    }

    // ---------------------------------------------------------------------
    // Helper: createInputPanel
    // - Constructs the LCARS-styled input node containing:
    //   label, input JTextField, and convert JButton.
    // - Uses GridLayout to stack these elements vertically.
    // ---------------------------------------------------------------------
    private JPanel createInputPanel(JTextField earthDateField, JButton convertButton) {
        JPanel panel = new JPanel();
        panel.setBounds(50, 200, PANEL_WIDTH, PANEL_HEIGHT); // position tied to LCARS layout
        panel.setBackground(LCARS_PANEL_INPUT);              // panel accent color
        panel.setLayout(new GridLayout(3, 1, 6, 6));         // 3 rows, 1 column, with gaps

        // Instruction label above the input field
        JLabel label = new JLabel("Earth Date (YYYY-MM-DD)", SwingConstants.CENTER);
        label.setForeground(Color.BLACK);                    // header label color
        label.setFont(new Font("Arial", Font.BOLD, 14));     // font choice for clarity

        // Configure the input text field
        earthDateField.setHorizontalAlignment(JTextField.CENTER); // center the typed text
        earthDateField.setBackground(LCARS_INPUT_BG);             // darker background for terminal feel
        earthDateField.setForeground(LCARS_INPUT_TEXT);           // orange input text color
        earthDateField.setBorder(BorderFactory.createLineBorder(LCARS_ACCENT_ORANGE, 2)); // orange border
        earthDateField.setFont(new Font("Monospaced", Font.PLAIN, 14)); // monospaced improves date readability

        // Add components to the panel in order
        panel.add(label);           // top: instruction label
        panel.add(earthDateField);  // middle: input field
        panel.add(convertButton);   // bottom: convert button

        return panel;
    }

    // ---------------------------------------------------------------------
    // Helper: createOutputPanel
    // - Builds the LCARS-styled output node: label + non-editable text field
    // ---------------------------------------------------------------------
    private JPanel createOutputPanel(JTextField starDateField) {
        JPanel panel = new JPanel();
        panel.setBounds(400, 200, PANEL_WIDTH, PANEL_HEIGHT); // positioned to the right
        panel.setBackground(LCARS_PANEL_OUTPUT);              // violet accent panel
        panel.setLayout(new GridLayout(2, 1, 6, 6));          // 2 rows: label + field

        // Output label for stardate
        JLabel label = new JLabel("Star Date", SwingConstants.CENTER);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        // Configure output field: read-only and styled for LCARS
        starDateField.setHorizontalAlignment(JTextField.CENTER);
        starDateField.setEditable(false); // prevent user edits
        starDateField.setBackground(LCARS_OUTPUT_BG);
        starDateField.setForeground(LCARS_OUTPUT_TEXT);
        starDateField.setBorder(BorderFactory.createLineBorder(LCARS_ACCENT_VIOLET, 2));
        starDateField.setFont(new Font("Monospaced", Font.BOLD, 14));

        // Add to panel
        panel.add(label);
        panel.add(starDateField);
        return panel;
    }

    // ---------------------------------------------------------------------
    // Stardate conversion method
    // - Parses input string expected in ISO format YYYY-MM-DD
    // - Uses LocalDate so leap years and day-of-year are accurate
    // - Formula:
    //     stardate = 1000 * (year - baseYear) + fractionalComponent
    //     fractionalComponent = 1000 * (dayOfYear - 1) / daysInYear
    //   The fractional part places the date proportionally through the year.
    // - On parse error or other exceptions, returns a clear "Invalid date format".
    // ---------------------------------------------------------------------
    private String convertToStardate(String input) {
        try {
            // Parse the input string into LocalDate. LocalDate.parse expects YYYY-MM-DD.
            LocalDate date = LocalDate.parse(input.trim());

            // Extract components needed for the stardate formula
            int year = date.getYear();                           // full 4-digit year
            int baseYear = 2323;                                 // chosen base stardate year constant
            int dayOfYear = date.getDayOfYear();                 // day index within year (1..365/366)
            int daysInYear = date.isLeapYear() ? 366 : 365;      // correct denominator for leap years

            // Compute stardate:
            // - 1000 units per year offset from baseYear
            // - fractional term scales day progress into the 0..999 range for that year
            double stardate = 1000.0 * (year - baseYear)
                    + (1000.0 * (dayOfYear - 1) / daysInYear);

            // Format result to two decimal places for consistent display
            return String.format("%.2f", stardate);

        } catch (Exception ex) {
            // Any parsing error or unexpected runtime error returns a clear, user-facing message
            // This prevents a DateTimeParseException from crashing the app when input is malformed
            return "Invalid date format";
        }
    }

    // ---------------------------------------------------------------------
    // Static method: convertToStardateStatic
    // - Public static version of the stardate conversion for headless mode or CLI use.
    // - Simplified error handling: returns "Invalid Date" on parse errors.
    // ---------------------------------------------------------------------
    public static String convertToStardateStatic(String input) {
        try {
            LocalDate date = LocalDate.parse(input.trim());
            int baseYear = 2323; // use your BASE_YEAR constant if present
            double yearDiff = date.getYear() - baseYear;
            double dayOfYear = date.getDayOfYear();
            return String.format("%.1f", yearDiff * 1000 + (dayOfYear / 365.0) * 1000);
        } catch (DateTimeParseException ex) {
            return "Invalid Date";
        }
    }

    // ---------------------------------------------------------------------
    // Static method: isValidDateFormatStatic
    // - Public static method to check date format validity without conversion.
    // - Returns true if the date string can be parsed into a LocalDate.
    // ---------------------------------------------------------------------
    public static boolean isValidDateFormatStatic(String input) {
        try {
            LocalDate.parse(input.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // Main entry point: schedule the GUI creation on the Event Dispatch Thread
    // ---------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        // Headless check: switch to CLI mode automatically on Replit / headless hosts
        if (GraphicsEnvironment.isHeadless()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Running in headless mode (CLI). Enter Earth date (YYYY-MM-DD) or blank to exit:");
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) break;
                if (isValidDateFormatStatic(line)) {
                    System.out.println("StarDate: " + convertToStardateStatic(line));
                } else {
                    System.out.println("Invalid date format. Use YYYY-MM-DD.");
                }
                System.out.println("\nEnter another date or blank to exit:");
            }
            System.out.println("Exiting.");
            return;
        }

        // Otherwise run the GUI as before
        SwingUtilities.invokeLater(() -> {
            try {
                new StarDateConverter().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
