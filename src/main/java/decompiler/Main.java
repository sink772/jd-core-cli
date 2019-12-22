/*
 * A simple Java class decompiler
 */
package decompiler;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import org.jd.core.v1.model.classfile.ClassFile;
import org.jd.core.v1.service.deserializer.classfile.ClassFileDeserializer;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar jd-core-cli.jar <classfile> <outfile>");
            return;
        }
        String basePath = new File(args[0]).getParent();
        String filename = new File(args[0]).getName();
        File outfile = new File(args[1]);
        if (!filename.endsWith(".class")) {
            System.err.println("Error: invalid <classfile>");
            return;
        }
        try {
            Loader loader = new DirectoryLoader(new File(basePath));
            Printer printer = new SimplePrinter();
            String internalName = filename.substring(0, filename.lastIndexOf(".class"));
            String thisClassName = getThisClassName(loader, internalName);
            Map<String, String> fileMap = Map.of(thisClassName, internalName);
            Loader mapLoader = new MapLoader(new File(basePath), fileMap);
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(mapLoader, printer, thisClassName);
            // overwrite the output file
            FileWriter writer = new FileWriter(outfile);
            writer.write(printer.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MapLoader extends DirectoryLoader {
        private final Map<String, String> fileMap;

        public MapLoader(File base, Map<String, String> fileMap) {
            super(base);
            this.fileMap = fileMap;
        }

        @Override
        public byte[] load(String internalName) throws LoaderException {
            internalName = fileMap.getOrDefault(internalName, internalName);
            return super.load(internalName);
        }
    }

    private static String getThisClassName(Loader loader, String internalName) throws Exception {
        ClassFileDeserializer deserializer = new ClassFileDeserializer();
        ClassFile classFile = deserializer.loadClassFile(loader, internalName);
        return classFile.getInternalTypeName();
    }
}
