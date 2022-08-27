package wtf.krypton.obfuscator.transformers.impl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import wtf.krypton.Krypton;
import wtf.krypton.obfuscator.transformers.Transformer;

import java.util.HashMap;
import java.util.Map;

public class StripDebugInformation implements Transformer {

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        Map<String, ClassNode> map = new HashMap<>();
        for (ClassNode cn : classMap.values()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(writer);
            ClassNode clone = new ClassNode();
            new ClassReader(writer.toByteArray()).accept(clone, ClassReader.SKIP_DEBUG);
            map.put(clone.name, clone);

            Krypton.instance.obfuscatedClasses++;
        }
        classMap.clear();
        classMap.putAll(map);
    }

}
