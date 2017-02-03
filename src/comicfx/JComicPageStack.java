/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comicfx;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;

/**
 *
 * @author matthew.stemen
 */
public class JComicPageStack  extends ArrayList<BufferedImage>{

    int currentPageNumber = 0;
    private Book myBook;
    
    JComicPageStack(Navigator myNavigator) {
        
        // retrieve all the images and convert them to pages
        myBook = myNavigator.getBook();        
        Resources bookResources = myBook.getResources();
        List<Resource> gifImageResources = bookResources.getResourcesByMediaType(MediatypeService.GIF);
        List<Resource> jpgImageResources = bookResources.getResourcesByMediaType(MediatypeService.JPG);
        List<Resource> pngImageResources = bookResources.getResourcesByMediaType(MediatypeService.PNG);
        Iterator<Resource> itr = jpgImageResources.iterator();
        int imageCount = 0;
        while( itr.hasNext() ) {
            Resource r = itr.next();
            // check to see if this is an image type of resource
           
            if( r != null ) {
                MediaType rType = r.getMediaType();
                if( rType == MediatypeService.JPG || rType== MediatypeService.PNG || rType == MediatypeService.GIF ) {            
                    try {
                        byte[] ba = r.getData();
                        BufferedImage bufIm = ImageIO.read(new ByteArrayInputStream(ba));       
                        this.add(bufIm);

                    } catch (IOException ex) {
                        Logger.getLogger(JComicPageStack.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
                
        }
        
    }

    void setChapterContents(Resource chapRes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
