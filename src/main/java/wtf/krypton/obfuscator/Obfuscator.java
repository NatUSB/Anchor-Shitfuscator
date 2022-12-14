package wtf.krypton.obfuscator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import wtf.krypton.obfuscator.transformers.Transformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Obfuscator {

    private final Map<String, ClassNode> classMap = new HashMap<>();
    private final Map<String, byte[]> files = new HashMap<>();

    private void supplyJar(JarFile jar) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            try (InputStream in = jar.getInputStream(entry)) {
                byte[] bytes;
                try (ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[256];
                    for (int n; (n = in.read(buf)) != -1; ) {
                        tmp.write(buf, 0, n);
                    }
                    bytes = tmp.toByteArray();
                }
                if (!entry.getName().endsWith(".class")) {
                    getFiles().put(entry.getName(), bytes);
                    continue;
                }
                ClassNode c = new ClassNode();
                new ClassReader(bytes).accept(c, getReadFlags());
                getClassMap().put(c.name, c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void supply(Path root) {
        try {
            if (root.toString().endsWith(".jar")) {
                supplyJar(new JarFile(root.toAbsolutePath().toString()));
                return;
            }
            if (!Files.isDirectory(root))
                throw new IOException("Cannot specify files, only classpath root directory.");
            Files.walkFileTree(root, new HashSet<>(), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".jar")) {
                        supplyJar(new JarFile(file.toAbsolutePath().toString()));
                        return FileVisitResult.CONTINUE;
                    }
                    String relative = root.relativize(file).toString();
                    byte[] bytes = Files.readAllBytes(file);
                    if (relative.endsWith(".class")) {
                        ClassNode node = new ClassNode();
                        ClassReader reader = new ClassReader(bytes);
                        reader.accept(node, getReadFlags());
                        getClassMap().put(node.name, node);
                    } else {
                        getFiles().put(relative, bytes);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getClassAmount() {
        return getClassMap().values().size();
    }

    public void apply(Transformer transformer) {
        transformer.transform(getClassMap());
        // re-populate class map, so that any changed names are registered
        List<ClassNode> values = new ArrayList<>(getClassMap().values());
        getClassMap().clear();
        for (ClassNode cn : values)
            getClassMap().put(cn.name, cn);
    }

    public void write(Path dest) {
        try {
            Files.deleteIfExists(dest);
            StandardOpenOption[] override = {StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};
            if (dest.toString().endsWith(".jar")) {
                try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(dest, override))) {
                    writeJar(out);
                }
            } else {
                for (ClassNode node : getClassMap().values()) {
                    ClassWriter writer = new ClassWriter(getWriteFlags());
                    node.accept(writer);
                    /**/
                    Files.write(Paths.get(dest.toString(), node.name + ".class"), writer.toByteArray(), override);
                }
                for (Map.Entry<String, byte[]> entry : getFiles().entrySet()) {
                    Files.write(Paths.get(dest.toString(), entry.getKey()), entry.getValue(), override);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeJar(JarOutputStream out) throws IOException {
        for (ClassNode node : getClassMap().values()) {
            JarEntry entry = new JarEntry(node.name + ".class");
            out.putNextEntry(entry);
            ClassWriter writer = new ClassWriter(getWriteFlags());
            node.accept(writer);
            out.write(writer.toByteArray());
            out.closeEntry();
        }
        for (Map.Entry<String, byte[]> entry : getFiles().entrySet()) {
            out.putNextEntry(new JarEntry(entry.getKey()));
            out.write(entry.getValue());
            out.closeEntry();
        }
    }

    public int getReadFlags() {
        return ClassReader.EXPAND_FRAMES;
    }

    public int getWriteFlags() {
        return ClassWriter.COMPUTE_MAXS;
    }

    public Map<String, byte[]> getFiles() {
        return files;
    }

    public Map<String, ClassNode> getClassMap() {
        return classMap;
    }

}
