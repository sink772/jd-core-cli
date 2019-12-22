package decompiler;

import org.jd.core.v1.api.printer.Printer;

public class SimplePrinter implements Printer {
    private static final boolean DEBUG = false;
    private static void debugPrintf(String s, Object ... args) {
        if (DEBUG) {
            System.err.printf(s, args);
        }
    }

    protected static final String TAB = "    ";
    protected static final String NEWLINE = "\n";

    protected int indentationCount = 0;
    protected StringBuilder sb = new StringBuilder();

    @Override
    public String toString() { return sb.toString(); }

    @Override
    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
        debugPrintf(">>> start - %d %d %d\n", maxLineNumber, majorVersion, minorVersion);
    }

    @Override
    public void end() {
        debugPrintf(">>> end\n");
    }

    @Override
    public void printText(String text) {
        debugPrintf(">>> printText - %s\n", text);
        sb.append(text);
    }

    @Override
    public void printNumericConstant(String constant) {
        debugPrintf(">>> printNumericConstant - %s\n", constant);
        sb.append(constant);
    }

    @Override
    public void printStringConstant(String constant, String ownerInternalName) {
        debugPrintf(">>> printStringConstant - %s %s\n", constant, ownerInternalName);
        sb.append(constant);
    }

    @Override
    public void printKeyword(String keyword) {
        debugPrintf(">>> printKeyword - %s\n", keyword);
        sb.append(keyword);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        debugPrintf(">>> printDeclaration - %d %s %s %s\n", type, internalTypeName, name, descriptor);
        sb.append(name);
    }

    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
        debugPrintf(">>> printReference - %d %s %s %s %s\n", type, internalTypeName, name, descriptor, ownerInternalName);
        sb.append(name);
    }

    @Override
    public void indent() { this.indentationCount++; }

    @Override
    public void unindent() { this.indentationCount--; }

    @Override
    public void startLine(int lineNumber) {
        debugPrintf(">>> startLine - %d\n", lineNumber);
        for (int i=0; i<indentationCount; i++) sb.append(TAB);
    }

    @Override
    public void endLine() {
        debugPrintf(">>> endLine\n");
        sb.append(NEWLINE);
    }

    @Override
    public void extraLine(int count) {
        debugPrintf(">>> extraLine - %d\n", count);
        while (count-- > 0) sb.append(NEWLINE);
    }

    @Override
    public void startMarker(int type) {
        debugPrintf(">>> startMarker - %d\n", type);
    }

    @Override
    public void endMarker(int type) {
        debugPrintf(">>> endMarker - %d\n", type);
    }
}
