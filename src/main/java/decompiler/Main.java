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
    private static final boolean DEBUG = false;
    private static void debugPrintf(String s, Object ... args) {
        if (DEBUG) {
            System.err.printf(s, args);
        }
    }

    private static Printer printer = new Printer() {
        protected static final String TAB = "    ";
        protected static final String NEWLINE = "\n";

        protected int indentationCount = 0;
        protected StringBuilder sb = new StringBuilder();

        @Override public String toString() { return sb.toString(); }

        @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {
            debugPrintf(">>> start - %d %d %d\n", maxLineNumber, majorVersion, minorVersion);
        }
        @Override public void end() {
            debugPrintf(">>> end\n");
        }

        @Override public void printText(String text) {
            debugPrintf(">>> printText - %s\n", text);
            sb.append(text);
        }
        @Override public void printNumericConstant(String constant) {
            debugPrintf(">>> printNumericConstant - %s\n", constant);
            sb.append(constant);
        }
        @Override public void printStringConstant(String constant, String ownerInternalName) {
            debugPrintf(">>> printStringConstant - %s %s\n", constant, ownerInternalName);
            sb.append(constant);
        }
        @Override public void printKeyword(String keyword) {
            debugPrintf(">>> printKeyword - %s\n", keyword);
            sb.append(keyword);
        }
        @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
            debugPrintf(">>> printDeclaration - %d %s %s %s\n", type, internalTypeName, name, descriptor);
            sb.append(name);
        }
        @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
            debugPrintf(">>> printReference - %d %s %s %s %s\n", type, internalTypeName, name, descriptor, ownerInternalName);
            sb.append(name);
        }

        @Override public void indent() { this.indentationCount++; }
        @Override public void unindent() { this.indentationCount--; }

        @Override public void startLine(int lineNumber) {
            debugPrintf(">>> startLine - %d\n", lineNumber);
            for (int i=0; i<indentationCount; i++) sb.append(TAB);
        }
        @Override public void endLine() {
            debugPrintf(">>> endLine\n");
            sb.append(NEWLINE);
        }
        @Override public void extraLine(int count) {
            debugPrintf(">>> extraLine - %d\n", count);
            while (count-- > 0) sb.append(NEWLINE);
        }

        @Override public void startMarker(int type) {
            debugPrintf(">>> startMarker - %d\n", type);
        }
        @Override public void endMarker(int type) {
            debugPrintf(">>> endMarker - %d\n", type);
        }
    };

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
