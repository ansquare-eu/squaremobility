package eu.ansquare.squaremobility;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.Create;

import com.simibubi.create.content.contraptions.ContraptionType;

import com.simibubi.create.foundation.data.CreateRegistrate;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Squaremobility implements ModInitializer {
	public static final String ID = "squaremobility";
	public static final String NAME = "Squaremobility";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

	public static ContraptionType VEHICLE;
	@Override
	public void onInitialize() {
		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);
		init();
	}
	private void init(){
		VEHICLE = ContraptionType.register("vehicle", VehicleContraption::new);
		REGISTRATE.useCreativeTab(AllCreativeModeTabs.MAIN_TAB.key());
		ModBlocks.init();
		ModEntityTypes.init();
		REGISTRATE.register();
	}
	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}
}
