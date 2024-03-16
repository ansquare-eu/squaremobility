package eu.ansquare.squaremobility;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.render.OrientedContraptionEntityRenderer;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import static eu.ansquare.squaremobility.Squaremobility.REGISTRATE;


public class ModEntityTypes {
	public static final EntityEntry<MobileContraptionEntity> MOBILE_CONTRAPTION = REGISTRATE
			.entity("mobile_contraption", MobileContraptionEntity::new, SpawnGroup.MISC)
			.properties(c -> c.fireImmune().trackedUpdateRate(5).forceTrackedVelocityUpdates(true).trackRangeChunks(3))
			.renderer(() -> MobileContraptionEntityRenderer::new)
			.register();
	public static void init(){}
}
