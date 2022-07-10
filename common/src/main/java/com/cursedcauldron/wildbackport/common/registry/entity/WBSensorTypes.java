package com.cursedcauldron.wildbackport.common.registry.entity;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.entities.brain.FrogBrain;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.FrogAttackablesSensor;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.IsInWaterSensor;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.WardenEntitySensor;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.SensorTypeAccessor;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;

import java.util.function.Supplier;

//<>

public class WBSensorTypes {
    public static final CoreRegistry<SensorType<?>> SENSORS = CoreRegistry.create(Registry.SENSOR_TYPE, WildBackport.MOD_ID);

    public static final Supplier<SensorType<WardenEntitySensor>> WARDEN_ENTITY_SENSOR   = create("warden_entity_sensor", WardenEntitySensor::new);
    public static final Supplier<SensorType<TemptingSensor>> FROG_TEMPTATIONS           = create("frog_temptations", () -> new TemptingSensor(FrogBrain.getTemptItems()));
    public static final Supplier<SensorType<FrogAttackablesSensor>> FROG_ATTACKABLES    = create("frog_attackables", FrogAttackablesSensor::new);
    public static final Supplier<SensorType<IsInWaterSensor>> IS_IN_WATER               = create("is_in_water", IsInWaterSensor::new);

    private static <U extends Sensor<?>> Supplier<SensorType<U>> create(String key, Supplier<U> sensor) {
        return SENSORS.register(key, () -> SensorTypeAccessor.createSensorType(sensor));
    }
}