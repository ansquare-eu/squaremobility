package eu.ansquare.squaremobility.items;

import eu.ansquare.squaremobility.MobileContraptionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class CarDisassembler extends Item {
	public CarDisassembler(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(world.isClient()) return TypedActionResult.pass(user.getStackInHand(hand));
		List<MobileContraptionEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(MobileContraptionEntity.class), Box.from(user.getPos()).expand(20), mobileContraptionEntity -> true);
		entities.forEach(mobileContraptionEntity -> mobileContraptionEntity.disassemble());
		return super.use(world, user, hand);
	}
}
