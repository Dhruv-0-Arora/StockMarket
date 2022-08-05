package me.dhruvarora.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.parser.ParseException;

import me.dhruvarora.Utility.StockData;
import me.dhruvarora.Utility.ChangeStockPage.BackButton;
import me.dhruvarora.Utility.ChangeStockPage.ChangeStockButton;
import me.dhruvarora.Utility.ChangeStockPage.InputField;

public class ChangeStocksPage extends JPanel {
    /* Global variables */
    private final JComponent content; // stores the frame content
    private final Frame frame; // stores the frame instance
    private GridBagConstraints constraints;

    private final JLabel invalidFieldInput; // stores instance of invisible red text reading "Invalid Ticker Symbol!"
    private final InputField InputField = new InputField(); // stores instance of input field
    private final ChangeStockButton changeStockButton; // stores instance of submit field button
    private final StockData stockData;

    /*
     * CONSTRUCTOR:
     * builds page for changing stocks shown on the mainpage JPanel
     * 
     * Layout:
     * - this (implements "null" layout || no layout (absolute positioning))
     * - - back button placed in top left of screen to go back to mainpage
     * - - cluster (gridbaglayout) hold the directions, field, button, and invalid
     * text label
     * 
     * extends JPanel
     */
    public ChangeStocksPage(JComponent content, Frame frame, StockData stockData)
            throws FileNotFoundException, IOException, ParseException {
        /*
         * initalizes all the variables
         * 
         * sets globals for JPanel
         */
        this.stockData = stockData;
        this.content = content;
        this.frame = frame;
        this.setBackground(Color.darkGray); // color darkGray to match frame
        this.setLayout(null);
        this.setSize(frame.getSize()); // sets JPanel frame same size as frame
        this.setVisible(false); // hides JPanel by default

        /*
         * creating cluster: JPanel
         * holds the directions, field, button, and invalid text label
         * 
         * centered in middle of screen with absolute positioning & math
         */
        JPanel cluster = new JPanel(new GridBagLayout());
        cluster.setBackground((Color.darkGray));

        // creating title
        constraints = new GridBagConstraints();
        cluster.add(createTitle(), constraints);

        // creating input field
        constraints = new GridBagConstraints();
        constraints.gridy = 1;
        constraints.insets = new Insets(10, 0, 0, 0);
        cluster.add(InputField, constraints);

        // creates change stock button
        constraints = new GridBagConstraints();
        constraints.gridy = 2;
        constraints.insets = new Insets(20, 0, 0, 0);
        changeStockButton = new ChangeStockButton(InputField, this, frame, this.stockData);
        cluster.add(changeStockButton, constraints);

        // adds cluster to this (extended) JPanel
        cluster.setSize(cluster.getPreferredSize());
        // setting cluster location to middle of screen
        cluster.setLocation((frame.getWidth() / 2) - (cluster.getWidth() / 2),
                (frame.getHeight() / 2) - (cluster.getHeight() / 2));
        this.add(cluster);

        /* end of cluster */

        // creates button to navigate back to mainPage
        this.add(new BackButton(frame));

        // creates invisible text for invalid input
        invalidFieldInput = invalidInput();
        // setting location of text below cluster
        invalidFieldInput.setLocation(
                (int) Math.round(cluster.getLocation().getX()
                        + ((cluster.getSize().width / 2) - invalidFieldInput.getSize().getWidth() / 2)),
                (int) cluster.getLocation().getY() + cluster.getSize().height + 20);
        this.add(invalidFieldInput); // adding it to JPanel

    }

    /*
     * Function to initalize JPanel
     * 
     * adds to frame
     * makes button default button if "enter" key pressed
     * sets visible
     */
    public void initialize() {
        content.add(this);
        frame.getRootPane().setDefaultButton(changeStockButton);
        this.setVisible(true);
    }

    /*
     * Function to remove JPanel
     * 
     * removes from frame
     * sets invisible
     */
    public void dispose() {
        content.remove(this);
        this.setVisible(false);
    }

    /*
     * returns title for page
     * 
     * used in top of cluster
     */
    private JLabel createTitle() {
        JLabel title = new JLabel();

        title.setText(
                "Input ticker symbol for stock you would like to watch or input company name for stock you would like to remove:");
        title.setForeground(Color.white);
        title.setFont(new Font("Arial", Font.PLAIN, 20));

        return title;
    }

    /*
     * returns red invisible label for below cluster
     * 
     * becomes visible if input is invalid
     */
    private JLabel invalidInput() {
        JLabel label = new JLabel();

        label.setText("Invalid Ticker Symbol!");
        label.setForeground(Color.red);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setSize(label.getPreferredSize());
        label.setVisible(false);

        return label;
    }

    /*
     * Function to make invalidInput visible
     * used in ChangeStockButton class
     */
    public void inputIsInvalid() {
        invalidFieldInput.setVisible(true);
        InputField.setText("");
    }

}
