package eu.ansquare.squaremobility.blocks;

import com.simibubi.create.content.contraptions.mounted.CartAssembleRailType;
import com.simibubi.create.foundation.block.IBE;

import eu.ansquare.squaremobility.ModBlocks;
import eu.ansquare.squaremobility.ModItems;
import eu.ansquare.squaremobility.Squaremobility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VehicleAnchorBlock extends HorizontalFacingBlock implements IBE<VehicleAnchorBlockEntity> {
	public VehicleAnchorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public Class<VehicleAnchorBlockEntity> getBlockEntityClass() {
		return VehicleAnchorBlockEntity.class;
	}
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> pBuilder) {
		super.appendProperties(pBuilder.add(FACING));
	}
	@Override
	public BlockState getPlacementState(ItemPlacementContext pContext) {
		return this.getDefaultState().with(FACING, pContext.getPlayerFacing().getOpposite());
	}
	@Override
	public BlockEntityType<? extends VehicleAnchorBlockEntity> getBlockEntityType() {
		return ModBlocks.VEHICLE_ANCHOR_ENTITY.get();
	}
	public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player,
							  @Nonnull Hand hand, @Nonnull BlockHitResult blockRayTraceResult) {
		if(world.isClient) return ActionResult.PASS;
		if(!player.getStackInHand(hand).isOf(ModItems.CAR_DISASSEMBLER.get())) {
			withBlockEntityDo(world, pos, be -> be.tryAssemble(player));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

}
