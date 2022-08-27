package wtf.krypton.obfuscator.transformers.impl;

import org.objectweb.asm.tree.ClassNode;
import wtf.krypton.Krypton;
import wtf.krypton.obfuscator.transformers.Transformer;
import wtf.krypton.obfuscator.util.ASMUtil;
import wtf.krypton.obfuscator.util.UniqueStringGenerator;

import java.util.*;

public class ClassRenamer implements Transformer {

    private final UniqueStringGenerator generator = new UniqueStringGenerator.Default();
    private final String basePackage = "";
    private final List<String> skip;

    public ClassRenamer(String... skip) {
        for (int i = 0; i < skip.length; i++) {
            skip[i] = skip[i].replace('.', '/');
        }
        this.skip = Arrays.asList(skip);    }

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        generator.reset();
        Map<String, String> remap = new HashMap<>();
        List<String> keys = new ArrayList<>(classMap.keySet());
        // shuffle order in which names are assigned
        // so that they're not always assigned the same name
        Collections.shuffle(keys);
        for (String key : keys) {
            Krypton.instance.obfuscatedClasses++;
            ClassNode cn = classMap.get(key);
            String name = cn.name;
            if (!skip.contains(name)) {
                name = generator.next();
                name = basePackage + "/" + name;
            }
            remap.put(cn.name, name);
        }
        ASMUtil.applyMappings(classMap, remap);
    }

}
