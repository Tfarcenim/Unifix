package tfar.unifix;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Unifix.MODID)
public class Unifix {
	// Directly reference a log4j logger.

	public static final String MODID = "unifix";

	private static final Logger LOGGER = LogManager.getLogger();

	public Unifix() {}

	public static void hookCrafting(Map<ResourceLocation, IRecipe<?>> recipeMap) {
		recipeMap.forEach((resourceLocation, iRecipe) -> {
			if (iRecipe instanceof ShapedRecipe || iRecipe instanceof ShapelessRecipe) {
				for (DataClass dataClass : ConfigParser.data) {
					ItemStack ogOutput = iRecipe.getRecipeOutput();
					if (dataClass.matches(ogOutput)) {
						if (iRecipe instanceof ShapelessRecipe) {
							((ShapelessRecipe) iRecipe).recipeOutput =
											new ItemStack(dataClass.prefer, ogOutput.getCount(), ogOutput.getTag());
						} else if (iRecipe instanceof ShapedRecipe) {
							((ShapedRecipe) iRecipe).recipeOutput =
											new ItemStack(dataClass.prefer, ogOutput.getCount(), ogOutput.getTag());
						}
					}
				}
			}
		});
	}

	public static void hookCooking(Map<ResourceLocation, IRecipe<?>> recipeMap) {
		recipeMap.forEach((resourceLocation, iRecipe) -> {
			if (iRecipe instanceof AbstractCookingRecipe) {
				for (DataClass dataClass : ConfigParser.data) {
					ItemStack ogOutput = iRecipe.getRecipeOutput();
					if (dataClass.matches(ogOutput)) {
						((AbstractCookingRecipe) iRecipe).result =
										new ItemStack(dataClass.prefer, ogOutput.getCount(), ogOutput.getTag());
					}
				}
			}
		});
	}
}
