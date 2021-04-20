package ley.modding.dartcraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Config {

    public static boolean hardHeat;
    public static boolean hardSturdy;
    public static boolean easyWing;
    public static boolean timeUpgradeRod;
    public static boolean timeUpgradeSword;
    public static boolean timeUpgradeTorch;
    public static boolean insaneImpervious;

    public static int speedLevel;
    public static int damageLevel;
    public static int sturdyLevel;

    public static float gemValue = 0.25F;

    public static int powerOreRenderID;

    public static File engineFile;
    public static boolean disableRodSpeed;
    public static boolean disableRodHeal;
    public static boolean disableRodHeat;
    public static boolean disableRodEnder;
    public static boolean generateNetherOre = true;
    public static boolean generateOre = true;
    public static int powerOreFreq = 4;
    public static double netherFreq = 1.5;
    public static int powerOreRarity = 8;
    public static int powerOreSpawnHeight = 48;


    private static void generateDefaultEngineFile() {
        try {
            FileWriter e = new FileWriter(engineFile);
            BufferedWriter buffer = new BufferedWriter(e);
            buffer.write("#Place the Forge Liquid name (all lowercase) you wish to add as a fuel or\n");
            buffer.write("#throttle under the appropriate category using the below syntax.\n");
            buffer.write("#The first number is the burn value and the second is burn time.\n");
            buffer.write("#Liquid Force must be added as a fuel or defaults will be asserted.\n");
            Iterator i$ = getDefaultFuels().iterator();

            while(i$.hasNext()) {
                String name = (String)i$.next();
                buffer.write(name);
                buffer.write(10);
            }

            buffer.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    private static ArrayList getDefaultFuels() {
        ArrayList<String> defaults = new ArrayList<String>();
        //defaults.add("#Fuels.");
        defaults.add("f:liquidforce=4.0;20000");
        defaults.add("f:lava=0.5;20000");
        defaults.add("f:oil=1.5;20000");
        defaults.add("f:fuel=3.0;100000");
        defaults.add("f:bioethanol=2.0;60000");
        //defaults.add("\n#Throttles.");
        defaults.add("t:water=2.0;600");
        defaults.add("t:milk=2.5;3000");
        defaults.add("t:ice=4.0;20000");
        defaults.add("t:honey=3.0;10000");
        return defaults;
    }

    public static ArrayList getFuels() {
       /* ArrayList<String> fuels = new ArrayList<String>();

        try {
            BufferedReader e = new BufferedReader(new FileReader(engineFile));
            String line = null;

            while((line = e.readLine()) != null) {
                if(!line.startsWith("#") && !line.startsWith("\n")) {
                    fuels.add("" + line);
                }
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return fuels;*/
       return getDefaultFuels();
    }

}
