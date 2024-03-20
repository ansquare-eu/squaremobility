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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.Structure;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Collection;
import java.util.List;

import static eu.ansquare.squaremobility.Squaremobility.LOGGER;
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
	private Vec3d adjustMovementForCollisions(Vec3d movement) {
		Box box = this.getBoundingBox();
		List<VoxelShape> list = List.of();
		Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustSingleAxisMovementForCollisions(this, movement, box, this.getWorld(), list);
		boolean bl = movement.x != vec3d.x;
		boolean bl2 = movement.y != vec3d.y;
		boolean bl3 = movement.z != vec3d.z;
		boolean bl4 = this.isOnGround() || bl2 && movement.y < 0.0;
		if (this.getStepHeight() > 0.0F && bl4 && (bl || bl3)) {
			Vec3d vec3d2 = adjustSingleAxisMovementForCollisions(this, new Vec3d(movement.x, (double)this.getStepHeight(), movement.z), box, this.getWorld(), list);
			Vec3d vec3d3 = adjustSingleAxisMovementForCollisions(this, new Vec3d(0.0, (double)this.getStepHeight(), 0.0), box.stretch(movement.x, 0.0, movement.z), this.getWorld(), list);
			if (vec3d3.y < (double)this.getStepHeight()) {
				Vec3d vec3d4 = adjustSingleAxisMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.getWorld(), list).add(vec3d3);
				if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
					vec3d2 = vec3d4;
				}
			}

			if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
				return vec3d2.add(adjustSingleAxisMovementForCollisions(this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.getWorld(), list));
			}
		}

		return vec3d;
	}
	public void move(MovementType movementType, Vec3d movement) {
		if (this.noClip) {
			this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
		} else {
			this.wasOnFire = this.isOnFire();
			if (movementType == MovementType.PISTON) {
				movement = this.adjustMovementForPiston(movement);
				if (movement.equals(Vec3d.ZERO)) {
					return;
				}
			}

			this.getWorld().getProfiler().push("move");
			if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
				movement = movement.multiply(this.movementMultiplier);
				this.movementMultiplier = Vec3d.ZERO;
				this.setVelocity(Vec3d.ZERO);
			}

			movement = this.adjustMovementForSneaking(movement, movementType);
			Vec3d vec3d = this.adjustMovementForCollisions(movement);
			double d = vec3d.lengthSquared();
			if (d > 1.0E-7) {
				if (this.fallDistance != 0.0F && d >= 1.0) {
					BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(this.getPos(), this.getPos().add(vec3d), RaycastContext.ShapeType.FALLDAMAGE_RESETTING, RaycastContext.FluidHandling.WATER, this));
					if (blockHitResult.getType() != HitResult.Type.MISS) {
						this.resetFallDistance();
					}
				}

				this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
			}

			this.getWorld().getProfiler().pop();
			this.getWorld().getProfiler().push("rest");
			boolean bl = !MathHelper.approximatelyEquals(movement.x, vec3d.x);
			boolean bl2 = !MathHelper.approximatelyEquals(movement.z, vec3d.z);
			this.horizontalCollision = bl || bl2;
			this.verticalCollision = movement.y != vec3d.y;
			this.verticalCollisionBelow = this.verticalCollision && movement.y < 0.0;
			if (this.horizontalCollision) {
				this.minorHorizontalCollision = this.hasCollidedSoftly(vec3d);
			} else {
				this.minorHorizontalCollision = false;
			}

			this.setOnGroundWithMovement(this.verticalCollisionBelow, vec3d);
			BlockPos blockPos = this.getLandingPosition();
			BlockState blockState = this.getWorld().getBlockState(blockPos);
			this.fall(vec3d.y, this.isOnGround(), blockState, blockPos);
			if (this.isRemoved()) {
				this.getWorld().getProfiler().pop();
			} else {
				if (this.horizontalCollision) {
					Vec3d vec3d2 = this.getVelocity();
					this.setVelocity(bl ? 0.0 : vec3d2.x, vec3d2.y, bl2 ? 0.0 : vec3d2.z);
				}

				Block block = blockState.getBlock();
				if (movement.y != vec3d.y) {
					block.onEntityLand(this.getWorld(), this);
				}

				if (this.isOnGround()) {
					block.onSteppedOn(this.getWorld(), blockPos, blockState, this);
				}

				MoveEffect moveEffect = this.getMoveEffect();
				if (moveEffect.hasAny() && !this.hasVehicle()) {
					double e = vec3d.x;
					double f = vec3d.y;
					double g = vec3d.z;
					this.flyDistance += (float)(vec3d.length() * 0.6);
					BlockPos blockPos2 = this.getSteppingPosition();
					BlockState blockState2 = this.getWorld().getBlockState(blockPos2);
					boolean bl3 = false;
					if (!bl3) {
						f = 0.0;
					}

					this.horizontalSpeed += (float)vec3d.horizontalLength() * 0.6F;
					this.distanceTraveled += (float)Math.sqrt(e * e + f * f + g * g) * 0.6F;

				}

				this.tryCheckBlockCollision();
				float h = this.getVelocityMultiplier();
				this.setVelocity(this.getVelocity().multiply((double)h, 1.0, (double)h));
				if (this.getWorld().getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6)).noneMatch((state) -> {
					return state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA);
				})) {


					if (this.wasOnFire && (this.inPowderSnow || this.isWet())) {
						this.playExtinguishSound();
					}
				}

				if (this.isOnFire() && (this.inPowderSnow || this.isWet())) {
					this.setFireTicks(-this.getBurningDuration());
				}

				this.getWorld().getProfiler().pop();
			}
		}	}
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
		if (heldControls.contains(3))
			targetSteer++;
		if (heldControls.contains(2))
			targetSteer--;
/*		if(targetSteer == 0 && steerAngle != 0){
			if(steerAngle > 0) steerAngle -= 0.5;
			if(steerAngle < 0) steerAngle += 0.5;
		}*/
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
		Vec3d vec3d = new Vec3d(0, 0, targetSpeed);

		if(steerAngle != 0.0f) {
			float turningRadius = wheelBase / MathHelper.sin((float) Math.toRadians(steerAngle));
			float distanceInTick = (float) (vec3d.length() * tickDuration);
			float f = distanceInTick / turningRadius;
			float velocityAngleChange = (float) Math.toDegrees(Math.asin(distanceInTick / turningRadius));
			LOGGER.info("angle is " + steerAngle + " velocity angle change " + velocityAngleChange);
			//vec3d = vec3d.rotateY(velocityAngleChange);
			rotateYaw(targetSteer);
			player.sendMessage(Text.literal("Yaw :" + getYaw()), true);

		} else {


		}

		vec3d = VecHelper.rotate(vec3d, getYaw(), Direction.Axis.Y);

		setVelocity(vec3d);
		velocityModified = true;

		boolean spaceDown = heldControls.contains(4);
		if(spaceDown) LOGGER.info(getContraption().getActors().size() + " actors");

		return true;
	}
	/** Don't call for yaw greater than 360 or lesser than -360 it may break**/
	public void rotateYaw(float yaw){
		float f = getYaw() + yaw;
		if(f > 180){
			f = -180 + (f - 180);
		} else if(f < -180){
			f = 180 - (MathHelper.abs(f) - 180);
		}

		setYaw(f);
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
