package tfar.unifix;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigParser {

	public static Gson g = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static final File configFile = new File("config/" + Unifix.MODID + ".json");

	public static final List<DataClass> data = new ArrayList<>();

	public static void handleConfig() {
		writeDefaultConfig();
		readConfig();
	}

	private static void writeDefaultConfig() {

		Path path = Paths.get("config/");
		if (!Files.isDirectory(path))
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}

		if (configFile.exists()) return;
		try {
			JsonArray jsonArray = new JsonArray();
			defaults.stream().map(ResourceLocation::new)
							.map(ItemTags.getCollection()::get)
							.filter(Objects::nonNull)
							.map(Tag::getAllElements)
							.filter(col -> !col.isEmpty())
							.map(Collection::iterator)
							.map(Iterator::next)
							.map(ForgeRegistryEntry::getRegistryName)
							.map(ResourceLocation::toString)
							.forEach(itemName -> {
								JsonObject jsonObject = new JsonObject();
								jsonObject.addProperty("tag", itemName);
								jsonObject.addProperty("prefer", itemName);
								JsonArray exclusion = new JsonArray();
								exclusion.add(itemName);
								jsonObject.add("exclude", exclusion);
								jsonArray.add(jsonObject);
							});
			FileWriter writer = new FileWriter(configFile);
			writer.write(g.toJson(jsonArray));
			writer.flush();
		} catch (IOException ohno) {
			throw new RuntimeException("this should be impossible, but report if it happens", ohno);
		}
	}

	private static final Set<String> defaults = new HashSet<>();

	static {
		defaults.add("forge:ingots/copper");
		defaults.add("forge:ingots/lead");
		defaults.add("forge:ingots/silver");
		defaults.add("forge:ingots/tin");

		defaults.add("forge:nuggets/copper");
		defaults.add("forge:nuggets/lead");
		defaults.add("forge:nuggets/silver");
		defaults.add("forge:nuggets/tin");
	}

	private static void readConfig() {
		try {
			data.clear();
			FileReader reader = new FileReader(configFile);
			JsonArray cfg = new JsonParser().parse(reader).getAsJsonArray();
			for (JsonElement entry : cfg) {
				JsonObject jsonObject = entry.getAsJsonObject();
				Tag<Item> tag = new ItemTags.Wrapper(new ResourceLocation(jsonObject.get("tag").getAsString()));
				Optional<Item> item = Registry.ITEM.getValue(new ResourceLocation(jsonObject.get("prefer").getAsString()));
				Item item1 = null;
				JsonElement jsonObject1 = jsonObject.get("except");
				Set<Item> except = new HashSet<>();
				if (jsonObject1 != null) {
					JsonArray array = jsonObject1.getAsJsonArray();
					array.forEach(jsonElement -> except.add(Registry.ITEM.getOrDefault(new ResourceLocation(jsonElement.getAsString()))));
				}
				if (item.isPresent()) {
					item1 = item.get();
				} else {
					System.out.println("invalid item found: " + jsonObject.get("prefer").getAsString());
				}
				if (item1 != null) {
					data.add(new DataClass(tag, item1, except));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
