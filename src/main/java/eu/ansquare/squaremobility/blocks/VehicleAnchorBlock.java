package eu.ansquare.squaremobility.blocks;

import com.simibubi.create.content.contraptions.mounted.CartAssembleRailType;
import com.simibubi.create.foundation.block.IBE;

import eu.ansquare.squaremobility.ModBlocks;
import eu.ansquare.squaremobility.Squaremobility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VehicleAnchorBlock extends Block implements IBE<VehicleAnchorBlockEntity> {
	public VehicleAnchorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public Class<VehicleAnchorBlockEntity> getBlockEntityClass() {
		return VehicleAnchorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends VehicleAnchorBlockEntity> getBlockEntityType() {
		return ModBlocks.VEHICLE_ANCHOR_ENTITY.get();
	}
	public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player,
							  @Nonnull Hand hand, @Nonnull BlockHitResult blockRayTraceResult) {
		if(world.isClient) return ActionResult.PASS;
		withBlockEntityDo(world, pos, be -> be.tryAssemble(player));
		return ActionResult.PASS;
	}
	public static class MovingVehicleAnchor extends Block{

		public MovingVehicleAnchor(Settings settings) {
			super(settings);
		}
	}
}
