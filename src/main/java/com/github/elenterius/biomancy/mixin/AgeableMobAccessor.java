package com.github.elenterius.biomancy.mixin;

import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableMob.class)
public interface AgeableMobAccessor {

	@Accessor("forcedAge")
	int biomancy_getForcedAge();

	@Accessor("forcedAge")
	void biomancy_setForcedAge(int age);

}
