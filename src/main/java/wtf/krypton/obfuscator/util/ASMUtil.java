package wtf.krypton.obfuscator.util;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.Map;

@UtilityClass
public class ASMUtil {

    public ClassNode getOwner(FieldNode f, Map<String, ClassNode> classMap) {
        for (ClassNode c : classMap.values())
            if (c.fields.contains(f))
                return c;
        return null;
    }

    public void applyMappings(Map<String, ClassNode> classMap, Map<String, String> remap) {
        SimpleRemapper remapper = new SimpleRemapper(remap);
        for (ClassNode node : new ArrayList<>(classMap.values())) {
            ClassNode copy = new ClassNode();
            ClassRemapper adapter = new ClassRemapper(copy, remapper);
            node.accept(adapter);
            classMap.put(node.name, copy);
        }
    }

}
