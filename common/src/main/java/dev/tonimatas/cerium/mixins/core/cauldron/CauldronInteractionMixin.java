package dev.tonimatas.cerium.mixins.core.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(CauldronInteraction.class)
public abstract class CauldronInteractionMixin {

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static void bootStrap() {
        Map<Item, CauldronInteraction> map = CauldronInteraction.EMPTY.map();
        CauldronInteraction.addDefaultInteractions(map);
        map.put(Items.POTION, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (PotionUtils.getPotion(itemStack) != Potions.WATER) {
                return InteractionResult.PASS;
            } else {
                if (!level.isClientSide) {
                    // CraftBukkit start
                    if (!LayeredCauldronBlock.changeLevel(blockState, level, blockPos, Blocks.WATER_CAULDRON.defaultBlockState(), player, CauldronLevelChangeEvent.ChangeReason.BOTTLE_EMPTY)) {
                        return InteractionResult.SUCCESS;
                    }
                    // CraftBukkit end
                    Item item = itemStack.getItem();
                    player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    // level.setBlockAndUpdate(blockPos, Blocks.WATER_CAULDRON.defaultBlockState()); // CraftBukkit
                    level.playSound((Player)null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, blockPos);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        });
        Map<Item, CauldronInteraction> map2 = CauldronInteraction.WATER.map();
        CauldronInteraction.addDefaultInteractions(map2);
        map2.put(Items.BUCKET, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            return CauldronInteraction.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(Items.WATER_BUCKET), (blockStatex) -> {
                return (Integer)blockStatex.getValue(LayeredCauldronBlock.LEVEL) == 3;
            }, SoundEvents.BUCKET_FILL);
        });
        map2.put(Items.GLASS_BOTTLE, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (!level.isClientSide) {
                // CraftBukkit start
                if (!LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos, player, CauldronLevelChangeEvent.ChangeReason.BOTTLE_FILL)) {
                    return InteractionResult.SUCCESS;
                }
                // CraftBukkit end
                Item item = itemStack.getItem();
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                // LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos); // CraftBukkit
                level.playSound((Player)null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, blockPos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
        map2.put(Items.POTION, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if ((Integer)blockState.getValue(LayeredCauldronBlock.LEVEL) != 3 && PotionUtils.getPotion(itemStack) == Potions.WATER) {
                if (!level.isClientSide) {
                    // CraftBukkit start
                    if (!LayeredCauldronBlock.changeLevel(blockState, level, blockPos, blockState.cycle(LayeredCauldronBlock.LEVEL), player, CauldronLevelChangeEvent.ChangeReason.BOTTLE_EMPTY)) {
                        return InteractionResult.SUCCESS;
                    }
                    // CraftBukkit end
                    player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                    // level.setBlockAndUpdate(blockPos, (BlockState)blockState.cycle(LayeredCauldronBlock.LEVEL)); // CraftBukkit
                    level.playSound((Player)null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, blockPos);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        });
        CauldronInteraction SHULKER_BOX = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            Block block = Block.byItem(itemStack.getItem());
            if (!(block instanceof ShulkerBoxBlock)) {
                return InteractionResult.PASS;
            } else {
                if (!level.isClientSide) {
                    // CraftBukkit start
                    if (!LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos, player, CauldronLevelChangeEvent.ChangeReason.SHULKER_WASH)) {
                        return InteractionResult.SUCCESS;
                    }
                    // CraftBukkit end
                    ItemStack itemStack2 = new ItemStack(Blocks.SHULKER_BOX);
                    if (itemStack.hasTag()) {
                        itemStack2.setTag(itemStack.getTag().copy());
                    }

                    player.setItemInHand(interactionHand, itemStack2);
                    player.awardStat(Stats.CLEAN_SHULKER_BOX);
                    // LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos); // CraftBukkit
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        };
        CauldronInteraction BANNER = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (BannerBlockEntity.getPatternCount(itemStack) <= 0) {
                return InteractionResult.PASS;
            } else {
                if (!level.isClientSide) {
                    // CraftBukkit start
                    if (!LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos, player, CauldronLevelChangeEvent.ChangeReason.BANNER_WASH)) {
                        return InteractionResult.SUCCESS;
                    }
                    // CraftBukkit end
                    ItemStack itemStack2 = itemStack.copyWithCount(1);
                    BannerBlockEntity.removeLastPattern(itemStack2);
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    if (itemStack.isEmpty()) {
                        player.setItemInHand(interactionHand, itemStack2);
                    } else if (player.getInventory().add(itemStack2)) {
                        player.inventoryMenu.sendAllDataToRemote();
                    } else {
                        player.drop(itemStack2, false);
                    }

                    player.awardStat(Stats.CLEAN_BANNER);
                    // LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos); // CraftBukkit
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        };
        CauldronInteraction DYED_ITEM = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            Item item = itemStack.getItem();
            if (!(item instanceof DyeableLeatherItem dyeableLeatherItem)) {
                return InteractionResult.PASS;
            } else if (!dyeableLeatherItem.hasCustomColor(itemStack)) {
                return InteractionResult.PASS;
            } else {
                if (!level.isClientSide) {
                    // CraftBukkit start
                    if (!LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos, player, CauldronLevelChangeEvent.ChangeReason.ARMOR_WASH)) {
                        return InteractionResult.SUCCESS;
                    }
                    // CraftBukkit end
                    dyeableLeatherItem.clearColor(itemStack);
                    player.awardStat(Stats.CLEAN_ARMOR);
                    // LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos); // CraftBukkit
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        };
        map2.put(Items.LEATHER_BOOTS, DYED_ITEM);
        map2.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
        map2.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
        map2.put(Items.LEATHER_HELMET, DYED_ITEM);
        map2.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
        map2.put(Items.WHITE_BANNER, BANNER);
        map2.put(Items.GRAY_BANNER, BANNER);
        map2.put(Items.BLACK_BANNER, BANNER);
        map2.put(Items.BLUE_BANNER, BANNER);
        map2.put(Items.BROWN_BANNER, BANNER);
        map2.put(Items.CYAN_BANNER, BANNER);
        map2.put(Items.GREEN_BANNER, BANNER);
        map2.put(Items.LIGHT_BLUE_BANNER, BANNER);
        map2.put(Items.LIGHT_GRAY_BANNER, BANNER);
        map2.put(Items.LIME_BANNER, BANNER);
        map2.put(Items.MAGENTA_BANNER, BANNER);
        map2.put(Items.ORANGE_BANNER, BANNER);
        map2.put(Items.PINK_BANNER, BANNER);
        map2.put(Items.PURPLE_BANNER, BANNER);
        map2.put(Items.RED_BANNER, BANNER);
        map2.put(Items.YELLOW_BANNER, BANNER);
        map2.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
        map2.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
        Map<Item, CauldronInteraction> map3 = CauldronInteraction.LAVA.map();
        map3.put(Items.BUCKET, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            return CauldronInteraction.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(Items.LAVA_BUCKET), (blockStatex) -> {
                return true;
            }, SoundEvents.BUCKET_FILL_LAVA);
        });
        CauldronInteraction.addDefaultInteractions(map3);
        Map<Item, CauldronInteraction> map4 = CauldronInteraction.POWDER_SNOW.map();
        map4.put(Items.BUCKET, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            return CauldronInteraction.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(Items.POWDER_SNOW_BUCKET), (blockStatex) -> {
                return (Integer)blockStatex.getValue(LayeredCauldronBlock.LEVEL) == 3;
            }, SoundEvents.BUCKET_FILL_POWDER_SNOW);
        });
        CauldronInteraction.addDefaultInteractions(map4);
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static InteractionResult fillBucket(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, ItemStack itemStack2, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(blockState)) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide) {
                // CraftBukkit start
                if (!LayeredCauldronBlock.changeLevel(blockState, level, blockPos, Blocks.CAULDRON.defaultBlockState(), player, CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL)) {
                    return InteractionResult.SUCCESS;
                }
                // CraftBukkit end
                Item item = itemStack.getItem();
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, itemStack2));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                // level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState()); // CraftBukkit
                level.playSound((Player)null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, blockPos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static InteractionResult emptyBucket(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, BlockState blockState, SoundEvent soundEvent) {
        if (!level.isClientSide) {
            // CraftBukkit start
            if (!LayeredCauldronBlock.changeLevel(blockState, level, blockPos, blockState, player, CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY)) {
                return InteractionResult.SUCCESS;
            }
            // CraftBukkit end
            Item item = itemStack.getItem();
            player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            // level.setBlockAndUpdate(blockPos, blockState); // CraftBukkit
            level.playSound((Player)null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, blockPos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
