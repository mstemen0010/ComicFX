/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comicfx.epubLibLite;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author matthew.stemen
 */
public class Librarian {

    private static HashMap<String, ZipEntry> zipEntryMap = new HashMap<>();
    private ZipFile classZipFile = null;
    private static File currentEpub = null;
    private static ArrayList<BufferedImage> pages = new ArrayList<>();

    private static Librarian librarian = null;

    private Librarian() {
        // singleton
    }

    public static Librarian getLibrarian() {
        if (librarian == null) {
            librarian = new Librarian();
        }

        return librarian;
    }

    public static Librarian getLibrarian(File epubFile) throws IOException {
        if (librarian == null) {
            librarian = new Librarian();
            librarian.setEpub(epubFile);
            librarian.buildPages(epubFile);
        }
        return librarian;
    }
    public static void setEpub(File epub) {
        Librarian.currentEpub = epub;
    }

    protected static void clearPageList() {
        if (pages != null) {
            pages.clear();
            zipEntryMap.clear();
            pages = null;
        }
    }
    
    public List<BufferedImage> getPages() {
        return Librarian.pages;
    }

    private String getClassName(String entry) {
        String retValue = null;
        if (entry != null) {
            String[] tokens = entry.split("/");
            // we want the last token
            retValue = tokens[tokens.length - 1];

        }
        return retValue;
    }

    private void buildPages(File epubFILE) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(epubFILE.getPath());
        ZipInputStream zis = new ZipInputStream(fis);
        this.classZipFile = new ZipFile(epubFILE.getPath());
        ZipEntry entry;

        clearPageList();
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().toLowerCase().contains("jpg") || entry.getName().toLowerCase().contains("png")) {
                System.out.println("Got image: " + entry.getName());

            } else if (!entry.getName().contains("class")) {
                continue;
            }

            StringBuilder sb = new StringBuilder("");
            String name = this.getClassName(entry.getName());
            // sb.append( className ).append(",").append(entry.getSize());            
            zipEntryMap.put(name, entry);
            BufferedImage nbi = explodeImage(name, entry);
            // System.out.println(sb.toString());
            if( nbi != null ) {
                pages.add(nbi);
            }           
            // consume all the data from this entry
            while (zis.available() > 0) {
                zis.read();
            }
            // I could close the entry, but getNextEntry does it automatically
            // zis.closeEntry()           
        }

    }
    
    private ZipEntry getEntry(String key) {
        ZipEntry foundEntry = null;
        if (this.zipEntryMap != null) {
            foundEntry = this.zipEntryMap.get(key);
        }

        return foundEntry;
    }
    
    protected BufferedImage explodeImage(String className, ZipEntry entry) {
        BufferedImage explodedImage = null;
        
        try {
            byte[] data = new byte[300000];
            int byteRead;

            BufferedOutputStream bout = null;
            int fLen = (int) entry.getSize();
            InputStream in = classZipFile.getInputStream(entry);
            byteRead = 0;
            data = new byte[fLen];
            // now either write an image or a class...
            boolean isImage = (entry.getName().toLowerCase().contains("jpg") || entry.getName().toLowerCase().contains("png"));
            if (isImage) {                
                explodedImage = ImageIO.read(in);                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return explodedImage;
    }
}
