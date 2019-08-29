package org.webapp.dataset;

import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class FileDelete {
    public void deleteFiles(String directory, String extension) throws IOException {
        ExtensionFilter filter = new ExtensionFilter(extension);
        File dirFile = new File(directory);

        String[] list = dirFile.list(filter);
        File file = null;
        FileOutputStream fileOutputStream = null;
        if (list.length == 0) {
            return;
        }
        for (int i = 0; i < list.length; i++) {
            try {
                file = new File(directory + "\\" + list[i]);
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write("a".getBytes());
            } catch (Exception e) {
            } finally {
                fileOutputStream.flush();
                fileOutputStream.close();
                fileOutputStream = null;
                System.gc();
            }
            boolean isdeleted = file.delete();
            System.out.print(file);
            System.out.println("  deleted " + (isdeleted == true ? "SUCCESS!!" : "FAIL"));
        }
    }
}

class ExtensionFilter implements FilenameFilter {
    private String extension;

    public ExtensionFilter( String extension ) {
        this.extension = extension;
    }
    public boolean accept(File dir, String name) {
        return (name.endsWith(extension));
    }
}
