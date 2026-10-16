package net.satisfy.camping.core.world.block;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class BackpackBlockShapes {
    public static final Supplier<VoxelShape> SMALL_BACKPACK = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.375, 0.375, 0.5625, 0.5, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.4375, 0.75, 0.625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.3125, 0.4375), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> LARGE_BACKPACK = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.4375, 0.8125, 0.75, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.3125, 0.75, 0.375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5, 0.375, 0.5625, 0.625, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.75, 0.375, 0.875, 1.0625, 0.6875), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> LARGE_BACKPACK_UNROLLED = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.4375, 0.8125, 0.75, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.3125, 0.75, 0.375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5, 0.375, 0.5625, 0.625, 0.4375), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> WANDERER_BACKPACK = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5, 0.4375, 0.875, 0.6875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.25, 0.3125, 0.8125, 0.5, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.3125, 0.8125, 0.5, 0.6875), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> WANDERER_BACKPACK_UNROLLED = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.3125, 0.8125, 0.5, 0.6875), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> WANDERER_BAG = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.3125, 0.75, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.375, 0.25, 0.25, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.375, 0.875, 0.25, 0.625), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> SHEEPBAG = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.34375, 0.4375, 0.40625, 0.65625, 0.75, 0.71875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.28125, 0, 0.46875, 0.71875, 0.4375, 0.71875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.28125, 0, 0.34375, 0.40625, 0.125, 0.46875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.59375, 0, 0.34375, 0.71875, 0.125, 0.46875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.28125, 0.3125, 0.34375, 0.40625, 0.4375, 0.46875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.59375, 0.3125, 0.34375, 0.71875, 0.4375, 0.46875), BooleanOp.OR);
        return shape;
    };

    public static final Supplier<VoxelShape> GOODYBAG = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.25, 0.75, 0.625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.25, 0.9375, 0.3125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.25, 0.125, 0.3125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.125, 0.6875, 0.3125, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.625, 0.5, 0.75, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.4375, 0.25, 0.75, 0.625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.375, 0.1875, 0.5, 0.5, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.003125, 0.125, 0.3125, 0.128125, 0.5, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.003125, 0.125, 0.4375, 0.128125, 0.75, 0.5625), BooleanOp.OR);
        return shape;
    };
}
