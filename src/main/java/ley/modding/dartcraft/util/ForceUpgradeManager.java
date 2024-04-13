package ley.modding.dartcraft.util;

import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.api.upgrades.IForceUpgradable;
import ley.modding.dartcraft.api.upgrades.IForceUpgrade;
import ley.modding.dartcraft.api.upgrades.IForceUpgradeMaterial;
import ley.modding.dartcraft.api.upgrades.UpgradeMaterialHelper;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.item.DartItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class ForceUpgradeManager {
    public static int firstUniqueID = 0;

    public static final String tomeDesc
        = "If you haven't already done so, you should make a Book of Mudora, as it contains the sum of all DartCraft knowledge.  Simply Force Transmute a Bookshelf and you'll be on your way to mastering DartCraft!";
    public static final String forceDesc
        = "This upgrade can be used to turn certain items into more Forceful versions of themselves.  It also seems to imbue Force Swords with Knockback.";
    public static final String speedDesc
        = "This upgrade imbues Force Tools with the ability to break blocks faster, and gives an overall speed boost to players when used on a Force Rod.  If used to upgrade armor the player will move faster.";
    public static final String lumberjackDesc
        = "This upgrade transforms a Force Axe into a treebliterating tool of mass destruction, although the durability cost for such a monstrous tool is also, well, monstrous.";
    public static final String damageDesc
        = "This upgrade serves to boost the damage of the weapon to which it is attached.  On Force Swords it gives Sharpness, while Punch is given to Force Bows.";
    public static final String luckDesc
        = "The Luck upgrade enhances the amount of loot one receives for performing almost any task.  On the Force Sword it gives Looting, causing enemies slain to drop more loot.  On any standard block breaking tool it will give Fortune, which increases the drop rate of certain items.";
    public static final String grindingDesc
        = "This upgrade allows for certain items and drops to be ground into powdery substances or other useful materials.  Try mining some ore with it and you'll get more return than usual.";
    public static final String rainbowDesc
        = "This upgrade is only valid on the Force Shears and randomly changes the color of wool dropped from sheep.  Try it out!";
    public static final String storageDesc
        = "This unique upgrade serves to increase the storage capacity of Force Packs by 8 slots per upgrade, up to a total size of 40 slots.  Force Packs can only be upgraded once per tier, so you'll have to get to tier 5 before you can max out any Force Pack.";
    public static final String storage2Desc
        = "This unique upgrade serves to increase the storage capacity of Storage Units by approximately one Storage Unit per upgrade.  You can only use one upgrade per tier and max size is dependent upon config settings.";
    public static final String expDesc
        = "This upgrade has only one function: to upgrade a normal book into an Experience Tome, which is able to store an infinite amount of experience for the player.  Shift right-click it to store experience, and right-click to recall it.  There is a small percent loss, but it's free.";
    public static final String touchDesc
        = "This upgrade imbues basic Force Tools with Silk Touch, which generally applies before upgrades like Grinding or Heat.";
    public static final String falseDesc
        = "The False upgrade, when present on a Force Sword will never deal a finishing blow to an enemy when struck.  This could be useful for weakening monsters so they can be easily captured in Force Flasks.";
    public static final String bleedDesc
        = "Bleeding will cause entities that are struck to quickly take small amounts of bleed damage over time.  This works on both the Force Sword and Bow and does stack with burning damage.";
    public static final String craftDesc
        = "This upgrade is only valid for the Item Card, and will allow the card to automatically craft the configured recipe from inside a Force Pack or Force Transport Pipe.";
    public static final String baneDesc
        = "When an entity is struck with a Bane imbued weapon they are likely to lose some of their inherent abilities.  Creepers and Endermen are perfect examples of this principle.";
    public static final String chargeDesc
        = "This upgrade can only be added to Force Armor.  Each IC2 battery added gives 10k EU storage, and of course allows the Force Armor to be recharged with IC2 power.";
    public static final String forgeDesc
        = "This upgrade is only valid for Item Cards, and allows them to transmute freely between items marked as 'equivalent' on the Ore Dictionary.";
    public static final String heatDesc
        = "Heat is valid on every iteration of Force Tool, and has a tendency to ignite things it comes into contact with.  If you put Heat on shears...you're a terrible person.";
    public static final String freezingDesc
        = "Don't eat that cookie!  It doesn't do anything anyway.  Upgrading your Force Bow with Freezing will allow it to shoot Ice Arrows which will freeze your enemies in place for a short time, and even change some blocks in the world.";
    public static final String wingDesc
        = "Wing Swords will fling their user when they right-click while holding the jump key, or allow small amounts of flight with a sneak jump right-click combination.  On armor it will also allow flight and increase the maximum duration of flight.";
    public static final String charge2Desc
        = "A more potent version of Charge, holding 100,000 EU per upgrade.  Multiple Charge2 upgrades are recommended for chest pieces as they will function like Lappacks.";
    public static final String healingDesc
        = "Healing will heal any entity it hits for 1 heart per level, unless of course that entity happens to be undead.  Healing can be placed on a sword, bow and rod, and will heal the user when the rod is used.";
    public static final String camoDesc
        = "Camo will make Force Armor invisible, if you'd rather not appear to be wearing armor, or it can be placed on a Force Rod to give short spurts of invisibility to its user.";
    public static final String sightDesc
        = "Upgrading a Force Rod with Sight will give Night Vision to the user for about a minute per use.  It's worth noting that only an unmodified Potion of Night Vision may be used.";
    public static final String enderDesc
        = "Teleportation - not just for endermen anymore!  Harness this space-bending upgrade on a Force Sword or Rod for easy transportation, or have loads of fun griefing your buddies with the Ender Bow.  Teleport responsibly.";
    public static final String sturdyDesc
        = "This upgrade can be used to imbue Unbreaking on all Force Tools but the Shears and Sword, or it can be attached to Force Armor once to reduce incoming damage by a fairly signifcant amount.  Sturdy can also be used to upgrade packs and belts to make them immune to damage and despawning.";
    public static final String explosionDesc = "This upgrade is not yet used.";
    public static final String graftingDesc
        = "This upgrade will give the Force Axe the potential to function as a Forestry Grafter on leaves that a grafter would normally function on.  Of course this uses significant durability each time, so be prepared to throw some serious Liquid Force at this tool and apply some Sturdy upgrades if possible.";
    public static final String repairDesc
        = "This upgrade gives the Repair enchant from Thaumcraft on Force Tools.  Repair will attempt to repair one durability damage per 10 seconds.";
    public static final String soulDesc
        = "The Soul upgrade on Force Swords and socketed Power Saws will occasionally cause Mob Chunks to drop, which can be used to craft vanilla spawners for that mob or even be smelted into colored Mob Ingots.";
    public static final String lightDesc
        = "At the moment this can only be used to add Smite to Force Swords.  (This will change eventually.)";
    public static final String treasureDesc
        = "When entities are killed with a Treasure imbued weapon they will occasionally drop Treasure Cards, which can then be crafted into a Spoils Bag for some phat loot.  Fortune increases the drop rate as well.";
    public static final String imperviousDesc
        = "An insanely powerful upgrade that prevents any Force Tool possessing this enhancement from ever breaking.";
    public static final String timeDesc
        = "Time is relative.  At least it will be when you place this upgrade on a Force Sword or Force Rod.  Shift right clicking the rod will change modes while right clicking will use it.  Try it out!";

    public static IForceUpgrade INFO;
    public static IForceUpgrade FORCE;
    public static IForceUpgrade SPEED;
    public static IForceUpgrade LUMBERJACK;
    public static IForceUpgrade DAMAGE;
    public static IForceUpgrade LUCK;
    public static IForceUpgrade GRINDING;
    public static IForceUpgrade RAINBOW;
    public static IForceUpgrade STORAGE;
    public static IForceUpgrade EXP;
    public static IForceUpgrade TOUCH;
    public static IForceUpgrade FALSE;
    public static IForceUpgrade BLEED;
    public static IForceUpgrade CRAFT;
    public static IForceUpgrade BANE;
    public static IForceUpgrade CHARGE;
    public static IForceUpgrade FORGE;
    public static IForceUpgrade HEAT;
    public static IForceUpgrade FREEZING;
    public static IForceUpgrade STORAGE2;
    public static IForceUpgrade WING;
    public static IForceUpgrade CHARGE2;
    public static IForceUpgrade HEALING;
    public static IForceUpgrade CAMO;
    public static IForceUpgrade SIGHT;
    public static IForceUpgrade ENDER;
    public static IForceUpgrade STURDY;
    public static IForceUpgrade TIME;
    public static IForceUpgrade EXPLOSION;
    public static IForceUpgrade GRAFTING;
    public static IForceUpgrade REPAIR;
    public static IForceUpgrade SOUL;
    public static IForceUpgrade LIGHT;
    public static IForceUpgrade TREASURE;
    public static IForceUpgrade IMPERVIOUS;

    public static ArrayList<IForceUpgrade> upgrades = new ArrayList();
    public static ArrayList<IForceUpgradeMaterial> materials = new ArrayList();

    public static void load() {
        loadUpgrades();
        addUpgrades();
        loadUpgradeMaterials();
        //loadWildCards();
    }

    private static void loadUpgrades() {
        INFO = new IForceUpgrade(0, "Info", 1, "");
        FORCE = new IForceUpgrade(
            1,
            "Force",
            2,
            "This upgrade can be used to turn certain items into more Forceful versions of themselves.  It also seems to imbue Force Swords with Knockback."
        );
        SPEED = new IForceUpgrade(
            1,
            "Speed",
            Config.speedLevel,
            "This upgrade imbues Force Tools with the ability to break blocks faster, and gives an overall speed boost to players when used on a Force Rod.  If used to upgrade armor the player will move faster."
        );
        LUMBERJACK = new IForceUpgrade(
            1,
            "Lumberjack",
            1,
            "This upgrade transforms a Force Axe into a treebliterating tool of mass destruction, although the durability cost for such a monstrous tool is also, well, monstrous."
        );
        DAMAGE = new IForceUpgrade(
            1,
            "Damage",
            Config.damageLevel,
            "This upgrade serves to boost the damage of the weapon to which it is attached.  On Force Swords it gives Sharpness, while Punch is given to Force Bows."
        );
        LUCK = new IForceUpgrade(
            2,
            "Luck",
            4,
            "The Luck upgrade enhances the amount of loot one receives for performing almost any task.  On the Force Sword it gives Looting, causing enemies slain to drop more loot.  On any standard block breaking tool it will give Fortune, which increases the drop rate of certain items."
        );
        GRINDING = new IForceUpgrade(
            2,
            "Grinding",
            1,
            "This upgrade allows for certain items and drops to be ground into powdery substances or other useful materials.  Try mining some ore with it and you'll get more return than usual."
        );
        RAINBOW = new IForceUpgrade(
            2,
            "Rainbow",
            1,
            "This upgrade is only valid on the Force Shears and randomly changes the color of wool dropped from sheep.  Try it out!"
        );
        STORAGE = new IForceUpgrade(
            2,
            "Holding",
            1,
            "This unique upgrade serves to increase the storage capacity of Force Packs by 8 slots per upgrade, up to a total size of 40 slots.  Force Packs can only be upgraded once per tier, so you'll have to get to tier 5 before you can max out any Force Pack."
        );
        EXP = new IForceUpgrade(
            2,
            "Experience",
            1,
            "This upgrade has only one function: to upgrade a normal book into an Experience Tome, which is able to store an infinite amount of experience for the player.  Shift right-click it to store experience, and right-click to recall it.  There is a small percent loss, but it's free."
        );
        TOUCH = new IForceUpgrade(
            3,
            "Touch",
            1,
            "This upgrade imbues basic Force Tools with Silk Touch, which generally applies before upgrades like Grinding or Heat."
        );
        FALSE = new IForceUpgrade(
            3,
            "False",
            1,
            "The False upgrade, when present on a Force Sword will never deal a finishing blow to an enemy when struck.  This could be useful for weakening monsters so they can be easily captured in Force Flasks."
        );
        BLEED = new IForceUpgrade(
            3,
            "Bleed",
            3,
            "Bleeding will cause entities that are struck to quickly take small amounts of bleed damage over time.  This works on both the Force Sword and Bow and does stack with burning damage."
        );
        CRAFT = new IForceUpgrade(
            3,
            "Craft",
            1,
            "This upgrade is only valid for the Item Card, and will allow the card to automatically craft the configured recipe from inside a Force Pack or Force Transport Pipe."
        );
        BANE = new IForceUpgrade(
            3,
            "Bane",
            1,
            "When an entity is struck with a Bane imbued weapon they are likely to lose some of their inherent abilities.  Creepers and Endermen are perfect examples of this principle."
        );
        TIME = new IForceUpgrade(
            3,
            "Time",
            1,
            "Time is relative.  At least it will be when you place this upgrade on a Force Sword or Force Rod.  Shift right clicking the rod will change modes while right clicking will use it.  Try it out!"
        );
        CHARGE = new IForceUpgrade(
            4,
            "Charge",
            5,
            "This upgrade can only be added to Force Armor.  Each IC2 battery added gives 10k EU storage, and of course allows the Force Armor to be recharged with IC2 power."
        );
        FORGE = new IForceUpgrade(
            4,
            "Forge",
            1,
            "This upgrade is only valid for Item Cards, and allows them to transmute freely between items marked as 'equivalent' on the Ore Dictionary."
        );
        HEAT = new IForceUpgrade(
            4,
            "Heat",
            1,
            "Heat is valid on every iteration of Force Tool, and has a tendency to ignite things it comes into contact with.  If you put Heat on shears...you're a terrible person."
        );
        FREEZING = new IForceUpgrade(
            4,
            "Freezing",
            1,
            "Don't eat that cookie!  It doesn't do anything anyway.  Upgrading your Force Bow with Freezing will allow it to shoot Ice Arrows which will freeze your enemies in place for a short time, and even change some blocks in the world."
        );
        STORAGE2 = new IForceUpgrade(
            4,
            "Storage",
            1,
            "This unique upgrade serves to increase the storage capacity of Storage Units by approximately one Storage Unit per upgrade.  You can only use one upgrade per tier and max size is dependent upon config settings."
        );
        WING = new IForceUpgrade(
            5,
            "Wing",
            1,
            "Wing Swords will fling their user when they right-click while holding the jump key, or allow small amounts of flight with a sneak jump right-click combination.  On armor it will also allow flight and increase the maximum duration of flight."
        );
        CHARGE2 = new IForceUpgrade(
            5,
            "Charge2",
            5,
            "A more potent version of Charge, holding 100,000 EU per upgrade.  Multiple Charge2 upgrades are recommended for chest pieces as they will function like Lappacks."
        );
        HEALING = new IForceUpgrade(
            5,
            "Healing",
            2,
            "Healing will heal any entity it hits for 1 heart per level, unless of course that entity happens to be undead.  Healing can be placed on a sword, bow and rod, and will heal the user when the rod is used."
        );
        CAMO = new IForceUpgrade(
            5,
            "Camo",
            1,
            "Camo will make Force Armor invisible, if you'd rather not appear to be wearing armor, or it can be placed on a Force Rod to give short spurts of invisibility to its user."
        );
        SIGHT = new IForceUpgrade(
            5,
            "Sight",
            1,
            "Upgrading a Force Rod with Sight will give Night Vision to the user for about a minute per use.  It's worth noting that only an unmodified Potion of Night Vision may be used."
        );
        ENDER = new IForceUpgrade(
            6,
            "Ender",
            1,
            "Teleportation - not just for endermen anymore!  Harness this space-bending upgrade on a Force Sword or Rod for easy transportation, or have loads of fun griefing your buddies with the Ender Bow.  Teleport responsibly."
        );
        STURDY = new IForceUpgrade(
            6,
            "Sturdy",
            Config.sturdyLevel,
            "This upgrade can be used to imbue Unbreaking on all Force Tools but the Shears and Sword, or it can be attached to Force Armor once to reduce incoming damage by a fairly signifcant amount.  Sturdy can also be used to upgrade packs and belts to make them immune to damage and despawning."
        );
        SOUL = new IForceUpgrade(
            6,
            "Soul",
            1,
            "The Soul upgrade on Force Swords and socketed Power Saws will occasionally cause Mob Chunks to drop, which can be used to craft vanilla spawners for that mob or even be smelted into colored Mob Ingots."
        );
        EXPLOSION = new IForceUpgrade(7, "Explosion", 1, "This upgrade is not yet used.");
        GRAFTING = new IForceUpgrade(
            7,
            "Grafting",
            1,
            "This upgrade will give the Force Axe the potential to function as a Forestry Grafter on leaves that a grafter would normally function on.  Of course this uses significant durability each time, so be prepared to throw some serious Liquid Force at this tool and apply some Sturdy upgrades if possible."
        );
        REPAIR = new IForceUpgrade(
            7,
            "Repair",
            1,
            "This upgrade gives the Repair enchant from Thaumcraft on Force Tools.  Repair will attempt to repair one durability damage per 10 seconds."
        );
        LIGHT = new IForceUpgrade(
            7,
            "Light",
            5,
            "At the moment this can only be used to add Smite to Force Swords.  (This will change eventually.)"
        );
        TREASURE = new IForceUpgrade(
            7,
            "Treasure",
            1,
            "When entities are killed with a Treasure imbued weapon they will occasionally drop Treasure Cards, which can then be crafted into a Spoils Bag for some phat loot.  Fortune increases the drop rate as well."
        );
        IMPERVIOUS = new IForceUpgrade(
            7,
            "Impervious",
            1,
            "An insanely powerful upgrade that prevents any Force Tool possessing this enhancement from ever breaking."
        );
    }

    private static void addUpgrades() {
        addUpgrade(INFO);
        addUpgrade(FORCE);
        addUpgrade(DAMAGE);
        addUpgrade(FALSE);
        addUpgrade(HEAT);
        addUpgrade(SPEED);
        addUpgrade(LUMBERJACK);

        addUpgrade(LUCK);
        addUpgrade(TOUCH);
        addUpgrade(BLEED);
        addUpgrade(HEALING);
        addUpgrade(CAMO);
        addUpgrade(CRAFT);

        addUpgrade(RAINBOW);
        addUpgrade(BANE);
        addUpgrade(STORAGE);
        addUpgrade(STORAGE2);
        addUpgrade(STURDY);
        addUpgrade(SOUL);
        addUpgrade(SIGHT);
        addUpgrade(FORGE);
        addUpgrade(FREEZING);
        addUpgrade(GRINDING);
        addUpgrade(CHARGE);
        addUpgrade(CHARGE2);
        addUpgrade(WING);
        addUpgrade(ENDER);
        addUpgrade(EXP);
        addUpgrade(TIME);
        addUpgrade(LIGHT);
        addUpgrade(EXPLOSION);
        addUpgrade(GRAFTING);
        addUpgrade(REPAIR);
        addUpgrade(TREASURE);
        addUpgrade(IMPERVIOUS);
    }

    private static void loadUpgradeMaterials() {
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            416,
            INFO.getID(),
            0,
            0.0F,
            "If you haven't already done so, you should make a Book of Mudora, as it contains the sum of all DartCraft knowledge.  Simply Force Transmute a Bookshelf and you'll be on your way to mastering DartCraft!",
            false
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.forcenugget,
            FORCE.getID(),
            -5,
            0.75F,
            "It appears to be a small fragment of Force.  Perhaps I could use this to modify a sword.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.claw,
            DAMAGE.getID(),
            0,
            0.75F,
            "They say that bats hold the secret to increased damage.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.sugar, SPEED.getID(), 0, 1.0F, "Tastes like win, and diabetes.", true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            128,
            FALSE.getID(),
            10,
            1.5F,
            "It appears to be some sort of dust left behind by a Flask-wielding lunatic.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Item.getItemFromBlock(DartBlocks.forcelog),
            LUMBERJACK.getID(),
            5,
            1.5F,
            "Apparently these Forceful Logs hold the power to quickly destroying their brethren.",
            true
        ));
        if (!Config.hardHeat) {
            addMaterial(UpgradeMaterialHelper.createMaterial(
                DartItems.goldenpower,
                HEAT.getID(),
                5,
                1.5F,
                "This Golden Power is not so hard to obtain.",
                true
            ));
        } else {
            addMaterial(UpgradeMaterialHelper.createMaterial(
                Items.potionitem,
                8195,
                HEAT.getID(),
                10,
                2.0F,
                "This fiery brew seems like it would help one to swim in lava...  Successfully that is.",
                true
            ));
        }
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.dye,
            4,
            RAINBOW.getID(),
            0,
            0.25F,
            "Colorful cloth abounds with this gorgeous gem.",
            true
        ));
        if (!Config.hardSturdy) {
            addMaterial(UpgradeMaterialHelper.createMaterial(
                Item.getItemFromBlock(Blocks.brick_block),
                STURDY.getID(),
                0,
                1.25F,
                "Well, at least it's harder than dirt.",
                true
            ));
            addMaterial(UpgradeMaterialHelper.createMaterial(
                Item.getItemFromBlock(Blocks.obsidian),
                STURDY.getID(),
                10,
                1.75F,
                "Collecting this surrepticiously sturdy stone can be a seriously strenuous stint.",
                true
            ));
        }
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            129,
            STURDY.getID(),
            25,
            3.0F,
            "The hardest substance known to post-apocalyptic blocky man, ground into a powdery substance.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.fortune,
            LUCK.getID(),
            0,
            2.0F,
            "A nugget of otherworldly wisdom stuffed inside a delicious confection",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Item.getItemFromBlock(Blocks.web),
            TOUCH.getID(),
            5,
            2.0F,
            "Spiders aren't the only ones who can weave this Silky material.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.arrow, BLEED.getID(), 0, 1.0F, "You'll shoot your eye out!", true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.spider_eye,
            BANE.getID(),
            10,
            1.5F,
            "You probably shouldn't eat this...  Just jam it on a sword.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            134,
            STORAGE.getID(),
            0,
            0.25F,
            "I might try and upgrade a Force Pack with this strange item.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            135,
            STORAGE2.getID(),
            0,
            0.5F,
            "It looks like this item could increase the storage capacity of a Storage Unit.",
            true
        ));
        if (Config.easyWing) {
            addMaterial(UpgradeMaterialHelper.createMaterial(
                Items.feather,
                WING.getID(),
                0,
                3.0F,
                "The traditional symbol of freedom... and avian flu.",
                true
            ));
        }
        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            131,
            WING.getID(),
            5,
            3.0F,
            "The traditional symbol of freedom... and avian flu.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.flint,
            GRINDING.getID(),
            0,
            1.5F,
            "This is either a rock, or a really misshapen pancreas.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.ghast_tear,
            HEALING.getID(),
            10,
            2.0F,
            "Your tears give me strength... and regeneration.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.potionitem,
            8206,
            CAMO.getID(),
            5,
            2.0F,
            "Nothing to see here, except a floating set of armor.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.potionitem,
            8198,
            SIGHT.getID(),
            5,
            2.0F,
            "I'm pretty sure it's a potion of some kind, but then again I'm just a sentence.  Perhaps if I could just See a little better...",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Item.getItemFromBlock(Blocks.crafting_table),
            CRAFT.getID(),
            -20,
            1.0F,
            "A very Crafty block.",
            false
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Item.getItemFromBlock(Blocks.furnace),
            FORGE.getID(),
            -25,
            2.0F,
            "Our best traits are Forged by fire.  Perhaps an Item Card might be able to use this.",
            false
        ));

        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            195,
            FREEZING.getID(),
            0,
            2.0F,
            "Is this made out of snow?",
            true
        ));

        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.ender_pearl,
            ENDER.getID(),
            0,
            5.0F,
            "This dark orb must house some potent spatial magic.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.experience_bottle,
            EXP.getID(),
            15,
            2.25F,
            "I might be able to get one from an Enchanted Book.  Now where did that Force Rod go?  If I can manage to get one I should put it on a book.",
            true
        ));
        addMaterial(UpgradeMaterialHelper.createMaterial(
            Items.glowstone_dust,
            LIGHT.getID(),
            0,
            3.0F,
            "It's glowing, yet stone-like.",
            true
        ));

        if ((Config.timeUpgradeRod) || (Config.timeUpgradeSword)
            || (Config.timeUpgradeTorch)) {
            addMaterial(UpgradeMaterialHelper.createMaterial(
                Items.clock, TIME.getID(), 0, 2.5F, "A perfect timepiece.", true
            ));
        }

        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.resource,
            132,
            TREASURE.getID(),
            0,
            8.0F,
            "When entities are killed with a Treasure imbued weapon they will occasionally drop Treasure Cards, which can then be crafted into a Spoils Bag for some phat loot.  Fortune increases the drop rate as well.",
            false
        ));

        if (Config.insaneImpervious) {
            addMaterial(UpgradeMaterialHelper.createMaterial(
                DartItems.resource,
                133,
                IMPERVIOUS.getID(),
                0,
                8.0F,
                "An insanely powerful upgrade that prevents any Force Tool possessing this enhancement from ever breaking.",
                false
            ));
        }

        addMaterial(UpgradeMaterialHelper.createMaterial(
            DartItems.soulwafer,
            SOUL.getID(),
            0,
            2.0F,
            "The Soul upgrade on Force Swords and socketed Power Saws will occasionally cause Mob Chunks to drop, which can be used to craft vanilla spawners for that mob or even be smelted into colored Mob Ingots.",
            true
        ));
    }

    /*private static void loadWildCards() {
        ForceWildCards.addWildCard(new IForceWildCard(new ItemStack(Block.stoneBrick, 1,
    0), new ItemStack(DartBlock.forceBrick, 1, 11), FORCE)); ItemStack tomeStack = new
    ItemStack((Item)DartItem.forceTome);
        tomeStack.setTagCompound(TomeUtils.initExpComp());
        ForceWildCards.addWildCard(new IForceWildCard(new ItemStack(Items.book),
    tomeStack, EXP));
    }*/

    public static boolean addUpgrade(IForceUpgrade upgrade) {
        if (upgrades.size() > 0)
            for (IForceUpgrade temp : upgrades) {
                if (temp.getID() == upgrade.getID())
                    return false;
            }
        upgrades.add(upgrade);
        return true;
    }

    public static int getFirstUniqueUpgradeID() {
        firstUniqueID++;
        return firstUniqueID - 1;
    }

    public static void addMaterial(IForceUpgradeMaterial mat) {
        try {
            materials.add(mat);
            getFromID(mat.getUpgradeID()).addMat(mat);
        } catch (Exception e) {
            if (!(e instanceof NullPointerException))
                e.printStackTrace();
        }
    }

    public static IForceUpgrade getFromID(int id) {
        if (upgrades.size() > 0)
            for (IForceUpgrade temp : upgrades) {
                if (temp.getID() == id)
                    return temp;
            }
        return null;
    }

    public static IForceUpgradeMaterial getMaterialFromItemStack(ItemStack stack) {
        if (stack == null)
            return null;
        for (IForceUpgradeMaterial mat : materials) {
            if (mat == null)
                continue;
            if (mat.getItem() == (stack.getItem())
                && (mat.getItemMeta() == stack.getItemDamage()
                    || mat.getItemMeta() == 32767))
                return mat;
        }
        return null;
    }

    public static boolean isUpgradeValid(IForceUpgrade upgrade, IForceUpgradable item) {
        if (item.validUpgrades() != null)
            for (int id : item.validUpgrades()) {
                if (upgrade.getID() == id)
                    return true;
            }
        return false;
    }
}
