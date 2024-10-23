package bench;

import com.ezzenix.math.ChunkPos;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ConcurrentHashMap;

@State(Scope.Benchmark)
public class Main {

	private ConcurrentHashMap<Long, Integer> longToChunk = new ConcurrentHashMap<>();
	private ConcurrentHashMap<ChunkPos, Integer> posToChunk = new ConcurrentHashMap<>();

	@Setup(Level.Trial)
	public void setUp() {
		for (int x = 0; x < 100; x++) {
			for (int z = 0; z < 100; z++) {
				ChunkPos pos = new ChunkPos(x, z);
				longToChunk.put(pos.toLong(), x * z);
				posToChunk.put(pos, x * z);
			}
		}
	}

	@Benchmark
	public void chunkTestLong() {
		for (int x = 0; x < 100; x++) {
			for (int z = 0; z < 100; z++) {
				int wow = longToChunk.get(ChunkPos.toLong(x, z));
			}
		}
	}


	@Benchmark
	public void chunkTestPos() {
		for (int x = 0; x < 100; x++) {
			for (int z = 0; z < 100; z++) {
				ChunkPos chunkPos = new ChunkPos(x, z);
				int wow = posToChunk.get(chunkPos);
			}
		}
	}
}