package club.someoneice.rmid;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import cpw.mods.fml.common.Loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegistryHelper {
    public static final Map<String, Integer> ItemIdMapping = Maps.newHashMap();
    public static final List<Integer> emptyIdForItem = Lists.newArrayList();

    public static final Map<String, Integer> BlockIdMapping = Maps.newHashMap();

    public static void readData() throws IOException {
        Set<String> newModids = Sets.newHashSet();
        Loader.instance().getModList().forEach(it -> newModids.add(it.getModId()));

        String path = System.getProperty("user.dir") + File.separator + "config" + File.separator + "rmid";
        File file = new File(path, "idConfig.json");
        File blockFile = new File(path, "idBlockConfig.json");

        if (!file.exists() || !file.isFile()) {
            file.getParentFile().mkdirs();
            file.createNewFile();

            ItemIdMapping.put("None", -1);
        }

        if (!blockFile.exists() || !blockFile.isFile()) {
            blockFile.getParentFile().mkdirs();
            blockFile.createNewFile();

            BlockIdMapping.put("None", -1);
        }

        ItemIdMapping.putAll(RMID.gson.fromJson(new String(Files.readAllBytes(file.toPath())), new TypeToken<Map<String, Integer>>() {}.getType()));
        BlockIdMapping.putAll(RMID.gson.fromJson(new String(Files.readAllBytes(blockFile.toPath())), new TypeToken<Map<String, Integer>>() {}.getType()));
    }

    public static boolean shouldGetItemIDByForge(String name) {
        return !ItemIdMapping.containsKey(name) && !emptyIdForItem.isEmpty();
    }

    public static boolean shouldGetBlockIDByForge(String name) {
        return !BlockIdMapping.containsKey(name);
    }

}
