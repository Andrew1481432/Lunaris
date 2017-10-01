package org.lunaris.material.block;

import org.lunaris.block.BlockColor;
import org.lunaris.material.BlockMaterial;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public abstract class SolidBlock extends BlockMaterial {

    protected SolidBlock(Material material, String name) {
        super(material, name);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.STONE_BLOCK_COLOR;
    }

}