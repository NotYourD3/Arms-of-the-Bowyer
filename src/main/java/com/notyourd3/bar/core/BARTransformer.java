package com.notyourd3.bar.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BARTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.item.ItemStack")) {
            return patchItemStack(basicClass);
        }
        return basicClass;
    }

    private byte[] patchItemStack(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            // 匹配方法名 (b 是 1.7.10 中 ItemStack.onPlayerStoppedUsing 的混淆名)
            if ((method.name.equals("onPlayerStoppedUsing") || method.name.equals("func_77974_b")|| method.name.equals("b"))
                    && (method.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;I)V") || method.desc.equals("(Lahb;Lyz;I)V"))) {
                InsnList toInject = new InsnList();
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
                toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));

                // 5. 调用你的静态 Hook
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/notyourd3/bar/core/BARHooks",
                        "onItemStackStopUsing",
                        "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;I)Z",
                        false));
                LabelNode notCanceled = new LabelNode();
                toInject.add(new JumpInsnNode(Opcodes.IFEQ, notCanceled));
                toInject.add(new InsnNode(Opcodes.RETURN));
                toInject.add(notCanceled);
                method.instructions.insert(toInject);
            }
            if ((method.name.equals("useItemRightClick") || method.name.equals("func_77957_a")|| method.name.equals("a"))
                    && (method.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;")
                    ||method.desc.equals("(Lahb;Lyz;)Ladd"))) {

                InsnList toInject = new InsnList();
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1)); // World
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 2)); // EntityPlayer
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/notyourd3/bar/core/BARHooks",
                        "onItemRightClickHook",
                        "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Z",
                        false));
                LabelNode proceed = new LabelNode();
                toInject.add(new JumpInsnNode(Opcodes.IFEQ, proceed));
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                toInject.add(new InsnNode(Opcodes.ARETURN));
                toInject.add(proceed);
                method.instructions.insert(toInject);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
