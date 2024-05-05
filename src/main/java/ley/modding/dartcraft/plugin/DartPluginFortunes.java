package ley.modding.dartcraft.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DartPluginFortunes {
    private static Random rand;
    private static List<String> fortunes = new ArrayList<>();
        
    static {
        load();
    }

    public static void addFortune(String fortune) {
        if (fortunes != null && fortune != null) {
            fortunes.add(fortune);
        }
    }

    public static void load() {
        rand = new Random(System.nanoTime());
        DartPluginFortunes.loadFortunes();
    }

    public static String getFortune() {
        if (fortunes == null || fortunes.size() < 1) {
            return "";
        }
        int index = rand.nextInt(fortunes.size());
        if (index >= fortunes.size()) {
            return "";
        }
        return (String)fortunes.get(index);
    }

    private static void loadFortunes() {
        addFortune("You aren't supposed to eat me.");
        addFortune("Beauty is in the eye of the tiger.");
        addFortune("Creeper!");
        addFortune("Which came first, the chicken or the chunk?");
        addFortune("Whatever happened to Doritos 3D?");
        addFortune("Diabetes, anyone?");
        addFortune("That wasn't a cookie!");
        addFortune("If a tree falls down in the woods, you have a special mod installed.");
        addFortune("The cake is a lie of omission.");
        addFortune("A wise man once said, \"Yes honey, it does make you look fat.\"  He never said that again.");
        addFortune("Don't stare directly at the pixels.");
        addFortune("I know where you live.");
        addFortune("Your lucky numbers are 0, -7, and 437922904678137496.708162");
        addFortune("There is never enough redstone.");
        addFortune("What you seek is surrounded by blocks.");
        addFortune("Today is Tuesday, or is it Friday - I can never tell.");
        addFortune("In the event of a creeper explosion, your keyboard can double as a broken keyboard.");
        addFortune("I didn't do it.");
        addFortune("You are 5.6 grams heavier than you were 10 seconds ago.");
        addFortune("I dropped my cookie.");
        addFortune("...Saltpeter?  Really?");
        addFortune("We're no strangers to love.  You know the rules and so do I.");
        addFortune("Eat another cookie.");
        addFortune("fontRenderer.drawString(\"Totally Real Accidental Glitch\", posX, posY, 0xFFFFFF);");
        addFortune("I CAN believe it's not butter.");
        addFortune("Beware enderman with a short fuse...");
        addFortune("Remember to repair your tools.");
        addFortune("Every rose has its bounding box.");
        addFortune("Get out of my chest!");
        addFortune("Please recycle.");
        addFortune("Great, now you've spoiled your dinner!");
        addFortune("Welcome to Hoarders: Minecraft edition!");
        addFortune("Not all Blocks are created equal.");
        addFortune("Don't touch that!");
        addFortune("Always name your machinations.");
        addFortune("Look in your inventory, now back to me - this fortune is now diamonds!");
        addFortune("Who put that there?");
        addFortune("Winners never cheat and cheaters never win, unless the winners cheated, in which case the cheaters won.");
        addFortune("Hi Bob!  What, they can't all be zingers.");
        addFortune("Have you ever thought to yourself, \"My, that's an awfully large open grave!\"");
        addFortune("Could you pick up my dry-cleaning?");
        addFortune("Never shower in a thunderstorm.  It's less efficient than bathing indoors and you'll freak out your neighbors.");
        addFortune("It is said that everyone experiences hardships, but God must REALLY hate YOU.");
        addFortune("If you play a country song backwards, you waste about 4 minutes of your life listening to garbled nonsense.");
        addFortune("No, you can't make that jump.");
        addFortune("I know they're furry and cuddly, but trust me, they're evil incarnate.");
        addFortune("Do you Dew?");
        addFortune("I see the land, but where exactly are the tracts?");
        addFortune("Creepers were originally from Dr. Who.");
        addFortune("Don't bogart my nasal spray!");
        addFortune("You'll live.");
        addFortune("Don't make me come back there!");
        addFortune("I've burned everything that reminds me of you in a ritualistic bonfire.");
        addFortune("We will be an unstoppable force; to our enemies we bring only death, to our allies we always bring cake.");
        addFortune("Heavy is the head that eats the crayons.");
        addFortune("Beware skinwalkers.");
        addFortune("Don't fear the creeper.");
        addFortune("Goodness and love will always win!");
        addFortune("I told you not to eat that!");
        addFortune("Winner!");
        addFortune("Beware anyone without an eye-patch!");
        addFortune("Don't kill all the animals!");
        addFortune("It's a wonder you get anything done!");
        addFortune("You will find happiness with a new love: Minecraft's latest patch.");
        addFortune("Seriously, who is in charge of the song/cowbell ratio?");
        addFortune("It's not a bug, it's a \"feature.\"");
        addFortune("Do you really need this many cookies?");
        addFortune("NCC1701");
        addFortune("I wanted to be a newspaper.");
        addFortune("Thank you!  It was really dark in there!");
        addFortune("Thank you for not eating me.");
        addFortune("Burn the door!");
        addFortune("That's the biggest flapjack I've ever seen!");
        addFortune("Please be kind, de-rind");
        addFortune("It's a secret to everybody.");
        addFortune("I AM ERROR.");
        addFortune("If all else fails use fire.");
        addFortune("Dig it!  Dig it!  Dig to the center of the earth!");
        addFortune("No!  Not into the pit!  It BURNS!");
        addFortune("Dawn of the First Day, 72 hours remain.");
        addFortune("Don't stare directly at the potato.");
        addFortune("The chicken is a double-agent.");
        addFortune("Good lord!");
        addFortune("What's all this junk?");
        addFortune("Creepers blow chunks.");
        addFortune("Equivalence is a lie.");
        addFortune("Body by Sycamore.");
        addFortune("I hear 'Innards of the Machine' is on tour.");
        addFortune("Do something else.");
        addFortune("The capital of The Ukraine is Kiev.");
        addFortune("DebugCookie4A73N82");
        addFortune("Point that somewhere else!");
        addFortune("Forking is strictly prohibited.");
        addFortune("Void where prohibited.");
        addFortune("This parrot is no more!");
        addFortune("He's dead, Jim.");
        addFortune("Leave me alone for a bit, okay?");
        addFortune("Don't you dare shift-click me!");
        addFortune("Me again.");
        addFortune("My summer home is a no-bake.");
        addFortune("We can still be friends.");
        addFortune("The night is young, but you are not.");
        addFortune("Keyboard cat has carpal tunnel.");
        addFortune("Pull lever, get key.");
        addFortune("Boats n' Hoes");
        addFortune("Never eat an entire chocolate bunny in one sitting.");
        addFortune("Push that button and die.");
        addFortune("That cookie was mostly spackle.");
        addFortune("Slime Chunkery.");
        addFortune("I hate Thaumic Slimes.");
        addFortune("Inertia is a property of mallard.");
        addFortune("I prefer cake.");
        addFortune("If you can read this, you're literate.");
        addFortune("Don't touch the sides!");
        addFortune("Crunchitize me Cap'n!");
        addFortune("Please head in an orderly fashion to the disintegration chamber.");
        addFortune("It's the deer's ears.");
        addFortune("Happy anniversary, Carol!");
        addFortune("Never startle a ninja.");
        addFortune("If the shoe fits, you probably own it.");
        addFortune("Cattle reproduce by budding.");
        addFortune("Has anyone seen my Fedora?");
        addFortune("I beg to defer.");
        addFortune("Everybody loops.");
        addFortune("Why is Count Chocula seasonal?  Pedophilic vampires are always in style!");
        addFortune("Eekum Bokum.");
        addFortune("Churba wurt!");
        addFortune("Darwa jit!");
        addFortune("Success is often preceded by failure, then followed again by failure.");
        addFortune("Man with Steve skin receive no cake.");
        addFortune("It's all Melnics to me!");
        addFortune("\"Steve\" means \"lazy\" in Swedish.");
        addFortune("This is the word of Notch.");
        addFortune("Don't attack the cute little endermen - you'll be sorry.");
        addFortune("I miss my sanity, but then again I never used it.");
        addFortune("So this is where germs are born!");
        addFortune("I hate mondays.");
        addFortune("My old nemesis: gravity.");
        addFortune("I'm upgrade fodder!");
        addFortune("You can do anything at Zombo.com.");
        addFortune("Remember to feed the cattle.");
        addFortune("TROGDOR was a man.  I mean, he was a dragon-man...  Maybe he was just a dragon.");
        addFortune("Charles in charge of our clicks left and right.  Charles in charge of every block in sight.");
        addFortune("Charles was never really in charge.");
        addFortune("Remember to use every part of the chicken.");
        addFortune("I'm not responsible for this.");
        addFortune("Every 10 minutes another crappy fortune is written.");
        addFortune("How many licks does it take to get to the center of a Chuck Norris?");
        addFortune("Roundhouse-kick for the win!");
        addFortune("DO NOT feed the beast!  (After midnight)");
        addFortune("Please stop doing that.");
        addFortune("I'm not a vending machine!");
        addFortune("Try Ubuntu.");
        addFortune("If you program for more than 20 hours straight, do not blink.");
        addFortune("Ceiling cat is watching you procrastinate.");
        addFortune("Get your hand off my thigh!  You can have the leg.");
        addFortune("Protective eyewear is a must!");
        addFortune("It's all in the wristwatch.");
        addFortune("Endermen in the UCB!");
        addFortune("Endermen can move TNT.  I'm serious.");
        addFortune("Aww, you got blood all over my new sword!");
        addFortune("The human pancreas can take only so many twinkies.");
        addFortune("Humus is something you eat when you want your innards to cry.");
        addFortune("By Mennen");
        addFortune("Put me back in!");
        addFortune("...I got nothin'.");
        addFortune("We're out of milk.");
        addFortune("Always edit-out the derps.");
        addFortune("I want a lawyer.");
        addFortune("Bring me a shrubbery!");
        addFortune("It's bigger on the inside.");
        addFortune("That's what she said.");
        addFortune("Have you heard the one about the Rabbi and the Priest?  Oh I forgot, you're deaf.");
        addFortune("There are worse appendages to get caught sticking inside the cookie jar.");
        addFortune("Ever have the feeling you're being watched?  That's me!");
        addFortune("He who handles his NullPointerExceptions is a wise man indeed.");
        addFortune("Taking candy from a baby often prevents choking.");
        addFortune("Texting while driving is a potent form of natural-selection.");
        addFortune("The secret to a good marriage is matching tattoos.");
        addFortune("A sucker is born every minute, however an idiot is born every two seconds.");
        addFortune("Error in Thread Main: ExceptionNotCaughtException: DartCraft.java:32");
        addFortune("I'll tear YOUR stub!");
        addFortune("Daydreaming is free, cookies are not.");
        addFortune("PINGAS!");
        addFortune("The run is dead.");

        addFortune("Alec Höfler");
    }
}

