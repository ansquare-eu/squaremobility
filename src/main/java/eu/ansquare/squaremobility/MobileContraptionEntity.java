package eu.ansquare.squaremobility;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;

import com.simibubi.create.content.contraptions.StructureTransform;

import com.simibubi.create.foundation.utility.VecHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobileContraptionEntity extends OrientedContraptionEntity {
	public MobileContraptionEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	protected void tickContraption() {
		super.tickContraption();
		this.contraption.anchor = this.getBlockPos();

	}
	@Override
	public Vec3d applyRotation(Vec3d localPos, float partialTicks) {
		localPos = VecHelper.rotate(localPos, this.getPitch(partialTicks), this.getInitialOrientation().getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
		localPos = VecHelper.rotate(localPos, this.getYaw(partialTicks), Direction.Axis.Y);
		localPos = VecHelper.rotate(localPos, this.getInitialYaw(), Direction.Axis.Y);
		return localPos;
	}

	@Override
	public Vec3d reverseRotation(Vec3d localPos, float partialTicks) {
		localPos = VecHelper.rotate(localPos, -this.getInitialYaw(), Direction.Axis.Y);
		localPos = VecHelper.rotate(localPos, -this.getYaw(partialTicks), Direction.Axis.Y);
		localPos = VecHelper.rotate(localPos, -this.getPitch(partialTicks), this.getInitialOrientation().getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
		return localPos;
	}
	@Override
	@Environment(EnvType.CLIENT)
	public void applyLocalTransforms(MatrixStack matrixStack, float partialTicks) {
		float angleInitialYaw = getInitialYaw();
		float angleYaw = getYaw(partialTicks);
		float anglePitch = getPitch(partialTicks);

		Entity vehicle = getVehicle();

		TransformStack.cast(matrixStack)
				.rotateY(angleYaw)
				.rotateZ(anglePitch)
				.rotateY(angleInitialYaw)
				.translate(0, 0.5, 0)
				.unCentre();
	}
	public void stopRiding() {
		this.dismountVehicle();
	}
	public static MobileContraptionEntity create(World world, Contraption contraption, Direction initialOrientation) {
		MobileContraptionEntity entity = new MobileContraptionEntity(ModEntityTypes.MOBILE_CONTRAPTION.get(), world);
		entity.setContraption(contraption);

		return entity;
	}
}
