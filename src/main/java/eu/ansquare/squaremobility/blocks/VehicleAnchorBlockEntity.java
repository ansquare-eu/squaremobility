package eu.ansquare.squaremobility.blocks;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;

import eu.ansquare.squaremobility.MobileContraptionEntity;
import eu.ansquare.squaremobility.Squaremobility;
import eu.ansquare.squaremobility.VehicleContraption;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class VehicleAnchorBlockEntity extends SmartBlockEntity {
	protected AssemblyException lastException;

	public VehicleAnchorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

	}
	public void tryAssemble(PlayerEntity user){
		assemble(user, world, pos);

	}
	protected void assemble(PlayerEntity assembler, World world, BlockPos pos) {
		VehicleContraption contraption = new VehicleContraption();
		BlockState state = world.getBlockState(pos);
		Direction direction = state.get(VehicleAnchorBlock.FACING);
		try {
			if (!contraption.assemble(world, pos))
				return;

			lastException = null;
			sendData();
		} catch (AssemblyException e) {

			lastException = e;
			sendData();
			return;
		}
		contraption.removeBlocksFromWorld(world, BlockPos.ORIGIN);
		contraption.startMoving(world);
		//contraption.expandBoundsAroundAxis(Direction.Axis.Y);
		MobileContraptionEntity entity = MobileContraptionEntity.create(world, contraption, direction);
		entity.setPosition(pos.getX() + .5, pos.getY(), pos.getZ() + .5);
		//entity.startRiding(assembler);

		world.spawnEntity(entity);

	}
	protected void disassemble(World world, BlockPos pos) {
		Entity entity = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, entity1 -> true);
		if (!(entity instanceof OrientedContraptionEntity))
			return;
		OrientedContraptionEntity contraption = (OrientedContraptionEntity) entity;
		entity.removeAllPassengers();



	}
}
