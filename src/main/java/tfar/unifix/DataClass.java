package tfar.unifix;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.Set;

public class DataClass {
	public final Tag<Item> tag;
	public final Item prefer;
	public final Set<Item> except;

	public DataClass(Tag<Item> tag, Item prefer, Set<Item> except) {
		this.tag = tag;
		this.prefer = prefer;
		this.except = except;
	}

	public boolean matches(ItemStack stack) {
		return stack.getItem().isIn(tag) && !except.contains(stack.getItem());
	}
}
