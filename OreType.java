package com.example.xray;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Predicate;

public enum OreType {
    COAL("Carvão", s -> s.is(BlockTags.COAL_ORES)),
    IRON("Ferro", s -> s.is(BlockTags.IRON_ORES)),
    COPPER("Cobre", s -> s.is(BlockTags.COPPER_ORES)),
    GOLD("Ouro", s -> s.is(BlockTags.GOLD_ORES)),
    REDSTONE("Redstone", s -> s.is(BlockTags.REDSTONE_ORES)),
    LAPIS("Lápis-lazúli", s -> s.is(BlockTags.LAPIS_ORES)),
    DIAMOND("Diamante", s -> s.is(BlockTags.DIAMOND_ORES)),
    EMERALD("Esmeralda", s -> s.is(BlockTags.EMERALD_ORES)),
    QUARTZ("Quartzo", s -> s.is(Blocks.NETHER_QUARTZ_ORE)),
    NETHER_GOLD("Ouro do Nether", s -> s.is(Blocks.NETHER_GOLD_ORE)),
    DEBRIS("Detrito antigo", s -> s.is(Blocks.ANCIENT_DEBRIS)),
    OTHER("Outros (mods)", s -> false);

    public final String label;
    private final Predicate<BlockState> matcher;
    public boolean enabled = true;

    OreType(String label, Predicate<BlockState> matcher) {
        this.label = label;
        this.matcher = matcher;
    }

    /** Retorna o tipo de minério do bloco, ou null se não for minério. */
    public static OreType of(BlockState s) {
        for (OreType t : values()) {
            if (t != OTHER && t.matcher.test(s)) return t;
        }
        if (s.is(Tags.Blocks.ORES)) return OTHER;
        return null;
    }
}
