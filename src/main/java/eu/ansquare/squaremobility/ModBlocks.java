package eu.ansquare.squaremobility;
 import com.simibubi.create.AllBlocks;
 import com.simibubi.create.AllTags;
 import com.simibubi.create.Create;
 import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
 import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity;
 import com.simibubi.create.foundation.data.BlockStateGen;
 import com.simibubi.create.foundation.data.SharedProperties;
 import com.simibubi.create.foundation.data.TagGen;
 import com.tterrag.registrate.util.entry.BlockEntityEntry;
 import com.tterrag.registrate.util.entry.BlockEntry;

 import eu.ansquare.squaremobility.blocks.QueryBlock;
 import eu.ansquare.squaremobility.blocks.VehicleAnchorBlock;
 import eu.ansquare.squaremobility.blocks.VehicleAnchorBlockEntity;
 import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
 import net.minecraft.block.MapColor;
 import net.minecraft.block.piston.PistonBehavior;
 import net.minecraft.client.render.RenderLayer;
 import net.minecraft.registry.tag.BlockTags;

 import static eu.ansquare.squaremobility.Squaremobility.REGISTRATE;
public class ModBlocks {
	public static final BlockEntry<VehicleAnchorBlock> VEHICLE_ANCHOR =
			REGISTRATE.block("vehicle_anchor", VehicleAnchorBlock::new)
					.initialProperties(SharedProperties::netheriteMetal)
					.blockstate((context, provider) -> provider.horizontalBlock(context.getEntry(), provider
							.models()
							.cubeBottomTop(context.getName(), Squaremobility.id("block/vehicle_anchor"), Squaremobility.id("block/vehicle_anchor_top"), Squaremobility.id("block/vehicle_anchor_top"))))
					.properties(p -> p.mapColor(MapColor.GRAY).pistonBehavior(PistonBehavior.BLOCK))
					.addLayer(() -> RenderLayer::getCutoutMipped)
					.item()
					.build()
					.transform(TagGen.pickaxeOnly())
					.lang("Vehicle anchor")
					.register();
	public static final BlockEntityEntry<VehicleAnchorBlockEntity> VEHICLE_ANCHOR_ENTITY = REGISTRATE
			.blockEntity("vehicle_anchor", VehicleAnchorBlockEntity::new)
			.validBlocks(VEHICLE_ANCHOR)
			.register();
	public static final BlockEntry<QueryBlock> QUERY_BLOCK =
			REGISTRATE.block("query_block", QueryBlock::new)
					.initialProperties(SharedProperties::wooden)
					.item()
					.build()
					.lang("Query block")
					.register();
	public static void init() {}

}
