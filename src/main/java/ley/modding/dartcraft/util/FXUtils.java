package ley.modding.dartcraft.util;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.fx.FXCharge;
import ley.modding.dartcraft.client.fx.FXCure;
import ley.modding.dartcraft.client.fx.FXDisney;
import ley.modding.dartcraft.client.fx.FXTime;
import ley.modding.dartcraft.client.fx.FXWindWaker;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
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
        byte red = (byte) (color << 24);
        byte green = (byte) (color << 16);
        byte blue = (byte) (color << 8);
        GL11.glColor4f((float) red, (float) green, (float) blue, -1.0F);
    }

    public static void makeEnderEffects(WorldVec pos, int number) {
        if (!Dartcraft.proxy.isSimulating(pos.world)) {
            float modifier = 0.1F;

            for (int i = 0; i < number; ++i) {
                double var10002
                    = pos.x + pos.world.rand.nextDouble() - pos.world.rand.nextDouble();
                double var10003
                    = pos.y + pos.world.rand.nextDouble() - pos.world.rand.nextDouble();
                double var10004
                    = pos.z + pos.world.rand.nextDouble() - pos.world.rand.nextDouble();
                double var10
                    = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F) * modifier);
                double var10006 = (double) modifier;
                pos.world.spawnParticle(
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

    public static void makeHeatEffects(WorldVec pos, int number, int area) {
        makeHeatEffects((WorldVec) pos.offset(0.5d, 1.0d, 0.5d), number, area, 1);
    }

    public static void makeHeatEffects(WorldVec pos, int number, int area, int type) {
        if (!Dartcraft.proxy.isSimulating(pos.world)) {
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

                                pos.world.spawnParticle(
                                    "flame",
                                    pos.x + pos.world.rand.nextDouble() * dist1
                                        - pos.world.rand.nextDouble() * dist1,
                                    pos.y + pos.world.rand.nextDouble() * dist1
                                        - pos.world.rand.nextDouble() * dist1,
                                    pos.z + pos.world.rand.nextDouble() * dist1
                                        - pos.world.rand.nextDouble() * dist1,
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

                    pos.world.spawnParticle(
                        "flame",
                        pos.x + pos.world.rand.nextDouble() * dist
                            - pos.world.rand.nextDouble() * dist,
                        pos.y + pos.world.rand.nextDouble() * dist
                            - pos.world.rand.nextDouble() * dist,
                        pos.z + pos.world.rand.nextDouble() * dist
                            - pos.world.rand.nextDouble() * dist,
                        var21,
                        (double) modifier,
                        var22
                    );
                }
            }
        }
    }

    public static void
    makeEnderEffects(Entity entity, Vec3 prev, int number, boolean sound) {
        World world = entity.worldObj;
        if (!Dartcraft.proxy.isSimulating(world)) {
            for (int i = 0; i < number; ++i) {
                double var19 = (double) i / ((double) number - 1.0D);
                float var21 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                double var221 = prev.x + (entity.posX - prev.x) * var19;
                double var24 = var221
                    + (CommonProxy.rand.nextDouble() - 0.5D) * (double) entity.width
                        * 2.0D;
                var221 = prev.y + (entity.posY - prev.y) * var19;
                double var26
                    = var221 + CommonProxy.rand.nextDouble() * (double) entity.height;
                var221 = prev.z + (entity.posZ - prev.z) * var19;
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
                prev.x,
                prev.y,
                prev.z,
                "mob.endermen.portal",
                2.0F,
                DartUtils.randomPitch()
            );
            world.playSoundAtEntity(
                entity, "mob.endermen.portal", 2.0F, DartUtils.randomPitch()
            );
        }
    }

    public static void
    makeEnderEffects(Entity entity, Vec3 pos, Vec3 prev, int number, boolean sound) {
        if (!Dartcraft.proxy.isSimulating(entity.worldObj)) {
            for (int i = 0; i < number; ++i) {
                double var19 = (double) i / ((double) number - 1.0D);
                float var21 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (CommonProxy.rand.nextFloat() - 0.5F) * 0.2F;
                double var27 = prev.x + (pos.x - prev.x) * var19;
                double var24 = var27
                    + (CommonProxy.rand.nextDouble() - 0.5D) * (double) entity.width
                        * 2.0D;
                var27 = prev.y + (pos.y - prev.y) * var19;
                double var26
                    = var27 + CommonProxy.rand.nextDouble() * (double) entity.height;
                var27 = prev.z + (pos.z - prev.z) * var19;
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
                prev.x, prev.y, prev.z, "mob.endermen.portal", 2.0F, 1.0F
            );
            entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 2.0F, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeChangeEffects(WorldVec pos, int type, int particles) {
        float modifier = 0.5F;
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;

        for (int i = 0; i < particles; ++i) {
            float x = (float) (pos.x + (double) (CommonProxy.rand.nextFloat() * modifier)
                               - (double) (modifier / 2.0F));
            float y = (float) (pos.y + (double) (CommonProxy.rand.nextFloat() * modifier)
                               - (double) (modifier / 2.0F));
            float z = (float) (pos.z + (double) (CommonProxy.rand.nextFloat() * modifier)
                               - (double) (modifier / 2.0F));
            renderer.addEffect(new FXWindWaker(
                pos.world, (double) x, (double) y, (double) z, 0xffffff, type
            ));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeWingEffects(WorldVec pos, int particles) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        float modifier = 1.0F;

        for (int i = 0; i < particles; ++i) {
            float x2
                = (float) (pos.x + (double) (CommonProxy.rand.nextFloat() * modifier));
            float y2
                = (float) (pos.y + (double) (CommonProxy.rand.nextFloat() * modifier));
            float z2
                = (float) (pos.z + (double) (CommonProxy.rand.nextFloat() * modifier));
            renderer.addEffect(new FXWindWaker(
                pos.world, (double) x2, (double) y2, (double) z2, 0xe4ff00, 0
            ));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeSkateEffects(WorldVec pos, int particles) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        float modifier = 1.0F;

        for (int i = 0; i < particles; ++i) {
            float x2
                = (float) (pos.x + (double) (CommonProxy.rand.nextFloat() * modifier));
            float y2
                = (float) (pos.y + (double) (CommonProxy.rand.nextFloat() * modifier));
            float z2
                = (float) (pos.z + (double) (CommonProxy.rand.nextFloat() * modifier));
            renderer.addEffect(new FXWindWaker(
                pos.world, (double) x2, (double) y2, (double) z2, 0xe4ff00, 3
            ));
        }
    }

    public static void spawnFlameFX(WorldVec pos) {
        for (int i = 0; i < 10; ++i) {
            float posX = (float) pos.x + pos.world.rand.nextFloat() * 1.0F;
            float posY = (float) pos.y + pos.world.rand.nextFloat() * 1.0F;
            float posZ = (float) pos.z + pos.world.rand.nextFloat() * 1.0F;
            pos.world.spawnParticle(
                "flame", (double) posX, (double) posY, (double) posZ, 0.0D, 0.0D, 0.0D
            );
        }
    }

    public static void makeBreakFX(WorldVec pos) {
        for (int i = 0; i < 10; ++i) {
            float posX = (float) pos.x + pos.world.rand.nextFloat() * 1.0F;
            float posY = (float) pos.y + pos.world.rand.nextFloat() * 1.0F;
            float posZ = (float) pos.z + pos.world.rand.nextFloat() * 1.0F;
            pos.world.spawnParticle(
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
    public static void
    makeShiny(WorldVec pos, int type, int color, int num, boolean add) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        double offset = 0.0D;
        if (add) {
            offset += 0.5D;
        }

        for (int i = 0; i < num; ++i) {
            FXDisney fx = new FXDisney(
                pos.world,
                pos.x + offset + pos.world.rand.nextDouble()
                    - pos.world.rand.nextDouble(),
                pos.y + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                pos.z + offset + pos.world.rand.nextDouble()
                    - pos.world.rand.nextDouble(),
                color,
                type
            );
            renderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void
    makeCureEffects(WorldVec pos, int type, int color, int num) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;

        for (int i = 0; i < num; ++i) {
            FXCure fx = new FXCure(
                pos.world,
                pos.x + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                pos.y + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                pos.z + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                color,
                type
            );
            renderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void
    makeChargeEffects(WorldVec pos, int type, int color, int num, boolean add) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        double offset = 0.0D;
        if (add) {
            offset += 0.5D;
        }

        for (int i = 0; i < num; ++i) {
            FXCharge fx = new FXCharge(
                pos.world,
                pos.x + offset + pos.world.rand.nextDouble()
                    - pos.world.rand.nextDouble(),
                pos.y + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                pos.z + offset + pos.world.rand.nextDouble()
                    - pos.world.rand.nextDouble(),
                color,
                type
            );
            renderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void makeIceEffects(WorldVec pos, int type, int num, int area) {
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
    public static void makeTimeEffects(WorldVec pos, int type, int num, int area) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        int i;
        if (area > 0) {
            for (i = -area; i < area + 1; ++i) {
                for (int fx = -area; fx < area + 1; ++fx) {
                    for (int k = -area; k < area + 1; ++k) {
                        for (int l = 0; l < num; ++l) {
                            FXTime fx1 = new FXTime(
                                pos.world,
                                pos.x + (double) i + pos.world.rand.nextDouble()
                                    - pos.world.rand.nextDouble(),
                                pos.y + (double) fx + pos.world.rand.nextDouble()
                                    - pos.world.rand.nextDouble(),
                                pos.z + (double) k + pos.world.rand.nextDouble()
                                    - pos.world.rand.nextDouble(),
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
                    pos.world,
                    pos.x + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                    pos.y + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                    pos.z + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                    type
                );
                renderer.addEffect(var16);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void
    makeWWEffects(WorldVec pos, int color, int type, int num, int area) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        int i;
        if (area > 0) {
            for (i = -area; i < area + 1; ++i) {
                for (int fx = -area; fx < area + 1; ++fx) {
                    for (int k = -area; k < area + 1; ++k) {
                        for (int l = 0; l < num; ++l) {
                            FXWindWaker fx1 = new FXWindWaker(
                                pos.world,
                                pos.x + (double) i + pos.world.rand.nextDouble()
                                    - pos.world.rand.nextDouble(),
                                pos.y + (double) fx + pos.world.rand.nextDouble()
                                    - pos.world.rand.nextDouble(),
                                pos.z + (double) k + pos.world.rand.nextDouble()
                                    - pos.world.rand.nextDouble(),
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
                    pos.world,
                    pos.x + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                    pos.y + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                    pos.z + pos.world.rand.nextDouble() - pos.world.rand.nextDouble(),
                    color,
                    type
                );
                renderer.addEffect(var17);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void randomFireworkAt(WorldVec pos) {
        EffectRenderer renderer = Dartcraft.proxy.getClientInstance().effectRenderer;
        NBTTagCompound comp = new NBTTagCompound();
        NBTTagList fireworks = new NBTTagList();

        for (int fx = 0; fx < pos.world.rand.nextInt(5) + 1; ++fx) {
            NBTTagCompound tempComp = new NBTTagCompound();
            tempComp.setBoolean("Flicker", pos.world.rand.nextBoolean());
            tempComp.setBoolean("Trails", pos.world.rand.nextBoolean());
            tempComp.setIntArray("Colors", randomColorArray());
            tempComp.setIntArray("FadeColors", randomColorArray());
            fireworks.appendTag(tempComp);
        }

        comp.setTag("Explosions", fireworks);
        EntityFireworkStarterFX var12 = new EntityFireworkStarterFX(
            pos.world, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D, renderer, comp
        );
        renderer.addEffect(var12);
    }

    @SideOnly(Side.CLIENT)
    public static void wrathAt(WorldVec pos) {
        EntityLightningBolt bolt
            = new EntityLightningBolt(pos.world, pos.x, pos.y, pos.z);
        pos.world.spawnEntityInWorld(bolt);
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
