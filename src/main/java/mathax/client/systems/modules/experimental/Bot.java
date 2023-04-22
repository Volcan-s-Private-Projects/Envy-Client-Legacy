package mathax.client.systems.modules.experimental;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.friends.Friends;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import net.minecraft.item.Items;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static mathax.client.systems.modules.movement.AntiJebus.Mode.Vanilla;
import static mathax.client.utils.render.EntityShaders.timer;

public class Bot extends Module {

    public Bot() {
        super(Categories.Experimental, Items.CRAFTING_TABLE, "Bot", "Experimental Super Customizable Bot");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //_____________________________CHAT______________________________________

    private int messageI, timer;

    private final SettingGroup sgChat = settings.createGroup("Chat Options");

    private final SettingGroup sgChatBypass = settings.createGroup("Chat Bypasses");

    private final Setting<Boolean> Chat = sgChat.add(new BoolSetting.Builder()
        .name("Chat")
        .description("Chat")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<String>> messages = sgChat.add(new StringListSetting.Builder()
        .name("messages")
        .description("Messages to use for spam. Use %player% for a name of a random player.")
        .defaultValue(
            "Envy on top!",
            "Volcan on top!",
            "https://discord.gg/fG5T78aQYP"
        )
        .visible(() -> Chat.get())
        .build()
    );
    private final Setting<Boolean> ignoreSelf = sgChat.add(new BoolSetting.Builder()
        .name("ignore-self")
        .description("Skips messages when you're in %player%.")
        .defaultValue(true)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgChat.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Skips messages when the %player% is a friend.")
        .defaultValue(true)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Integer> delay = sgChat.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between specified messages in ticks.")
        .defaultValue(250)
        .min(0)
        .sliderRange(0, 1000)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Boolean> disableOnLeave = sgChat.add(new BoolSetting.Builder()
        .name("disable-on-leave")
        .description("Disables spam when you leave a server.")
        .defaultValue(true)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Integer> LowHealthDisable = sgChat.add(new IntSetting.Builder()
        .name("health disable")
        .description("Disables spam when you reach a health value")
        .defaultValue(5)
        .min(1)
        .max(19)
        .sliderRange(1, 19)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Boolean> disableOnDisconnect = sgChat.add(new BoolSetting.Builder()
        .name("disable-on-disconnect")
        .description("Disables spam when you are disconnected from a server.")
        .defaultValue(true)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Boolean> random = sgChat.add(new BoolSetting.Builder()
        .name("randomise")
        .description("Selects a random message from your spam message list.")
        .defaultValue(true)
        .visible(() -> Chat.get())
        .build()
    );

    // Anti Spam Bypass

    private final Setting<Boolean> randomText = sgChatBypass.add(new BoolSetting.Builder()
        .name("random-text")
        .description("Adds random text at the bottom of the text.")
        .defaultValue(false)
        .visible(() -> Chat.get())
        .build()
    );

    private final Setting<Integer> randomTextLength = sgChatBypass.add(new IntSetting.Builder()
        .name("length")
        .description("Text length of anti spam bypass.")
        .defaultValue(16)
        .sliderRange(1, 256)
        .visible(() -> (Chat.get()) && randomText.get())
        .build()
    );

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (disableOnLeave.get()) toggle();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (messages.get().isEmpty()) return;

        if (timer <= 0) {
            int i;
            if (random.get()) i = Utils.random(0, messages.get().size());
            else {
                if (messageI >= messages.get().size()) messageI = 0;
                i = messageI++;
            }

            String text = messages.get().get(i);
            if (randomText.get()) text += " " + RandomStringUtils.randomAlphabetic(randomTextLength.get()).toLowerCase();

            String player;
            do {
                player = Utils.getRandomPlayer();
            } while (ignoreSelf.get() && player.equals(mc.getSession().getUsername()) || ignoreFriends.get() && Friends.get().get(player) != null);

            mc.player.sendChatMessage(text.replace("%player%", player));

            timer = delay.get();
        } else timer--;

        if (LowHealthDisable.get() < mc.player.getHealth()) {
            toggle();
        }
    }
}
