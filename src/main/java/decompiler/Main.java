/*
 * A simple Java class decompiler
 */
package decompiler;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static Loader loader = new Loader() {
        @Override
        public byte[] load(String internalName) throws LoaderException {
            System.out.println("*** load, internalName=" + internalName);
            InputStream is = this.getClass().getResourceAsStream("/" + internalName + ".class");
            if (is == null) {
                Path path = Paths.get(internalName);
                try {
                    return Files.readAllBytes(path);
                } catch (IOException e) {
                    throw new LoaderException(e);
                }
            } else {
                try (InputStream in=is; ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int read = in.read(buffer);
                    while (read > 0) {
                        out.write(buffer, 0, read);
                        read = in.read(buffer);
                    }
                    return out.toByteArray();
                } catch (IOException e) {
                    throw new LoaderException(e);
                }
            }
        }

        @Override
        public boolean canLoad(String internalName) {
            System.out.println("*** canLoad, internalName=" + internalName);
            return this.getClass().getResource("/" + internalName + ".class") != null;
        }
    };

    private static Printer printer = new Printer() {
        protected static final String TAB = "    ";
        protected static final String NEWLINE = "\n";

        protected int indentationCount = 0;
        protected StringBuilder sb = new StringBuilder();

        @Override public String toString() { return sb.toString(); }

        @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {
            System.out.printf(">>> start - %d %d %d\n", maxLineNumber, majorVersion, minorVersion);
        }
        @Override public void end() {
            System.out.println(">>> end");
        }

        @Override public void printText(String text) {
            System.out.printf(">>> printText - %s\n", text);
            sb.append(text);
        }
        @Override public void printNumericConstant(String constant) {
            System.out.printf(">>> printNumericConstant - %s\n", constant);
            sb.append(constant);
        }
        @Override public void printStringConstant(String constant, String ownerInternalName) {
            System.out.printf(">>> printStringConstant - %s %s\n", constant, ownerInternalName);
            sb.append(constant);
        }
        @Override public void printKeyword(String keyword) {
            System.out.printf(">>> printKeyword - %s\n", keyword);
            sb.append(keyword);
        }
        @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
            System.out.printf(">>> printDeclaration - %d %s %s %s\n", type, internalTypeName, name, descriptor);
            sb.append(name);
        }
        @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
            System.out.printf(">>> printReference - %d %s %s %s %s\n", type, internalTypeName, name, descriptor, ownerInternalName);
            sb.append(name);
        }

        @Override public void indent() { this.indentationCount++; }
        @Override public void unindent() { this.indentationCount--; }

        @Override public void startLine(int lineNumber) {
            System.out.printf(">>> startLine - %d\n", lineNumber);
            for (int i=0; i<indentationCount; i++) sb.append(TAB);
        }
        @Override public void endLine() {
            System.out.println(">>> endLine");
            sb.append(NEWLINE);
        }
        @Override public void extraLine(int count) {
            System.out.printf(">>> extraLine - %d\n", count);
            while (count-- > 0) sb.append(NEWLINE);
        }

        @Override public void startMarker(int type) {
            System.out.printf(">>> startMarker - %d\n", type);
        }
        @Override public void endMarker(int type) {
            System.out.printf(">>> endMarker - %d\n", type);
        }
    };

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar jdecomp.jar <classfile> <outfile>");
            return;
        }
        String infile = args[0];
        File outfile = new File(args[1]);
        try {
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(loader, printer, infile);
            if (outfile.createNewFile()) {
                FileWriter writer = new FileWriter(outfile);
                writer.write(printer.toString());
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
