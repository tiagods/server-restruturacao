import org.apache.commons.io.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileStoreAttributeView;

public class DiskAnaliser{
    public static void main(String[] args) throws IOException {
        FileStore fs = Files.getFileStore(Paths.get("c://"));
        System.out.println(fs.getTotalSpace()+"\t"+fs.getUsableSpace()+"\t"+fs.type()+"\t"+fs.getUnallocatedSpace());
    }
}
