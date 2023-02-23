package id.rajaopak.xquest.gui;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.quest.QuestCreator;
import id.rajaopak.xquest.quest.QuestType;
import id.rajaopak.xquest.util.ChatSession;
import id.rajaopak.xquest.util.GuiBuilder;
import id.rajaopak.xquest.util.ItemBuilder;
import id.rajaopak.xquest.util.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestCreatorGui {

    private final Player player;

    private final QuestCreator creator;
    private GuiBuilder builder;

    private QuestCreator.ObjectBuilder objectBuilder;

    public QuestCreatorGui(Player player, QuestCreator creator) {
        this.player = player;
        this.creator = creator;
    }

    public void open() {
        this.mainPage().open(this.player);
    }

    private GuiBuilder mainPage() {
        this.builder = new GuiBuilder(54, "&6Quest Editor");

        Arrays.stream(this.builder.getBorders()).forEach(value -> this.builder.setItem(value, ItemBuilder.from(Material.YELLOW_STAINED_GLASS_PANE).build()));

        this.builder.setItem(22, ItemBuilder.from(Material.NAME_TAG)
                .setName("&eQuest Name")
                .setLore("&8> &7Current: &6" +
                        (this.creator.getName() == null || this.creator.getName().isEmpty() ?
                                "&7none" : this.creator.getName())).build());

        this.builder.setItem(31, ItemBuilder.from(Material.BOOK)
                .setName("&eQuest Description")
                .setLore(Utils.wordWrap(
                        this.creator.getDescription() != null ?
                                "&8> &7Text: &r" + this.creator.getDescription() :
                                "&8> &7Text: none", 40)).build());

        this.builder.setItem(20, ItemBuilder.from(Material.BRICK).setName("&eObject")
                .setLore("", "&7" + this.creator.getObject().size() + " Object(s)").build());
        this.builder.setItem(21, ItemBuilder.from(Material.WRITABLE_BOOK).setName("&eStart Dialog")
                .setLore("", "&7" + this.creator.getDialog().size() + " Dialog(s)").build());

        this.builder.setItem(23, ItemBuilder.from(Material.ENCHANTING_TABLE).setName("&eStart Rewards")
                .setLore("", "&7" + this.creator.getStartRewards().size() + " Reward(s)").build());
        this.builder.setItem(24, ItemBuilder.from(Material.PAPER).setName("&eStart Message")
                .setLore(Utils.wordWrap(
                        this.creator.getDescription() != null ?
                                "&8> &7Text: &r" + this.creator.getStartMessage() :
                                "&8> &7Text: none", 40)).build());

        this.builder.setItem(32, ItemBuilder.from(Material.END_PORTAL_FRAME).setName("&eEnd Rewards")
                .setLore("", "&7" + this.creator.getEndRewards().size() + " Reward(s)").build());
        this.builder.setItem(33, ItemBuilder.from(Material.PAPER).setName("&eEnd Message")
                .setLore(Utils.wordWrap(
                        this.creator.getDescription() != null ?
                                "&8> &7Text: &r" + this.creator.getEndMessage() :
                                "&8> &7Text: none", 40)).build());

        this.builder.setItem(29, ItemBuilder.from(Material.CLOCK).setName("&eRestart timer")
                .setLore("&8> &7Time: " + (this.creator.getRestartTime() != null ? this.creator.getRestartTime() + " minute(s)" : "0 minute(s)")).build());
        this.builder.setItem(30, ItemBuilder.from(Material.VILLAGER_SPAWN_EGG).setName("&eSelect Npc")
                .setLore("&8> &7NpcId: " + (this.creator.getNpcId() != null ? this.creator.getNpcId() : "none")).build());

        this.builder.setItem(49, ItemBuilder.from(Material.BEACON).setName("&aSave Quest")
                .setLore(Utils.wordWrap("&8> &cQuest name is require to set for saving the quest.", 40)).build());

        this.builder.setFilterItem(ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).build());

        this.builder.addClickHandler(e -> {
            // set the name
            if (e.getSlot() == 22) {
                this.setValue().thenAccept(s -> {
                    this.creator.setName(s);
                    this.open();
                });
            }

            // set the description
            if (e.getSlot() == 31) {
                this.setValueChat().thenAccept(s -> {
                    this.creator.setDescription(s);
                    Bukkit.getScheduler().runTask(XQuest.getInstance(), () -> this.mainPage().open(this.player));
                });
            }

            // create some object
            if (e.getSlot() == 20) {
                this.setObjects().open(e.getWhoClicked());
            }

            // add some dialog
            if (e.getSlot() == 21) {
                this.setDialog().open(e.getWhoClicked());
            }

            // add start rewards
            if (e.getSlot() == 23) {
                this.setStartRewards().open(e.getWhoClicked());
            }

            // set start message
            if (e.getSlot() == 24) {
                this.setValueChat().thenAccept(s -> {
                    this.creator.setStartMessage(s);
                    this.open();
                });
            }

            // add end rewards
            if (e.getSlot() == 32) {
                this.setEndRewards().open(e.getWhoClicked());
            }

            // set end message
            if (e.getSlot() == 33) {
                this.setValueChat().thenAccept(s -> {
                    this.creator.setEndMessage(s);
                    this.open();
                });
            }

            if (e.getSlot() == 30) {
                this.setValueChat().thenAccept(s -> {
                    this.creator.setNpcId(Integer.parseInt(s));
                    this.open();
                });
            }

            // set the restart time
            if (e.getSlot() == 29) {
                this.setValueChat().thenAccept(s -> {
                    this.creator.setRestartTime(Long.parseLong(s));
                    this.open();
                });
                Utils.sendMessage(e.getWhoClicked(), "&6&lWrite the require time (in minutes).");
            }

            // build the quest
            if (e.getSlot() == 49) {
                if (this.creator.build()) {
                    if (this.creator.getName() == null || this.creator.getName().isEmpty()) {
                        return;
                    }

                    XQuest.getInstance().getQuestCreateMec().removeCreator(player);
                }
            }
        });

        return this.builder;
    }

    private GuiBuilder setDialog() {
        this.builder = new GuiBuilder(54, "&6Set some Dialog");

        this.builder.setItem(40, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 40) {
                this.open();
            }
        });

        return this.builder;
    }

    private GuiBuilder setStartRewards() {
        this.builder = new GuiBuilder(54, "&6Set Start Rewards");

        this.builder.setItem(40, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 40) {
                this.open();
            }
        });

        return this.builder;
    }

    private GuiBuilder setEndRewards() {
        this.builder = new GuiBuilder(54, "&6Set End Rewards");

        this.builder.setItem(40, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 40) {
                this.open();
            }
        });

        return this.builder;
    }

    private GuiBuilder setObjects() {
        this.builder = new GuiBuilder(54, "&6Create some Object");

        Arrays.stream(this.builder.getBorders()).forEach(value -> this.builder.setItem(value, ItemBuilder.from(Material.YELLOW_STAINED_GLASS_PANE).build()));

        this.builder.setItem(4, ItemBuilder.from(Material.NETHER_STAR).setName("&7Add Object").build());

        this.builder.setItem(49, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        AtomicInteger i = new AtomicInteger(0);
        this.creator.getObject().forEach(builder -> this.builder.addItem(ItemBuilder.from(Material.CHEST)
                .setName("&7Object #" + i.incrementAndGet())
                .setLore("",
                        "&7Type: &d" + builder.getType(),
                        "&7Object-1: &6" + builder.getObjectOne(),
                        "&7Object-2: &6" + builder.getObjectTwo()).build()));

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 4) {
                this.createObject().open(e.getWhoClicked());
            }

            if (e.getSlot() == 49) {
                this.open();
            }
        });

        return this.builder;
    }

    public GuiBuilder createObject() {
        this.builder = new GuiBuilder(27, "&6Create your object.");

        if (this.objectBuilder == null) {
            this.objectBuilder = this.creator.objectBuilder();
        }

        Arrays.stream(this.builder.getBorders()).forEach(value -> this.builder.setItem(value, ItemBuilder.from(Material.YELLOW_STAINED_GLASS_PANE).build()));

        this.builder.setItem(12, ItemBuilder.from(Material.BOOK).setName("&dType").build());
        this.builder.setItem(13, ItemBuilder.from(Material.PAPER).setName("&7Object 1").build());
        this.builder.setItem(14, ItemBuilder.from(Material.PAPER).setName("&7Object 2").build());

        this.builder.setItem(22, ItemBuilder.from(Material.NETHER_STAR).setName("&aSave").build());

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 12) {
                this.setType().open(e.getWhoClicked());
            }

            if (e.getSlot() == 13) {
                this.setObjectOne().open(e.getWhoClicked());
            }

            if (e.getSlot() == 14) {
                this.setObjectTwo().open(e.getWhoClicked());
            }

            if (e.getSlot() == 22) {
                if (this.objectBuilder.getType() != null && this.objectBuilder.getObjectOne() != null && this.objectBuilder.getObjectTwo() != null) {
                    this.creator.addObject(this.objectBuilder);
                }
            }
        });

        return this.builder;
    }

    private GuiBuilder setType() {
        this.builder = new GuiBuilder(54, "&6Select the quest type...");

        Arrays.stream(this.builder.getBorders()).forEach(value -> this.builder.setItem(value, ItemBuilder.from(Material.YELLOW_STAINED_GLASS_PANE).build()));

        Arrays.stream(QuestType.values()).forEach(questType -> {
            if (this.objectBuilder.getType() != null) {
                if (this.objectBuilder.getType() == questType) {
                    this.builder.addItem(ItemBuilder.from(Material.OAK_SIGN)
                            .setName("&d" + questType.name().toUpperCase())
                            .setLore("",
                                    "&aLeft click to set.",
                                    "&cRight click to unset.")
                            .setGlowing(true)
                            .build());
                    return;
                }
            }
            this.builder.addItem(ItemBuilder.from(Material.OAK_SIGN)
                    .setName("&d" + questType.name().toUpperCase())
                    .setLore("",
                            "&aLeft click to set.",
                            "&cRight click to unset.")
                    .build());
        });

        for (int i = 0; i < this.builder.getInventory().getSize(); i++) {
            if (this.builder.getInventory().getItem(i) == null || this.builder.getInventory().getItem(i).getType() == Material.AIR) {
                this.builder.setItem(i, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).build());
            }
        }

        this.builder.setItem(49, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        this.builder.addClickHandler(e -> {
            if (e.getCurrentItem() == null) return;
            if (QuestType.matches(ChatColor.stripColor(ItemBuilder.from(e.getCurrentItem()).getMeta().getDisplayName()))) {
                String itemName = ChatColor.stripColor(ItemBuilder.from(e.getCurrentItem()).getMeta().getDisplayName());

                if (e.getClick() == ClickType.LEFT) {
                    this.objectBuilder.setType(QuestType.parseString(itemName));
                    this.setType().open(player);
                } else if (e.getClick() == ClickType.RIGHT) {
                    this.objectBuilder.setType(null);
                    this.setType().open(player);
                }
            }

            if (e.getSlot() == 49) {
                this.createObject().open(e.getWhoClicked());
            }
        });

        return this.builder;
    }

    private GuiBuilder setObjectOne() {
        this.builder = new GuiBuilder(54, "&6Set Object One");

        this.builder.setItem(40, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 40) {
                this.createObject().open(e.getWhoClicked());
            }
        });

        return this.builder;
    }

    private GuiBuilder setObjectTwo() {
        this.builder = new GuiBuilder(54, "&6Set Object Two");

        this.builder.setItem(40, ItemBuilder.from(Material.BARRIER).setName("&7Back").build());

        this.builder.addClickHandler(e -> {
            if (e.getSlot() == 40) {
                this.createObject().open(e.getWhoClicked());
            }
        });

        return this.builder;
    }

    private CompletableFuture<String> setValue() {
        AnvilGUI.Builder anvil = new AnvilGUI.Builder();
        CompletableFuture<String> future = new CompletableFuture<>();

        anvil.itemLeft(ItemBuilder.from(Material.PAPER).build())
                .itemOutput(ItemBuilder.from(Material.PAPER).setName("&aSave the value.").build())
                .onComplete(completion -> List.of(AnvilGUI.ResponseAction.run(() -> future.complete(completion.getText()))))
                .onClose(player1 -> Bukkit.getScheduler().runTask(XQuest.getInstance(), () -> this.builder.open(this.player)))
                .text("type value here...")
                .title("Enter your value.")
                .plugin(XQuest.getInstance())
                .open(this.player);

        return future;
    }

    private CompletableFuture<String> setValueChat() {
        ChatSession session = new ChatSession();
        CompletableFuture<String> future = new CompletableFuture<>();

        this.builder.close(this.player);
        session.onComplete(complete -> ChatSession.Action.run(() -> future.complete(complete.message())))
                .onClose(player1 -> Bukkit.getScheduler().runTask(XQuest.getInstance(), () -> this.builder.open(this.player)))
                .plugin(XQuest.getInstance()).open(this.player);

        return future;
    }

    public Player getPlayer() {
        return this.player;
    }

    public GuiBuilder getBuilder() {
        return this.builder;
    }

    public QuestCreator getCreator() {
        return this.creator;
    }
}
