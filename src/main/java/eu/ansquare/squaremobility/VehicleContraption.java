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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.world.WorldAccess;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix2f;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.Queue;

public class VehicleContraption extends Contraption {
	public Vector2f topRight = new Vector2f();
	public Vector2f topLeft = new Vector2f();
	public Vector2f bottomRight = new Vector2f();
	public Vector2f bottomLeft= new Vector2f();

	@Override
	public boolean assemble(World world, BlockPos pos) throws AssemblyException {
		BlockState state = world.getBlockState(pos);
		if (!searchMovedStructure(world, pos, null))
			return false;
		addBlock(pos, Pair.of(new Structure.StructureBlockInfo(pos, state, null), null));
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
		frontier.add(pos.down());
		frontier.add(pos.west());
		frontier.add(pos.east());
		frontier.add(pos.north());
		frontier.add(pos.south());
		return true;
	}
	public ContraptionLighter<?> makeLighter() {
		return new NonStationaryLighter<>(this);
	}
	public void rotateBounds(float yaw){
		float phi = (float) Math.toRadians(yaw);
		Matrix2f matrix = new Matrix2f(MathHelper.cos(phi), -MathHelper.sin(phi), MathHelper.sin(phi), -MathHelper.cos(phi));
		Vector2f tr = matrix.transform(new Vector2f(topRight));
		Vector2f tl = matrix.transform(new Vector2f(topLeft));
		Vector2f br = matrix.transform(new Vector2f(bottomRight));
		Vector2f bl = matrix.transform(new Vector2f(bottomLeft));
		float minZ = Math.min(Math.min(tr.x(), tl.x()), Math.min(bl.x(), br.x())) + anchor.getZ();
		float maxZ = Math.max(Math.max(tr.x(), tl.x()), Math.max(bl.x(), br.x()))+ anchor.getZ();
		float minX = Math.min(Math.max(tr.y(), tl.y()), Math.min(bl.y(), br.y()))+ anchor.getX();
		float maxX = Math.max(Math.max(tr.y(), tl.y()), Math.max(bl.y(), br.y()))+ anchor.getX();
		bounds = new Box(minX, bounds.minY, minZ, maxX, bounds.maxY, maxZ);
	}
}
