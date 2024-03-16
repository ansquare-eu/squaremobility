package eu.ansquare.squaremobility;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import com.simibubi.create.foundation.utility.FilesHelper;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;

public class ModDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		Squaremobility.LOGGER.info("Initializing data generator");
		Path resources = Paths.get(System.getProperty(ExistingFileHelper.EXISTING_RESOURCES));
		ExistingFileHelper helper = new ExistingFileHelper(
				Set.of(resources), Set.of("create"), false, null, null
		);

		Squaremobility.REGISTRATE.setupDatagen(gen.createPack(), helper);
		FabricDataGenerator.Pack pack = gen.createPack();

		pack.addProvider(AdvancementsProvider::new);
		setLang("en_us");
	}

	private static void setLang(String fileName) {
		String path = "assets/" + Squaremobility.ID + "/lang/" + fileName + ".json";

		JsonObject jsonObject = Preconditions.checkNotNull(FilesHelper.loadJsonResource(path),
				"Could not find default lang file: %s", path).getAsJsonObject();

		jsonObject.entrySet().forEach(entry ->
				Squaremobility.REGISTRATE.addRawLang(entry.getKey(), entry.getValue().getAsString())
		);
	}
	static class AdvancementsProvider extends FabricAdvancementProvider {
		protected AdvancementsProvider(FabricDataOutput output) {
			super(output);
		}

		// ... (Rest of the code)

		@Override
		public void generateAdvancement(Consumer<Advancement> consumer) {
			Advancement rootAdvancement = Advancement.Task.create()
					.display(
							Items.DIRT, // The display icon
							Text.literal("Your test"), // The title
							Text.literal("Now test"), // The description
							new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
							AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
							true, // Show toast top right
							true, // Announce to chat
							false // Hidden in the advancement tab
					)
					// The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
					.criterion("got_dirt", InventoryChangedCriterion.Conditions.items(Items.DIRT))
					.build(consumer, Squaremobility.ID + "/root");
		}
	}
}

