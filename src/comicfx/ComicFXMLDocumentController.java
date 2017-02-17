/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comicfx;

import comicfx.epubLibLite.Librarian;
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
import java.util.List;
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
    
    @FXML
    private ScrollPane comicPagePane;
            
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
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
                    Librarian librarian = Librarian.getLibrarian(comicFile);
                    List<BufferedImage> pages = librarian.getPages();
                    comicPageStack = new JComicPageStack(librarian);
                    if (librarian != null) {                          
                        String title = "Fill me in";
                        comicPageStack = new JComicPageStack( librarian );
                        this.maxPages = comicPageStack.size();
                        System.out.println("Loaded comic. This title: " + title + "  has: (" + (comicPageStack.size() + 1) + ") pages available");
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
        }
        
        if ( pagePointer == 0) {
            this.prevButton.setDisable(true);
        }
        else {
            this.prevButton.setDisable(false);
        }
            
        this.pageNumLabel.setText("Page: " + String.valueOf(pagePointer));
        BufferedImage bi = comicPageStack.get(pagePointer);
        // WritableImage wr =  new WritableImage(bi.getWidth(), bi.getHeight());
        Image image = SwingFXUtils.toFXImage(bi, null);        
        this.ComicPageView.setImage(image);
        // this.ComicPageView.resize(900, 700); 
        this.comicPagePane.setContent(null);
        this.comicPagePane.setContent(ComicPageView);
        // this.comicPagePane.setPrefWidth(bi.getWidth());
        // this.comicPagePane.setPrefHeight(bi.getHeight());
        
    }

    @FXML
    public void showPrevPage() {
        pagePointer--;
        if( pagePointer <= 0  ) {
            pagePointer = 0;
            this.nextButton.setDisable(false);
        }
        else {
            this.nextButton.setDisable(false);
        }
        this.pageNumLabel.setText("Page: " + String.valueOf(pagePointer));
        BufferedImage bi = comicPageStack.get(pagePointer);
        // WritableImage wr =  new WritableImage(bi.getWidth(), bi.getHeight());
        Image image = SwingFXUtils.toFXImage(bi, null);        
        this.ComicPageView.setImage(image);
        // this.ComicPageView.resize(900, 700);
        this.comicPagePane.setContent(null);
        this.comicPagePane.setContent(ComicPageView);
        // this.comicPagePane.setPrefWidth(bi.getWidth());
        // this.comicPagePane.setPrefHeight(bi.getHeight());
        
        
    }
 
}
