package wtf.krypton.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public interface Transformer {

    void transform(Map<String, ClassNode> classMap);

}
