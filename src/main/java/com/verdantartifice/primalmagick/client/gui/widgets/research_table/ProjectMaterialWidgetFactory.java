package com.verdantartifice.primalmagick.client.gui.widgets.research_table;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.theorycrafting.AbstractProjectMaterial;
import com.verdantartifice.primalmagick.common.theorycrafting.ExperienceProjectMaterial;
import com.verdantartifice.primalmagick.common.theorycrafting.ItemProjectMaterial;
import com.verdantartifice.primalmagick.common.theorycrafting.ItemTagProjectMaterial;
import com.verdantartifice.primalmagick.common.theorycrafting.ObservationProjectMaterial;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Factory for creating widgets to display research project materials.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ProjectMaterialWidgetFactory {
    @Nullable
    public static AbstractProjectMaterialWidget create(AbstractProjectMaterial material, int x, int y) {
        if (material instanceof ItemProjectMaterial) {
            return new ItemProjectMaterialWidget((ItemProjectMaterial)material, x, y);
        } else if (material instanceof ItemTagProjectMaterial) {
            return new ItemTagProjectMaterialWidget((ItemTagProjectMaterial)material, x, y);
        } else if (material instanceof ObservationProjectMaterial) {
            return new ObservationProjectMaterialWidget((ObservationProjectMaterial)material, x, y);
        } else if (material instanceof ExperienceProjectMaterial) {
            return new ExperienceProjectMaterialWidget((ExperienceProjectMaterial)material, x, y);
        } else {
            return null;
        }
    }
}
