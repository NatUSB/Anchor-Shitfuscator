package wtf.krypton.obfuscator.transformers.impl;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import wtf.krypton.Krypton;
import wtf.krypton.obfuscator.transformers.Transformer;
import wtf.krypton.obfuscator.util.ASMUtil;
import wtf.krypton.obfuscator.util.UniqueStringGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class FieldRenamer implements Transformer {

    private final UniqueStringGenerator generator = new UniqueStringGenerator.Default();

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        Map<String, String> remap = new HashMap<>();
        generator.reset();
        List<FieldNode> fields = new ArrayList<>();
        for (ClassNode c : classMap.values()) {
            fields.addAll(c.fields);
            Krypton.instance.obfuscatedClasses++;
        }
        Collections.shuffle(fields);
        for (FieldNode f : fields) {
            ClassNode c = ASMUtil.getOwner(f, classMap);
            String name = generator.next();
            Stack<ClassNode> stack = new Stack<>();
            stack.add(c);
            while (stack.size() > 0) {
                ClassNode node = stack.pop();
                String key = node.name + "." + f.name;
                remap.put(key, name);
                stack.addAll(classMap.values().stream().
                        filter(cn -> cn.superName.equals(node.name)).
                        collect(Collectors.toList()));
            }
        }
        ASMUtil.applyMappings(classMap, remap);
    }

}
