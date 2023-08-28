package io.bluestaggo.voxelthing.world.block;

import io.bluestaggo.voxelthing.math.AABB;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.IBlockAccess;
import io.bluestaggo.voxelthing.world.block.texture.AllSidesTexture;
import io.bluestaggo.voxelthing.world.block.texture.BlockTexture;
import io.bluestaggo.voxelthing.world.block.texture.ColumnTexture;
import io.bluestaggo.voxelthing.world.block.texture.GrassTexture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Block {
	public static final int TEXTURE_RES = 512;
	public static final int TEXTURE_ROWS = TEXTURE_RES / 16;
	public static final float TEXTURE_WIDTH = 1.0f / TEXTURE_ROWS;

	private static short nextId = 0;
	private static Block[] blocks = new Block[256];
	private static final List<Block> REGISTERED_BLOCKS_MUTABLE = new ArrayList<>();
	public static final List<Block> REGISTERED_BLOCKS = Collections.unmodifiableList(REGISTERED_BLOCKS_MUTABLE);

	public static final Block STONE = new Block().withTex(1, 0);
	public static final Block GRASS = new Block().withTex(new GrassTexture(0, 1, 0, 0, 0, 2));
	public static final Block DIRT = new Block().withTex(0, 2);
	public static final Block COBBLESTONE = new Block().withTex(1, 1);
	public static final Block BRICKS = new Block().withTex(3, 2);
	public static final Block PLANKS = new Block().withTex(3, 0);
	public static final Block LOG = new Block().withTex(new ColumnTexture(3, 1, 4, 1));
	public static final Block LEAVES = new Block().withTex(4, 0).transparency(BlockTransparency.THICK);
	public static final Block BIRCH_PLANKS = new Block().withTex(5, 0);
	public static final Block BIRCH_LOG = new Block().withTex(new ColumnTexture(5, 1, 6, 1));
	public static final Block BIRCH_LEAVES = new Block().withTex(6, 0).transparency(BlockTransparency.THICK);
	public static final Block ACACIA_PLANKS = new Block().withTex(9, 0);
	public static final Block ACACIA_LOG = new Block().withTex(new ColumnTexture(9, 1, 10, 1));
	public static final Block ACACIA_LEAVES = new Block().withTex(10, 0).transparency(BlockTransparency.THICK);
	public static final Block SPRUCE_PLANKS = new Block().withTex(7, 0);
	public static final Block SPRUCE_LOG = new Block().withTex(new ColumnTexture(7, 1, 8, 1));
	public static final Block SPRUCE_LEAVES = new Block().withTex(8, 0).transparency(BlockTransparency.THICK);
	public static final Block JUNGLE_PLANKS = new Block().withTex(11, 0);
	public static final Block JUNGLE_LOG = new Block().withTex(new ColumnTexture(11, 1, 12, 1));
	public static final Block JUNGLE_LEAVES = new Block().withTex(12, 0).transparency(BlockTransparency.THICK);
	public static final Block DARK_OAK_PLANKS = new Block().withTex(13, 0);
	public static final Block DARK_OAK_LOG = new Block().withTex(new ColumnTexture(13, 1, 14, 1));
	public static final Block DARK_OAK_LEAVES = new Block().withTex(14, 0).transparency(BlockTransparency.THICK);
	public static final Block GLASS = new Block().withTex(4, 2).transparency(BlockTransparency.FULL);
	public static final Block SAND = new Block().withTex(2, 0);
	public static final Block GRAVEL = new Block().withTex(2, 1);
	public static final Block STONE_BRICKS = new Block().withTex(2, 2);
	public static final Block POLISHED_STONE = new Block().withTex(1, 2);
	public static final Block[] WOOL = IntStream.range(0, 16)
			.mapToObj(i -> new Block().withTex(i % 4, i / 4 + 3))
			.toArray(Block[]::new);

	public final short id;
	protected BlockTexture texture;
	protected BlockTransparency transparency = BlockTransparency.NONE;

	public Block() {
		id = ++nextId;
		if (id == 0) {
			throw new IllegalStateException("Block ID limit of 65535 exceeded!");
		}
		if (id >= blocks.length) {
			blocks = Arrays.copyOf(blocks, blocks.length + 256);
		}

		blocks[id] = this;
		REGISTERED_BLOCKS_MUTABLE.add(this);
	}

	public static Block fromId(short id) {
		if (id < 0 || id >= blocks.length) {
			return null;
		}
		return blocks[id & 0xFFFF];
	}

	protected Block withTex(int x, int y) {
		return withTex(new AllSidesTexture(x, y));
	}

	protected Block withTex(BlockTexture texture) {
		this.texture = texture;
		return this;
	}

	protected Block transparency(BlockTransparency transparency) {
		this.transparency = transparency;
		return this;
	}

	public BlockTexture getTexture() {
		return texture;
	}

	public boolean isTransparent() {
		return transparency.transparent;
	}

	public boolean isFaceDrawn(IBlockAccess blockAccess, int x, int y, int z, Direction face) {
		Block block = blockAccess.getBlock(x, y, z);
		if (block == null) {
			return true;
		}

		if (transparency.transparent) {
			if (!transparency.drawSameFaces) {
				return block != this;
			}
		} else {
			return block.isTransparent();
		}

		return true;
	}

	public AABB getCollisionBox(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
}
