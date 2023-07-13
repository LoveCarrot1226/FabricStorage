package com.example.DizkZKP.profiler.profiling;

import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import com.example.DizkZKP.algebra.msm.FixedBaseMSM;
import com.example.DizkZKP.configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import com.example.DizkZKP.profiler.generation.FixedBaseMSMGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FixedBaseMSMProfiling {

    public static void serialFixedBaseMSMG1Profiling(final Configuration config, final long size) {
        final BN254aFr fieldFactory = new BN254aFr(2L);
        final BN254aG1 groupFactory = new BN254aG1Parameters().ONE();
        final int scalarSize = groupFactory.bitSize();

        final Random rand = new Random(System.nanoTime());
        final ArrayList<BN254aFr> scalars = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            scalars.add(fieldFactory.random(rand.nextLong(), null));
        }

        config.setContext("FixedBaseMSMG1-Serial");
        config.beginRuntimeMetadata("Size (inputs)", size);

        config.beginLog("FixedBaseMSM");
        config.beginRuntime("FixedBaseMSM");
        final int windowSize = FixedBaseMSM.getWindowSize((int) size, groupFactory);
        final List<List<BN254aG1>> multiplesOfBase = FixedBaseMSM
                .getWindowTable(groupFactory, scalarSize, windowSize);
        final List<BN254aG1> result = FixedBaseMSM
                .batchMSM(scalarSize, windowSize, multiplesOfBase, scalars);
        config.endRuntime("FixedBaseMSM");
        config.endLog("FixedBaseMSM");

        config.writeRuntimeLog(config.context());
    }

    public static void serialFixedBaseMSMG2Profiling(final Configuration config, final long size) {
        final BN254aFr fieldFactory = new BN254aFr(2L);
        final BN254aG2 groupFactory = new BN254aG2Parameters().ONE();
        final int scalarSize = groupFactory.bitSize();

        final Random rand = new Random(System.nanoTime());
        final ArrayList<BN254aFr> scalars = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            scalars.add(fieldFactory.random(rand.nextLong(), null));
        }

        config.setContext("FixedBaseMSMG2-Serial");
        config.beginRuntimeMetadata("Size (inputs)", size);

        config.beginLog("FixedBaseMSM");
        config.beginRuntime("FixedBaseMSM");
        final int windowSize = FixedBaseMSM.getWindowSize((int) size, groupFactory);
        final List<List<BN254aG2>> multiplesOfBase = FixedBaseMSM
                .getWindowTable(groupFactory, scalarSize, windowSize);
        final List<BN254aG2> result = FixedBaseMSM
                .batchMSM(scalarSize, windowSize, multiplesOfBase, scalars);
        config.endRuntime("FixedBaseMSM");
        config.endLog("FixedBaseMSM");

        config.writeRuntimeLog(config.context());
    }

    public static void distributedFixedBaseMSMG1Profiling(final Configuration config, final long size) {
        final BN254aG1 groupFactory = new BN254aG1Parameters().ONE();
        final int scalarSize = groupFactory.bitSize();

        final JavaPairRDD<Long, BN254aFr> scalars = FixedBaseMSMGenerator.generateData(config, size);

        config.setContext("FixedBaseMSMG1");
        config.beginRuntimeMetadata("Size (inputs)", size);

        config.beginLog("FixedBaseMSM");
        config.beginRuntime("FixedBaseMSM");
        final int windowSize = FixedBaseMSM
                .getWindowSize(size / config.numPartitions(), groupFactory);
        final List<List<BN254aG1>> multiplesOfBase = FixedBaseMSM
                .getWindowTable(groupFactory, scalarSize, windowSize);
        FixedBaseMSM.distributedBatchMSM(
                scalarSize,
                windowSize,
                multiplesOfBase,
                scalars,
                config.sparkContext()).count();
        config.endRuntime("FixedBaseMSM");
        config.endLog("FixedBaseMSM");

        config.writeRuntimeLog(config.context());
    }

    public static void distributedFixedBaseMSMG2Profiling(final Configuration config, final long size) {
        final BN254aG2 groupFactory = new BN254aG2Parameters().ONE();
        final int scalarSize = groupFactory.bitSize();

        final JavaPairRDD<Long, BN254aFr> scalars = FixedBaseMSMGenerator.generateData(config, size);

        config.setContext("FixedBaseMSMG2");
        config.beginRuntimeMetadata("Size (inputs)", size);

        config.beginLog("FixedBaseMSM");
        config.beginRuntime("FixedBaseMSM");
        final int windowSize = FixedBaseMSM
                .getWindowSize(size / config.numPartitions(), groupFactory);
        final List<List<BN254aG2>> multiplesOfBase = FixedBaseMSM
                .getWindowTable(groupFactory, scalarSize, windowSize);
        FixedBaseMSM.distributedBatchMSM(
                scalarSize,
                windowSize,
                multiplesOfBase,
                scalars,
                config.sparkContext()).count();
        config.endRuntime("FixedBaseMSM");
        config.endLog("FixedBaseMSM");

        config.writeRuntimeLog(config.context());
    }
}
