package eu.ansquare.squaremobility;

import com.google.common.base.Strings;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;

import com.simibubi.create.content.contraptions.StructureTransform;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.trains.TrainHUDUpdatePacket;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;

import com.simibubi.create.infrastructure.config.AllConfigs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.Structure;
import net.minecraft.text.MutableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;

public class MobileContraptionEntity extends OrientedContraptionEntity {
	public MobileContraptionEntity(EntityType<?> type, World world) {
		super(type, world);
	}
	float steerAngle = 0;
	float forwardSpeed = 0;
	float tickDuration = 1/20f;

	@Override
	protected void tickContraption() {
		super.tickContraption();
		move(MovementType.SELF, getVelocity());
		this.contraption.anchor = this.getBlockPos();
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.yaw = this.getYaw();
		this.pitch = this.getPitch();

	}
	public void move(MovementType movementType, Vec3d movement) {
		this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
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


		TransformStack.cast(matrixStack)
				.rotateY(angleYaw)
				.rotateZ(anglePitch)
				.rotateY(angleInitialYaw)
				.translate(0, 0.5, 0)
				.unCentre();
	}
	@Override
	public void setVelocity(Vec3d motionIn) {
		setContraptionMotion(motionIn);

	}

	@Override
	public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, PlayerEntity player) {
		setVelocity(0, 0, 0);
		if (getWorld().isClient)
			return true;
		if (player.isSpectator())
			return false;
		if (!toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1).isInRange(player.getPos(), 8))
			return false;
		if (heldControls.contains(5))
			return false;

		int targetSpeed = 0;
		if (heldControls.contains(0))
			targetSpeed++;
		if (heldControls.contains(1))
			targetSpeed--;

		int targetSteer = 0;
		if (heldControls.contains(2))
			targetSteer++;
		if (heldControls.contains(3))
			targetSteer--;
		if(targetSteer == 0 && steerAngle != 0){
			if(steerAngle > 0) steerAngle -= 0.5;
			if(steerAngle < 0) steerAngle += 0.5;
		}
		if(targetSpeed == 0 && forwardSpeed != 0){
			if(forwardSpeed > 0) forwardSpeed -= 0.5;
			if(forwardSpeed < 0) forwardSpeed += 0.5;
		}
		if(targetSteer != 0){
			steerAngle += targetSteer;
		}
		if(targetSpeed != 0){
			forwardSpeed += targetSpeed;
		}
		steerAngle = MathHelper.clamp(steerAngle, -45, 45);
		forwardSpeed= MathHelper.clamp(forwardSpeed, -10, 10);

		float wheelBase = 3.0f;
		Vec3d vec3d = getVelocity().add(targetSpeed, 0, 0);

		if(steerAngle != 0) {
			float turningRadius = MathHelper.sin((float) Math.toRadians(steerAngle));
			float distanceInTick = (float) (vec3d.length() * tickDuration);
			float f = distanceInTick / turningRadius;
			float velocityAngleChange = (float) Math.toDegrees(Math.asin(distanceInTick / turningRadius));
			Squaremobility.LOGGER.info("angle is " + steerAngle + " velocity angle change " + velocityAngleChange);
			setYaw(MathHelper.clamp(getYaw() + velocityAngleChange, -180, 180));
			vec3d = vec3d.rotateY(velocityAngleChange);

		} else {


		}

		setVelocity(vec3d);
		velocityModified = true;

		boolean spaceDown = heldControls.contains(4);


		return true;
	}

	public boolean startControlling(BlockPos controlsLocalPos, PlayerEntity player) {
		if (player == null || player.isSpectator())
			return false;
		return true;
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
