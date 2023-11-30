package club.someoneice.rmid;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
@Mod(modid = RMID.MODID, dependencies = "after:spongemixins@[1.1.0,)")
public class RMID {
    public static final String MODID = "rmid";

    static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    @Mod.EventHandler
    public void ServerStartEvent(FMLServerStartedEvent event) throws IOException {
        saveIdTable();
    }



    @Mod.EventHandler
    public void c(FMLInitializationEvent event) {
        GameRegistry.registerBlock(new TestBlock(), "BlockName");
    }

    static void saveIdTable() throws IOException {
        ((FMLControlledNamespacedRegistry<Item>) Item.itemRegistry).typeSafeIterable().forEach((it) -> {
            String rgName = Item.itemRegistry.getNameForObject(it);
            int id = Item.getIdFromItem(it);
            RegistryHelper.ItemIdMapping.put(rgName, id);
        });

        ((FMLControlledNamespacedRegistry<Block>) Block.blockRegistry).typeSafeIterable().forEach((it) -> {
            String rgName = Block.blockRegistry.getNameForObject(it);
            int id = Block.getIdFromBlock(it);
            RegistryHelper.BlockIdMapping.put(rgName, id);
        });

        String path = System.getProperty("user.dir") + File.separator + "config" + File.separator + "rmid" + File.separator + "idConfig.json";
        String blockPath = System.getProperty("user.dir") + File.separator + "config" + File.separator + "rmid" + File.separator + "idBlockConfig.json";
        File file = new File(path);
        File blockFile = new File(blockPath);
        String data = gson.toJson(RegistryHelper.ItemIdMapping);
        String blockData = gson.toJson(RegistryHelper.BlockIdMapping);
        Files.write(file.toPath(), data.getBytes());
        Files.write(blockFile.toPath(), blockData.getBytes());
    }
}
