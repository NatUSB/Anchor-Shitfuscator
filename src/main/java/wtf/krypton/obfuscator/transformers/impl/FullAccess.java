package wtf.krypton.obfuscator.transformers.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import wtf.krypton.Krypton;
import wtf.krypton.obfuscator.transformers.Transformer;

import java.util.Map;

public class FullAccess implements Transformer {

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        classMap.values().forEach(c -> {
            c.access = access(c.access);
            c.methods.forEach(m -> m.access = access(m.access));
            c.fields.forEach(m -> m.access = access(m.access));

            Krypton.instance.obfuscatedClasses++;
        });
    }

    private int access(int access) {
        int a = Opcodes.ACC_PUBLIC;
        if ((access & Opcodes.ACC_NATIVE) != 0) a |= Opcodes.ACC_NATIVE;
        if ((access & Opcodes.ACC_ABSTRACT) != 0) a |= Opcodes.ACC_ABSTRACT;
        if ((access & Opcodes.ACC_ANNOTATION) != 0) a |= Opcodes.ACC_ANNOTATION;
        if ((access & Opcodes.ACC_BRIDGE) != 0) a |= Opcodes.ACC_BRIDGE;
        if ((access & Opcodes.ACC_ENUM) != 0) a |= Opcodes.ACC_ENUM;
        if ((access & Opcodes.ACC_FINAL) != 0) a |= Opcodes.ACC_FINAL;
        if ((access & Opcodes.ACC_INTERFACE) != 0) a |= Opcodes.ACC_INTERFACE;
        if ((access & Opcodes.ACC_MANDATED) != 0) a |= Opcodes.ACC_MANDATED;
        if ((access & Opcodes.ACC_STATIC) != 0) a |= Opcodes.ACC_STATIC;
        if ((access & Opcodes.ACC_STRICT) != 0) a |= Opcodes.ACC_STRICT;
        if ((access & Opcodes.ACC_SUPER) != 0) a |= Opcodes.ACC_SUPER;
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) a |= Opcodes.ACC_SYNCHRONIZED;
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) a |= Opcodes.ACC_SYNTHETIC;
        if ((access & Opcodes.ACC_TRANSIENT) != 0) a |= Opcodes.ACC_TRANSIENT;
        if ((access & Opcodes.ACC_VARARGS) != 0) a |= Opcodes.ACC_VARARGS;
        if ((access & Opcodes.ACC_VOLATILE) != 0) a |= Opcodes.ACC_VOLATILE;
        return a;
    }

}
