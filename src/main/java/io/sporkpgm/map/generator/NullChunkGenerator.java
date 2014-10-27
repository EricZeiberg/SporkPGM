package io.sporkpgm.map.generator;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class NullChunkGenerator extends ChunkGenerator {

	public byte[] generate(World world, Random random, int x, int z) {
		return new byte[65536];
	}

}
