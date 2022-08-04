package me.dhruvarora.Utility.ChangeStockPage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.RoundRectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.json.simple.parser.ParseException;

import me.dhruvarora.UI.ChangeStocksPage;
import me.dhruvarora.UI.Frame;
import me.dhruvarora.Utility.StockData;

/*
 * extension of JButton
 * 
 * creates a rounded edge button which checks if a user inputted a valid stock
 */
public class ChangeStockButton extends JButton {
    // color of button text
    private final Color textColor = Color.white;
    // color of button background (Currently blue)
    private final Color backgroundColor = new Color(67, 129, 255);
    // color of button border
    private final Color borderColor = Color.BLACK;
    // new shape which later is turned into button
    private Shape shape;
    // instance of text field used to get input
    private final InputField inputField;
    // instance of changeStocksPage used to make red invalid text visible
    private final ChangeStocksPage changeStocksPage;
    // instance of StockData utility
    private final StockData stockData;
    // instance of frame
    private final Frame frame;

    public ChangeStockButton(InputField inputField, ChangeStocksPage changeStocksPage, Frame frame, StockData stockData)
            throws FileNotFoundException, IOException, ParseException {

        /* Initializing global variables */
        this.frame = frame;
        this.inputField = inputField;
        this.changeStocksPage = changeStocksPage;
        this.stockData = stockData;

        /*
         * setting defaults
         * 
         * allowing button's corners to become round
         * setting font to Arial 20 pt
         */
        setText("Change Watched Stocks");
        setForeground(textColor);
        setOpaque(false);
        setPreferredSize(new Dimension(300, 50));
        setBackground(backgroundColor);
        setFont(new Font("Arial", Font.PLAIN, 20));
        setBorderPainted(false);
        setFocusPainted(false);

        /*
         * Listener for if button pressed
         * 
         * if pressed - check if input is valid to add or remove. then executes
         * 
         * may throw IOException or ParseException traced from StockData.java
         */
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputedText = ChangeStockButton.this.inputField.getText(); // gets text inputed in field
                try {
                    /*
                     * if field is blank then makes red invalid text appear on page
                     */
                    if (inputedText.length() == 0) {
                        ChangeStockButton.this.changeStocksPage.inputIsInvalid();
                        return;
                    }

                    /*
                     * deletes stock if already exists
                     * 
                     * then, swaps screens to main screen
                     */
                    else if (ChangeStockButton.this.stockData.isStockExisting(inputedText)) {
                        ChangeStockButton.this.stockData.deleteStock(inputedText, ChangeStockButton.this.frame);
                        ChangeStockButton.this.frame.swapScreens();
                        return;
                    }

                    /*
                     * checks if ticker is valid and exists
                     * 
                     * if not, makes red invalid text appear on page
                     * and
                     * returns out
                     */
                    else if (!stockData.checkStockValidity(inputedText)) {
                        ChangeStockButton.this.changeStocksPage.inputIsInvalid();
                        return;
                    }

                    /*
                     * adding company name and ticker to watchlist
                     * - is appended to StockMarkets.json
                     * 
                     * then swaps screen to main page
                     */
                    else {
                        stockData.addStockToWatchList(inputedText);
                        ChangeStockButton.this.frame.swapScreens();
                    }
                } catch (IOException | ParseException e2) {
                    e2.printStackTrace();
                }
            }
        });

    }

    /*
     * rest of code used to rounds corners of button
     * 
     * 
     * 
     * 
     * 
     * 
     * overriding JButton functions to custom draw button
     */
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(borderColor);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
    }

    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        }
        return shape.contains(x, y);
    }

}
