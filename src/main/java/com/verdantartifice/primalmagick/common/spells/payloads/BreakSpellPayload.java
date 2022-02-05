package com.verdantartifice.primalmagick.common.spells.payloads;

import java.util.Map;

import com.verdantartifice.primalmagick.common.enchantments.EnchantmentsPM;
import com.verdantartifice.primalmagick.common.misc.BlockBreaker;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Definition for a block breaking spell.  Breaks the target block over time without further interaction
 * from the player, with the time required increasing with the block's hardness.  Has no effect on
 * entities.  Has no effect when cast by non-players.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.misc.BlockBreaker}
 */
public class BreakSpellPayload extends AbstractSpellPayload {
    public static final String TYPE = "break";
    protected static final CompoundResearchKey RESEARCH = CompoundResearchKey.from(SimpleResearchKey.parse("SPELL_PAYLOAD_BREAK"));

    public BreakSpellPayload() {
        super();
    }
    
    public BreakSpellPayload(int power) {
        super();
        this.getProperty("power").setValue(power);
    }
    
    public static CompoundResearchKey getResearch() {
        return RESEARCH;
    }
    
    @Override
    protected Map<String, SpellProperty> initProperties() {
        Map<String, SpellProperty> propMap = super.initProperties();
        propMap.put("power", new SpellProperty("power", "primalmagick.spell.property.power", 1, 5));
        propMap.put("silk_touch", new SpellProperty("silk_touch", "primalmagick.spell.property.silk_touch", 0, 1));
        return propMap;
    }
    
    @Override
    public void execute(RayTraceResult target, Vector3d burstPoint, SpellPackage spell, World world, LivingEntity caster, ItemStack spellSource) {
        if (target != null && target.getType() == RayTraceResult.Type.BLOCK && caster instanceof PlayerEntity) {
            // Create and enqueue a block breaker for the target block
            BlockRayTraceResult blockTarget = (BlockRayTraceResult)target;
            BlockPos pos = blockTarget.getPos();
            BlockState state = world.getBlockState(pos);
            float durability = (float)Math.sqrt(100.0F * state.getBlockHardness(world, pos));
            boolean silk = (this.getPropertyValue("silk_touch") == 1);
            int treasure = EnchantmentHelper.getEnchantmentLevel(EnchantmentsPM.TREASURE.get(), spellSource);
            BlockBreaker breaker = new BlockBreaker.Builder().power(this.getModdedPropertyValue("power", spell, spellSource)).target(pos, state).durability(durability).player((PlayerEntity)caster).tool(spellSource).silkTouch(silk).fortune(treasure).alwaysDrop().build();
            BlockBreaker.schedule(world, 1, breaker);
        }
    }

    @Override
    public Source getSource() {
        return Source.EARTH;
    }

    @Override
    public int getBaseManaCost() {
        return this.getPropertyValue("power") + (10 * this.getPropertyValue("silk_touch"));
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        world.playSound(null, origin, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.PLAYERS, 1.0F, 1.0F + (float)(world.rand.nextGaussian() * 0.05D));
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }
}
