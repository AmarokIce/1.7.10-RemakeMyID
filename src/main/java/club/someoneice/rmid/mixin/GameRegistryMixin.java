package club.someoneice.rmid.mixin;


import club.someoneice.rmid.RegistryHelper;
import com.google.common.collect.ObjectArrays;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.lang.reflect.Constructor;

@Mixin(GameRegistry.class)
public abstract class GameRegistryMixin {
    @Inject(method = "registerItem(Lnet/minecraft/item/Item;Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/item/Item;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void registerItemHead(Item item, String name, String modid, CallbackInfoReturnable<Item> info) {
        if (RegistryHelper.ItemIdMapping.isEmpty() || RegistryHelper.BlockIdMapping.isEmpty()) {
            try {
                RegistryHelper.readData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (item instanceof ItemBlock) return;

        ModContainer mc = Loader.instance().activeModContainer();
        modid = mc.getModId();
        String rgName = modid + ":" + name;

        if (!RegistryHelper.shouldGetItemIDByForge(rgName)) {
            int id;
            if (RegistryHelper.ItemIdMapping.containsKey(rgName)) {
                id = RegistryHelper.ItemIdMapping.get(rgName);
            } else {
                id = RegistryHelper.emptyIdForItem.get(0);
                RegistryHelper.emptyIdForItem.remove(0);
            }

            Item.itemRegistry.addObject(id, name, item);
            info.setReturnValue(item);
        }
    }


/*
    @Inject(method = "registerBlock(Lnet/minecraft/block/Block;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/block/Block;", at = @At("HEAD"), remap = false, cancellable = true)
    private static void registerBlockHead(Block block, Class<? extends ItemBlock> itemclass, String name, Object[] itemCtorArgs, CallbackInfoReturnable<Block> cir) {

        ModContainer mc = Loader.instance().activeModContainer();
        String modid = mc.getModId();
        String rgName = modid + ":" + name;

        if (!RegistryHelper.shouldGetBlockIDByForge(rgName)) return;

        try {
            assert block != null : "registerBlock: block cannot be null";
            ItemBlock i;
            if (itemclass != null) {
                Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + 1];
                ctorArgClasses[0] = Block.class;
                for (int idx = 1; idx < ctorArgClasses.length; idx++) {
                    ctorArgClasses[idx] = itemCtorArgs[idx - 1].getClass();
                }
                Constructor<? extends ItemBlock> itemCtor = itemclass.getConstructor(ctorArgClasses);
                i = itemCtor.newInstance(ObjectArrays.concat(block, itemCtorArgs));

                int id = RegistryHelper.ItemIdMapping.get(rgName);
                Block.blockRegistry.addObject(id, name, block);
                Item.itemRegistry.addObject(id, name, i);
                cir.setReturnValue(block);
            }
        }
        catch (Exception e) {
            FMLLog.log(Level.ERROR, e, "Caught an exception during block registration");
            throw new LoaderException(e);
        }
    }

 */
}
