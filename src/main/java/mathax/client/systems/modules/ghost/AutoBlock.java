/*package mathax.client.systems.modules.ghost;

import mathax.client.eventbus.EventHandler;
import mathax.client.systems.modules.Categories;
import net.minecraft.item.Items;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;

import java.util.Optional;

import static mathax.client.MatHax.mc;

public class AutoBlock extends Module {

    public AutoBlock() {
        super(Categories.Ghost, Items.COMMAND_BLOCK, "auto-block", "Automatically blocks.");
    }

    @EventHandler
    public void onTick() {
        if (mc.player.handSwinging && !mc.player.isBlocking()) {
            mc.player.isBlocking();
        }
    }
}*/
