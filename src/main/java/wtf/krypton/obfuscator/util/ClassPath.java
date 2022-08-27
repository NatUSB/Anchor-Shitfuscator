package wtf.krypton.obfuscator.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ClassPath extends HashMap<String, ClassNode> {

    private static ClassPath instance;

    private ClassPath() {
        String[] classpath = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        Stream.of(classpath).filter(path -> path.endsWith(".jar")).forEach(this::append);
    }

    private void append(String jarPath) {
        try(JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                try (InputStream in = jar.getInputStream(entry)) {
                    if (entry.getName().endsWith(".class")) {
                        byte[] bytes;
                        try (ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
                            byte[] buf = new byte[256];
                            for (int n; (n = in.read(buf)) != -1; ) {
                                tmp.write(buf, 0, n);
                            }
                            bytes = tmp.toByteArray();
                        }
                        ClassNode c = new ClassNode();
                        ClassReader r = new ClassReader(bytes);
                        r.accept(c, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
                        put(c.name, c);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading jar file");
        }
    }

    public static ClassPath getInstance() {
        if (instance == null)
            instance = new ClassPath();
        return instance;
    }

}