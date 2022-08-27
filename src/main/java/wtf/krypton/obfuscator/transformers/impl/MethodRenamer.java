package wtf.krypton.obfuscator.transformers.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import wtf.krypton.Krypton;
import wtf.krypton.obfuscator.transformers.Transformer;
import wtf.krypton.obfuscator.util.ASMUtil;
import wtf.krypton.obfuscator.util.ClassPath;
import wtf.krypton.obfuscator.util.UniqueStringGenerator;

import java.util.*;
import java.util.function.Predicate;

public class MethodRenamer implements Transformer {

    private ClassPath env;
    private Map<String, ClassNode> classMap;
    private final UniqueStringGenerator generator = new UniqueStringGenerator.Default();

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        this.classMap = classMap;
        // loads all jre libraries from 'java.class.path' system property
        this.env = ClassPath.getInstance();
        // todo: add more in-depth verification
        List<String> pass = Arrays.asList("main", "createUI");
        // reset the unique string generator, so that is starts at 'a'
        generator.reset();
        Map<String, String> mappings = new HashMap<>();
        List<MethodNode> methods = new LinkedList<>();
        for (ClassNode c : classMap.values()) {
            Krypton.instance.obfuscatedClasses++;
            if((c.access & Opcodes.ACC_INTERFACE) != 0) continue;
            methods.addAll(c.methods);
        }
        // shuffle the methods so that there isn't a naming pattern
        Collections.shuffle(methods);
        // create obfuscated name mappings
        methods:
        for (MethodNode m : methods) {
            ClassNode owner = getOwner(m);
            // skip entry points, constructors etc
            if (m.name.indexOf('<') != -1 || pass.contains(m.name) || (m.access & Opcodes.ACC_NATIVE) != 0) {
                continue;
            }
            Stack<ClassNode> stack = new Stack<>();
            stack.add(owner);
            // check this is the top-level method
            while (stack.size() > 0) {
                ClassNode node = stack.pop();
                if (node != owner && getMethod(node, m.name, m.desc) != null)
                    // not top-level member
                    continue methods;
                // push superclass
                ClassNode parent = getClassNode(node.superName);
                if (parent != null)
                    stack.push(parent);
                // push interfaces
                Set<ClassNode> interfaces = new HashSet<>();
                String[] interfacesNames = node.interfaces.toArray(new String[0]);
                for (String iname : interfacesNames) {
                    ClassNode iface = getClassNode(iname);
                    if (iface != null) {
                        interfaces.add(iface);
                    }
                }
                stack.addAll(interfaces);
            }
            // generate obfuscated name
            String name = generator.next();
            stack.add(owner);
            // go through all sub-classes, and define the new name
            // regardless of if the method exists in the given class or not
            while (stack.size() > 0) {
                ClassNode node = stack.pop();
                String key = node.name + '.' + m.name + m.desc;
                mappings.put(key, name);
                // push subclasses
                classMap.values().forEach(c -> {
                    if (c.superName.equals(node.name) || c.interfaces.contains(node.name))
                        stack.push(c);
                });
            }
        }
        ASMUtil.applyMappings(classMap, mappings);
    }

    private MethodNode getMethod(ClassNode node, String name, String desc) {
        return findFirst(node.methods, m -> m.name.equals(name) && m.desc.equals(desc));
    }

    private ClassNode getClassNode(String name) {
        if (name == null) return null;
        ClassNode n = classMap.get(name);
        return n == null ? env.get(name) : n;
    }

    private ClassNode getOwner(MethodNode m) {
        return findFirst(classMap.values(), c -> c.methods.contains(m));
    }

    private <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
        for (T t : collection)
            if (predicate.test(t))
                return t;
        return null;
    }

}
