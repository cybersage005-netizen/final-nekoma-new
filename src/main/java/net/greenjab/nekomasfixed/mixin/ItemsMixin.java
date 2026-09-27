package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.registry.item.CarpetItem;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.item.ItemAccessor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.waypoints.Waypoint;
import org.apache.commons.lang3.function.TriFunction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Locale;
import java.util.function.BiFunction;


@Mixin(Items.class)
public class ItemsMixin {


	@Shadow
	private static Item registerBlock(BlockItemId id, Block block, BiFunction<Block, Item.Properties, Item> itemFactory, Item.Properties properties) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@WrapOperation(method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ColorCollection;registerBlockItems(" + "Lnet/minecraft/world/level/block/ColorCollection;" + "Lnet/minecraft/world/level/block/ColorCollection;" + "Lorg/apache/commons/lang3/function/TriFunction;" + ")Lnet/minecraft/world/level/block/ColorCollection;"),
			slice = @Slice(
					from = @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;CARPET:Lnet/minecraft/world/level/block/ColorCollection;", opcode = Opcodes.GETSTATIC),
					to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;CARPET:Lnet/minecraft/world/level/block/ColorCollection;", opcode = Opcodes.PUTSTATIC)))
	private static ColorCollection<Item> carpetCustom(ColorCollection<BlockItemId> ids, ColorCollection<Block> blocks, TriFunction<BlockItemId, Block, DyeColor, Item> itemFactory, Operation<ColorCollection<Item>> original) {

		TriFunction<BlockItemId, Block, DyeColor, Item> myFactory = (id, block, color) ->
				Items.registerBlock(id, block, (b, p) -> new CarpetItem(color,
						p.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(color))
								.cookingFuel(ContextIntProviders.COOKING_TIME_WOOL_CARPETS), false));

		return original.call(ids, blocks, myFactory);
	}

	@WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;"), slice = @Slice( from =
	@At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;CLOCK:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
	@At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;CLOCK:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
	private static Item wallFloorClock(ResourceKey<Item> id, Operation<Item> original) {
		return registerBlock(BlockItemId.create("clock"), BlockRegistry.CLOCK, (block, settings) -> new StandingAndWallBlockItem(
						block, BlockRegistry.WALL_CLOCK, Direction.DOWN, Waypoint.addHideAttribute(settings)),
				new Item.Properties());
	}
}