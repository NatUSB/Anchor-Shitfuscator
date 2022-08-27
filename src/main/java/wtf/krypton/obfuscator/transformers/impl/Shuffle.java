package wtf.krypton.obfuscator.transformers.impl;

import org.objectweb.asm.tree.ClassNode;
import wtf.krypton.Krypton;
import wtf.krypton.obfuscator.transformers.Transformer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Shuffle implements Transformer {

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        classMap.values().forEach(c -> {
            shuffle(c.fields);
            shuffle(c.methods);
            shuffle(c.innerClasses);
            shuffle(c.interfaces);
            shuffle(c.attrs);
            shuffle(c.invisibleAnnotations);
            shuffle(c.visibleAnnotations);
            shuffle(c.invisibleTypeAnnotations);
            shuffle(c.visibleTypeAnnotations);
            c.fields.forEach(f -> {
                shuffle(f.attrs);
                shuffle(f.invisibleAnnotations);
                shuffle(f.visibleAnnotations);
                shuffle(f.invisibleTypeAnnotations);
                shuffle(f.visibleTypeAnnotations);
            });
            c.methods.forEach(m -> {
                shuffle(m.attrs);
                shuffle(m.invisibleAnnotations);
                shuffle(m.visibleAnnotations);
                shuffle(m.invisibleTypeAnnotations);
                shuffle(m.visibleTypeAnnotations);
                shuffle(m.exceptions);
                shuffle(m.invisibleLocalVariableAnnotations);
                shuffle(m.visibleLocalVariableAnnotations);
                shuffle(m.localVariables);
                shuffle(m.parameters);
            });
            c.innerClasses.clear();

            Krypton.instance.obfuscatedClasses++;
        });
    }

    private void shuffle(List<?> list) {
        if (list!=null) Collections.shuffle(list);
    }

}
