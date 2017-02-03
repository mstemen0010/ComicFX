/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comicfx;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Mathias Stemen Class to serve as an ebook "adaptor" for any number of
 * ebook formats (currrently epub)
 */
public final class JBook {

    public enum JBookType {

        Unknown,
        Novel,
        Novella,
        ShortStory;
    }
    
    private Path myPath;
    private Book myBook;
    private int frameW;
    private int frameH;
    private String title = "";
    private Navigator myNavigator = null;        
    private DefaultListModel<String> chapterListModel = new DefaultListModel<>();
    private JBookType myJBookType = JBookType.Unknown;
    private Image coverImage = null;
    private int tabCount = -1;
    private boolean hasContent = false;

    JBook(Book readEpub, Path readEpubPath, int coverWidth, int coverHeight) {

        myBook = readEpub;
        myPath = readEpubPath;
       
        myNavigator = new Navigator(readEpub);
        this.initTo(readEpub, coverWidth, coverHeight);
        title = readEpub.getTitle();

        this.hasContent = true;
    }

    public void reset() {
        this.myBook = null;
        this.hasContent = false;        
        this.coverImage = null;
        this.myJBookType = JBookType.Unknown;        
        this.myPath = null;
        this.myNavigator = new Navigator();
        this.tabCount = -1;
        this.title = "";
    }

    public void initTo(Book readEpub, int w, int h) {
        myBook = readEpub;
        this.frameW = w;
        this.frameH = h;        
        myNavigator = new Navigator(readEpub);
        title = readEpub.getTitle();

        Resource imageResource = getCover();
        InputStream imageIS;
        try {
            if (imageResource != null) {
                imageIS = imageResource.getInputStream();
                createImage(imageIS, myPath.toString(), frameW, frameH);

            }
            buildTOC();

        } catch (IOException ex) {
            // ex.printStackTrace();
        }

        this.hasContent = true;
    }

    private void buildTOC() throws IOException {
        TableOfContents toc = this.getTOC();
        List<TOCReference> tocr = toc.getTocReferences();
        StringBuilder tocs = new StringBuilder("");
        // String tocAsString = jBookToOpen.getBook().getContents().toString();

        Iterator<TOCReference> it = tocr.iterator();
        while (it.hasNext()) {
            if (myJBookType.equals(JBookType.Unknown)) {
                myJBookType = JBookType.ShortStory;
            }
            TOCReference tocRefLine = it.next();
            String tocLine = tocRefLine.getTitle();
            if (tocLine.toLowerCase().contains("chapter")) {
                this.myJBookType = JBookType.Novel;
            }                         

            this.chapterListModel.addElement(tocLine);
        }

    }

 

    /**
     * @return the chapterListModel
     */
    public DefaultListModel<String> getChapterListModel() {
        return chapterListModel;
    }

    /**
     * @param chapterListModel the chapterListModel to set
     */
    public void setChapterListModel(DefaultListModel<String> chapterListModel) {
        this.chapterListModel = chapterListModel;
    }

    /**
     * @return the myJBookType
     */
    public JBookType getMyJBookType() {
        return myJBookType;
    }

    /**
     * @param myJBookType the myJBookType to set
     */
    public void setMyJBookType(JBookType myJBookType) {
        this.myJBookType = myJBookType;
    }

    public Book getBook() {
        return this.myBook;
    }

    public String getTitle() {
        return this.myBook.getTitle();
    }

    public List<String> getDescriptions() {
        return this.myBook.getMetadata().getDescriptions();
    }

    public List<Author> getAuthors() {
        return this.myBook.getMetadata().getAuthors();
    }

    public Resource getCover() {
        return this.myBook.getCoverImage();
    }

    public TableOfContents getTOC() {
        return this.myBook.getTableOfContents();
    }

    public Resource getChapterAsResource(int index) {
        Resource retResource;
        Spine spine = this.myBook.getSpine();
        retResource = spine.getResource(index);

        return retResource;
    }

    public String getChapter(int index) throws IOException {
        Spine spine = this.myBook.getSpine();
        Resource res = spine.getResource(index);

        String chapterAsStr = null;
        StringBuilder sb = new StringBuilder("");
        Reader chapterReader = res.getReader();
        String pageContent = IOUtils.toString(res.getReader());
        if (chapterReader != null) {
//            while( chapterReader.ready())
//            {
//                sb.append(chapterReader.read());
//            }
        }
        sb.append(pageContent);
        return sb.toString();
    }
   
    
    private void createImage(InputStream inStream, String iconName, int width, int height) {
        // ImageIcon result = null;
        // String fullIconPath = "/viewer/icons/" + iconName + ".png";
        try {

            // Image image = ImageIO.read(JBookView.class.getResourceAsStream(fullIconPath));
            Image image = ImageIO.read(inStream);
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            this.coverImage = scaledImage;
        } catch (IOException e) {
//            log.error("Icon \'" + fullIconPath + "\' not found");
        }
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public int getTabCount() {
        return this.tabCount;
    }


    public Image getCoverImage() {
        return coverImage;
    }

    public Navigator getNavigator() {
        return this.myNavigator;
    }
}
