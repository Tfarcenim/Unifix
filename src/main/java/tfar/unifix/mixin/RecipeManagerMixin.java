package tfar.unifix.mixin;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.unifix.ConfigParser;
import tfar.unifix.Unifix;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Shadow private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes;

	@Inject(method = "apply",at = @At("RETURN"))
	private void unidictv2(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo ci){
		recipes = new HashMap<>(recipes);
		ConfigParser.handleConfig();
		recipes.put(IRecipeType.CRAFTING,new HashMap<>(recipes.get(IRecipeType.CRAFTING)));
		recipes.put(IRecipeType.SMELTING,new HashMap<>(recipes.get(IRecipeType.SMELTING)));
		recipes.put(IRecipeType.BLASTING,new HashMap<>(recipes.get(IRecipeType.BLASTING)));
		recipes.put(IRecipeType.SMOKING,new HashMap<>(recipes.get(IRecipeType.SMOKING)));

		Unifix.hookCrafting(recipes.get(IRecipeType.CRAFTING));
		Unifix.hookCooking(recipes.get(IRecipeType.SMELTING));
		Unifix.hookCooking(recipes.get(IRecipeType.BLASTING));
		Unifix.hookCooking(recipes.get(IRecipeType.SMOKING));

	}
}
