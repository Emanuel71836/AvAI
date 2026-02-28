package com.avai.mixin;

import com.avai.AdvancedAIMod;
import com.avai.ai.BehaviorTreeAI;
import com.avai.pathfinding.AvoidTrapdoorNodeEvaluator;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobAIMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("mob_ai_mixin");

    @Unique
    private BehaviorTreeAI avai_behaviorAI;

    @Inject(method = "serverAiStep", at = @At("HEAD"))
    private void onServerAiStepHead(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (avai_behaviorAI == null) {
            try {
                avai_behaviorAI = new BehaviorTreeAI(self);
                LOGGER.info("BehaviorTreeAI initialized for {}", self.getName().getString());

                // replace the path node evaluator with one that avoids open trapdoors
                PathNavigation navigation = self.getNavigation();
                if (navigation instanceof GroundPathNavigation) {
                    // use the accessor to set our custom evaluator if not already set
                    if (navigation.getNodeEvaluator() == null ||
                        !(navigation.getNodeEvaluator() instanceof AvoidTrapdoorNodeEvaluator)) {
                        ((PathNavigationAccessor) navigation).setNodeEvaluator(new AvoidTrapdoorNodeEvaluator());
                        AdvancedAIMod.LOGGER.info("Set AvoidTrapdoorNodeEvaluator for {}", self.getName().getString());
                    } else {
                        AdvancedAIMod.LOGGER.debug("AvoidTrapdoorNodeEvaluator already set for {}", self.getName().getString());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to create BehaviorTreeAI", e);
            }
        }
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void onServerAiStepTail(CallbackInfo ci) {
        if (avai_behaviorAI != null) {
            try {
                avai_behaviorAI.tick();
            } catch (Exception e) {
                LOGGER.error("Error in AI tick", e);
            }
        }
    }
}