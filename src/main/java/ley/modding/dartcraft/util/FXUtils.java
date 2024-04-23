package ley.modding.dartcraft.util;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.fx.FXCure;
import ley.modding.dartcraft.client.fx.FXDisney;
import ley.modding.dartcraft.client.fx.FXTime;
import ley.modding.dartcraft.client.fx.FXWindWaker;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class FXUtils {
    public static Random rand = new Random();

    @SideOnly(Side.CLIENT)
    public static void bindGLColor(int color) {
        byte e = (byte) (color << 24);
        byte green = (byte) (color << 16);
        byte blue = (byte) (color << 8);
        GL11.glColor4f((float) e, (float) green, (float) blue, -1.0F);
    }

    public static void
    makeEnderEffects(World world, double x, double y, double z, int number) {
        if (!Dartcraft.proxy.isSimulating(world)) {
            float modifier = 0.1F;

            for (int i = 0; i < number; ++i) {
                double var10002 = x + world.rand.nextDouble() - world.rand.nextDouble();
                double var10003 = y + world.rand.nextDouble() - world.rand.nextDouble();
                double var10004 = z + world.rand.nextDouble() - world.rand.nextDouble();
                double var10
                    = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F) * modifier);
                double var10006 = (double) modifier;
                world.spawnParticle(
                    "portal",
                    var10002,
                    var10003,
                    var10004,
                    var10,
                    var10006,
                    (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F) * modifier)
                );
            }
        }
    }

    public static void
    makeHeatEffects(World world, int x, int y, int z, int number, int area) {
        makeHeatEffects(
            world,
            (double) x + 0.5D,
            (double) y + 1.0D,
            (double) z + 0.5D,
            number,
            area,
            1
        );
    }

    public static void
    makeHeatEffects(World world, double x, double y, double z, int number, int area) {
        makeHeatEffects(world, x, y, z, number, area, 0);
    }

    public static void makeHeatEffects(
        World world, double x, double y, double z, int number, int area, int type
    ) {
        if (!Dartcraft.proxy.isSimulating(world)) {
            float modifier = 0.1F;
            int i;
            if (area > 0) {
                for (i = -area; i < area + 1; ++i) {
                    for (int velX = -area; velX < area + 1; ++velX) {
                        for (int k = -area; k < area + 1; ++k) {
                            for (int velZ = 0; velZ < number; ++velZ) {
                                double velX1
                                    = (double) ((CommonProxy.rand.nextFloat() * 2.0F
                                                 - 1.0F)
                                                * modifier);
                                double velZ1
                                    = (double) ((CommonProxy.rand.nextFloat() * 2.0F
                                                 - 1.0F)
                                                * modifier);
                                double dist1 = 1.0D;
                                if (type == 1) {
                                    velX1 = 0.0D;
                                    velZ1 = 0.0D;
                                    modifier = (float) ((double) modifier * 0.5D);
                                    dist1 *= 0.5D;
                                }

                                world.spawnParticle(
                                    "flame",
                                    x + world.rand.nextDouble() * dist1
                                        - world.rand.nextDouble() * dist1,
                                    y + world.rand.nextDouble() * dist1
                                        - world.rand.nextDouble() * dist1,
                                    z + world.rand.nextDouble() * dist1
                                        - world.rand.nextDouble() * dist1,
                                    velX1,
                                    (double) modifier,
                                    velZ1
                                );
                            }
                        }
                    }
                }
            } else {
                for (i = 0; i < number; ++i) {
                    double var21 = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                             * modifier);
                    double var22 = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                             * modifier);
                    double dist = 1.0D;
                    if (type == 1) {
                        var21 = 0.0D;
                        var22 = 0.0D;
                        modifier = (float) ((double) modifier * 0.25D);
                        dist *= 0.5D;
                    }

                    world.spawnParticle(
                        "flame",
                        x + world.rand.nextDouble() * dist
                            - world.rand.nextDouble() * dist,
                        y + world.rand.nextDouble() * dist
                            - world.rand.nextDouble() * dist,
                        z + world.rand.nextDouble() * dist
                            - world.rand.nextDouble() * dist,
                        var21,
                        (double) modifier,
                        var22
                    );
                }
            }
        }
    }

    public static void makeEnderEffects(
        Entity entity, double prevX, double prevY, double prevZ, int number, boolean sound
    ) {
        World world = entity.worldObj;
        if (!Dartcraft.proxy.isSimulating(world)) {
            for (int i = 0; i < number; ++i) {
                double var19 = (double) i / ((double) number - 1.0D);
                float var21 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                double var221 = prevX + (entity.posX - prevX) * var19;
                double var24 = var221
                    + (CommonProxy.rand.nextDouble() - 0.5D) * (double) entity.width
                        * 2.0D;
                var221 = prevY + (entity.posY - prevY) * var19;
                double var26
                    = var221 + CommonProxy.rand.nextDouble() * (double) entity.height;
                var221 = prevZ + (entity.posZ - prevZ) * var19;
                double var28 = var221
                    + (CommonProxy.rand.nextDouble() - 0.5D) * (double) entity.width
                        * 2.0D;
                world.spawnParticle(
                    "portal",
                    var24,
                    var26,
                    var28,
                    (double) var21,
                    (double) var22,
                    (double) var23
                );
            }
        } else if (sound) {
            world.playSoundEffect(
                prevX, prevY, prevZ, "mob.endermen.portal", 2.0F, DartUtils.randomPitch()
            );
            world.playSoundAtEntity(
                entity, "mob.endermen.portal", 2.0F, DartUtils.randomPitch()
            );
        }
    }

    public static void makeEnderEffects(
        Entity entity,
        double posX,
        double posY,
        double posZ,
        double prevX,
        double prevY,
        double prevZ,
        int number,
        boolean sound
    ) {
        if (!Dartcraft.proxy.isSimulating(entity.worldObj)) {
            for (int i = 0; i < number; ++i) {
                double var19 = (double) i / ((double) number - 1.0D);
                float var21 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                double var27 = prevX + (posX - prevX) * var19;
                double var24 = var27
                    + (CommonProxy.rand.nextDouble() - 0.5D) * (double) entity.width
                        * 2.0D;
                var27 = prevY + (posY - prevY) * var19;
                double var26
                    = var27 + CommonProxy.rand.nextDouble() * (double) entity.height;
                var27 = prevZ + (posZ - prevZ) * var19;
                double var28 = var27
                    + (CommonProxy.rand.nextDouble() - 0.5D) * (double) entity.width
                        * 2.0D;
                entity.worldObj.spawnParticle(
                    "portal",
                    var24,
                    var26,
                    var28,
                    (double) var21,
                    (double) var22,
                    (double) var23
                );
            }
        } else if (sound) {
            entity.worldObj.playSoundEffect(
                prevX, prevY, prevZ, "mob.endermen.portal", 2.0F, 1.0F
            );
            entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 2.0F, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeChangeEffects(
        World world, double x2, double y2, double z2, int type, int particles
    ) {
        float modifier = 0.5F;
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;

        for (int i = 0; i < particles; ++i) {
            float x = (float) (x2 + (double) (CommonProxy.rand.nextFloat() * modifier)
                               - (double) (modifier / 2.0F));
            float y = (float) (y2 + (double) (CommonProxy.rand.nextFloat() * modifier)
                               - (double) (modifier / 2.0F));
            float z = (float) (z2 + (double) (CommonProxy.rand.nextFloat() * modifier)
                               - (double) (modifier / 2.0F));
            renderer.addEffect(
                new FXWindWaker(world, (double) x, (double) y, (double) z, 0xffffff, type)
            );
        }
    }

    @SideOnly(Side.CLIENT)
    public static void
    makeWingEffects(World world, double x, double y, double z, int particles) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        float modifier = 1.0F;

        for (int i = 0; i < particles; ++i) {
            float x2 = (float) (x + (double) (CommonProxy.rand.nextFloat() * modifier));
            float y2 = (float) (y + (double) (CommonProxy.rand.nextFloat() * modifier));
            float z2 = (float) (z + (double) (CommonProxy.rand.nextFloat() * modifier));
            renderer.addEffect(
                new FXWindWaker(world, (double) x2, (double) y2, (double) z2, '\ue4ff', 0)
            );
        }
    }

    @SideOnly(Side.CLIENT)
    public static void
    makeSkateEffects(World world, double x, double y, double z, int particles) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        float modifier = 1.0F;

        for (int i = 0; i < particles; ++i) {
            float x2 = (float) (x + (double) (CommonProxy.rand.nextFloat() * modifier));
            float y2 = (float) (y + (double) (CommonProxy.rand.nextFloat() * modifier));
            float z2 = (float) (z + (double) (CommonProxy.rand.nextFloat() * modifier));
            renderer.addEffect(
                new FXWindWaker(world, (double) x2, (double) y2, (double) z2, '\ue4ff', 3)
            );
        }
    }

    public static void spawnFlameFX(World world, int x, int y, int z) {
        for (int i = 0; i < 10; ++i) {
            float posX = (float) x + world.rand.nextFloat() * 1.0F;
            float posY = (float) y + world.rand.nextFloat() * 1.0F;
            float posZ = (float) z + world.rand.nextFloat() * 1.0F;
            world.spawnParticle(
                "flame", (double) posX, (double) posY, (double) posZ, 0.0D, 0.0D, 0.0D
            );
        }
    }

    public static void makeBreakFX(World world, int x, int y, int z) {
        for (int i = 0; i < 10; ++i) {
            float posX = (float) x + world.rand.nextFloat() * 1.0F;
            float posY = (float) y + world.rand.nextFloat() * 1.0F;
            float posZ = (float) z + world.rand.nextFloat() * 1.0F;
            world.spawnParticle(
                "portal", (double) posX, (double) posY, (double) posZ, 0.0D, 0.0D, 0.0D
            );
        }
    }

    @SideOnly(Side.CLIENT)
    public static void drawTexturedQuad(
        int par1, int par2, int par3, int par4, int par5, int par6, double zLevel
    ) {
        float var7 = 0.0039063F;
        float var8 = 0.0039063F;
        Tessellator tessy = Tessellator.instance;
        tessy.startDrawingQuads();
        tessy.addVertexWithUV(
            (double) (par1 + 0),
            (double) (par2 + par6),
            zLevel,
            (double) ((float) (par3 + 0) * var7),
            (double) ((float) (par4 + par6) * var8)
        );
        tessy.addVertexWithUV(
            (double) (par1 + par5),
            (double) (par2 + par6),
            zLevel,
            (double) ((float) (par3 + par5) * var7),
            (double) ((float) (par4 + par6) * var8)
        );
        tessy.addVertexWithUV(
            (double) (par1 + par5),
            (double) (par2 + 0),
            zLevel,
            (double) ((float) (par3 + par5) * var7),
            (double) ((float) (par4 + 0) * var8)
        );
        tessy.addVertexWithUV(
            (double) (par1 + 0),
            (double) (par2 + 0),
            zLevel,
            (double) ((float) (par3 + 0) * var7),
            (double) ((float) (par4 + 0) * var8)
        );
        tessy.draw();
    }

    @SideOnly(Side.CLIENT)
    public static void makeShiny(
        World world,
        double x,
        double y,
        double z,
        int type,
        int color,
        int num,
        boolean add
    ) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        double offset = 0.0D;
        if (add) {
            offset += 0.5D;
        }

        for (int i = 0; i < num; ++i) {
            FXDisney fx = new FXDisney(
                world,
                x + offset + world.rand.nextDouble() - world.rand.nextDouble(),
                y + world.rand.nextDouble() - world.rand.nextDouble(),
                z + offset + world.rand.nextDouble() - world.rand.nextDouble(),
                color,
                type
            );
            renderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeCureEffects(
        World world, double x, double y, double z, int type, int color, int num
    ) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;

        for (int i = 0; i < num; ++i) {
            FXCure fx = new FXCure(
                world,
                x + world.rand.nextDouble() - world.rand.nextDouble(),
                y + world.rand.nextDouble() - world.rand.nextDouble(),
                z + world.rand.nextDouble() - world.rand.nextDouble(),
                color,
                type
            );
            renderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeChargeEffects(
        World world,
        double x,
        double y,
        double z,
        int type,
        int color,
        int num,
        boolean add
    ) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        double offset = 0.0D;
        if (add) {
            offset += 0.5D;
        }

        for (int i = 0; i < num; ++i) {
            // TODO
            //FXCharge fx = new FXCharge(
            //    world,
            //    x + offset + world.rand.nextDouble() - world.rand.nextDouble(),
            //    y + world.rand.nextDouble() - world.rand.nextDouble(),
            //    z + offset + world.rand.nextDouble() - world.rand.nextDouble(),
            //    color,
            //    type
            //);
            //renderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeIceEffects(
        World world, double x, double y, double z, int type, int num, int area
    ) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        int i;
        if (area > 0) {
            for (i = -area; i < area + 1; ++i) {
                for (int fx = -area; fx < area + 1; ++fx) {
                    for (int k = -area; k < area + 1; ++k) {
                        for (int l = 0; l < num; ++l) {
                            // TODO
                            //FXIce fx1 = new FXIce(
                            //    world,
                            //    x + (double) i + world.rand.nextDouble()
                            //        - world.rand.nextDouble(),
                            //    y + (double) fx + world.rand.nextDouble()
                            //        - world.rand.nextDouble(),
                            //    z + (double) k + world.rand.nextDouble()
                            //        - world.rand.nextDouble(),
                            //    type
                            //);
                            //renderer.addEffect(fx1);
                        }
                    }
                }
            }
        } else {
            for (i = 0; i < num; ++i) {
                // TODO
                //FXIce var16 = new FXIce(
                //    world,
                //    x + world.rand.nextDouble() - world.rand.nextDouble(),
                //    y + world.rand.nextDouble() - world.rand.nextDouble(),
                //    z + world.rand.nextDouble() - world.rand.nextDouble(),
                //    type
                //);
                //renderer.addEffect(var16);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeTimeEffects(
        World world, double x, double y, double z, int type, int num, int area
    ) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        int i;
        if (area > 0) {
            for (i = -area; i < area + 1; ++i) {
                for (int fx = -area; fx < area + 1; ++fx) {
                    for (int k = -area; k < area + 1; ++k) {
                        for (int l = 0; l < num; ++l) {
                            FXTime fx1 = new FXTime(
                                world,
                                x + (double) i + world.rand.nextDouble()
                                    - world.rand.nextDouble(),
                                y + (double) fx + world.rand.nextDouble()
                                    - world.rand.nextDouble(),
                                z + (double) k + world.rand.nextDouble()
                                    - world.rand.nextDouble(),
                                type
                            );
                            renderer.addEffect(fx1);
                        }
                    }
                }
            }
        } else {
            for (i = 0; i < num; ++i) {
                FXTime var16 = new FXTime(
                    world,
                    x + world.rand.nextDouble() - world.rand.nextDouble(),
                    y + world.rand.nextDouble() - world.rand.nextDouble(),
                    z + world.rand.nextDouble() - world.rand.nextDouble(),
                    type
                );
                renderer.addEffect(var16);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeWWEffects(
        World world, double x, double y, double z, int color, int type, int num, int area
    ) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        int i;
        if (area > 0) {
            for (i = -area; i < area + 1; ++i) {
                for (int fx = -area; fx < area + 1; ++fx) {
                    for (int k = -area; k < area + 1; ++k) {
                        for (int l = 0; l < num; ++l) {
                            FXWindWaker fx1 = new FXWindWaker(
                                world,
                                x + (double) i + world.rand.nextDouble()
                                    - world.rand.nextDouble(),
                                y + (double) fx + world.rand.nextDouble()
                                    - world.rand.nextDouble(),
                                z + (double) k + world.rand.nextDouble()
                                    - world.rand.nextDouble(),
                                color,
                                type
                            );
                            renderer.addEffect(fx1);
                        }
                    }
                }
            }
        } else {
            for (i = 0; i < num; ++i) {
                FXWindWaker var17 = new FXWindWaker(
                    world,
                    x + world.rand.nextDouble() - world.rand.nextDouble(),
                    y + world.rand.nextDouble() - world.rand.nextDouble(),
                    z + world.rand.nextDouble() - world.rand.nextDouble(),
                    color,
                    type
                );
                renderer.addEffect(var17);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void randomFireworkAt(World world, double x, double y, double z) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        NBTTagCompound comp = new NBTTagCompound();
        NBTTagList fireworks = new NBTTagList();

        for (int fx = 0; fx < world.rand.nextInt(5) + 1; ++fx) {
            NBTTagCompound tempComp = new NBTTagCompound();
            tempComp.setBoolean("Flicker", world.rand.nextBoolean());
            tempComp.setBoolean("Trails", world.rand.nextBoolean());
            tempComp.setIntArray("Colors", randomColorArray());
            tempComp.setIntArray("FadeColors", randomColorArray());
            fireworks.appendTag(tempComp);
        }

        comp.setTag("Explosions", fireworks);
        EntityFireworkStarterFX var12 = new EntityFireworkStarterFX(
            world, x, y, z, 0.0D, 0.0D, 0.0D, renderer, comp
        );
        renderer.addEffect(var12);
    }

    @SideOnly(Side.CLIENT)
    public static void wrathAt(World world, int x, int y, int z) {
        EntityLightningBolt bolt
            = new EntityLightningBolt(world, (double) x, (double) y, (double) z);
        world.spawnEntityInWorld(bolt);
    }

    public static int[] randomColorArray() {
        byte size = 16;
        int[] colors = new int[size];

        for (int i = 0; i < size; ++i) {
            colors[i] = randomColor();
        }

        return colors;
    }

    public static int randomColor() {
        int color = (int) ((float) rand.nextInt(256) * (rand.nextFloat() * 0.2F + 0.9F))
                << 16
            | (int) ((float) rand.nextInt(256) * (rand.nextFloat() * 0.2F + 0.9F)) << 8
            | (int) ((float) rand.nextInt(256) * (rand.nextFloat() * 0.2F + 0.9F));
        return color;
    }

    public static int randomParticleColor() {
        int color = Color.yellow.getRGB();
        switch (rand.nextInt(6)) {
            case 0:
                color = Color.blue.getRGB();
                break;
            case 1:
                color = Color.pink.getRGB();
                break;
            case 2:
                color = Color.red.getRGB();
                break;
            case 3:
                color = Color.magenta.getRGB();
                break;
            case 4:
                color = Color.green.getRGB();
        }

        return color;
    }
}
