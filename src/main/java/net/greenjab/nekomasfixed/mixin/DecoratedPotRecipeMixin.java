package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.other.PotEngravingDecoration;
import net.greenjab.nekomasfixed.registry.other.PotFaceDecoration;
import net.greenjab.nekomasfixed.registry.registries.ComponentRegistry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(DecoratedPotRecipe.class)
public class DecoratedPotRecipeMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void customAssemble(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        DecoratedPotRecipe recipe = (DecoratedPotRecipe)(Object)this;
        PotDecorations decorations = new PotDecorations(DecoratedPotRecipe.back(input).getItem(), DecoratedPotRecipe.left(input).getItem(), DecoratedPotRecipe.right(input).getItem(), DecoratedPotRecipe.front(input).getItem());
        DataComponentPatch components = DataComponentPatch.builder().set(DataComponents.POT_DECORATIONS, decorations)
                .set(ComponentRegistry.POT_FACE_BLOCK, new PotFaceDecoration(Optional.of(Blocks.BRICKS.defaultBlockState())))
                .set(ComponentRegistry.POT_ENGRAVED_DECORATIONS, PotEngravingDecoration.EMPTY)
                .build();
         cir.setReturnValue(recipe.result.apply(components));
    }
}
