package eu.ansquare.squaremobility.blocks;

import eu.ansquare.squaremobility.Squaremobility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class QueryBlock extends Block {
	public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player,
							  @Nonnull Hand hand, @Nonnull BlockHitResult blockRayTraceResult) {
		if(world.isClient) return ActionResult.PASS;
		Entity entity = player.getFirstPassenger();
		if(entity == null) {
			Squaremobility.LOGGER.error("nopas");
			return ActionResult.PASS;
		}
		Squaremobility.LOGGER.warn("playerpos " + player.getPos().toString());
		Squaremobility.LOGGER.warn("paspos " + entity.getPos().toString());

		return ActionResult.PASS;
	}
	public QueryBlock(Settings settings) {
		super(settings);
	}
}
