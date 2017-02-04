/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comicfx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 *
 * @author matthew.stemen
 */
public class ComicFXMLDocumentController implements Initializable {
    
    private String comicLibraryPathName = "C:\\Users\\matthew.stemen\\Documents\\books\\";    
    EpubReader ePubReader = new EpubReader();
    FileInputStream fs = null;
    private Path currentPath;
    private File comicFile;
    private int maxPages =0;
    private int pageNumber = 1;
    JComicPageStack comicPageStack;
    Navigator comicBookNavigator;
    int pagePointer = 0;
               
    @FXML
    private ImageView ComicPageView;
    
    @FXML
    private TextField jarPathValueDisplay;
    
    @FXML
    private Button browseButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Button prevButton;
    
    @FXML 
    private Label pageNumLabel;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
    }    
    
    private void loadInNewComicEpub() {
        
        comicPageStack = new JComicPageStack(comicBookNavigator);
    }
    
    @FXML
    public void setComicPath(ActionEvent ae) {
        System.out.println("Click");

        FileChooser fileChooser = new FileChooser();
        comicFile = new File(comicLibraryPathName);
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Java Class Jars (*.epub)", "*.epub");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(comicFile);

        //Show open <span class="adtext" id="adtext_1">file dialog</span>
        comicFile = fileChooser.showOpenDialog(null);
        // set the "currentPath" based on path and selection
        StringBuilder sb = new StringBuilder(fileChooser.getInitialDirectory().getAbsolutePath());
        String testStr = comicFile.getAbsolutePath();
        currentPath = comicFile.toPath();
        
        loadComicBook( testStr, 500, 500);

      
        
    }
    
    public void loadComicBook( String fileName, int w, int h ) {
            try {
                FileReader reader = new FileReader(fileName);
                fs = new FileInputStream(fileName);
                if (reader != null) {
                try {
                    // Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));\
                    Book readBook = ePubReader.readEpub(fs);
                    JBook tempBook = new JBook(readBook, currentPath, w, h);
                    if (tempBook != null) {  
                        comicBookNavigator = tempBook.getNavigator();
                        comicPageStack = new JComicPageStack( comicBookNavigator );
                        this.maxPages = comicPageStack.size();
                        showFirstPage();
                        
                    }
                } catch (IOException ex) {
                }
            }
        } catch (FileNotFoundException ex) {
        }
    }
    
    private void showFirstPage( ) {
        BufferedImage bi = comicPageStack.get(0);
        this.pagePointer = 0;
        WritableImage wr =  new WritableImage(bi.getWidth(), bi.getHeight());
        Image image = SwingFXUtils.toFXImage(bi, null);        
        this.ComicPageView.setImage(image);
        this.ComicPageView.resize(900, 700);
        this.prevButton.setDisable(true);
        this.nextButton.setDisable(false);
    }
    
    @FXML
    public void showNextPage() {
        pagePointer++;
        if( pagePointer >= this.maxPages ) {
            pagePointer = this.maxPages - 1;  
            this.prevButton.setVisible(false);
        }
        else {
            this.prevButton.setVisible(true);
        }
        this.pageNumLabel.setText("Page: " + String.valueOf(pagePointer));
        BufferedImage bi = comicPageStack.get(pagePointer);
        WritableImage wr =  new WritableImage(bi.getWidth(), bi.getHeight());
        Image image = SwingFXUtils.toFXImage(bi, null);        
        this.ComicPageView.setImage(image);
        this.ComicPageView.resize(900, 700);                
    }

    @FXML
    public void showPrevPage() {
        pagePointer--;
        if( pagePointer <= 0  ) {
            pagePointer = 0;
        }       
        this.nextButton.setVisible(true);
        this.pageNumLabel.setText("Page: " + String.valueOf(pagePointer));
        BufferedImage bi = comicPageStack.get(pagePointer);
        WritableImage wr =  new WritableImage(bi.getWidth(), bi.getHeight());
        Image image = SwingFXUtils.toFXImage(bi, null);        
        this.ComicPageView.setImage(image);
        this.ComicPageView.resize(900, 700);
        
        
    }
 
}
