package eu.ansquare.squaremobility;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;

import eu.ansquare.squaremobility.items.CarDisassembler;
import net.minecraft.client.render.item.ItemModels;

import static eu.ansquare.squaremobility.Squaremobility.REGISTRATE;

public class ModItems {
	public static final ItemEntry<CarDisassembler> CAR_DISASSEMBLER = REGISTRATE
			.item("car_disassembler", CarDisassembler::new)
			.properties(p -> p.maxCount(1))
			.model((context, provider) -> provider.handheld(() -> context.getEntry()))
			.lang("Car disassembler")
			.register();
	public static void init(){}

}
