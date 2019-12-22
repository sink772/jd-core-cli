/*
 * A simple Java class decompiler
 */
package decompiler;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.printer.Printer;

import java.io.File;
import java.io.FileWriter;

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
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(loader, printer, internalName);
            // overwrite the output file
            FileWriter writer = new FileWriter(outfile);
            writer.write(printer.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
