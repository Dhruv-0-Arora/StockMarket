package me.dhruvarora.UI;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Taskbar.Feature;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Taskbar;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.json.simple.parser.ParseException;

import me.dhruvarora.Utility.StockData;

public class Frame extends JFrame {
    // holds all the content of the frame and is what components are appended to.
    public static JComponent content;
    // the mainPage is the JPanel which has all the graphs and historical data
    private MainPage mainPage;
    /*
     * the changeStocksPage is the JPanel which is where the user may add or remove
     * stocks that they would like to watch in form of a graph.
     */
    private ChangeStocksPage changeStocksPage;
    // instance of StockData class (this instance is avalible throughout the entire
    // project)
    private StockData stockData;

    /*
     * CONSTRUCTOR
     * 
     * builds the windowed frame
     * default JPanel is mainPage
     * 
     * also builds changeStocksPage but hides
     */
    public Frame() throws FileNotFoundException, IOException, ParseException {
        // sets the desktop icon for the project
        setIcons("../Icon.png");

        /*
         * setting the settings for the frame
         * 
         * not fullscreen by default
         */
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Stock Market Predictor");
        setLocationRelativeTo(null);
        setLocationByPlatform(true);

        /*
         * set background of frame to dark gray
         */
        content = (JComponent) this.getContentPane();
        content.setBackground(Color.darkGray);

        /*
         * initializing mainJPanel & global stockData instance
         */
        this.stockData = new StockData();
        mainPage = new MainPage(content, this, this.stockData);
        mainPage.initialize();

        // setting frame visible
        setVisible(true);

        /*
         * window RESIZING event
         * 
         * if run, then will delete and recreate open JPanel
         */
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (mainPage == null) {
                    changeStocksPage.dispose();
                    try {
                        changeStocksPage = new ChangeStocksPage(content, Frame.this, Frame.this.stockData);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    changeStocksPage.initialize();
                    Frame.this.revalidate();
                    repaint();
                }
            }
        });
    }

    /*
     * function to set the desktop getImage
     * 
     * works for macOS, windows, and linux
     * TODO: have it work for jar files
     */
    private void setIcons(String path) {
        // windows & linux
        setIconImage(Toolkit.getDefaultToolkit().getImage(Frame.class.getResource(path)));

        // MacOS
        if (Taskbar.getTaskbar().isSupported(Feature.ICON_IMAGE)) {
            Taskbar.getTaskbar().setIconImage(
                    Toolkit.getDefaultToolkit().getImage(
                            Frame.class.getResource(
                                    path)));
        }
    }

    /*
     * function to swap JPanel visible on frame
     * 
     * on swap, will delete old panel and recreate new one
     * then, it will refresh frame
     * 
     * exceptions traced back to mainPage class
     */
    public void swapScreens()
            throws FileNotFoundException, IOException, ParseException {

        // if swapping to changeStocksPage
        if (mainPage != null) {
            mainPage.dispose();
            mainPage = null;
            changeStocksPage = new ChangeStocksPage(content, this, this.stockData);
            changeStocksPage.initialize();
            this.revalidate();
            repaint();
        }

        // if swapping to mainPage
        else if (changeStocksPage != null) {
            changeStocksPage.dispose();
            changeStocksPage = null;
            mainPage = new MainPage(content, this, this.stockData);
            mainPage.initialize();
            this.revalidate();
            repaint();
        }
    }

    /*
     * function to refresh mainPage
     * - deletes mainpage and recreates
     * 
     * executed when refresh button is pressed
     */
    public void refresh()
            throws FileNotFoundException, IOException, ParseException {
        mainPage.dispose();
        mainPage = new MainPage(content, this, this.stockData);
        mainPage.initialize();
        this.revalidate();
        repaint();
    }

}
