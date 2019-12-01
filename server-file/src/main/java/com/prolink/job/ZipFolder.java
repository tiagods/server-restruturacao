package com.prolink.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
    public static void main(String[] a) throws Exception {
        new ZipFolder().
        zipFolder("E:\\Obrigacoes\\contabil\\_old - obrigações de anos anteriores 2014\\DESLIGAMENTOS_( A PARTIR DE 22082008 )\\0011_28022010",
                "backup.zip");
    }

    public void zipFolder(String srcFolder, String destZipFile) throws IOException {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws IOException {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }
    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws IOException {
        File folder = new File(srcFolder);
        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }
}
