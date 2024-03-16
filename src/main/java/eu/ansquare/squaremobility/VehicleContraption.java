package eu.ansquare.squaremobility;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;

import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import com.simibubi.create.content.contraptions.render.NonStationaryLighter;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

import net.minecraft.world.WorldAccess;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Queue;

public class VehicleContraption extends Contraption {
	@Override
	public boolean assemble(World world, BlockPos pos) throws AssemblyException {
		BlockState state = world.getBlockState(pos);
		if (!searchMovedStructure(world, pos, null))
			return false;
		addBlock(pos, Pair.of(new Structure.StructureBlockInfo(pos, ModBlocks.MOVING_VEHICLE_ANCHOR.getDefaultState(), null), null));
		Squaremobility.LOGGER.warn(String.valueOf(blocks.size()) + " blocks");



		return true;
	}

	@Override
	public boolean canBeStabilized(Direction facing, BlockPos localPos) {
		return true;
	}
	@Override
	protected boolean movementAllowed(BlockState state, World world, BlockPos pos) {
		return true;
	}
	@Override
	public ContraptionType getType() {
		return Squaremobility.VEHICLE;
	}
	@Override
	protected boolean addToInitialFrontier(World world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
		frontier.clear();
		frontier.add(pos.up());
		return true;
	}
	public ContraptionLighter<?> makeLighter() {
		return new NonStationaryLighter<>(this);
	}
	@Override
	protected boolean customBlockPlacement(WorldAccess world, BlockPos pos, BlockState state) {
		return ModBlocks.MOVING_VEHICLE_ANCHOR.has(state);
	}

	@Override
	protected boolean customBlockRemoval(WorldAccess world, BlockPos pos, BlockState state) {
		return ModBlocks.MOVING_VEHICLE_ANCHOR.has(state);
	}
}