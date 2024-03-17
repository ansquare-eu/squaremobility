package eu.ansquare.squaremobility;

import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MobileContraptionEntityRenderer extends ContraptionEntityRenderer<MobileContraptionEntity> {
	public MobileContraptionEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public void render(MobileContraptionEntity entity, float yaw, float partialTicks, MatrixStack ms, VertexConsumerProvider buffers, int overlay) {
		super.render(entity, yaw, partialTicks, ms, buffers, overlay);
	}

	@Override
	public boolean shouldRender(MobileContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
