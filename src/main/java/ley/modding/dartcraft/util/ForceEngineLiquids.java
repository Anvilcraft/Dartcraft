package ley.modding.dartcraft.util;

import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.api.energy.EngineLiquid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ForceEngineLiquids {
    private static ArrayList fuels = new ArrayList();
    private static ArrayList throttles = new ArrayList();

    public static void load() {
        assertMilk();
        new ArrayList();
        boolean defaults = false;
        ArrayList input = Config.getFuels();
        if (input == null || input.size() <= 0) {
            defaults = true;
        }

        try {
            ArrayList e = getValues(input);
            HashMap fuels = (HashMap) e.get(0);
            HashMap throttles = (HashMap) e.get(1);
            if (defaults || fuels.size() < 1 || throttles.size() < 1
                || !fuels.containsKey("liquidforce")) {
                throw new Exception();
            }

            Iterator i$ = fuels.keySet().iterator();

            String name;
            FluidStack e1;
            EngineLiquid throttle;
            while (i$.hasNext()) {
                name = (String) i$.next();

                try {
                    e1 = new FluidStack(FluidRegistry.getFluid(name), 1000);
                    throttle = new EngineLiquid(
                        e1,
                        0,
                        (int) ((float[]) fuels.get(name))[1],
                        ((float[]) fuels.get(name))[0]
                    );
                    addLiquid(throttle);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            i$ = throttles.keySet().iterator();

            while (i$.hasNext()) {
                name = (String) i$.next();

                try {
                    e1 = new FluidStack(FluidRegistry.getFluid(name), 1000);
                    throttle = new EngineLiquid(
                        e1,
                        1,
                        (int) ((float[]) throttles.get(name))[1],
                        ((float[]) throttles.get(name))[0]
                    );
                    addLiquid(throttle);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        } catch (Exception e) {
            // Dartcraft..warning("There was an error loading the fuels.txt.  Please
            // configure it correctly.");
            e.printStackTrace();
            defaults = true;
        }

        if (defaults) {
            //DartCraft.dartLog.info("Loading default Fuels.");
            vanillaSupport();
            buildcraftSupport();
            forestrySupport();
        }
    }

    private static void assertMilk() {
        try {
            if (FluidRegistry.isFluidRegistered("milk")
                && FluidContainerRegistry.getFluidForFilledItem(
                       new ItemStack(Items.milk_bucket)
                   ) == null) {
                Fluid e = FluidRegistry.getFluid("milk");
                FluidStack milkStack = new FluidStack(e, 1000);
                FluidContainerRegistry.registerFluidContainer(
                    new FluidContainerRegistry.FluidContainerData(
                        milkStack,
                        new ItemStack(Items.milk_bucket),
                        FluidContainerRegistry.EMPTY_BUCKET
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList getValues(ArrayList input) {
        ArrayList values = new ArrayList();
        HashMap fuels = new HashMap();
        HashMap throttles = new HashMap();

        try {
            Iterator e = input.iterator();

            while (e.hasNext()) {
                String check = (String) e.next();
                if (check != null && check.length() > 0) {
                    try {
                        String e1 = "";
                        float value = 0.0F;
                        boolean time = false;
                        int time1;
                        switch (check.charAt(0)) {
                            case 102:
                                e1 = check.substring(2, check.indexOf(61));
                                if (!fuels.containsKey(e1)
                                    && FluidRegistry.getFluid(e1) != null) {
                                    value = (new Float(check.substring(
                                                 check.indexOf(61) + 1, check.indexOf(59)
                                             )))
                                                .floatValue();
                                    time1 = (new Integer(
                                                 check.substring(check.indexOf(59) + 1)
                                             ))
                                                .intValue();
                                    if (value < 0.5F) {
                                        value = 0.5F;
                                    }

                                    if (value > 20.0F) {
                                        value = 20.0F;
                                    }

                                    if (time1 < 100) {
                                        time1 = 100;
                                    }

                                    if (time1 > 1000000) {
                                        time1 = 1000000;
                                    }

                                    fuels.put(e1, new float[] { value, (float) time1 });
                                }
                                break;
                            case 116:
                                e1 = check.substring(2, check.indexOf(61));
                                if (!throttles.containsKey(e1)
                                    && FluidRegistry.getFluid(e1) != null) {
                                    value = (new Float(check.substring(
                                                 check.indexOf(61) + 1, check.indexOf(59)
                                             )))
                                                .floatValue();
                                    time1 = (new Integer(
                                                 check.substring(check.indexOf(59) + 1)
                                             ))
                                                .intValue();
                                    if (value < 1.0F) {
                                        value = 1.0F;
                                    }

                                    if (value > 20.0F) {
                                        value = 20.0F;
                                    }

                                    if (time1 < 100) {
                                        time1 = 100;
                                    }

                                    if (time1 > 1000000) {
                                        time1 = 1000000;
                                    }

                                    throttles.put(
                                        e1, new float[] { value, (float) time1 }
                                    );
                                }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        values.add(fuels);
        values.add(throttles);
        return values;
    }

    private static void vanillaSupport() {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        FluidStack milk = FluidRegistry.getFluidStack("milk", 1000);
        if (liquidForce != null) {
            addLiquid(new EngineLiquid(new FluidStack(liquidForce, 1000), 0, 20000, 4.0F)
            );
        }

        if (milk != null) {
            addLiquid(new EngineLiquid(milk, 1, 3000, 2.5F));
        }

        addLiquid(
            new EngineLiquid(new FluidStack(FluidRegistry.WATER, 1000), 1, 600, 2.0F)
        );
        addLiquid(
            new EngineLiquid(new FluidStack(FluidRegistry.LAVA, 1000), 0, 20000, 0.5F)
        );
    }

    private static void buildcraftSupport() {
        FluidStack oil = FluidRegistry.getFluidStack("oil", 1000);
        if (oil != null) {
            addLiquid(new EngineLiquid(oil, 0, 20000, 1.5F));
        }

        FluidStack fuel = FluidRegistry.getFluidStack("fuel", 1000);
        if (fuel != null) {
            addLiquid(new EngineLiquid(fuel, 0, 100000, 3.0F));
        }
    }

    private static void forestrySupport() {
        FluidStack crushedIce = FluidRegistry.getFluidStack("ice", 1000);
        if (crushedIce != null) {
            addLiquid(new EngineLiquid(crushedIce, 1, 20000, 4.0F));
        }

        FluidStack ethanol = FluidRegistry.getFluidStack("bioethanol", 1000);
        if (ethanol != null) {
            addLiquid(new EngineLiquid(ethanol, 0, '\uea60', 2.0F));
        }
    }

    public static void addLiquid(EngineLiquid liquid) {
        if (liquid != null) {
            if (liquid.getType() == 0 && !isThrottle(liquid.getLiquid())) {
                fuels.add(liquid);
                System.out.println(
                    "Added fuel: " + liquid.getLiquid().getFluid().getName()
                );
            }

            if (liquid.getType() == 1 && !isFuel(liquid.getLiquid())) {
                throttles.add(liquid);
                System.out.println(
                    "Added throttle: " + liquid.getLiquid().getFluid().getName()
                );
            }
        }
    }

    public static boolean isFuel(FluidStack liquid) {
        if (fuels != null && fuels.size() > 0 && liquid != null) {
            Iterator i$ = fuels.iterator();

            while (i$.hasNext()) {
                EngineLiquid fuel = (EngineLiquid) i$.next();
                if (fuel != null && fuel.getLiquid() != null
                    && fuel.getLiquid().isFluidEqual(liquid)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isThrottle(FluidStack liquid) {
        if (throttles != null && throttles.size() > 0 && liquid != null) {
            Iterator i$ = throttles.iterator();

            while (i$.hasNext()) {
                EngineLiquid throttle = (EngineLiquid) i$.next();
                if (throttle != null && throttle.getLiquid() != null
                    && throttle.getLiquid().isFluidEqual(liquid)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static EngineLiquid getEngineLiquid(FluidStack liquid) {
        Iterator i$;
        EngineLiquid throttle;
        if (fuels != null && fuels.size() > 0 && liquid != null) {
            i$ = fuels.iterator();

            while (i$.hasNext()) {
                throttle = (EngineLiquid) i$.next();
                if (throttle != null && throttle.getLiquid() != null
                    && throttle.getLiquid().isFluidEqual(liquid)) {
                    return throttle;
                }
            }
        }

        if (throttles != null && throttles.size() > 0 && liquid != null) {
            i$ = throttles.iterator();

            while (i$.hasNext()) {
                throttle = (EngineLiquid) i$.next();
                if (throttle != null && throttle.getLiquid() != null
                    && throttle.getLiquid().isFluidEqual(liquid)) {
                    return throttle;
                }
            }
        }

        return null;
    }
}
